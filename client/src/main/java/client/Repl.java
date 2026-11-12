package client;

import java.util.Scanner;

public class Repl {
    private final ChessClient client;
    private final Scanner scanner = new Scanner(System.in);

    public Repl(ChessClient client) {this.client = client;}

    public void run() {
        System.out.println("Welcome to 240 Chess!");
        boolean running = true;
        while (running) {
            try {
                printPrompt();
                String input = scanner.nextLine().trim();
                if (input.equalsIgnoreCase("quit")) {
                    running = false;
                }
                else {
                    executeCommand(input);
                }
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
        System.out.println("Goodbye.");
    }

    private void printPrompt() {
        if (client.getState() ==
                ChessClient.State.PRELOGIN) {
            System.out.print("[LOGGED OUT] >>> ");
        }
        else {
            System.out.print("[LOGGED IN] >>> ");
        }
    }

    private void executeCommand(String input)
            throws Exception {
        String[] tokens =
                input.split("\\s+");
        String command =
                tokens[0].toLowerCase();
        switch (client.getState()) {
            case PRELOGIN ->
                    executePrelogin(command);
            case POSTLOGIN ->
                    executePostlogin(command);
            case GAMEPLAY ->
                    executeGameplay(command);
        }
    }

    private void executePrelogin(String command)
            throws Exception {
        switch (command) {
            case "help" -> printPreloginHelp();
            case "register" -> register();
            case "login" -> login();
            default ->
                    System.out.println(
                            "Unknown command. Type 'help'.");
        }
    }

    private void printPreloginHelp() {
        System.out.println("""
                register - to create an account
                login - to play chess
                quit - playing chess
                help - with possible commands
                """);
    }



}
