import com.genesiis.core.security.auth.tfa.OtpGen;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

public class SendEmailApp {
    private static final String DEFAULT_HOST = "sandbox.smtp.mailtrap.io";
    private static final int DEFAULT_PORT = 2525;
    private static final String DEFAULT_USERNAME = "9031da9412c8bf";
    private static final String DEFAULT_PASSWORD = "44ee9871185b00";
    private static final String DEFAULT_FROM = "sender@example.com";
    private static final String DEFAULT_TO = "receiver@example.com";
    private static final String DEFAULT_SUBJECT = "Your OTP Code";
    private static final String DEFAULT_BODY_PREFIX = "Your OTP is: ";

    public static void main(String[] args) throws Exception {
        String otp = OtpGen.genarate();
        MailConfig config = MailConfig.hardCoded(otp);

        try (SmtpClient smtp = SmtpClient.connect(config.host(), config.port())) {
            smtp.expect(220);
            smtp.ehlo();
            smtp.startTls();
            smtp.ehlo();
            smtp.authPlain(config.username(), config.password());
            smtp.sendMail(config.from(), config.to(), config.subject(), config.body());
            smtp.quit();
        }

        System.out.println("Email sent to Mailtrap with OTP: " + otp);
    }

    private static final class MailConfig {
        private final String host;
        private final int port;
        private final String username;
        private final String password;
        private final String from;
        private final String to;
        private final String subject;
        private final String body;

        private MailConfig(
                String host,
                int port,
                String username,
                String password,
                String from,
                String to,
                String subject,
                String body
        ) {
            this.host = host;
            this.port = port;
            this.username = username;
            this.password = password;
            this.from = from;
            this.to = to;
            this.subject = subject;
            this.body = body;
        }

        static MailConfig hardCoded(String otp) {
            return new MailConfig(
                    DEFAULT_HOST,
                    DEFAULT_PORT,
                    DEFAULT_USERNAME,
                    DEFAULT_PASSWORD,
                    DEFAULT_FROM,
                    DEFAULT_TO,
                    DEFAULT_SUBJECT,
                    DEFAULT_BODY_PREFIX + otp
            );
        }

        String host() {
            return host;
        }

        int port() {
            return port;
        }

        String username() {
            return username;
        }

        String password() {
            return password;
        }

        String from() {
            return from;
        }

        String to() {
            return to;
        }

        String subject() {
            return subject;
        }

        String body() {
            return body;
        }
    }

    private static final class SmtpClient implements AutoCloseable {
        private final String host;
        private Socket socket;
        private BufferedReader reader;
        private BufferedWriter writer;

        private SmtpClient(String host, Socket socket) throws IOException {
            this.host = host;
            this.socket = socket;
            resetStreams();
        }

        static SmtpClient connect(String host, int port) throws IOException {
            return new SmtpClient(host, new Socket(host, port));
        }

        void ehlo() throws IOException {
            sendCommand("EHLO localhost");
            expect(250);
        }

        void startTls() throws IOException {
            sendCommand("STARTTLS");
            expect(220);

            SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            SSLSocket tlsSocket = (SSLSocket) sslSocketFactory.createSocket(socket, host, socket.getPort(), true);
            tlsSocket.startHandshake();
            socket = tlsSocket;
            resetStreams();
        }

        void authPlain(String username, String password) throws IOException {
            String credentials = "\0" + username + "\0" + password;
            String encoded = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

            sendCommand("AUTH PLAIN " + encoded);
            expect(235);
        }

        void sendMail(String from, String to, String subject, String body) throws IOException {
            sendCommand("MAIL FROM:<" + from + ">");
            expect(250);

            sendCommand("RCPT TO:<" + to + ">");
            expect(Arrays.asList(250, 251));

            sendCommand("DATA");
            expect(354);

            writeDataLine("From: " + sanitizeHeader(from));
            writeDataLine("To: " + sanitizeHeader(to));
            writeDataLine("Subject: " + sanitizeHeader(subject));
            writeDataLine("Date: " + DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now()));
            writeDataLine("Message-ID: <" + UUID.randomUUID() + "@localhost>");
            writeDataLine("MIME-Version: 1.0");
            writeDataLine("Content-Type: text/plain; charset=UTF-8");
            writeDataLine("");
            writeMessageBody(body);
            writeDataLine(".");
            writer.flush();

            expect(250);
        }

        void quit() throws IOException {
            sendCommand("QUIT");
            expect(Arrays.asList(221, 250));
        }

        void expect(int expectedCode) throws IOException {
            expect(Arrays.asList(expectedCode));
        }

        void expect(List<Integer> expectedCodes) throws IOException {
            SmtpReply reply = readReply();
            if (!expectedCodes.contains(reply.code())) {
                throw new IOException("Expected SMTP code " + expectedCodes + " but received: " + reply.text());
            }
        }

        private void sendCommand(String command) throws IOException {
            writer.write(command);
            writer.write("\r\n");
            writer.flush();
        }

        private SmtpReply readReply() throws IOException {
            StringBuilder text = new StringBuilder();
            int code = -1;

            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    throw new IOException("SMTP server closed the connection.");
                }

                text.append(line).append(System.lineSeparator());

                if (line.length() >= 3 && Character.isDigit(line.charAt(0))
                        && Character.isDigit(line.charAt(1)) && Character.isDigit(line.charAt(2))) {
                    code = Integer.parseInt(line.substring(0, 3));
                }

                if (line.length() < 4 || line.charAt(3) != '-') {
                    break;
                }
            }

            return new SmtpReply(code, text.toString().trim());
        }

        private void writeMessageBody(String body) throws IOException {
            String normalizedBody = body.replace("\r\n", "\n").replace('\r', '\n');
            for (String line : normalizedBody.split("\n", -1)) {
                if (line.startsWith(".")) {
                    line = "." + line;
                }
                writeDataLine(line);
            }
        }

        private void writeDataLine(String line) throws IOException {
            writer.write(line);
            writer.write("\r\n");
        }

        private static String sanitizeHeader(String value) {
            return value.replace('\r', ' ').replace('\n', ' ');
        }

        private void resetStreams() throws IOException {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        }

        @Override
        public void close() throws IOException {
            socket.close();
        }
    }

    private static final class SmtpReply {
        private final int code;
        private final String text;

        private SmtpReply(int code, String text) {
            this.code = code;
            this.text = text;
        }

        int code() {
            return code;
        }

        String text() {
            return text;
        }
    }
}
