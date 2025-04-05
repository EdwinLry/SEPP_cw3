package system_tests;

import controller.StudentController;
import external.MockAuthenticationService;
import external.MockEmailService;
import model.SharedContext;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;
import view.TextUserInterface;

import java.io.IOException;
import java.net.URISyntaxException;

public class ViewTimetableSystemTests extends TUITest {

    private SharedContext context;

    @Test
    public void testEmptyTimetable() throws URISyntaxException, IOException, ParseException {
        context = new SharedContext();
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
