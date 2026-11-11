package client;

import com.google.gson.Gson;
import model.AuthData;
import service.requests.*;
import service.results.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;

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





    private <T> T makeRequest(
            String method,
            String path,
            Object request,
            String authToken,
            Class<T> responseClass) throws Exception {
        // Combine the base address and path into a full endpoint URL.
        URI uri = new URI(serverUrl + path);
        // Get an HTTP connection object so we can configure the request.
        HttpURLConnection connection =
                (HttpURLConnection) uri.toURL().openConnection();
        connection.setRequestMethod(method);
        // For authenticated calls, put the token in the header where the
        // server can read it and look up the associated session.
        if (authToken != null) {
            connection.setRequestProperty(
                    "Authorization",
                    authToken);
        }
        // Declare JSON as the format used when this request has a body.
        connection.setRequestProperty(
                "Content-Type",
                "application/json");
        // A request object means there is a body to send. Enable output,
        // turn the Java object into JSON, and write the JSON as bytes.
        if (request != null) {
            connection.setDoOutput(true);
            // Closes the stream when the block ends,
            // including if serialization or writing throws an exception.
            try (OutputStream body =
                    connection.getOutputStream()) {
                String json = gson.toJson(request);
                body.write(json.getBytes());
            }
        }
        // Ensure the connection is established
        connection.connect();
        // Read the status before deciding which response stream to use.
        int statusCode = connection.getResponseCode();
        // HTTP status codes of 400 or higher represent errors. Decode the
        // server's error JSON and throw its message back to the caller.
        if (statusCode >= 400) {
            try (InputStream errorStream =
                    connection.getErrorStream()) {
                ErrorResponse error =
                        gson.fromJson(
                                new String(errorStream.readAllBytes()),
                                ErrorResponse.class);
                throw new Exception(error.message());
            }
        }
        // If all you needed was a successful connection, return nothing
        if (responseClass == null) {
            return null;
        }
        // Read the response bytes, turn them into JSON text, and let Gson
        // build the requested Java object
        try (InputStream responseBody =
                connection.getInputStream()) {
            String json =
                    new String(responseBody.readAllBytes());
            return gson.fromJson(json, responseClass);
        }
    }
}
