package andrew.project.socialNetwork.backend.api.services;

import org.springframework.mail.MailException;
import org.springframework.scheduling.annotation.Async;

public interface EmailSenderService {

    void sendEmail(String toEmail, String subject, String body) throws MailException;

    @Async
    void sendEmailAsync(String toEmail, String subject, String body);

}
