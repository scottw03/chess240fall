package service;

import dataaccess.*;
import service.requests.*;
import service.results.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.mindrot.jbcrypt.BCrypt;

//used to test the "register", "login", and "logout" functionality

public class UserServiceTest {

    private UserService userService;
    private UserDAO userDAO;
    private AuthDAO authDAO;

    @BeforeEach
    public void setup() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        userService = new UserService(userDAO, authDAO);
    }

    //Register

    //Register Success
    @Test
    @DisplayName("Register Positive")
    public void registerPositive() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "player1", "password", "player@email.com");
        RegisterResult result = userService.register(request);
        assertNotNull(result);
        assertEquals("player1", result.username());
        assertNotNull(result.authToken());
        assertNotNull(userDAO.getUser("player1"));
        assertNotNull(authDAO.getAuth(result.authToken()));
        var storedUser = userDAO.getUser("player1");
        assertNotEquals("password", storedUser.password());
        assertTrue(BCrypt.checkpw("password", storedUser.password()));
    }

    //Register Failure (Duplicate Registration)
    @Test
    @DisplayName("Register Negative Duplicate User")
    public void registerNegativeDuplicate() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "player1", "password", "player1@email.com");
        userService.register(request);
        Exception ex = assertThrows(Exception.class, () ->
                userService.register(request));
        assertEquals("already taken", ex.getMessage());
    }

    //Login

    //Login Success
    @Test
    @DisplayName("Login Positive")
    public void loginPositive() throws Exception {
        RegisterRequest registerRequest =
                new RegisterRequest("player1", "password", "player1@email.com");
        userService.register(registerRequest);
        LoginRequest loginRequest =
                new LoginRequest("player1", "password");
        LoginResult result = userService.login(loginRequest);
        assertNotNull(result);
        assertEquals("player1", result.username());
        assertNotNull(result.authToken());
        assertNotNull(authDAO.getAuth(result.authToken()));
    }

    //Login Failure (Wrong Password)
    @Test
    @DisplayName("Login Negative Wrong Password")
    public void loginNegativeWrongPassword() throws Exception {
        RegisterRequest registerRequest =
                new RegisterRequest("player1", "password", "player1@email.com");
        userService.register(registerRequest);
        LoginRequest badLogin = new LoginRequest(
                "player1", "wrongpassword");
        Exception ex = assertThrows(Exception.class, () ->
                userService.login(badLogin));
        assertEquals("unauthorized", ex.getMessage());
    }

}
