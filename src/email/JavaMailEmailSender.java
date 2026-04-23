package email;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Properties;

public final class JavaMailEmailSender implements EmailSender {
    @Override
    public void send(MailConfig config) throws Exception {
        try {
            sendWithJavaMail(config);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(
                    "JavaMail (jakarta.mail) is not on the classpath.\n"
                            + "Put the Jakarta Mail jars into lib/ and run with: java -cp \"out;lib/*\" SendEmailApp --sender=javamail\n"
                            + "Or run via Maven: mvn -q compile exec:java \"-Dexec.args=--sender=javamail\"",
                    e
            );
        }
    }

    private static void sendWithJavaMail(MailConfig config) throws Exception {
        Properties props = new Properties();
        props.put("mail.smtp.host", config.host());
        props.put("mail.smtp.port", String.valueOf(config.port()));
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Class<?> sessionClass = Class.forName("jakarta.mail.Session");
        Object session = sessionClass.getMethod("getInstance", Properties.class).invoke(null, props);

        Class<?> mimeMessageClass = Class.forName("jakarta.mail.internet.MimeMessage");
        Constructor<?> mimeCtor = mimeMessageClass.getConstructor(sessionClass);
        Object message = mimeCtor.newInstance(session);

        Class<?> addressClass = Class.forName("jakarta.mail.Address");
        Class<?> internetAddressClass = Class.forName("jakarta.mail.internet.InternetAddress");
        Object from = internetAddressClass.getConstructor(String.class).newInstance(config.from());
        Object to = internetAddressClass.getConstructor(String.class).newInstance(config.to());

        Method setFrom = mimeMessageClass.getMethod("setFrom", addressClass);
        setFrom.invoke(message, from);

        Class<?> recipientTypeClass = Class.forName("jakarta.mail.Message$RecipientType");
        Field toField = recipientTypeClass.getField("TO");
        Object toType = toField.get(null);
        Method setRecipient = mimeMessageClass.getMethod("setRecipient", recipientTypeClass, addressClass);
        setRecipient.invoke(message, toType, to);

        setSubject(mimeMessageClass, message, config.subject());
        setText(mimeMessageClass, message, config.body());

        mimeMessageClass.getMethod("saveChanges").invoke(message);

        Object transport = sessionClass.getMethod("getTransport", String.class).invoke(session, "smtp");

        Class<?> transportClass = Class.forName("jakarta.mail.Transport");
        try {
            transportClass.getMethod("connect", String.class, int.class, String.class, String.class)
                    .invoke(transport, config.host(), config.port(), config.username(), config.password());

            Object recipients = mimeMessageClass.getMethod("getAllRecipients").invoke(message);
            if (recipients == null) {
                recipients = Array.newInstance(addressClass, 0);
            }

            Class<?> messageClass = Class.forName("jakarta.mail.Message");
            Class<?> addressArrayClass = Array.newInstance(addressClass, 0).getClass(); // Address[]
            Method sendMessage = transportClass.getMethod("sendMessage", messageClass, addressArrayClass);
            sendMessage.invoke(transport, message, recipients);
        } finally {
            transportClass.getMethod("close").invoke(transport);
        }
    }

    private static void setSubject(Class<?> mimeMessageClass, Object message, String subject) throws Exception {
        try {
            mimeMessageClass.getMethod("setSubject", String.class, String.class).invoke(message, subject, "UTF-8");
        } catch (NoSuchMethodException ignored) {
            mimeMessageClass.getMethod("setSubject", String.class).invoke(message, subject);
        }
    }

    private static void setText(Class<?> mimeMessageClass, Object message, String text) throws Exception {
        try {
            mimeMessageClass.getMethod("setText", String.class, String.class).invoke(message, text, "UTF-8");
        } catch (NoSuchMethodException ignored) {
            mimeMessageClass.getMethod("setText", String.class).invoke(message, text);
        }
    }
}
