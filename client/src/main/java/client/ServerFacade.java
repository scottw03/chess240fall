package client;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import model.AuthData;
import service.requests.*;
import service.results.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class ServerFacade {

    // Initialize base gson and serverUrl instances
    private final Gson gson = new Gson();
    private final String serverUrl;

    // Supplies the port on which the local chess server is listening.
    public ServerFacade(int port) {serverUrl = "http://localhost:" + port;}

    public AuthData register(String username,
                             String password,
                             String email) throws Exception {
        RegisterRequest request =
                new RegisterRequest(username, password, email);
        return makeRequest(
                "POST",
                "/user",
                request,
                null,
                AuthData.class);
    }

    public AuthData login(String username,
                          String password) throws Exception {
        LoginRequest request =
                new LoginRequest(username, password);
        return makeRequest(
                "POST",
                "/session",
                request,
                null,
                AuthData.class);
    }

    public void logout(String authToken) throws Exception {
        makeRequest(
                "DELETE",
                "/session",
                null,
                authToken,
        null);
    }

    public CreateGameResult createGame(String gameName,
                                       String authToken) throws Exception {
        CreateGameRequest request =
                new CreateGameRequest(gameName, authToken);
        return makeRequest(
                "POST",
                "/game",
                request,
                authToken,
                CreateGameResult.class);
    }

    public ListGamesResult listGames(String authToken)
            throws Exception {
        return makeRequest(
                "GET",
                "/game",
                null,
                authToken,
                ListGamesResult.class);
    }

    public void joinGame(String color,
                         int gameID,
                         String authToken) throws Exception {
        JoinGameRequest request =
                new JoinGameRequest(color, gameID, authToken);
        makeRequest(
                "PUT",
                "/game",
                request,
                authToken,
                null);
    }

    public void clear() throws Exception {
        makeRequest(
                "DELETE",
                "/db",
                null,
                null,
                null);
    }

    // Sends an HTTP request, checks for errors, and parses the expected response.
    private <T> T makeRequest(
            String method,
            String path,
            Object request,
            String authToken,
            Class<T> responseClass) throws Exception {

        URI uri = new URI(serverUrl + path);

        HttpURLConnection connection =
                (HttpURLConnection) uri.toURL().openConnection();

        try {
            // Configure timeouts before any operation can connect.
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setRequestMethod(method);

            if (authToken != null) {
                connection.setRequestProperty(
                        "Authorization",
                        authToken);
            }

            connection.setRequestProperty(
                    "Content-Type",
                    "application/json; charset=UTF-8");

            if (request != null) {
                connection.setDoOutput(true);

                try (OutputStream body = connection.getOutputStream()) {
                    String json = gson.toJson(request);
                    body.write(json.getBytes(StandardCharsets.UTF_8));
                }
            }

            // This establishes the connection if it is not already open.
            int statusCode = connection.getResponseCode();

            if (statusCode >= 400) {
                String message;

                if (statusCode >= 500) {
                    message = "The server could not complete the request. "
                            + "Please try again.";
                } else if (statusCode == 401) {
                    message = path.equals("/session") && method.equals("POST")
                            ? "Incorrect username or password."
                            : "Your session is not valid. Please log in again.";
                } else if (statusCode == 403 && path.equals("/user")) {
                    message = "That username is already taken.";
                } else if (statusCode == 403
                        && path.equals("/game")
                        && method.equals("PUT")) {
                    message = "That player position is already taken.";
                } else if (path.equals("/game") && method.equals("PUT")) {
                    message = "Could not join that game. Refresh the game list "
                            + "and choose an available color.";
                } else {
                    message = "The request was not accepted. "
                            + "Please check your information.";
                }

                throw new Exception(message);
            }

            try (InputStream responseBody = connection.getInputStream()) {
                if (responseClass == null) {
                    return null;
                }

                String json = new String(
                        responseBody.readAllBytes(),
                        StandardCharsets.UTF_8);

                T result = gson.fromJson(json, responseClass);

                if (result == null) {
                    throw new Exception(
                            "The server returned an unexpected response. "
                                    + "Please try again.");
                }

                return result;
            }
        } catch (IOException ex) {
            throw new Exception(
                    "Could not communicate with the server. "
                            + "Check that it is running and try again.");
        } catch (JsonParseException ex) {
            throw new Exception(
                    "The server returned an unexpected response. "
                            + "Please try again.");
        } finally {
            connection.disconnect();
        }
    }
}
