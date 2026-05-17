import java.util.List;

public class MinimaxAgent {
    private final int maxDepth;

    public MinimaxAgent(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public int getBestMove(Connect4State state) {
        int[] result = minimax(state, maxDepth, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
        return result[1];
    }

    private int[] minimax(Connect4State state, int depth, int alpha, int beta, boolean isMaximizingPlayer) {
        List<Integer> validMoves = state.getValidMoves();
        boolean isTerminal = state.isTerminal();

        if (depth == 0 || isTerminal) {
            if (isTerminal) {
                if (state.checkWin(Connect4State.AI)) return new int[]{10000000, -1};
                else if (state.checkWin(Connect4State.PLAYER)) return new int[]{-10000000, -1};
                else return new int[]{0, -1};
            } else {
                return new int[]{state.evaluateBoard(), -1};
            }
        }

        int bestColumn = validMoves.get(0);

        if (isMaximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (int col : validMoves) {
                Connect4State childState = new Connect4State(state);
                childState.dropDisc(col, Connect4State.AI);

                int eval = minimax(childState, depth - 1, alpha, beta, false)[0];

                if (eval > maxEval) {
                    maxEval = eval;
                    bestColumn = col;
                }
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) break;
            }
            return new int[]{maxEval, bestColumn};
        } else {
            int minEval = Integer.MAX_VALUE;
            for (int col : validMoves) {
                Connect4State childState = new Connect4State(state);
                childState.dropDisc(col, Connect4State.PLAYER);

                int eval = minimax(childState, depth - 1, alpha, beta, true)[0];

                if (eval < minEval) {
                    minEval = eval;
                    bestColumn = col;
                }
                beta = Math.min(beta, eval);
                if (beta <= alpha) break;
            }
            return new int[]{minEval, bestColumn};
        }
    }
}