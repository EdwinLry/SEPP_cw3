package system_tests;

import controller.AdminStaffController;
import controller.InquirerController;
import controller.TeachingStaffController;
import external.MockAuthenticationService;
import external.MockEmailService;
import view.TextUserInterface;
import model.SharedContext;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.net.URISyntaxException;

public class ConsultMemberOfStaffSystemTests extends TUITest {

    // TODO: a test where they write an email to someone on a course and then admin logs in and looks at that inquiry and then the actual user logs in and replies to email.
    @Test
    public void testValidInquiryNoTag() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();
        setMockInput("student1@hindeburg.ac.uk", "n", "Subject", "Inquiry.");

        InquirerController controller = new InquirerController(context,
                new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());

        startOutputCapture();
        controller.contactStaff();
        assertOutputContains("Your inquiry has been recorded. Someone will be in touch via email soon!");
    }

    @Test
    public void testValidCourseCode() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();

        // Admin adds course
        loginAsAdminStaff(context);
        setMockInput("-2", "INF2B", "SEPP", "Software Engineering and Professional Practice", "n",
                "Mrs A", "mrsa@ed.ac.uk", "Mr B", "mrb@ed.ac.uk", "1", "3", "-1", "0");

        AdminStaffController adminController = new AdminStaffController(
                context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService()
        );
        adminController.manageCourses();

        // Guest sends inquiry
        setMockInput(
                "student1@hindeburg.ac.uk", "y", "INF2B", "Subject","Inquiry"
        );

        InquirerController inquirerController = new InquirerController(
                context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService()
        );

        startOutputCapture();
        inquirerController.contactStaff();

        assertOutputContains("Your inquiry has been recorded. Someone will be in touch via email soon!");
    }

    @Test
    public void testInvalidCourseCode() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();

        // Admin adds course
        loginAsAdminStaff(context);
        setMockInput("-2", "INF2B", "SEPP", "Software Engineering and Professional Practice", "n",
                "Mrs A", "mrsa@ed.ac.uk", "Mr B", "mrb@ed.ac.uk", "1", "3", "-1", "0");

        AdminStaffController adminController = new AdminStaffController(
                context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService()
        );

        adminController.manageCourses();

        // Guest tries to send inquiry with invalid course code
        setMockInput(
                "y", "not-a-code"
        );

        InquirerController inquirerController = new InquirerController(
                context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService()
        );

        startOutputCapture();
        inquirerController.contactStaff();

        assertOutputContains("Invalid course code. No course found with code 'not-a-code'");
    }

    @Test
    public void testNoCourses() throws URISyntaxException, IOException, ParseException {

        SharedContext context = new SharedContext();
        setMockInput("student1@hindeburg.ac.uk", "y");

        InquirerController controller = new InquirerController(context,
                new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());

        startOutputCapture();
        controller.contactStaff();
        assertOutputContains("No courses currently in the system.");
    }

    @Test
    public void testInvalidEmail() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();
        setMockInput("2", "not-an-email");

        InquirerController controller = new InquirerController(context,
                new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());

        startOutputCapture();
        controller.contactStaff();
        assertOutputContains("Invalid email address!");
    }

    @Test
    public void testBlankSubject() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();
        setMockInput("student1@hindeburg.ac.uk", "n", "", "Valid inquiry content.");

        InquirerController controller = new InquirerController(context,
                new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());

        startOutputCapture();
        controller.contactStaff();
        assertOutputContains("Inquiry subject cannot be blank!");
    }

    @Test
    public void testBlankInquiryBody() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();
        setMockInput("student1@hindeburg.ac.uk", "n", "Subject Line", "");

        InquirerController controller = new InquirerController(context,
                new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());

        startOutputCapture();
        controller.contactStaff();
        assertOutputContains("Inquiry content cannot be blank!");
    }


    @Test
    //TODO: finish this section
    public void testInquiryFlow() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();

        // --- Admin adds a course with valid teaching staff ---
        loginAsAdminStaff(context);
        setMockInput("-2", "123", "123", "123", "n",
                "teacher3", "teacher3@hindeburg.ac.uk",
                "teacher2", "teacher2@hindeburg.ac.uk",
                "2", "3", "-1", "0");
        AdminStaffController adminController = new AdminStaffController(
                context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService()
        );
        adminController.manageCourses();

        // --- Student submits inquiry to that course ---
        loginAsStudent(context);
        setMockInput("y", "123", "Question", "Inquiry");
        InquirerController inquirer = new InquirerController(
                context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService()
        );
        inquirer.contactStaff();

        // --- Admin reassigns the inquiry to teacher1 ---
        loginAsAdminStaff(context);
        setMockInput(
                "0",                      // Select the first pending inquiry
                "0",                      // Choose to redirect it
                "teacher1@hindeburg.ac.uk", // Email of teaching staff
                "-1",                     // Back to inquiries list
                "-1",                     // Back to main menu
                "0"                       // Logout
        );
        startOutputCapture();  // Start capturing just before we do output-generating things
        adminController.manageInquiries();

        // --- Teaching staff logs in and responds to inquiry ---
        loginAsTeachingStaff(context);  // assumes teacher1/teacher1pass are valid
        setMockInput(
                "0",              // Select first assigned inquiry
                "0",              // Choose to respond
                "Subject",        // Response subject
                "Answer",         // Response body
                "-1" , "-1"             // Back to assigned inquiries
        );
        TeachingStaffController staffController = new TeachingStaffController(
                context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService()
        );
        staffController.manageReceivedInquiries();

        // --- Verify both success messages appear ---
        assertOutputContains("Inquiry has been reassigned");
        assertOutputContains("Email response sent!");
    }

    @Test
    public void consultWithOnlyTags () throws URISyntaxException, IOException, ParseException {
        // admin staff add a course with course organiser:

        //     "username": "teacher1",
        //    "password": "teacher1pass",
        //    "email": "teacher1@hindeburg.ac.uk",
        //    "role": "TeachingStaff"

        // student then makes an inquiry to the course
        // admin staff then logs in and looks at inquries
        // teacher then logs in and responds to inquriry
    }

}
