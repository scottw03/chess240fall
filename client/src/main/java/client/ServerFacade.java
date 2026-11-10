package client;

import com.google.gson.Gson;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;

public class ServerFacade {

    private final Gson gson = new Gson();
    private final String serverUrl;

    public ServerFacade(int port) {serverUrl = "http://localhost:" + port;}

    private <T> T makeRequest(
            String method,
            String path,
            Object request,
            String authToken,
            Class<T> responseClass) throws Exception {
        URI uri = new URI(serverUrl + path);
        HttpURLConnection connection =
                (HttpURLConnection) uri.toURL().openConnection();
        connection.setRequestMethod(method);
        if (authToken != null) {
            connection.setRequestProperty(
                    "Authorization",
                    authToken);
        }
        connection.setRequestProperty(
                "Content-Type",
                "application/json");
        if (request != null) {
            connection.setDoOutput(true);
            try (OutputStream body =
                    connection.getOutputStream()) {
                String json = gson.toJson(request);
                body.write(json.getBytes());
            }
        }
        connection.connect();
    }
}
