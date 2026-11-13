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
}
