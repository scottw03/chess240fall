package client;

public class ClientMain {
    public static void main(String[] args) {
        try {
            ServerFacade facade = new ServerFacade(8080);
            ChessClient client = new ChessClient(facade);
            Repl repl = new Repl(client);
            repl.run();
        } catch (Exception ex) {
        System.out.println(
                "Could not start the client. Please check your setup and try again.");
    }
    }
}
