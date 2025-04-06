package system_tests;

import controller.StudentController;
import external.MockAuthenticationService;
import external.MockEmailService;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import view.TextUserInterface;

import java.io.IOException;
import java.net.URISyntaxException;

public class ChooseActivitySystemTests extends TUITest{
    @BeforeEach
    public void init() throws URISyntaxException, IOException, ParseException {
        // Initialize the context
        loginAsStudent(context);
        setMockInput("1", "CS101", "-1");
        StudentController studentController = new StudentController(context,
                new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());
        studentController.manageTimetable();
    }
    @Test
    public void testChooseOneActivitySuccess() throws URISyntaxException, IOException, ParseException {
        setMockInput("4", "CS101", "4", "-1");
        StudentController studentController = new StudentController(context,
                new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());
        startOutputCapture();
        studentController.manageTimetable();
        assertOutputContains("The activity was successfully chosen");
    }

    @Test
    public void testChooseAllActivitySuccess() throws URISyntaxException, IOException, ParseException {
        setMockInput("4", "CS101", "2", "4", "3", "4", "4", "3", "-1");
        StudentController studentController = new StudentController(context,
                new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());
        startOutputCapture();
        studentController.manageTimetable();
        assertOutputContains("The activity was successfully chosen");
    }


    @Test
    public void testChooseActivityWithInvalidCourseCode() throws URISyntaxException, IOException, ParseException {
        // This test is not implemented yet.
        // You can implement it based on your requirements.
        setMockInput("4", "not-a-course", "-1");
        StudentController studentController = new StudentController(context,
                new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());
        startOutputCapture();
        studentController.manageTimetable();
        assertOutputContains("Incorrect course code.");
    }

    @Test
    public void testChooseActivityWithInvalidActivityCode() throws URISyntaxException, IOException, ParseException {
        setMockInput("4", "CS101", "not-a-code", "-1");
        StudentController studentController = new StudentController(context,
                new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());
        startOutputCapture();
        studentController.manageTimetable();
        assertOutputContains("Invalid option:");
    }
}
