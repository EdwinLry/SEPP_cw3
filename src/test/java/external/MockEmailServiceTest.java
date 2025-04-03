package external;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestMockEmailService {

    @Test
    void sendEmail() {
        MockEmailService emailService = new MockEmailService();
        assertAll(
                () -> assertEquals(EmailService.STATUS_SUCCESS, emailService.sendEmail("sender@icloud.com",
                                "recipient@cloud.com", "Subject", "Content"),
                        "Success status (0) when both emails are valid."),

                () -> assertEquals(EmailService.STATUS_INVALID_SENDER_EMAIL, emailService.sendEmail("invalid-sender",
                                "recipient@icloud.com", "Subject", "Content"),
                        "Invalid sender status (1) when sender is invalid."),

                () -> assertEquals(EmailService.STATUS_INVALID_SENDER_EMAIL, emailService.sendEmail(null,
                                "recipient@cloud.com", "Subject", "Content"),
                        "Invalid sender status (1) when sender is null."),

                () -> assertEquals(EmailService.STATUS_INVALID_RECIPIENT_EMAIL, emailService.sendEmail("sender@icloud.com",
                                "invalid-recipient", "Subject", "Content"),
                        "Invalid recipient status (2) when recipient is invalid."),

                () -> assertEquals(EmailService.STATUS_INVALID_RECIPIENT_EMAIL, emailService.sendEmail("sender@icloud.com",
                                null, "Subject", "Content"),
                        "Invalid recipient status (2) when recipient is null.")
        );
    }
}