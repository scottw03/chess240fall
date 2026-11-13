package client;

import java.util.Scanner;

public class Repl {
    private final ChessClient client;
    private final Scanner scanner = new Scanner(System.in);

    // Connects the command interface to the chess client
    public Repl(ChessClient client) {this.client = client;}

    // Runs the command loop until the user quits of input closes
    public void run() {
        System.out.println("Welcome to 240 Chess!");
        System.out.println("Type help to list possible actions");
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
            } catch (RuntimeException ex) {
                System.out.println(
                        "Could not complete the command. Please try again.");
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
        System.out.println("Goodbye.");
    }

    // Prints a prompt for the current login state
    private void printPrompt() {
        if (client.getState() ==
                ChessClient.State.PRELOGIN) {
            System.out.print("[LOGGED OUT] >>> ");
        }
        else {
            System.out.print("[LOGGED IN] >>> ");
        }
    }

    // Validates the command and sends it to the appropriate menu
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

    // Handles commands available before logging in
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

    // Lists the commands available before logging in
    private void printPreloginHelp() {
        System.out.println("""
                register - to create an account
                login - to play chess
                quit - exit the client
                help - with possible commands
                """);
    }

    // Collects registration details and requests a new account
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

    // Collects credentials and requests login
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

    // Handles commands available after logging in
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

    // Lists the commands available after logging in
    private void printPostloginHelp() {
        System.out.println("""
                create - a game
                list - games
                join - a game
                observe - a game
                logout - when you are done
                quit - exit the client
                help - with possible commands
                """);
    }

    // Logs out the user
    private void logout() throws Exception {
        System.out.println(client.logout());
    }

    // Collects a game name and requests game creation
    private void createGame() throws Exception {
        System.out.print("Game Name: ");
        String gameName = readLine();
        System.out.println(
                client.createGame(
                        gameName));
    }

    // Displays numbered games and their players
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
                    game.whiteUsername() == null
                            ? "(open)"
                            : game.whiteUsername());

            System.out.printf(
                    "   Black: %s%n%n",
                    game.blackUsername() == null
                            ? "(open)"
                            : game.blackUsername());
        }
    }

    // Collects a game number and color and requests to join
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
        } catch (RuntimeException ex) {
            System.out.println(
                    "Could not complete the command. Please try again.");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    // Selects a listed game to view
    private void observeGame() {
        try {
            System.out.print("Game Number: ");
            int gameNumber =
                    Integer.parseInt(
                            readLine().trim());
            client.observeGame(gameNumber);
        } catch (java.util.NoSuchElementException ex) {
            throw ex;
        } catch (NumberFormatException ex) {
            System.out.println(
                    "Please enter a valid game number.");
        } catch (RuntimeException ex) {
            System.out.println(
                    "Could not complete the command. Please try again.");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    // Reads one line or signals that terminal input has closed
    private String readLine() {
        if (!scanner.hasNextLine()) {
            throw new java.util.NoSuchElementException();
        }
        return scanner.nextLine();
    }

}
