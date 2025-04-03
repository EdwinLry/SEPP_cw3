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

public class AddCourseToTimetableSystemTests extends TUITest{

    private SharedContext context;

    @BeforeEach
    public void beforeTest() throws URISyntaxException, IOException, ParseException {
        context = new SharedContext();
        loginAsAdminStaff(context);
        // Add a course and logout
        setMockInput("here", "-2", "INF2B", "SEPP", "Software Engineering and Professional Practise", "n", "Mrs A", "mrsa@ed.ac.uk",
                "Mr B", "mrb@ed.ac.uk", "1", "3", "-1", "0");

        AdminStaffController adminStaff = new AdminStaffController(context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());
        adminStaff.manageCourses();
    }
    /**
     * Tests successfully adding a course.
     */
    @Test
    public void addCourseSuccess() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();
        loginAsStudent(context);

        setMockInput("4", "1", "INF1B", "-1");

        StudentController controller = new StudentController(
                context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService()
        );

        startOutputCapture();
        controller.manageTimetable();

        assertOutputContains("The course was successfully added to your timetable");
    }

    /**
     * Tests adding a course with the wrong course code.
     */
    @Test
    public void addCourseWrongCode() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();
        loginAsStudent(context);

        setMockInput("4", "1", "Not-a-course");

        StudentController controller = new StudentController(
                context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService()
        );

        startOutputCapture();
        controller.manageTimetable();

        assertOutputContains("Incorrect course code");
    }



}
