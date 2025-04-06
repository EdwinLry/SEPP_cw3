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
                "Mr B", "mrb@ed.ac.uk", "1", "3", "0",
                "-2", "INF2B-2", "SEPP-2", "Software Engineering and Professional Practice", "n", "Mrs A", "mrsa@ed.ac.uk",
                "Mr B", "mrb@ed.ac.uk", "1", "3", "0", "-1", "0");
        AdminStaffController adminStaff = new AdminStaffController(context,
                new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());
        adminStaff.manageCourses();
        setMockInput("1", "3");
        ViewerController controller = new ViewerController(
                context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());
        startOutputCapture();
        controller.viewCoursesMenu();
        assertOutputContains("Course name: SEPP - Course code: INF2B");
        assertOutputContains("Course name: SEPP-2 - Course code: INF2B-2");

    }

    @Test
    public void testSuccessSpecific() throws URISyntaxException, IOException, ParseException {
        // Guest views a specific course by course name
        setMockInput("2", "CS101", "3");
        ViewerController controller = new ViewerController(
                context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());
        startOutputCapture();
        controller.viewCoursesMenu();

        // Full course details are displayed
        assertOutputContains("Course Code: CS101");
        assertOutputContains("Course Name: Introduction to Computer Science");
        assertOutputContains("Course Description: This course covers basic CS concepts.");
        assertOutputContains("Requires Computers: false");
        assertOutputContains("Course Organiser Name: Teacher 1");
        assertOutputContains("Course Organiser Email: teacher1@hindeburg.ac.uk");
        assertOutputContains("Course Secretary Name: Teacher 2");
        assertOutputContains("Course Secretary Email: teacher2@hindeburg.ac.uk");
        assertOutputContains("Required Tutorials: 2");
        assertOutputContains("Required Labs: 1");
        assertOutputContains("Course Activity Timetable:");
        assertOutputContains("Lecture{id=0, startDate=2023-09-01, startTime=09:00, endDate=2023-12-15, " +
                "endTime=10:00, location='Room 101', day=MONDAY, recorded=true}");
        assertOutputContains("Lecture{id=1, startDate=2023-09-01, startTime=11:00, endDate=2023-12-15, " +
                "endTime=12:00, location='Room 102', day=WEDNESDAY, recorded=false}");
        assertOutputContains("Tutorial{id=2, startDate=2023-09-01, startTime=14:00, endDate=2023-12-15, " +
                "endTime=15:00, location='Room 201', day=TUESDAY, capacity=30}");
        assertOutputContains("Tutorial{id=3, startDate=2023-09-01, startTime=15:00, endDate=2023-12-15, " +
                "endTime=16:00, location='Room 202', day=THURSDAY, capacity=25}");
        assertOutputContains("Lab{id=4, startDate=2023-09-01, startTime=10:00, endDate=2023-12-15, " +
                "endTime=12:00, location='Lab A', day=FRIDAY, capacity=20}");
        assertOutputContains("Lab{id=5, startDate=2023-09-01, startTime=13:00, endDate=2023-12-15, " +
                "endTime=15:00, location='Lab B', day=MONDAY, capacity=15}");
    }

    @Test
    public void testWrongCourseCode() throws URISyntaxException, IOException, ParseException {
        // Guest searches for invalid course code
        setMockInput("2", "not-a-code", "3");
        ViewerController controller = new ViewerController(
                context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());
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
