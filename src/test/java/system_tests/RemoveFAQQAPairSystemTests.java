package system_tests;

import controller.AdminStaffController;
import external.MockAuthenticationService;
import external.MockEmailService;
import view.TextUserInterface;
import model.SharedContext;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.net.URISyntaxException;


public class RemoveFAQQAPairSystemTests extends TUITest {

    // FAQ is empty, no FAQ item to remove.
    // No FAQ item with id 1 found in this section.

    @Test
    public void testRemoveFAQWithReindexing() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();
        loginAsAdminStaff(context);

        // Step 1: Add FAQ topic and first item
        setMockInput("2", "-2", "topic 1", "q1", "a1", "n",
                // Step 2: Enter topic and add second item without new topic
                "0", "-2", "n", "q2", "a2", "n",
                // Step 3: Remove first item (index 0)
                "-3", "0",
                // Step 4: Remove remaining item (now at index 0 again after reindex)
                "-3", "0",
                // Step 5: Return to FAQ and then exit to main
                "-1", "-1");

        AdminStaffController adminStaffController = new AdminStaffController(
                context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());

        startOutputCapture();
        adminStaffController.manageFAQ();

        assertOutputContains("FAQ item removed successfully.");
        assertOutputContains("Top-level section was empty and removed. Subsections moved up.");
        assertOutputContains("FAQ is Empty.");
    }

    @Test
    public void testRemoveFromEmptyFAQ() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();
        loginAsAdminStaff(context);

        setMockInput("-3", "-1"); // Attempt to remove from empty FAQ, then exit

        AdminStaffController admin = new AdminStaffController(
                context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService()
        );

        startOutputCapture();
        admin.manageFAQ();
        assertOutputContains("FAQ is empty, no FAQ item to remove.");
    }

    @Test
    public void testRemoveInvalidFAQItemId() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();
        loginAsAdminStaff(context);

        setMockInput("-2", "topic 1", "q1", "a1", "n", // Add 1 item
                "0",                             // Navigate to topic
                "-3", "1",                       // Try to remove non-existent item ID 1
                "-1", "-1");                     // Exit

        AdminStaffController admin = new AdminStaffController(
                context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService()
        );

        startOutputCapture();
        admin.manageFAQ();
        assertOutputContains("No FAQ item with ID 1 found in this section.");
    }

}
