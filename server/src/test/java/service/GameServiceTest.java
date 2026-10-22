package service;

import dataaccess.*;
import model.*;
import service.requests.*;
import service.results.*;
import org.junit.jupiter.api.*;
import java.util.Collection;
import static org.junit.jupiter.api.Assertions.*;

//used to test the "createGame", "listGame", and "joinGame" functionality

public class GameServiceTest {
    private GameService gameService;
    private GameDAO gameDAO;
    private AuthDAO authDAO;
    private static final String VALID_TOKEN = "validToken";

    @BeforeEach
    public void setup() throws Exception {
        gameDAO = new MemoryGameDAO();
        authDAO = new MemoryAuthDAO();
        gameService = new GameService(gameDAO, authDAO);
        authDAO.createAuth(new AuthData(VALID_TOKEN, "player1"));
    }

    //CreateGame

    //CreateGame Success
    @Test
    @DisplayName("Create Game Positive")
    public void createGamePositive() throws Exception {
        CreateGameRequest request = new CreateGameRequest(
                "Test Game", VALID_TOKEN);
        CreateGameResult result = gameService.createGame(request);
        assertNotNull(result);
        assertTrue(result.gameID() > 0);
        GameData game = gameDAO.getGame(result.gameID());
        assertNotNull(game);
        assertEquals("Test Game", game.gameName());
    }

    //CreateGame Failure by bad auth token
    @Test
    @DisplayName("Create Game Negative Unauthorized")
    public void createGameNegativeUnauthorized() {
        CreateGameRequest request = new CreateGameRequest(
                "TestGame", "badToken");
        Exception ex = assertThrows(Exception.class, () ->
                gameService.createGame(request));
        assertEquals("unauthorized", ex.getMessage());
    }

    //ListGames

    //ListGames Success
    @Test
    @DisplayName("List Games Positive")
    public void listGamesPositive() throws Exception {
        gameService.createGame(new CreateGameRequest(
                "Game1", VALID_TOKEN));
        gameService.createGame(new CreateGameRequest(
                "Game 2", VALID_TOKEN));
        ListGamesResult result = gameService.listGames(
                new ListGamesRequest(VALID_TOKEN));
        Collection<GameData> games = result.games();
        assertNotNull(games);
        assertEquals(2, games.size());
    }

    //ListGames Failure due to bad auth token
    @Test
    @DisplayName("List Games Negative Unauthorized")
    public void listGamesNegativeUnauthorized() {
        Exception ex = assertThrows(Exception.class, () ->
                gameService.listGames(
                        new ListGamesRequest("badToken")));
        assertEquals("unauthorized", ex.getMessage());
    }

    //JoinGame


}
