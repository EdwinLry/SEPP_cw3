package unit_tests;

import external.MockEmailService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestMockEmailService {

    private final MockEmailService emailService = new MockEmailService();

    @Test
    public void sendEmail_withValidEmails_shouldReturnSuccess() {
        int result = emailService.sendEmail("sender@example.com", "recipient@example.com", "Subject", "Body content");
        assertEquals(MockEmailService.STATUS_SUCCESS, result, "Expected STATUS_SUCCESS when both emails are valid");
    }

    @Test
    public void sendEmail_withInvalidSenderEmail_shouldReturnInvalidSenderStatus() {
        int result = emailService.sendEmail("invalid-sender", "recipient@example.com", "Subject", "Body content");
        assertEquals(MockEmailService.STATUS_INVALID_SENDER_EMAIL, result, "Expected STATUS_INVALID_SENDER_EMAIL for invalid sender");
    }

    @Test
    public void sendEmail_withNullSender_shouldReturnInvalidSenderStatus() {
        int result = emailService.sendEmail(null, "recipient@example.com", "Subject", "Body content");
        assertEquals(MockEmailService.STATUS_INVALID_SENDER_EMAIL, result, "Expected STATUS_INVALID_SENDER_EMAIL when sender is null");
    }

    @Test
    public void sendEmail_withInvalidRecipientEmail_shouldReturnInvalidRecipientStatus() {
        int result = emailService.sendEmail("sender@example.com", "not-an-email", "Subject", "Body content");
        assertEquals(MockEmailService.STATUS_INVALID_RECIPIENT_EMAIL, result, "Expected STATUS_INVALID_RECIPIENT_EMAIL for invalid recipient");
    }

    @Test
    public void sendEmail_withNullRecipient_shouldReturnInvalidRecipientStatus() {
        int result = emailService.sendEmail("sender@example.com", null, "Subject", "Body content");
        assertEquals(MockEmailService.STATUS_INVALID_RECIPIENT_EMAIL, result, "Expected STATUS_INVALID_RECIPIENT_EMAIL when recipient is null");
    }
}