package system_tests;

import model.SharedContext;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import view.TextUserInterface;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class AddFAQQASystemTests extends TUITest {

    @BeforeEach
    //login as an admin staff
    // manage FAQ

    @Test
    public void testAddFAQQAWithoutTag() throws URISyntaxException, IOException, ParseException {
        // add faq q with no tag
    }

    public void testAddFAQQAWithTag() throws URISyntaxException, IOException, ParseException {
        // add course
        // add faq q with tag
    }

    public void testRepeatTopic() throws URISyntaxException, IOException, ParseException {
        // Topic '' already exists!
    }

    public void testNoQuestion() throws URISyntaxException, IOException, ParseException {
        // "Question cannot be empty"
    }

    public void testNoAnswer() throws URISyntaxException, IOException, ParseException {
        // "Answer cannot be empty"
    }
}
