package system_tests;

import controller.AdminStaffController;
import controller.GuestController;
import external.MockAuthenticationService;
import external.MockEmailService;
import model.SharedContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import view.TextUserInterface;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AddFAQQASystemTests extends TUITest {

    @BeforeEach
    public void preStuff{
        //login
        //select add faq
        setMockInput("section","question","answer");

    }

    @Test
    @DisplayName("Admin adds FAQ Q&A to existing section successfully")
    public void addFAQToExistingSection_success() throws URISyntaxException, IOException {
        setMockInput("section","question","answer");
        SharedContext context = new SharedContext();
        AdminStaffController adminStaffController = new AdminStaffController(
            context,
            new TextUserInterface(),
            new MockAuthenticationService(),
            new MockEmailService()
        );

        startOutputCapture();
        adminStaffController.run();
        assertOutputContains("FAQ added to section 'Assessment'");
    }

    @Test
    @DisplayName("Admin adds FAQ to a new section successfully")
    public void addFAQToNewSection_success() throws URISyntaxException, IOException {
        setMockInput(new String[]{
            "admin1", "admin1pass",
            "3",
            "Exams",                                 // New section
            "What should I bring?",                  // Question
            "Student ID and calculator."             // Answer
        });

        SharedContext context = new SharedContext();
        AdminStaffController controller = new AdminStaffController(
            context,
            new TextUserInterface(),
            new MockAuthenticationService(),
            new MockEmailService()
        );

        startOutputCapture();
        controller.run();
        assertOutputContains("New section 'Exams' created");
        assertOutputContains("FAQ added to section 'Exams'");
    }

    @Test
    @DisplayName("Admin tries to add FAQ with empty question")
    public void addFAQWithEmptyQuestion_shouldFail() throws URISyntaxException, IOException {
        setMockInput(new String[]{
            "admin1", "admin1pass",
            "3",
            "Assessment",
            "",                                       // Empty question
            "It is due Friday."                      // Answer
        });

        SharedContext context = new SharedContext();
        AdminStaffController controller = new AdminStaffController(
            context,
            new TextUserInterface(),
            new MockAuthenticationService(),
            new MockEmailService()
        );

        startOutputCapture();
        controller.run();
        assertOutputContains("Question cannot be empty");
    }

    @Test
    @DisplayName("Admin tries to add FAQ with empty answer")
    public void addFAQWithEmptyAnswer_shouldFail() throws URISyntaxException, IOException {
        setMockInput(new String[]{
            "admin1", "admin1pass",
            "3",
            "Assessment",
            "When is the exam?",                      // Question
            ""                                        // Empty answer
        });

        SharedContext context = new SharedContext();
        AdminStaffController controller = new AdminStaffController(
            context,
            new TextUserInterface(),
            new MockAuthenticationService(),
            new MockEmailService()
        );

        startOutputCapture();
        controller.run();
        assertOutputContains("Answer cannot be empty");
    }

    @Test
    @DisplayName("Admin adds course-tagged FAQ item, student consults it by course code")
    public void addCourseTaggedFAQ_andConsultByTag() throws Exception {
        setMockInput(new String[]{
            "admin1", "admin1pass",
            "3", "Labs", "Where is Lab 2?", "Room B.21", "CS101",
            "logout",
            "student1", "student1pass",
            "6", "CS101"
        });

        SharedContext context = new SharedContext();
        GuestController guestController = new GuestController(
            context,
            new TextUserInterface(),
            new MockAuthenticationService(),
            new MockEmailService()
        );

        startOutputCapture();
        guestController.run();
        assertOutputContains("Where is Lab 2?");
        assertOutputContains("Room B.21");
    }

    @Test
    @DisplayName("Admin adds, removes, and re-adds an FAQ item in same section")
    public void addRemoveReaddFAQItem_sameSection() throws Exception {
        setMockInput(new String[]{
            "admin1", "admin1pass",
            "3", "Assessment", "When is the test?", "Week 6",         // Add
            "4", "Assessment", "0",                                   // Remove item ID 0
            "3", "Assessment", "Is the test open book?", "Yes"        // Re-add
        });

        SharedContext context = new SharedContext();
        AdminStaffController controller = new AdminStaffController(
            context,
            new TextUserInterface(),
            new MockAuthenticationService(),
            new MockEmailService()
        );

        startOutputCapture();
        controller.run();
        assertOutputContains("FAQ item with ID 0 removed");
        assertOutputContains("FAQ added to section 'Assessment'");
    }

    @Test
    @DisplayName("Admin tries to add duplicate question in the same section")
    public void addDuplicateFAQQuestion_sameSection() throws Exception {
        setMockInput(new String[]{
            "admin1", "admin1pass",
            "3", "Support", "When is office hour?", "Mondays 2pm",
            "3", "Support", "When is office hour?", "Tuesdays 3pm"
        });

        SharedContext context = new SharedContext();
        AdminStaffController controller = new AdminStaffController(
            context,
            new TextUserInterface(),
            new MockAuthenticationService(),
            new MockEmailService()
        );

        startOutputCapture();
        controller.run();
        assertOutputContains("Warning: duplicate question in section 'Support'");
    }
}
