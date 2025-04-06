package system_tests;

import controller.AdminStaffController;
import external.MockAuthenticationService;
import external.MockEmailService;
import view.TextUserInterface;
import model.SharedContext;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.net.URISyntaxException;


public class AddCourseSystemTests extends TUITest{
    //TODO: implement this
    //@BeforeEach
    @Test
    public void testAddCourseNoActivities() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();
        loginAsAdminStaff(context);

        setMockInput("-2", "INF1A", "Informatics 1", "Intro to Informatics",
                "y", "Dr X", "drx@ed.ac.uk", "Ms Y", "msy@ed.ac.uk",
                "2", "3","0", "-1");

        AdminStaffController controller = new AdminStaffController(
                context,
                new TextUserInterface(),
                new MockAuthenticationService(),
                new MockEmailService()
        );

        startOutputCapture();
        controller.manageCourses();

        assertOutputContains("Course added successfully");
    }


    @Test
    public void testAddCourseWithThreeActivities() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();
        loginAsAdminStaff(context);

        setMockInput(
                // === Course Info ===
                "-2", "CS2025", "Software Engineering", "Covers software practices",
                "n", "Dr. Ada", "ada@cs.edu", "Ms. Turing", "turing@cs.edu",
                "1", "1", "3", // 3 activities

                // === Activity 1: Lecture ===
                "100", "Lecture", "2025-09-01", "09:00", "2025-12-01", "10:00",
                "Room L1", "MONDAY", "y",

                // === Activity 2: Tutorial ===
                "101", "Tutorial", "2025-09-03", "11:00", "2025-12-03", "12:00",
                "Room T1", "WEDNESDAY", "20",

                // === Activity 3: Lab ===
                "102", "Lab", "2025-09-05", "14:00", "2025-12-05", "16:00",
                "Lab 1", "FRIDAY", "15",

                // Exit back to main
                "-1"
        );

        AdminStaffController controller = new AdminStaffController(
                context,
                new TextUserInterface(),
                new MockAuthenticationService(),
                new MockEmailService()
        );

        startOutputCapture();
        controller.manageCourses();

        assertOutputContains("Course added successfully");
        assertOutputContains("Activity added successfully");
    }


    //Invalid input. Please enter the correct data types.
    @Test
    public void testAddInvalidDataTypes() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();
        loginAsAdminStaff(context);

        setMockInput(
                // === Course Information ===
                "-2",                          // Choose "Add course"
                "CS2025",                      // Course code
                "Software Engineering",        // Course name
                "Covers software practices",   // Course description
                "n",                           // Requires computers?
                "Dr. Ada", "ada@cs.edu",       // Organiser
                "Ms. Turing", "turing@cs.edu", // Secretary
                "2", "1",                      // Required tutorials/labs
                "1",                           // Number of activities

                // === Activity 1: Lecture ===
                "100",                         // Activity ID
                "Lecture",                     // Activity type
                "wrong-date-format",                  // Start date
                "2025-12-01",
                "09:00",                       // Start time
                "2025-12-01", "10:00",
                "Room L1",                     // Location
                "MONDAY",                      // Day of week
                "y",                           // Is lecture recorded?

                "-1"                           // Return to main menu
        );

        AdminStaffController controller = new AdminStaffController(
                context,
                new TextUserInterface(),
                new MockAuthenticationService(),
                new MockEmailService()
        );

        startOutputCapture();
        controller.manageCourses();

        assertOutputContains("Invalid date format. Please use YYYY-MM-DD.");
    }

    @Test
    public void testAddCourseDuplicateCourseCode() throws Exception {
        SharedContext context = new SharedContext();
        loginAsAdminStaff(context);

        // Add once
        setMockInput("-2", "INF1A", "Informatics 1", "Desc", "n",
                "A", "a@x.com", "B", "b@x.com", "1", "1", "0","-1");
        new AdminStaffController(context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService()).manageCourses();

        // Try to add again
        loginAsAdminStaff(context);
        setMockInput("-2", "INF1A", "-1");

        AdminStaffController controller = new AdminStaffController(context,
                new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());

        startOutputCapture();
        controller.manageCourses();

        assertOutputContains("Course with the same code already exists");
    }

    @Test
    public void testNotNumberTutorials() throws Exception {
        SharedContext context = new SharedContext();
        loginAsAdminStaff(context);

        setMockInput("-2", "INF1B", "Informatics 2", "Desc", "n",
                "Dr C", "c@x.com", "Ms D", "d@x.com", "not-a-number", // Not a number input
                "1", "-1");

        AdminStaffController controller = new AdminStaffController(context,
                new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());

        startOutputCapture();
        controller.manageCourses();

        assertOutputContains("Invalid input. Please enter a number.");
    }

    @Test
    public void testNegativeNumberTutorials() throws Exception {
        SharedContext context = new SharedContext();
        loginAsAdminStaff(context);

        setMockInput("-2", "INF1B", "Informatics 2", "Desc", "n",
                "Dr C", "c@x.com", "Ms D", "d@x.com", "-3", // Negative tutorials input
                "1", "-1");

        AdminStaffController controller = new AdminStaffController(context,
                new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());

        startOutputCapture();
        controller.manageCourses();

        assertOutputContains("Need 0 or more of each.");
    }

    @Test
    public void testInvalidEmail() throws Exception {
        SharedContext context = new SharedContext();
        loginAsAdminStaff(context);

        setMockInput("-2", "INF1B", "Informatics 2", "Desc", "n",
                "Dr C", "not-an-email", "-1");

        AdminStaffController controller = new AdminStaffController(context,
                new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());

        startOutputCapture();
        controller.manageCourses();

        assertOutputContains("Invalid course organiser email.");
    }
}
