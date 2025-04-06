package system_tests;

import controller.AdminStaffController;
import controller.InquirerController;
import controller.TeachingStaffController;
import external.MockAuthenticationService;
import external.MockEmailService;
import view.TextUserInterface;
import model.SharedContext;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.net.URISyntaxException;


public class ConsultFAQSystemTests extends TUITest {

    /**
     * Tests when there are no questions in the FAQ.
     */
    @Test
    public void testNoQuestions() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();
        setMockInput("-1", "-1");
        InquirerController controller = new InquirerController(
                context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());
        startOutputCapture();
        controller.consultFAQ();
        assertOutputContains("FAQ is Empty.");
    }

    @Test
    public void testSuccessConsult() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();
        loginAsAdminStaff(context);
        setMockInput("-2", "Topic 1", "Question 1?", "Answer 1", "n", "-1", "0");
        AdminStaffController adminStaff = new AdminStaffController(
                context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());
        adminStaff.manageFAQ();
        setMockInput("0","0", "-1", "-1");
        InquirerController controller = new InquirerController(
                context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());
        startOutputCapture();
        controller.consultFAQ();
        assertOutputContains("Topic 1");
        assertOutputContains("Question 1?");
        assertOutputContains("> Answer 1");
    }


    /**
     * Tests Subsections and Super-sections in FAQ hierarchy and navigating it.
     */
    @Test
    public void testHierarchy() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();
        loginAsAdminStaff(context);
        // Add Topic 1
        setMockInput("-2", "Topic 1", "Question 1?", "Answer 1", "n",
                // Add Topic 2 as a subsection under Topic 1
                "0", "-2", "y", "Topic 2", "Question 2?", "Answer 2", "n",
                "-1", "-1", "-1", "0");
        AdminStaffController adminStaff = new AdminStaffController(
                context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());
        adminStaff.manageFAQ();
        //Navigate through subsections
        setMockInput("0", "0", "0","-1", "-1", "-1", "-1");
        InquirerController controller = new InquirerController(
                context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());
        startOutputCapture();
        controller.consultFAQ();
        assertOutputContains("[-1] Return to Topic 1");
        assertOutputContains("[-1] Return to FAQ");
        assertOutputContains("Direct Super-Topics:");
        assertOutputContains("Topic 1");
        assertOutputContains("Direct Subsections:");
        assertOutputContains("[0] Topic 2");
    }

    @Test
    public void testHierarchyTopicCreation() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();
        loginAsAdminStaff(context);
        // Add Topic 1
        setMockInput("-2", "Topic 1", "Question 1?", "Answer 1", "n",
                // Add Topic 2 as a subsection under Topic 1
                "0", "-2", "y", "Topic 2", "Question 2?", "Answer 2", "n",
                "-1", "-1", "-1", "0");
        AdminStaffController adminStaff = new AdminStaffController(
                context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());
        startOutputCapture();
        adminStaff.manageFAQ();
        assertOutputContains("Created topic 'Topic 2' under 'Topic 1'");
    }


    /**
     Tests navigating the hierarchy putting an invalid option
     */
    @Test
    public void testInvalidOptionNumber() throws URISyntaxException, IOException, ParseException {
        setMockInput("n", "0", "5", "-1", "-1");
        InquirerController controller = new InquirerController(
                context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());
        startOutputCapture();
        controller.consultFAQ();
        assertOutputContains("Invalid option: 5");
    }

    /**
     Tests navigating the hierarchy putting an invalid input
     */
    @Test
    public void testInvalidInputString() throws URISyntaxException, IOException, ParseException {
        setMockInput("n", "0", "not-a-number", "-1", "-1");
        InquirerController controller = new InquirerController(
                context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());
        startOutputCapture();
        controller.consultFAQ();
        assertOutputContains("Invalid input: not-a-number");
    }
}
