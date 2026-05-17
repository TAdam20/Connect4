import java.util.ArrayList;
import java.util.List;

public class Connect4State {
    public static final int ROWS = 6;
    public static final int COLS = 7;
    public static final int EMPTY = 0;
    public static final int PLAYER = 1;
    public static final int AI = 2;

    private int[][] board;

    public Connect4State() {
        board = new int[ROWS][COLS];
    }

    public Connect4State(Connect4State original) {
        board = new int[ROWS][COLS];
        for (int i = 0; i < ROWS; i++) {
            System.arraycopy(original.board[i], 0, this.board[i], 0, COLS);
        }
    }

    public boolean dropDisc(int col, int playerNum) {
        if (col < 0 || col >= COLS || board[0][col] != EMPTY) {
            return false;
        }
        for (int row = ROWS - 1; row >= 0; row--) {
            if (board[row][col] == EMPTY) {
                board[row][col] = playerNum;
                return true;
            }
        }
        return false;
    }

    public void printBoard() {
        System.out.println("\n  0 1 2 3 4 5 6");
        for (int i = 0; i < ROWS; i++) {
            System.out.print("| ");
            for (int j = 0; j < COLS; j++) {
                if(board[i][j] == EMPTY) System.out.print(". ");
                else if(board[i][j] == PLAYER) System.out.print("O ");
                else System.out.print("X ");
            }
            System.out.println("|");
        }
        System.out.println("-----------------");
    }

    public List<Integer> getValidMoves() {
        List<Integer> validMoves = new ArrayList<>();
        for (int c = 0; c < COLS; c++) {
            if (board[0][c] == EMPTY) {
                validMoves.add(c);
            }
        }
        return validMoves;
    }

    public boolean checkWin(int player) {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS - 3; c++) {
                if (board[r][c] == player && board[r][c+1] == player &&
                    board[r][c+2] == player && board[r][c+3] == player) return true;
            }
        }
        for (int r = 0; r < ROWS - 3; r++) {
            for (int c = 0; c < COLS; c++) {
                if (board[r][c] == player && board[r+1][c] == player &&
                    board[r+2][c] == player && board[r+3][c] == player) return true;
            }
        }
        for (int r = 3; r < ROWS; r++) {
            for (int c = 0; c < COLS - 3; c++) {
                if (board[r][c] == player && board[r-1][c+1] == player &&
                    board[r-2][c+2] == player && board[r-3][c+3] == player) return true;
            }
        }
        for (int r = 0; r < ROWS - 3; r++) {
            for (int c = 0; c < COLS - 3; c++) {
                if (board[r][c] == player && board[r+1][c+1] == player &&
                    board[r+2][c+2] == player && board[r+3][c+3] == player) return true;
            }
        }
        return false;
    }

    public boolean isBoardFull() {
        return getValidMoves().isEmpty();
    }

    public int[][] getBoard() {
        return board;
    }

    public int evaluateBoard() {
        int score = 0;

        int centerCount = 0;
        for (int r = 0; r < ROWS; r++) {
            if (board[r][COLS / 2] == AI) centerCount++;
        }
        score += centerCount * 3;

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS - 3; c++) {
                int[] window = {board[r][c], board[r][c+1], board[r][c+2], board[r][c+3]};
                score += evaluateWindow(window);
            }
        }
        for (int r = 0; r < ROWS - 3; r++) {
            for (int c = 0; c < COLS; c++) {
                int[] window = {board[r][c], board[r+1][c], board[r+2][c], board[r+3][c]};
                score += evaluateWindow(window);
            }
        }
        for (int r = 3; r < ROWS; r++) {
            for (int c = 0; c < COLS - 3; c++) {
                int[] window = {board[r][c], board[r-1][c+1], board[r-2][c+2], board[r-3][c+3]};
                score += evaluateWindow(window);
            }
        }
        for (int r = 0; r < ROWS - 3; r++) {
            for (int c = 0; c < COLS - 3; c++) {
                int[] window = {board[r][c], board[r+1][c+1], board[r+2][c+2], board[r+3][c+3]};
                score += evaluateWindow(window);
            }
        }
        return score;
    }

    private int evaluateWindow(int[] window) {
        int score = 0;
        int aiCount = 0;
        int playerCount = 0;
        int emptyCount = 0;

        for (int cell : window) {
            if (cell == AI) aiCount++;
            else if (cell == PLAYER) playerCount++;
            else emptyCount++;
        }

        if (aiCount == 4) score += 100;
        else if (aiCount == 3 && emptyCount == 1) score += 5;
        else if (aiCount == 2 && emptyCount == 2) score += 2;

        if (playerCount == 3 && emptyCount == 1) score -= 80; // Blokkolás

        return score;
    }

    public boolean isTerminal() {
        return checkWin(PLAYER) || checkWin(AI) || isBoardFull();
    }
}
