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
                String input = readLine().trim();
                if (input.equalsIgnoreCase("quit")) {
                    running = false;
                }
                else {
                    executeCommand(input);
                }
            } catch (java.util.NoSuchElementException ex) {
                running = false;
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

    private void executeCommand(String input) throws Exception {
        if (input.isBlank()) {
            return;
        }

        String[] tokens = input.split("\\s+");

        if (tokens.length != 1) {
            System.out.println(
                    "Enter a command by itself. Details will be prompted.");
            return;
        }

        String command =
                tokens[0].toLowerCase(java.util.Locale.ROOT);

        switch (client.getState()) {
            case PRELOGIN -> executePrelogin(command);
            case POSTLOGIN -> executePostlogin(command);
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

    private void register() throws Exception {
        System.out.print("Username: ");
        String username = readLine();
        System.out.print("Password: ");
        String password = readLine();
        System.out.print("Email: ");
        String email = readLine();
        String result =
                client.register(
                        username,
                        password,
                        email);
        System.out.println(result);
    }

    private void login() throws Exception {
        System.out.print("Username: ");
        String username = readLine();
        System.out.print("Password: ");
        String password = readLine();
        String result = client.login(
                username,
                password);
        System.out.println(result);
    }

    private void executePostlogin(String command)
            throws Exception {
        switch (command) {
            case "help" -> printPostloginHelp();
            case "logout" -> logout();
            case "create" -> createGame();
            case "list" -> listGames();
            case "join" -> playGame();
            case "observe" -> observeGame();
            default ->
                    System.out.println(
                            "Unknown command. Type 'help'.");
        }
    }

    private void printPostloginHelp() {
        System.out.println("""
                create - a game
                list - games
                join - a game
                observe - a game
                logout - when you are done
                quit - playing chess
                help - with possible commands
                """);
    }

    private void logout() throws Exception {
        System.out.println(client.logout());
    }

    private void createGame() throws Exception {
        System.out.print("Game Name: ");
        String gameName = readLine();
        System.out.println(
                client.createGame(
                        gameName));
    }

    private void listGames() throws Exception {
        var games = client.listGames();
        if (games.isEmpty()) {
            System.out.println("No games found.");
            return;
        }
        int index = 1;
        for (var game : games) {
            System.out.printf(
                    "%d. %s%n",
                    index++,
                    game.gameName());
            System.out.printf(
                    "   White: %s%n",
                    game.whiteUsername());
            System.out.printf(
                    "   Black: %s%n%n",
                    game.blackUsername());
        }
    }

    private void playGame() {
        try {
            System.out.print("Game Number: ");
            int gameNumber =
                    Integer.parseInt(
                            readLine().trim());
            System.out.print(" Color (WHITE/BLACK): ");
            String color = readLine();
            System.out.println(
                    client.joinGame(
                            color,
                            gameNumber));
        } catch (java.util.NoSuchElementException ex) {
            throw ex;
        } catch (NumberFormatException ex) {
            System.out.println(
                    "Please enter a valid game number.");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void observeGame() {
        try {
            System.out.print("Game Number: ");
            int gameNumber =
                    Integer.parseInt(
                            readLine());
            client.observeGame(gameNumber);
        } catch (java.util.NoSuchElementException ex) {
            throw ex;
        } catch (NumberFormatException ex) {
            System.out.println(
                    "Please enter a valid game number.");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private String readLine() {
        if (!scanner.hasNextLine()) {
            throw new java.util.NoSuchElementException();
        }
        return scanner.nextLine();
    }

}
