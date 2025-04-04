package system_tests;

import controller.AdminStaffController;
import controller.InquirerController;
import external.MockAuthenticationService;
import external.MockEmailService;
import view.TextUserInterface;
import model.SharedContext;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.net.URISyntaxException;

public class ConsultMemberOfStaff extends TUITest {

    // Invalid email address! Please try again

    // Inquiry subject cannot be blank! Please try again

    // Inquiry content cannot be blank! Please try again

    //Email from inquiries@hindeburg.ac.nz to inquiries@hindeburg.ac.nz
    //New inquiry from leila.d@df.com
    //Subject: gh
    //Please log into the Self Service Portal to review and respond to the inquiry.
    //Your inquiry has been recorded. Someone will be in touch via email soon!

    //login as admin staff
    // Inquirer: leila.d@df.com
    //Created at: 2025-04-04T14:34:34.534093
    //Assigned to: No one
    //Query:
    //snfjknsgjdjrbgjk

    // Inquiry has been reassigned when valid email

    // need to check for valid email

    //Email from admin1@hindeburg.ac.uk to leila.d@df.com
    //response
    //drkjnvjabjkrbajkv
    //Email response sent!
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

}
