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
    public void testChooseActivity() throws URISyntaxException, IOException, ParseException {
        setMockInput("4", "CS101", "4", "-1");
        StudentController studentController = new StudentController(context,
                new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());
        startOutputCapture();
        studentController.manageTimetable();
        assertOutputContains("The activity was successfully chosen");
    }



    @Test
    public void testChooseActivityWithInvalidInput() {
        // This test is not implemented yet.
        // You can implement it based on your requirements.
    }

    @Test
    public void testChooseActivityWithValidInput() {
        // This test is not implemented yet.
        // You can implement it based on your requirements.
    }
}
