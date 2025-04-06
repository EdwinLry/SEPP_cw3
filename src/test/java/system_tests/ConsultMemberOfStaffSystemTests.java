package system_tests;

import controller.AdminStaffController;
import controller.InquirerController;
import controller.StudentController;
import controller.TeachingStaffController;
import external.MockAuthenticationService;
import external.MockEmailService;
import org.junit.jupiter.api.BeforeEach;
import view.TextUserInterface;
import model.SharedContext;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.net.URISyntaxException;

public class ConsultMemberOfStaffSystemTests extends TUITest {

    @BeforeEach
    public void init() throws URISyntaxException, IOException, ParseException {
        // Initialize the context
        //loginAsStudent(context);
        //setMockInput("1", "CS101", "-1");
        //StudentController studentController = new StudentController(context,
        //        new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());
        //studentController.manageTimetable();
    }

    // TODO: a test where they write an email to someone on a course and then admin logs in and looks at that inquiry and then the actual user logs in and replies to email.
    @Test
    public void testValidInquiryNoTag() throws URISyntaxException, IOException, ParseException {
        //SharedContext context = new SharedContext();
        setMockInput("student1@hindeburg.ac.uk", "n", "Subject", "Inquiry.");

        InquirerController controller = new InquirerController(context,
                new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());

        startOutputCapture();
        controller.contactStaff();
        assertOutputContains("Your inquiry has been recorded. Someone will be in touch via email soon!");
    }

    @Test
    public void testValidCourseCode() throws URISyntaxException, IOException, ParseException {
        setMockInput(
                "student1@hindeburg.ac.uk", "y", "CS101", "Subject","Inquiry"
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
        // Guest tries to send inquiry with invalid course code
        setMockInput("inquirer@hindeburg.ac.uk", "y", "not-a-code");
        InquirerController inquirerController = new InquirerController(
                context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());
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
    public void testRespond() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();
        setMockInput("inquirer@hindeburg.ac.uk", "n", "Question", "Inquiry");
        InquirerController inquirer = new InquirerController(
                context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService()
        );
        inquirer.contactStaff();

        // Admin logs in and responds
        loginAsAdminStaff(context);
        setMockInput("0", "1", "Subject", "Response", "-1");
        AdminStaffController adminController = new AdminStaffController(
                context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService()
        );
        startOutputCapture();
        adminController.manageInquiries();
        assertOutputContains("Email response sent!");
    }

    @Test
    public void testRespondTeacher() throws URISyntaxException, IOException, ParseException {
        setMockInput("inquirer@hindeburg.ac.uk", "y", "CS101","Question", "Inquiry");
        InquirerController inquirer = new InquirerController(
                context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService()
        );
        inquirer.contactStaff();

        // Admin logs in and responds
        loginAsTeachingStaff(context);
        setMockInput("0", "0", "Subject", "Response", "-1", "-1");
        TeachingStaffController teachingController = new TeachingStaffController(
                context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService()
        );
        startOutputCapture();
        teachingController.manageReceivedInquiries();
        assertOutputContains("Email response sent!");
    }

    @Test
    public void testRedirect() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();
        setMockInput("inquirer@hindeburg.ac.uk", "n", "Question", "Inquiry");
        InquirerController inquirer = new InquirerController(
                context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService()
        );
        inquirer.contactStaff();

        // Admin logs in and responds
        loginAsAdminStaff(context);
        setMockInput("0", "0", "teacher1@hindeburg.ac.uk", "-1", "-1");
        AdminStaffController adminController = new AdminStaffController(
                context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService()
        );
        startOutputCapture();
        adminController.manageInquiries();
        assertOutputContains("Inquiry has been reassigned");
    }


    @Test
    public void testConsultWithOnlyTags() throws URISyntaxException, IOException, ParseException {
        setMockInput("inquirer@hindeburg.ac.uk", "y", "CS101", "Question", "Inquiry", "-1");
        InquirerController inquirer = new InquirerController(
                context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService()
        );
        startOutputCapture();
        inquirer.contactStaff();
        assertOutputContains("Email from inquiries@hindeburg.ac.uk to teacher1@hindeburg.ac.uk");
    }
}
