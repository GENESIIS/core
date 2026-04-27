package email;

import jakarta.mail.Address;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

public final class JavaMailEmailSender implements EmailSender {
    @Override
    public void send(MailConfig config) throws Exception {
        sendWithJavaMail(config);
    }

    private static void sendWithJavaMail(MailConfig config) throws Exception {
        Properties props = new Properties();
        props.put("mail.smtp.host", config.host());
        props.put("mail.smtp.port", String.valueOf(config.port()));
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props);
        MimeMessage message = new MimeMessage(session);

        message.setFrom(new InternetAddress(config.from()));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(config.to()));
        message.setSubject(config.subject(), StandardCharsets.UTF_8.name());
        message.setText(config.body(), StandardCharsets.UTF_8.name());
        message.saveChanges();

        Transport transport = session.getTransport("smtp");
        try {
            transport.connect(config.host(), config.port(), config.username(), config.password());

            Address[] recipients = message.getAllRecipients();
            if (recipients == null) {
                recipients = new Address[0];
            }

            transport.sendMessage(message, recipients);
        } finally {
            transport.close();
        }
    }
}
