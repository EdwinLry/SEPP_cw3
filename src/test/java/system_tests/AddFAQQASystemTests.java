package system_tests;

import controller.AdminStaffController;
import external.AuthenticationService;
import external.EmailService;
import external.MockAuthenticationService;
import external.MockEmailService;
import view.View;
import view.TextUserInterface;
import model.SharedContext;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import view.TextUserInterface;
import view.View;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class AddFAQQASystemTests extends TUITest {


    /**
     * Tests adding a new FAQ item without a course tag.
     */
    @Test
    public void testAddFAQQAWithoutTag() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();
        loginAsAdminStaff(context);

        setMockInput("2", "-2",             // Go to manage FAQ → Add FAQ
                "Topic 1",     // New topic
                "Question 1?",    // Question
                "Answer 1", // Answer
                "n", "-1");                  // No course tag

        AdminStaffController controller = new AdminStaffController(
                context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService()
        );

        startOutputCapture();
        controller.manageFAQ();

        assertOutputContains("Created topic 'Topic 1'");
        assertOutputContains("Created new FAQ item");
    }


    /**
     * Tests adding a new FAQ item with a course tag.
     */
    @Test
    public void testAddFAQQAWithTag() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();
        loginAsAdminStaff(context);

        // Add a course
        setMockInput("3", "-2", "INF2B", "SEPP", "Software Engineering and Professional Practise", "n", "Mrs A", "mrsa@ed.ac.uk",
                "Mr B", "mrb@ed.ac.uk", "1", "3", "-1");

        setMockInput("2", "-2",             // Go to manage FAQ → Add FAQ
                "Topic 2",     // New topic
                "Question 2?",    // Question
                "Answer 2", // Answer
                "y", "-1");                  // No course tag

        AdminStaffController controller = new AdminStaffController(
                context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService()
        );

        startOutputCapture();
        controller.manageFAQ();

        assertOutputContains("Created topic 'Topic 1'");
        assertOutputContains("Created new FAQ item");
        // add course
        // add faq q with tag
    }

    /**
     * Tests adding a new FAQ item when topic already exists.
     */
    @Test
    public void testRepeatTopic() throws URISyntaxException, IOException, ParseException {
        // Topic '' already exists!
        SharedContext context = new SharedContext();
        loginAsAdminStaff(context);

        setMockInput("2", "-2", "RepeatTopic", "Q1", "A1", "n",
                "-2", "RepeatTopic", "Q2", "A2", "n", "-1");

        AdminStaffController controller = new AdminStaffController(
                context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService()
        );

        startOutputCapture();
        controller.manageFAQ();

        assertOutputContains("Topic 'RepeatTopic' already exists!");
    }

    /**
     * Tests adding a new FAQ item with no question.
     */
    @Test
    public void testNoQuestion() throws URISyntaxException, IOException, ParseException {
        // "Question cannot be empty"
        SharedContext context = new SharedContext();
        loginAsAdminStaff(context);

        setMockInput("2", "-2", "No question", "", "Answer", "n", "-1");

        AdminStaffController controller = new AdminStaffController(
                context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService()
        );

        startOutputCapture();
        controller.manageFAQ();

        assertOutputContains("Question cannot be empty");
    }

    /**
     * Tests adding a new FAQ item with no answer.
     */
    @Test
    public void testNoAnswer() throws URISyntaxException, IOException, ParseException {
        // "Answer cannot be empty"
        SharedContext context = new SharedContext();
        loginAsAdminStaff(context);

        setMockInput("2", "-2", "No answer", "Question", "", "n", "-1");

        AdminStaffController controller = new AdminStaffController(
                context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService()
        );

        startOutputCapture();
        controller.manageFAQ();

        assertOutputContains("Answer cannot be empty");
    }
}
