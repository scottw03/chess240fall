package server;

import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.*;
import handler.*;
import service.*;
import service.results.ErrorResponse;

public class Server {

    private final Javalin javalin;
    private final Gson gson = new Gson();

    public Server() {
        javalin = Javalin.create(config -> {
                config.staticFiles.add("web");});

        //create data access objects using MemoryDAO classes
        var userDAO = new MemoryUserDAO();
        var gameDAO = new MemoryGameDAO();
        var authDAO = new MemoryAuthDAO();

        //pass created objects into service classes
        UserService userService = new UserService(userDAO, authDAO);
        GameService gameService = new GameService(gameDAO, authDAO);
        ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);

        //pass the initialized services into the handlers
        UserHandler userHandler = new UserHandler(userService);
        GameHandler gameHandler = new GameHandler(gameService);
        ClearHandler clearHandler = new ClearHandler(clearService);

        // register endpoints and exceptions using handlers names
        //endpoints
        javalin.delete("/db", clearHandler::clear);
        javalin.post("/user", userHandler::register);
        javalin.post("/session", userHandler::login);
        javalin.delete("/session", userHandler::logout);
        javalin.get("/game", gameHandler::listGames);
        javalin.post("/game", gameHandler::createGame);
        javalin.put("/game", gameHandler::joinGame);
        //exceptions
        javalin.exception(Exception.class, (ex, ctx) -> {
            String message = ex.getMessage();
            if ("bad request".equals(message)) {
                ctx.status(400);
            }
            else if ("unauthorized".equals(message)) {
                ctx.status(401);
            }
            else if ("already taken".equals(message)) {
                ctx.status(403);
            }
            else {
                ex.printStackTrace();
                ctx.status(500);
                message = "internal server error";
            }
            ctx.contentType("application/json");
            ctx.result(gson.toJson(new ErrorResponse("Error: " + message)));
        });
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
