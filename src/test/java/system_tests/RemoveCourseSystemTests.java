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

public class RemoveCourseSystemTests extends TUITest{
    @BeforeEach
    public void createTestCourse() throws URISyntaxException, IOException, ParseException {
        loginAsStudent(context);
        setMockInput("1", "CS101", "-1");
        StudentController studentController = new StudentController(context,
                new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());
        studentController.manageTimetable();
        loginAsAdminStaff(context);
        context.getCourseManager().addCourse("INF1A", "Informatics 1", "Intro to Informatics",
                true, "Dr X", "drx@ed.ac.uk", "Ms Y", "msy@ed.ac.uk",
                2, 3);

    }

    @Test
    public void testRemoveCourseSuccessfully() throws URISyntaxException, IOException, ParseException {
        setMockInput("-4", "INF1A", "-1");
        AdminStaffController controller = new AdminStaffController(context, new TextUserInterface(),
                new MockAuthenticationService(), new MockEmailService());
        startOutputCapture();
        controller.manageCourses();
        assertOutputContains("Course removed successfully");
    }

    @Test
    public void testRemoveCourseUnSuccessfully() throws URISyntaxException, IOException, ParseException {
        setMockInput("-4", "INF1B", "-1");
        AdminStaffController controller = new AdminStaffController(context, new TextUserInterface(),
                new MockAuthenticationService(), new MockEmailService());
        startOutputCapture();
        controller.manageCourses();
        assertOutputContains("Course not found");
    }

    @Test
    public void testUncapitalizedCourseCode() throws URISyntaxException, IOException, ParseException {
        setMockInput("-4", "inf1a", "-1");
        AdminStaffController controller = new AdminStaffController(context, new TextUserInterface(),
                new MockAuthenticationService(), new MockEmailService());
        startOutputCapture();
        controller.manageCourses();
        assertOutputContains("Course not found");
    }

    @Test
    public void testNoCourseToBeRemoved() throws URISyntaxException, IOException, ParseException {
        context = new SharedContext();
        setMockInput("-4", "-1");
        AdminStaffController controller = new AdminStaffController(
                context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService()
        );
        startOutputCapture();
        controller.manageCourses();
        assertOutputContains("No courses available");
    }

    @Test
    public void testNullInput() throws URISyntaxException, IOException, ParseException {
        setMockInput("-4", null,"-1");
        AdminStaffController controller = new AdminStaffController(context, new TextUserInterface(),
                new MockAuthenticationService(), new MockEmailService());
        startOutputCapture();
        controller.manageCourses();
        assertOutputContains("Course not found");
    }
    @Test
    public void removeCourseInTimetable() throws URISyntaxException, IOException, ParseException {
        setMockInput("-4", "CS101", "-1");
        AdminStaffController controller = new AdminStaffController(context, new TextUserInterface(),
                new MockAuthenticationService(), new MockEmailService());
        startOutputCapture();
        controller.manageCourses();
        assertOutputContains("Course removed successfully");
    }
}
