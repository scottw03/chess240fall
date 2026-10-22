package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.*;
import chess.ChessGame;
import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTest {
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private GameDAO gameDAO;

    private ClearService clearService;

    @BeforeEach
    public void setup() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();

        clearService = new ClearService(userDAO, authDAO, gameDAO);
    }

    @Test
    @DisplayName("Clear Positive")
    public void clearPositive() throws Exception {

        userDAO.createUser(new UserData(
                "player1", "password", "email"));

        authDAO.createAuth(new AuthData(
                "token1", "player1"));

        ChessGame chessGame = new ChessGame();
        int gameID = gameDAO.createGame(
                new GameData(
                        0,
                        null,
                        null,
                        "Test Game",
                        chessGame));

        assertNotNull(userDAO.getUser("player1"));
        assertNotNull(authDAO.getAuth("token1"));
        assertNotNull(gameDAO.getGame(gameID));

        clearService.clear();

        assertNull(userDAO.getUser("player1"));
        assertNull(authDAO.getAuth("token1"));
        assertNull(gameDAO.getGame(gameID));

        int newGameID = gameDAO.createGame(
                new GameData(
                        0,
                        null,
                        null,
                        "New Game",
                        new ChessGame()));
        assertEquals(1, newGameID);
    }
}
