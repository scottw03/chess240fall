package client;

import java.net.HttpURLConnection;
import java.net.URI;

public class ServerFacade {

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

    }
}
