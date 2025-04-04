package system_tests;

import controller.AdminStaffController;
import controller.ViewerController;
import controller.StudentController;
import external.MockAuthenticationService;
import external.MockEmailService;
import model.SharedContext;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;
import view.TextUserInterface;

import java.io.IOException;
import java.net.URISyntaxException;

public class ViewCoursesSystemTests extends TUITest {
    @Test
    public void testSuccessList() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();

        // Admin logs in and adds two courses
        loginAsAdminStaff(context);
        setMockInput(
                "-2", "INF2B", "SEPP", "Software Engineering and Professional Practice", "n", "Mrs A", "mrsa@ed.ac.uk",
                "Mr B", "mrb@ed.ac.uk", "1", "3",
                "-2", "INF2B-2", "SEPP-2", "Software Engineering and Professional Practice", "n", "Mrs A", "mrsa@ed.ac.uk",
                "Mr B", "mrb@ed.ac.uk", "1", "3", "-1", "0"
        );
        AdminStaffController adminStaff = new AdminStaffController(context,
                new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());
        adminStaff.manageCourses();

        setMockInput("1", "3");

        ViewerController controller = new ViewerController(
                context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService()
        );

        startOutputCapture();
        controller.viewCoursesMenu();
        assertOutputContains("Course name: SEPP - Course code: INF2B");
        assertOutputContains("Course name: SEPP-2 - Course code: INF2B-2");

    }

    @Test
    public void testSuccessSpecific() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();

        // Admin logs in and adds one course
        loginAsAdminStaff(context);
        setMockInput(
                "-2", "INF2B", "SEPP", "Software Engineering and Professional Practice", "n",
                "Mrs A", "mrsa@ed.ac.uk", "Mr B", "mrb@ed.ac.uk", "2", "3",
                "-1", "0"
        );
        AdminStaffController adminStaff = new AdminStaffController(context,
                new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());
        adminStaff.manageCourses();

        // Guest views a specific course by course name
        setMockInput("2", "INF2B", "3");
        ViewerController controller = new ViewerController(
                context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService()
        );

        startOutputCapture();
        controller.viewCoursesMenu();

        // Full course details are displayed
        assertOutputContains("Course Code: INF2B");
        assertOutputContains("Course Name: SEPP");
        assertOutputContains("Course Description: Software Engineering and Professional Practice");
        assertOutputContains("Requires Computers: false");
        assertOutputContains("Course Organiser Name: Mrs A");
        assertOutputContains("Course Organiser Email: mrsa@ed.ac.uk");
        assertOutputContains("Course Secretary Name: Mr B");
        assertOutputContains("Course Secretary Email: mrb@ed.ac.uk");
        assertOutputContains("Required Tutorials: 2");
        assertOutputContains("Required Labs: 3");
    }

    @Test
    public void testWrongCourseCode() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();

        // Admin logs in and adds one course
        loginAsAdminStaff(context);
        setMockInput(
                "-2", "INF2B", "SEPP", "Software Engineering and Professional Practice", "n",
                "Mrs A", "mrsa@ed.ac.uk", "Mr B", "mrb@ed.ac.uk", "2", "3",
                "-1", "0"
        );
        AdminStaffController adminStaff = new AdminStaffController(context,
                new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());
        adminStaff.manageCourses();

        // Guest searches for invalid course code
        setMockInput("2", "not-a-code", "3");
        ViewerController controller = new ViewerController(
                context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService()
        );

        startOutputCapture();
        controller.viewCoursesMenu();

        assertOutputContains("Course not found.");
    }

    @Test
    public void testNoEnteredCourses() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();

        // Guest searches for invalid course code
        setMockInput("1", "3");
        ViewerController controller = new ViewerController(
                context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService()
        );

        startOutputCapture();
        controller.viewCoursesMenu();

        assertOutputContains("No courses available.");
    }



}
