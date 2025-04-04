package system_tests;

import controller.AdminStaffController;
import controller.StudentController;
import external.MockAuthenticationService;
import external.MockEmailService;
import model.SharedContext;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import view.TextUserInterface;

import java.io.IOException;
import java.net.URISyntaxException;

public class AddCourseToTimetableSystemTests extends TUITest {

    //TODO: try to add to timetable when there are no courses

    private SharedContext context;

    @BeforeEach
    public void setUp() throws URISyntaxException, IOException, ParseException {
        context = new SharedContext();

        // Admin logs in and adds the course
        loginAsAdminStaff(context);
        setMockInput("-2", "INF2B", "SEPP", "Software Engineering and Professional Practice", "n", "Mrs A", "mrsa@ed.ac.uk",
                "Mr B", "mrb@ed.ac.uk", "1", "3", "-1", "0");

        AdminStaffController adminStaff = new AdminStaffController(context,
                new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());
        adminStaff.manageCourses();
    }

    /**
     * Tests successfully adding a course.
     */
    @Test
    public void testAddCourseSuccess() throws URISyntaxException, IOException, ParseException {
        loginAsStudent(context);
        setMockInput("1", "INF2B", "-1");

        StudentController studentController = new StudentController(context,
                new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());

        startOutputCapture();
        studentController.manageTimetable();

        assertOutputContains("The course was successfully added to your timetable");
    }

    /**
     * Tests adding a course with an invalid course code.
     */
    @Test
    public void testAddCourseInvalidCode() throws URISyntaxException, IOException, ParseException {
        loginAsStudent(context);
        setMockInput("1", "Not-a-course", "-1");

        StudentController studentController = new StudentController(context,
                new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());

        startOutputCapture();
        studentController.manageTimetable();

        assertOutputContains("Incorrect course code, failed to add course.");
    }

    /**
     * Tests adding a course with an empty course code.
     */
    @Test
    public void testAddCourseEmptyCode() throws URISyntaxException, IOException, ParseException {
        loginAsStudent(context);
        setMockInput("1", "", "-1");

        StudentController studentController = new StudentController(context,
                new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());

        startOutputCapture();
        studentController.manageTimetable();

        assertOutputContains("Must input a course code.");
    }
}
