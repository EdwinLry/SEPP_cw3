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

    @Test
    public void testRemoveSuccess() throws URISyntaxException, IOException, ParseException {
        //SharedContext context = new SharedContext();
        loginAsAdminStaff(context);
        // Add FAQ topic and item
        setMockInput("-2", "Topic 1", "Q1", "A1", "n", "0",
                // Remove item
                "-3", "0", "-1", "-1");
        AdminStaffController adminStaffController = new AdminStaffController(
                context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());
        startOutputCapture();
        adminStaffController.manageFAQ();
        assertOutputContains("FAQ item removed successfully.");
    }

    @Test
    public void testRemoveFAQWithReindexing() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();
        loginAsAdminStaff(context);
        // Add FAQ topic and two items
        setMockInput("2", "-2", "Topic 1", "Q1", "A1", "n",
                "0", "-2", "n", "Q2", "A2", "n",
                // Remove first item (index 0)
                "-3", "0",
                // Remove remaining item (now at index 0 again after reindex)
                "-3", "0", "-1", "-1");
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
        setMockInput("-3", "-1");
        AdminStaffController admin = new AdminStaffController(
                context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());
        startOutputCapture();
        admin.manageFAQ();
        assertOutputContains("FAQ is empty, no FAQ item to remove.");
    }

    @Test
    public void testRemoveInvalidFAQItemId() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();
        loginAsAdminStaff(context);
        setMockInput("-2", "Topic 1", "Q1", "A1", "n",
                "0", "-3", "1", "-1", "-1");
        AdminStaffController admin = new AdminStaffController(
                context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());
        startOutputCapture();
        admin.manageFAQ();
        assertOutputContains("No FAQ item with ID 1 found in this section.");
    }

    @Test
    public void testSectionDeletionAfterLastItemRemoved() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();
        loginAsAdminStaff(context);
        setMockInput(
                // Login
                "2", "-2", "Topic 1", "Q1", "A1", "n",
                "0", "-2", "y", "Topic 2", "Q2", "A2", "n",
                "-3", "0", "-1", "0", "-3", "0",
                "-1", "-1" , "-1");
        AdminStaffController controller = new AdminStaffController(
                context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());
        startOutputCapture();
        controller.manageFAQ();
        assertOutputContains("FAQ item removed successfully.");
        assertOutputContains("Section was empty after removal and has been deleted. Its subsections were moved up.");
    }

    @Test
    public void testNeedToNavigate() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();
        loginAsAdminStaff(context);
        setMockInput(
                "2", "-2", "Topic 1", "Q1", "A1", "n", "0",
                "-2", "y", "Topic 2", "Q2", "A2", "n", "-1",
                "-3", "0", "-1", "-1");
        AdminStaffController controller = new AdminStaffController(
                context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());
        startOutputCapture();
        controller.manageFAQ();
        assertOutputContains("Please navigate into a topic to remove its FAQ items.");
    }

}
