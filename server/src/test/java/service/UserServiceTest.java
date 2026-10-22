package service;

import dataaccess.*;
import service.requests.*;
import service.results.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

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


}
