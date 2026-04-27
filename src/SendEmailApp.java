import com.genesiis.core.security.auth.tfa.OtpGen;

import email.EmailSender;
import email.EmailSenderFactory;
import email.MailConfig;

public class SendEmailApp {
    // Change this value and run the program (no args needed).
    // Supported: raw-smtp, javamail (also accepts: rawemail, javaemail)
    private static final String EMAIL_SENDER_METHOD = "raw-smtp";

    private static final String DEFAULT_HOST = "sandbox.smtp.mailtrap.io";
    private static final int DEFAULT_PORT = 2525;
    private static final String DEFAULT_USERNAME = "9031da9412c8bf";
    private static final String DEFAULT_PASSWORD = "44ee9871185b00";
    private static final String DEFAULT_FROM = "sender@example.com";
    private static final String DEFAULT_TO = "receiver@example.com";
    private static final String DEFAULT_SUBJECT = "Your OTP Code";
    private static final String DEFAULT_BODY_PREFIX = "Your OTP is: ";

    public static void main(String[] args) throws Exception {
        String otp = OtpGen.generate();
        MailConfig config = hardCodedConfig(otp);

        String senderId = EmailSenderFactory.normalize(EMAIL_SENDER_METHOD);
        EmailSender sender = EmailSenderFactory.create(senderId);
        try {
            sender.send(config);
        } catch (IllegalStateException e) {
            System.err.println(e.getMessage());
            System.exit(2);
        }

        System.out.println("Email sent (" + senderId + ") with OTP: " + otp);
    }

    private static MailConfig hardCodedConfig(String otp) {
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
}
