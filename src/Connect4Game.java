import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Connect4Game {

    public static void main(String[] args) {
        DatabaseManager dbManager = new DatabaseManager();
        Scanner scanner = new Scanner(System.in);

        System.out.println("--- Connect 4 Játék ---");

        System.out.print("Kérlek, add meg a neved: ");
        String playerName = scanner.nextLine();
        if (playerName.trim().isEmpty()) {
            playerName = "Névtelen Játékos";
        }

        dbManager.printScoreBoard();

        Connect4State gameState = new Connect4State();
        MinimaxAgent ai = new MinimaxAgent(8);

        System.out.println("Tábla betöltése a kezdoallas.txt fájlból...");
        loadBoardFromFile(gameState, "kezdoallas.txt");
        // ----------------------------------

        gameState.printBoard();
        boolean isHumanTurn = true;

        while (!gameState.isTerminal()) {
            if (isHumanTurn) {
                System.out.println("\nTe jössz (" + playerName + ") -> O");
                int col = -1;
                while (true) {
                    System.out.print("Válassz oszlopot (0-6): ");
                    if (scanner.hasNextInt()) {
                        col = scanner.nextInt();
                        if (gameState.getValidMoves().contains(col)) {
                            gameState.dropDisc(col, Connect4State.PLAYER);
                            break;
                        } else {
                            System.out.println("Érvénytelen vagy tele oszlop! Próbáld újra.");
                        }
                    } else {
                        System.out.println("Kérlek számot adj meg!");
                        scanner.next();
                    }
                }
                scanner.nextLine();
            } else {
                System.out.println("\nA Gép gondolkodik...");
                int bestCol = ai.getBestMove(gameState);
                System.out.println("A Gép a " + bestCol + ". oszlopba dobott (X).");
                gameState.dropDisc(bestCol, Connect4State.AI);
            }

            gameState.printBoard();
            isHumanTurn = !isHumanTurn;
        }

        System.out.println("\n--- JÁTÉK VÉGE ---");
        String finalResult = "";

        if (gameState.checkWin(Connect4State.PLAYER)) {
            System.out.println("Gratulálok " + playerName + ", TE NYERTÉL!");
            finalResult = "Győzelem";
        } else if (gameState.checkWin(Connect4State.AI)) {
            System.out.println("A GÉP NYERT! Próbáld újra.");
            finalResult = "Vereség";
        } else {
            System.out.println("Döntetlen!");
            finalResult = "Döntetlen";
        }

        dbManager.saveMatchResult(playerName, finalResult);

        dbManager.printScoreBoard();

        scanner.close();
    }

    private static void loadBoardFromFile(Connect4State state, String filename) {
        try {
            Scanner fileScanner = new Scanner(new File(filename));
            int[][] tempBoard = new int[Connect4State.ROWS][Connect4State.COLS];

            for (int r = 0; r < Connect4State.ROWS; r++) {
                for (int c = 0; c < Connect4State.COLS; c++) {
                    if (fileScanner.hasNextInt()) {
                        tempBoard[r][c] = fileScanner.nextInt();
                    }
                }
            }

            int[][] actualBoard = state.getBoard();
            for (int r = 0; r < Connect4State.ROWS; r++) {
                System.arraycopy(tempBoard[r], 0, actualBoard[r], 0, Connect4State.COLS);
            }

            System.out.println("[Rendszer] Sikeresen betöltve a(z) " + filename + " fájlból.");
            fileScanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("\n[Rendszer] Nem található a fájl: " + filename + " -> Üres táblával indulunk.");
        } catch (Exception e) {
            System.out.println("\n[Rendszer] Hibás fájlformátum -> Üres táblával indulunk.");
        }
    }
}