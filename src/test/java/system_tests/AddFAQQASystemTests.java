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


public class AddFAQQASystemTests extends TUITest {

    /**
     * Tests adding a new FAQ item without a course tag.
     */
    @Test
    public void testAddFAQQAWithoutTag() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();
        loginAsAdminStaff(context);

        setMockInput("2", "-2", "Topic 1", "Question 1?", "Answer 1", "n", "-1");

        AdminStaffController controller = new AdminStaffController(
                context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService()
        );

        startOutputCapture();
        controller.manageFAQ();

        assertOutputContains("Created topic 'Topic 1'");
        assertOutputContains("New FAQ item was added without tag.");
    }


    /**
     * Tests adding a new FAQ item with a course tag.
     */
    @Test
    public void testWithCourseTag() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();
        loginAsAdminStaff(context);

        setMockInput(
                // Add course
                "-2", "INF2B", "SEPP", "Software Engineering and Professional Practice",
                "n", "Mrs A", "mrsa@ed.ac.uk",
                "Mr B", "mrb@ed.ac.uk", "1", "3", "0", "-1", "0",

                // Add FAQ question
                "2", "-2",             // Manage FAQ → Add FAQ
                "Topic 2",             // Topic title
                "Question 2?",         // Question
                "Answer 2",            // Answer
                "y",                   // Add course tag
                "INF2B",               // Tag with the course code
                "-1", "-1"             // Exit FAQ and back to main menu
        );

        AdminStaffController controller = new AdminStaffController(
                context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService()
        );

        controller.manageCourses();
        startOutputCapture();
        controller.manageFAQ();

        assertOutputContains("New FAQ item was added with tag.");
    }


    /**
     * Tests adding a new FAQ item with an invalid course tag.
     */
    @Test
    public void testWithInvalidCourseTag() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();
        loginAsAdminStaff(context);

        setMockInput(
                // Add a course
                "-2", "INF2B", "SEPP", "Software Engineering and Professional Practice",
                "n", "Mrs A", "mrsa@ed.ac.uk",
                "Mr B", "mrb@ed.ac.uk", "1", "3", "0", "-1", "0",

                // Add a question
                "2", "-2",             // Manage FAQ → Add FAQ
                "Topic 2",             // Topic title
                "Question 2?",         // Question
                "Answer 2",            // Answer
                "y",                   // Add course tag
                "not-a-tag",               // Tag with the course code
                "-1", "-1"             // Exit FAQ and back to main menu
        );

        AdminStaffController controller = new AdminStaffController(
                context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService()
        );

        controller.manageCourses();
        startOutputCapture();
        controller.manageFAQ();

        assertOutputContains("The tag must correspond to a course code.");
    }


    /**
     * Tests adding a new FAQ item with a course tag when there are no courses in the system.
     */
    @Test
    public void testNoCourses() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();
        loginAsAdminStaff(context);

        setMockInput("2", "-2", "Topic 2", "Question 2?", "Answer 2", "y", "-1");// No course tags in system

        AdminStaffController controller = new AdminStaffController(
                context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService()
        );

        startOutputCapture();
        controller.manageFAQ();

        assertOutputContains("No courses currently available in the system.");
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
