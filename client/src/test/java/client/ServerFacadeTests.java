package client;

import org.junit.jupiter.api.*;
import server.Server;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        facade = new ServerFacade(port);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void clearDatabase() throws Exception { facade.clear(); }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test
    public void registerPositive() throws Exception {
        var auth =
                facade.register(
                        "player1",
                        "password:",
                        "player1@email.com");
        Assertions.assertNotNull(auth);
        Assertions.assertNotNull(auth.authToken());
    }

    @Test
    public void registerNegative() throws Exception {
        facade.register(
                "player1",
                "password",
                "email");
        Assertions.assertThrows(
                Exception.class,
                () -> facade.register(
                        "player1",
                        "password",
                        "email"));
    }

    @Test
    public void loginPositive() throws Exception {
        facade.register(
                "player1",
                "password",
                "player1@email.com");

        var auth = facade.login("player1", "password");

        Assertions.assertNotNull(auth);
        Assertions.assertEquals("player1", auth.username());
        Assertions.assertNotNull(auth.authToken());
        Assertions.assertFalse(auth.authToken().isBlank());

        Assertions.assertDoesNotThrow(
                () -> facade.listGames(auth.authToken()));
    }

    @Test
    public void loginNegative() throws Exception {
        facade.register(
                "player1",
                "password",
                "player1@email.com");

        Assertions.assertThrows(
                Exception.class,
                () -> facade.login("player1", "wrongPassword"));
    }

    @Test
    public void logoutPositive() throws Exception {
        var auth = facade.register(
                "player1",
                "password",
                "player1@email.com");

        Assertions.assertDoesNotThrow(
                () -> facade.logout(auth.authToken()));

        Assertions.assertThrows(
                Exception.class,
                () -> facade.listGames(auth.authToken()));

        var newAuth = facade.login("player1", "password");
        Assertions.assertEquals("player1", newAuth.username());
    }

    @Test
    public void logoutNegative() {
        Assertions.assertThrows(
                Exception.class,
                () -> facade.logout("invalidToken"));
    }

    @Test
    public void createGamePositive() throws Exception {
        var auth = facade.register(
                "player1",
                "password",
                "player1@email.com");

        var result = facade.createGame(
                "Practice",
                auth.authToken());

        Assertions.assertNotNull(result);

        var games = facade.listGames(auth.authToken()).games();

        Assertions.assertEquals(1, games.size());

        var game = games.iterator().next();

        Assertions.assertEquals(result.gameID(), game.gameID());
        Assertions.assertEquals("Practice", game.gameName());

        Assertions.assertNull(game.whiteUsername());
        Assertions.assertNull(game.blackUsername());
    }

    @Test
    public void createGameNegative() throws Exception {
        var auth = facade.register(
                "player1",
                "password",
                "player1@email.com");

        Assertions.assertThrows(
                Exception.class,
                () -> facade.createGame("Practice", "invalidToken"));

        var games = facade.listGames(auth.authToken()).games();
        Assertions.assertTrue(games.isEmpty());
    }

    @Test
    public void listGamesPositive() throws Exception {
        var auth = facade.register(
                "player1",
                "password",
                "player1@email.com");

        var first = facade.createGame(
                "First Game",
                auth.authToken());

        var second = facade.createGame(
                "Second Game",
                auth.authToken());

        facade.joinGame(
                "WHITE",
                first.gameID(),
                auth.authToken());

        var result = facade.listGames(auth.authToken());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.games().size());

        var firstGame = result.games().stream()
                .filter(game -> game.gameID() == first.gameID())
                .findFirst()
                .orElseThrow();

        var secondGame = result.games().stream()
                .filter(game -> game.gameID() == second.gameID())
                .findFirst()
                .orElseThrow();

        Assertions.assertEquals("First Game", firstGame.gameName());
        Assertions.assertEquals("player1", firstGame.whiteUsername());
        Assertions.assertNull(firstGame.blackUsername());

        Assertions.assertEquals("Second Game", secondGame.gameName());
        Assertions.assertNull(secondGame.whiteUsername());
        Assertions.assertNull(secondGame.blackUsername());
    }

    @Test
    public void listGamesEmptyPositive() throws Exception {
        var auth = facade.register(
                "player1",
                "password",
                "player1@email.com");

        var result = facade.listGames(auth.authToken());

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.games());
        Assertions.assertTrue(result.games().isEmpty());
    }

    @Test
    public void listGamesNegative() {
        Assertions.assertThrows(
                Exception.class,
                () -> facade.listGames("invalidToken"));
    }

    @Test
    public void joinGameWhitePositive() throws Exception {
        var auth = facade.register(
                "player1",
                "password",
                "player1@email.com");

        var result = facade.createGame(
                "Practice",
                auth.authToken());

        facade.joinGame(
                "WHITE",
                result.gameID(),
                auth.authToken());

        var game = facade.listGames(auth.authToken())
                .games().iterator().next();

        Assertions.assertEquals("player1", game.whiteUsername());
        Assertions.assertNull(game.blackUsername());
    }

    @Test
    public void joinGameBlackPositive() throws Exception {
        var auth = facade.register(
                "player1",
                "password",
                "player1@email.com");

        var result = facade.createGame(
                "Practice",
                auth.authToken());

        facade.joinGame(
                "BLACK",
                result.gameID(),
                auth.authToken());

        var game = facade.listGames(auth.authToken())
                .games().iterator().next();

        Assertions.assertNull(game.whiteUsername());
        Assertions.assertEquals("player1", game.blackUsername());
    }

    @Test
    public void joinGameOccupiedNegative() throws Exception {
        var player1 = facade.register(
                "player1",
                "password",
                "player1@email.com");

        var player2 = facade.register(
                "player2",
                "password",
                "player2@email.com");

        var result = facade.createGame(
                "Practice",
                player1.authToken());

        facade.joinGame(
                "WHITE",
                result.gameID(),
                player1.authToken());

        Assertions.assertThrows(
                Exception.class,
                () -> facade.joinGame(
                        "WHITE",
                        result.gameID(),
                        player2.authToken()));

        var game = facade.listGames(player1.authToken())
                .games().iterator().next();

        Assertions.assertEquals("player1", game.whiteUsername());
        Assertions.assertNull(game.blackUsername());
    }

    @Test
    public void joinGameInvalidTokenNegative() throws Exception {
        var auth = facade.register(
                "player1",
                "password",
                "player1@email.com");

        var result = facade.createGame(
                "Practice",
                auth.authToken());

        Assertions.assertThrows(
                Exception.class,
                () -> facade.joinGame(
                        "WHITE",
                        result.gameID(),
                        "invalidToken"));
    }

    @Test
    public void joinGameInvalidColorNegative() throws Exception {
        var auth = facade.register(
                "player1",
                "password",
                "player1@email.com");

        var result = facade.createGame(
                "Practice",
                auth.authToken());

        Assertions.assertThrows(
                Exception.class,
                () -> facade.joinGame(
                        "PURPLE",
                        result.gameID(),
                        auth.authToken()));
    }

    @Test
    public void joinGameMissingGameNegative() throws Exception {
        var auth = facade.register(
                "player1",
                "password",
                "player1@email.com");

        Assertions.assertThrows(
                Exception.class,
                () -> facade.joinGame(
                        "WHITE",
                        12345,
                        auth.authToken()));
    }

    @Test
    public void clearPositive() throws Exception {
        var auth = facade.register(
                "player1",
                "password",
                "player1@email.com");

        facade.createGame("Practice", auth.authToken());

        Assertions.assertDoesNotThrow(() -> facade.clear());

        Assertions.assertThrows(
                Exception.class,
                () -> facade.listGames(auth.authToken()));

        Assertions.assertThrows(
                Exception.class,
                () -> facade.login("player1", "password"));

        var newAuth = facade.register(
                "player1",
                "newPassword",
                "player1@email.com");

        Assertions.assertNotNull(newAuth.authToken());

        var games = facade.listGames(newAuth.authToken()).games();
        Assertions.assertTrue(games.isEmpty());
    }

    @Test
    public void clearEmptyPositive() {
        Assertions.assertDoesNotThrow(() -> facade.clear());
    }

    @Test
    public void clearNegative() throws Exception {
        var failingServer = com.sun.net.httpserver.HttpServer.create(
                new java.net.InetSocketAddress("localhost", 0),
                0);

        failingServer.createContext("/db", exchange -> {
            var body = """
                    {"message":"Error: Unable to clear database"}
                    """.getBytes(java.nio.charset.StandardCharsets.UTF_8);

            exchange.getResponseHeaders()
                    .set("Content-Type", "application/json");

            exchange.sendResponseHeaders(500, body.length);

            try (var output = exchange.getResponseBody()) {
                output.write(body);
            } finally {
                exchange.close();
            }
        });

        failingServer.start();

        try {
            var failingFacade =
                    new ServerFacade(failingServer.getAddress().getPort());

            Assertions.assertThrows(
                    Exception.class,
                    () -> failingFacade.clear());
        } finally {
            failingServer.stop(0);
        }
    }
}
