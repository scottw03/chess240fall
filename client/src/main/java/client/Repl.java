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

}
