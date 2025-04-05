package system_tests;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

import model.Course;
import model.CourseManager;
import model.FAQ.FAQSection;
import model.FAQManager;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;

import controller.GuestController;
import external.MockAuthenticationService;
import external.MockEmailService;
import model.SharedContext;
import view.TextUserInterface;

public class TUITest {
    private PrintStream backupOut;
    private InputStream backupIn;

    protected SharedContext sharedContext;

    @BeforeEach
    public void backupSystemStreams() {
        backupOut = System.out;
        backupIn = System.in;
    }

    @BeforeEach
    public void setUp() {
        sharedContext = new SharedContext();
        // Set up the context with necessary data
        initCourse();
        initFAQ();
    }

    private void initFAQ() {
        // Initialize FAQ with some data
        FAQManager faqManager = sharedContext.faqManager;
        FAQSection sectionWithTag = new FAQSection("TopicWithTag");
        sectionWithTag.addItem("What is CS101?", "CS101 is an introductory course.", "CS101");
        sectionWithTag.addItem("How many credits is CS101?", "CS101 is a 3-credit course.", "CS101");

        FAQSection sectionWithoutTag = new FAQSection("TopicWithoutTag");
        sectionWithoutTag.addItem("What is a FAQ?", "FAQ stands for Frequently Asked Questions.");
        sectionWithoutTag.addItem("How to use a FAQ?", "A FAQ helps answer common questions.");

        faqManager.addSection(sectionWithTag);
        faqManager.addSection(sectionWithoutTag);
    }
    private void initCourse() {
        CourseManager courseManager = sharedContext.getCourseManager();
        courseManager.addCourse(
                "CS101",
                "Introduction to Computer Science",
                "This course covers basic CS concepts.",
                false,
                "Dr. John Doe",
                "john.doe@example.com",
                "Prof. Jane Smith",
                "jane.smith@example.com",
                2,
                1
        );
        Course course = courseManager.getCourse("CS101");

        LocalDate semesterStart = LocalDate.of(2023, 9, 1);
        LocalDate semesterEnd = LocalDate.of(2023, 12, 15);

        course.addActivity(
                semesterStart,
                LocalTime.of(9, 0),
                semesterEnd,
                LocalTime.of(10, 0),
                "Room 101",
                DayOfWeek.MONDAY,
                "Lecture",
                true
        );

        course.addActivity(
                semesterStart,
                LocalTime.of(11, 0),
                semesterEnd,
                LocalTime.of(12, 0),
                "Room 102",
                DayOfWeek.WEDNESDAY,
                "Lecture",
                false
        );

        course.addActivity(
                semesterStart,
                LocalTime.of(14, 0),
                semesterEnd,
                LocalTime.of(15, 0),
                "Room 201",
                DayOfWeek.TUESDAY,
                "Tutorial",
                30
        );

        course.addActivity(
                semesterStart,
                LocalTime.of(15, 0),
                semesterEnd,
                LocalTime.of(16, 0),
                "Room 202",
                DayOfWeek.THURSDAY,
                "Tutorial",
                25
        );

        course.addActivity(
                semesterStart,
                LocalTime.of(10, 0),
                semesterEnd,
                LocalTime.of(12, 0),
                "Lab A",
                DayOfWeek.FRIDAY,
                "Lab",
                20
        );

        course.addActivity(
                semesterStart,
                LocalTime.of(13, 0),
                semesterEnd,
                LocalTime.of(15, 0),
                "Lab B",
                DayOfWeek.MONDAY,
                "Lab",
                15
        );
    }

    @AfterEach
    public void restoreSystemStreams() {
        System.setOut(backupOut);
        System.setIn(backupIn);
    }

    private ByteArrayOutputStream out;

    protected void setMockInput(String... inputLines) {
        StringBuilder sb = new StringBuilder();
        for (String line : inputLines) {
            sb.append(line);
            sb.append(System.lineSeparator());
        }
        ByteArrayInputStream in = new ByteArrayInputStream(sb.toString().getBytes());
        System.setIn(in);
    }

    protected void startOutputCapture() {
        // NOTE: be careful, if the captured output exceeds 8192 bytes, the remainder will be lost!
        out = new ByteArrayOutputStream(8192);
        System.setOut(new PrintStream(out));
    }

    protected void assertOutputContains(String expected) {
        String output = out.toString();
        assertTrue(output.contains(expected), "Output does not contain expected '" + expected + "':" + System.lineSeparator() + output);
    }

    protected void loginAsAdminStaff(SharedContext context) throws URISyntaxException, IOException, ParseException {
        setMockInput("admin1", "admin1pass");
        GuestController guestController = new GuestController(context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());
        guestController.login();
    }
 
    protected void loginAsTeachingStaff(SharedContext context) throws URISyntaxException, IOException, ParseException {
        setMockInput("teacher1", "teacher1pass");
        GuestController guestController = new GuestController(context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());
        guestController.login();
    }
 
    protected void loginAsStudent(SharedContext context) throws URISyntaxException, IOException, ParseException {
        setMockInput("student1", "student1pass");
        GuestController guestController = new GuestController(context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());
        guestController.login();
    }

}
