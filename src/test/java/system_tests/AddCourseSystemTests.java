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

    @Test
    public void testAddCourseNoActivities() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();
        loginAsAdminStaff(context);
        setMockInput("-2", "INF1A", "Informatics 1", "Intro to Informatics",
                "y", "Dr X", "drx@ed.ac.uk", "Ms Y", "msy@ed.ac.uk",
                "2", "3", "0", "-1");
        AdminStaffController controller = new AdminStaffController(context, new TextUserInterface(),
                new MockAuthenticationService(), new MockEmailService());
        startOutputCapture();
        controller.manageCourses();
        assertOutputContains("Course added successfully");
    }


    @Test
    public void testAddCourseWithThreeActivities() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();
        loginAsAdminStaff(context);
        setMockInput(
                // Course Information
                "-2", "INF1A", "Informatics 1", "Intro to Informatics",
                "y", "Dr X", "drx@ed.ac.uk", "Ms Y", "msy@ed.ac.uk",
                "2", "3", "3",

                // Lecture
                "1", "Lecture", "2025-09-01", "09:00", "2025-12-01", "10:00",
                "Gordon Aikman", "MONDAY", "y",

                // Tutorial
                "2", "Tutorial", "2025-09-03", "11:00", "2025-12-03", "12:00",
                "Appleton 6.05", "WEDNESDAY", "20",

                // Lab
                "3", "Lab", "2025-09-05", "14:00", "2025-12-05", "16:00",
                "Appleton 4.02", "FRIDAY", "15",

                "-1"
        );
        AdminStaffController controller = new AdminStaffController(context, new TextUserInterface(),
                new MockAuthenticationService(), new MockEmailService());

        startOutputCapture();
        controller.manageCourses();
        assertOutputContains("Course added successfully");
        assertOutputContains("Activity added successfully");
    }


    @Test
    public void testAddInvalidDateFormat() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();
        loginAsAdminStaff(context);

        setMockInput(
                // Course Information
                "-2", "INF1A", "Informatics 1", "Intro to Informatics",
                "y", "Dr X", "drx@ed.ac.uk", "Ms Y", "msy@ed.ac.uk",
                "2", "3", "1",

                // Adding with wrong date
                "1",
                "Lecture",
                "wrong-date-format",
                "2025-12-01",
                "09:00",
                "2025-12-01", "10:00",
                "Appleton 1.05",
                "MONDAY",
                "y",

                "-1");

        AdminStaffController controller = new AdminStaffController(context, new TextUserInterface(),
                new MockAuthenticationService(), new MockEmailService());
        startOutputCapture();
        controller.manageCourses();
        assertOutputContains("Invalid date format. Please use YYYY-MM-DD.");
    }

    @Test
    public void testAddCourseDuplicateCourseCode() throws Exception {
        SharedContext context = new SharedContext();
        loginAsAdminStaff(context);

        // Add course
        setMockInput("-2", "INF1A", "Informatics 1", "Desc", "n",
                "A", "a@x.com", "B", "b@x.com", "1", "1", "0","-1");
        new AdminStaffController(context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService()).manageCourses();

        // Try to add same course
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
                "Dr C", "c@x.com", "Ms D", "d@x.com", "not-a-number", "1", "-1");
        AdminStaffController controller = new AdminStaffController(context,
                new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());
        startOutputCapture();
        controller.manageCourses();
        assertOutputContains("Invalid input. Please enter a number.");
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

    @Test
    public void testInvalidEmail() throws Exception {
        SharedContext context = new SharedContext();
        loginAsAdminStaff(context);
        setMockInput("-2", "INF1B", "Informatics 2", "Desc", "n", "Dr C", "not-an-email", "-1");
        AdminStaffController controller = new AdminStaffController(context,
                new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());
        startOutputCapture();
        controller.manageCourses();
        assertOutputContains("Invalid course organiser email.");
    }
}
