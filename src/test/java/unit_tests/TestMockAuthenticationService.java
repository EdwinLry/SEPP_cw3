package unit_tests;

import external.MockAuthenticationService;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestMockAuthenticationService {

    private static JSONObject result = new JSONObject();
    private static HashMap<String, String> errorObj = new HashMap<>();

    private TestMockAuthenticationService(){
        result.put("password", "admin1pass");
        result.put("role", "AdminStaff");
        result.put("email", "admin1@hindeburg.ac.uk");
        result.put("username", "admin1");
        // result.toJSONString()

        errorObj.put("error", "Wrong username or password");
        // new JSONObject(errorObj).toJSONString()
    }

    @Test
    @DisplayName("Test Valid Login")
    void validLogin() throws URISyntaxException, IOException, ParseException {
        MockAuthenticationService authenticationService = new MockAuthenticationService();

        assertEquals(result.toJSONString(),
                authenticationService.login("admin1", "admin1pass"),
                "Success when both details are valid.");
    }

    @Test
    @DisplayName("Test Invalid Username")
    void invalidUser() throws URISyntaxException, IOException, ParseException {
        MockAuthenticationService authenticationService = new MockAuthenticationService();

        assertEquals(new JSONObject(errorObj).toJSONString(),
                authenticationService.login("admn1", "admin1pass"),
                "Should return error when username is invalid.");
    }

    @Test
    @DisplayName("Test Invalid Password")
    void invalidPass() throws URISyntaxException, IOException, ParseException {
        MockAuthenticationService authenticationService = new MockAuthenticationService();

        assertEquals(new JSONObject(errorObj).toJSONString(),
                authenticationService.login("admin1", "admn1pass"),
                "Should return error when password is invalid.");
    }

    @Test
    @DisplayName("Test Username Capitalisation")
    void capitalisedUser() throws URISyntaxException, IOException, ParseException {
        MockAuthenticationService authenticationService = new MockAuthenticationService();

        assertEquals(new JSONObject(errorObj).toJSONString(),
                authenticationService.login("ADMIN1", "admin1pass"),
                "Should return error when username is capitalised.");
    }

    @Test
    @DisplayName("Test Password Capitalisation")
    void capitalisedPass() throws URISyntaxException, IOException, ParseException {
        MockAuthenticationService authenticationService = new MockAuthenticationService();

        assertEquals(new JSONObject(errorObj).toJSONString(),
                authenticationService.login("admin1", "ADMIN1PASS"),
                "Should return error when password is capitalised.");
    }

    @Test
    @DisplayName("Test Null User")
    void nullUser() throws URISyntaxException, IOException, ParseException {
        MockAuthenticationService authenticationService = new MockAuthenticationService();

        assertEquals(new JSONObject(errorObj).toJSONString(),
                authenticationService.login(null, "ADMIN1PASS"),
                "Should return error when user is null.");
    }

    @Test
    @DisplayName("Test Null Password")
    void nullPass() throws URISyntaxException, IOException, ParseException {
        MockAuthenticationService authenticationService = new MockAuthenticationService();

        assertEquals(new JSONObject(errorObj).toJSONString(),
                authenticationService.login("admin1", null),
                "Should return error when password is null.");
    }
}