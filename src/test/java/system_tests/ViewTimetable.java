package system_tests;

import controller.AdminStaffController;
import controller.ViewerController;
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

public class ViewTimetable extends TUITest {

    @BeforeEach
    public void setUp() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();

        // Admin logs in and adds the course
        loginAsAdminStaff(context);
        setMockInput("-2", "INF2B", "SEPP", "Software Engineering and Professional Practice", "n", "Mrs A", "mrsa@ed.ac.uk",
                "Mr B", "mrb@ed.ac.uk", "1", "3", "-1", "0");

        AdminStaffController adminStaff = new AdminStaffController(context,
                new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());
        adminStaff.manageCourses();
    }

    @Test
    public void emptyTimetable() throws URISyntaxException, IOException, ParseException {

        //loginAsStudent(context);
    }


}
