package email;

public final class EmailSenderFactory {
    public static final String RAW_SMTP = "raw-smtp";
    public static final String JAVAMAIL = "javamail";

    private EmailSenderFactory() {
    }

    public static EmailSender create(String value) {
        String normalized = normalize(value);
        return switch (normalized) {
            case RAW_SMTP -> new RawSmtpEmailSender();
            case JAVAMAIL -> new JavaMailEmailSender();
            default -> throw new IllegalArgumentException(
                    "Unknown email sender: " + value + ". Supported: " + RAW_SMTP + ", " + JAVAMAIL
            );
        };
    }

    public static String normalize(String value) {
        if (value == null) {
            return RAW_SMTP;
        }

        String v = value.trim().toLowerCase();
        if (v.isEmpty()) {
            return RAW_SMTP;
        }

        return switch (v) {
            case "smtp", "raw", "rawsmtp", "raw_smtp", "raw-smtp", "rawemail", "raw-email" -> RAW_SMTP;
            case "mail", "java-mail", "java_mail", "javamail", "javaemail", "java-email", "jakarta-mail", "jakarta_mail" -> JAVAMAIL;
            default -> v;
        };
    }
}
