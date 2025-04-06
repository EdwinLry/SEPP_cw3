package system_tests;

import controller.StudentController;
import controller.AdminStaffController;
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

    @Test
    public void testViewTimetable() throws URISyntaxException, IOException, ParseException {
        // Add activity 4 to timetable then view
        setMockInput("4", "CS101", "1", "3", "-1");
        startOutputCapture();
        StudentController controller = new StudentController(context, new TextUserInterface(),
                new MockAuthenticationService(), new MockEmailService());
        controller.manageTimetable();
        assertOutputContains("Timetable for student1@hindeburg.ac.uk");
        assertOutputContains("Course Code: CS101");
        assertOutputContains("Activity ID: 4");
    }

    @Test
    public void testViewTimetableWithLecture() throws URISyntaxException, IOException, ParseException {
        // View timetable
        setMockInput("3", "-1");
        startOutputCapture();
        StudentController controller = new StudentController(context, new TextUserInterface(),
                new MockAuthenticationService(), new MockEmailService());
        controller.manageTimetable();
        assertOutputContains("Course Code: CS101");
        assertOutputContains("Activity ID: 0");
    }



}
