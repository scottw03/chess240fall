package client;

import chess.ChessBoard;
import chess.ChessGame;
import model.AuthData;
import model.GameData;
import service.results.ListGamesResult;
import ui.BoardRenderer;

import java.util.ArrayList;
import java.util.List;

public class ChessClient {

    private final ServerFacade server;
    private String authToken;
    private final List<GameData> listedGames = new ArrayList<>();
    private State state = State.PRELOGIN;

    public enum State {
        PRELOGIN,
        POSTLOGIN
    }

    // Stores the facade used to communicate with the server
    public ChessClient(ServerFacade server) {
        this.server = server;
    }

    // Returns the login state
    public State getState() {
        return state;
    }

    // Validates registration details and logs in new user
    public String register(
            String username,
            String password,
            String email)
            throws Exception {
        if (username == null || username.isBlank()) {
            throw new Exception(
                    "Please enter a username.");
        }
        if (password == null || password.isBlank()) {
            throw new Exception(
                    "Please enter a password.");
        }
        if (email == null || email.isBlank()) {
            throw new Exception(
                    "Please enter an email.");
        }
        AuthData auth =
                server.register(
                        username,
                        password,
                        email);
        this.authToken = auth.authToken();
        this.state = State.POSTLOGIN;
        return "Successfully registered.";
    }

    // Validates credentials and stores successful login session
    public String login(
            String username,
            String password)
            throws Exception {
        if (username == null || username.isBlank()) {
            throw new Exception(
                    "Please enter a username.");
        }
        if (password == null || password.isBlank()) {
            throw new Exception(
                    "Please enter a password.");
        }

        AuthData auth =
                server.login(
                        username,
                        password);
        this.authToken = auth.authToken();
        this.state = State.POSTLOGIN;
        return "Successfully logged in.";
    }

    // Ends the session and returns client to prelogin state
    public String logout()
            throws Exception {
        server.logout(authToken);
        authToken = null;
        listedGames.clear();
        state = State.PRELOGIN;
        return "Logged out.";
    }

    // Creates a named game without joining
    public String createGame(String gameName) throws Exception {
        if (gameName == null || gameName.isBlank()) {
            throw new Exception("Please enter a game name.");
        }

        server.createGame(gameName, authToken);

        return "Game created. Use 'list' to see available games.";
    }

    // Refreshes games list while preserving their order
    public List<GameData> listGames()
            throws Exception {
        ListGamesResult result =
                server.listGames(authToken);
        listedGames.clear();
        listedGames.addAll(result.games());
        return new ArrayList<>(result.games());
    }

    // Joins a numbered game and draws initial board
    public String joinGame(String color, int gameNumber) throws Exception {
        GameData game = selectListedGame(gameNumber);

        if (color == null || color.isBlank()) {
            throw new Exception("Please choose white or black.");
        }

        String normalizedColor =
                color.trim().toUpperCase(java.util.Locale.ROOT);

        if (!normalizedColor.equals("WHITE")
                && !normalizedColor.equals("BLACK")) {
            throw new Exception("Please choose white or black.");
        }

        server.joinGame(
                normalizedColor,
                game.gameID(),
                authToken);

        ChessGame.TeamColor perspective =
                normalizedColor.equals("WHITE")
                        ? ChessGame.TeamColor.WHITE
                        : ChessGame.TeamColor.BLACK;

        drawInitialBoard(perspective);

        return "Joined game.";
    }

    // Selects a game and displays initial board to observer
    public void observeGame(int gameNumber) throws Exception {
        GameData game = selectListedGame(gameNumber);

        drawInitialBoard(ChessGame.TeamColor.WHITE);

        System.out.println("Observing " + game.gameName() + ".");
    }

    // Converts displayed game number into game information
    private GameData selectListedGame(int gameNumber) throws Exception {
        if (listedGames.isEmpty()) {
            throw new Exception(
                    "No games are listed. Use 'list' to check available games.");
        }

        if (gameNumber < 1 || gameNumber > listedGames.size()) {
            throw new Exception(
                    "Choose a game number between 1 and "
                            + listedGames.size() + ".");
        }

        return listedGames.get(gameNumber - 1);
    }

    // Draws initial board position from chosen perspective
    private void drawInitialBoard(ChessGame.TeamColor perspective) {
        ChessBoard board = new ChessBoard();
        board.resetBoard();

        BoardRenderer.drawBoard(board, perspective);
    }

}
