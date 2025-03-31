package unit_tests;

import external.MockEmailService;
import external.EmailService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestMockEmailService {

    @Test
    public void validEmails() {
        MockEmailService emailService = new MockEmailService();
        int result = emailService.sendEmail("sender@icloud.com", "recipient@cloud.com", "Subject", "Content");
        assertEquals(EmailService.STATUS_SUCCESS, result, "Success (0) when both emails are valid.");
    }

    @Test
    public void invalidSenderEmail() {
        MockEmailService emailService = new MockEmailService();
        int result = emailService.sendEmail("invalid-sender", "recipient@icloud.com", "Subject", "Content");
        assertEquals(EmailService.STATUS_INVALID_SENDER_EMAIL, result, "Invalid sender (1) when sender is invalid.");
    }

    @Test
    public void nullSenderEmail() {
        MockEmailService emailService = new MockEmailService();
        int result = emailService.sendEmail(null, "recipient@icloud.com", "Subject", "Body content");
        assertEquals(EmailService.STATUS_INVALID_SENDER_EMAIL, result, "Invalid sender (1) when sender is null.");
    }

    @Test
    public void invalidRecipientEmail() {
        MockEmailService emailService = new MockEmailService();
        int result = emailService.sendEmail("sender@icloud.com", "invalid-reciepent", "Subject", "Content");
        assertEquals(EmailService.STATUS_INVALID_RECIPIENT_EMAIL, result, "Invalid recipient (2) when recipient is invalid.");
    }

    @Test
    public void sendEmail_withNullRecipient_shouldReturnInvalidRecipientStatus() {
        MockEmailService emailService = new MockEmailService();
        int result = emailService.sendEmail("sender@example.com", null, "Subject", "Content");
        assertEquals(EmailService.STATUS_INVALID_RECIPIENT_EMAIL, result, "Invalid recipient (2) when recipient is null.");
    }
}
