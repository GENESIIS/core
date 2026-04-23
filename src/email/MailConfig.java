package email;

public record MailConfig(
        String host,
        int port,
        String username,
        String password,
        String from,
        String to,
        String subject,
        String body
) {
}

