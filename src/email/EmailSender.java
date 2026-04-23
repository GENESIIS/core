package email;

public interface EmailSender {
    void send(MailConfig config) throws Exception;
}

