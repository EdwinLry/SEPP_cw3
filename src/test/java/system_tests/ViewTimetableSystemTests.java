package system_tests;

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

public class ViewTimetableSystemTests extends TUITest {

    //TODO: check view
    @BeforeEach
    public void init() throws URISyntaxException, IOException, ParseException {
        // Add course to timetable
        loginAsStudent(context);
        setMockInput("1", "CS101", "-1");
        StudentController studentController = new StudentController(context,
                new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());
        studentController.manageTimetable();
    }
    @Test
    public void testChooseOneActivitySuccess() throws URISyntaxException, IOException, ParseException {
        loginAsStudent(context);
        setMockInput("3", "-1");
        StudentController controller = new StudentController(
                context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService()
        );
        startOutputCapture();
        controller.manageTimetable();
        assertOutputContains("Timetable for");
    }
    @Test
    public void testEmptyTimetable() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();
        loginAsStudent(context);
        setMockInput("3", "-1", "-1");
        StudentController controller = new StudentController(
                context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService()
        );
        startOutputCapture();
        controller.manageTimetable();
        assertOutputContains("No timetable found.");
    }



}
