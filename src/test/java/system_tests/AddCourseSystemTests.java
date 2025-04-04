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


public class AddCourseSystemTests extends TUITest{
    //TODO: implement this
    //@BeforeEach
    @Test
    public void testAddCourseSuccessfully() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();
        loginAsAdminStaff(context);

        setMockInput("-2", "INF1A", "Informatics 1", "Intro to Informatics",
                "y", "Dr X", "drx@ed.ac.uk", "Ms Y", "msy@ed.ac.uk",
                "2", "3", "-1");

        AdminStaffController controller = new AdminStaffController(
                context,
                new TextUserInterface(),
                new MockAuthenticationService(),
                new MockEmailService()
        );

        startOutputCapture();
        controller.manageCourses();

        assertOutputContains("Course added successfully");
    }

    @Test
    public void testAddCourseDuplicateCode() throws Exception {
        SharedContext context = new SharedContext();
        loginAsAdminStaff(context);

        // Add once
        setMockInput("-2", "INF1A", "Informatics 1", "Desc", "n",
                "A", "a@x.com", "B", "b@x.com", "1", "1", "-1");
        new AdminStaffController(context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService()).manageCourses();

        // Try to add again
        loginAsAdminStaff(context);
        setMockInput("-2", "INF1A", "-1");

        AdminStaffController controller = new AdminStaffController(context,
                new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());

        startOutputCapture();
        controller.manageCourses();

        assertOutputContains("Course with the same code already exists");
    }

    @Test
    public void testNotNumberTutorials() throws Exception {
        SharedContext context = new SharedContext();
        loginAsAdminStaff(context);

        setMockInput("-2", "INF1B", "Informatics 2", "Desc", "n",
                "Dr C", "c@x.com", "Ms D", "d@x.com", "not-a-number", // Not a number input
                "1", "-1");

        AdminStaffController controller = new AdminStaffController(context,
                new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());

        startOutputCapture();
        controller.manageCourses();

        assertOutputContains("Invalid input for required tutorials or labs");
    }

    @Test
    public void testNegativeNumberTutorials() throws Exception {
        SharedContext context = new SharedContext();
        loginAsAdminStaff(context);

        setMockInput("-2", "INF1B", "Informatics 2", "Desc", "n",
                "Dr C", "c@x.com", "Ms D", "d@x.com", "-3", // Negative tutorials input
                "1", "-1");

        AdminStaffController controller = new AdminStaffController(context,
                new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());

        startOutputCapture();
        controller.manageCourses();

        assertOutputContains("Need 0 or more of each.");
    }

    //TODO: invalid email
    public void testInvalidEmail() throws Exception {
        SharedContext context = new SharedContext();
        loginAsAdminStaff(context);

        setMockInput("-2", "INF1B", "Informatics 2", "Desc", "n",
                "Dr C", "not-an-email", "-1");

        AdminStaffController controller = new AdminStaffController(context,
                new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());

        startOutputCapture();
        controller.manageCourses();

        assertOutputContains("Invalid course organiser email.");
    }
}
