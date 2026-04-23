import com.genesiis.core.security.auth.tfa.OtpGen;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Properties;

public class SendEmailApp {
    private static final String DEFAULT_HOST = "sandbox.smtp.mailtrap.io";
    private static final int DEFAULT_PORT = 2525;
    private static final String DEFAULT_USERNAME = "9031da9412c8bf";
    private static final String DEFAULT_PASSWORD = "44ee9871185b00";
    private static final String DEFAULT_FROM = "sender@example.com";
    private static final String DEFAULT_TO = "receiver@example.com";
    private static final String DEFAULT_SUBJECT = "Your OTP Code";
    private static final String DEFAULT_BODY_PREFIX = "Your OTP is: ";

    public static void main(String[] args) throws MessagingException {
        String otp = OtpGen.genarate();
        MailConfig config = MailConfig.hardCoded(otp);

        Session session = Session.getInstance(config.smtpProperties(), new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(config.username(), config.password());
            }
        });

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(config.from()));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(config.to(), false));
        message.setSubject(config.subject(), StandardCharsets.UTF_8.name());
        message.setSentDate(Date.from(Instant.now()));
        message.setText(config.body(), StandardCharsets.UTF_8.name());

        Transport.send(message);

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

        Properties smtpProperties() {
            Properties properties = new Properties();
            properties.setProperty("mail.transport.protocol", "smtp");
            properties.setProperty("mail.smtp.host", host);
            properties.setProperty("mail.smtp.port", Integer.toString(port));
            properties.setProperty("mail.smtp.auth", "true");
            properties.setProperty("mail.smtp.starttls.enable", "true");
            properties.setProperty("mail.smtp.starttls.required", "true");
            return properties;
        }
    }
}
