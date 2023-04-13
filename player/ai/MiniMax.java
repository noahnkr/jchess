package player.ai;

import java.text.DecimalFormat;

import board.Board;
import board.Move;
import board.MoveTransition;

public class MiniMax implements MoveStrategy {

    private BoardEvaluator evaluator;
    private int searchDepth;

    private static final int NEGATIVE_INFINITY = Integer.MIN_VALUE;
    private static final int POSITIVE_INFINITY = Integer.MAX_VALUE;

    public MiniMax(int searchDepth) {
        evaluator = new StandardBoardEvaluator();
        this.searchDepth = searchDepth;

    }

    @Override
    public Move execute(Board board) {
        long startTIme = System.currentTimeMillis();

        Move bestMove = new Move.NullMove();
        int highestSeenValue = NEGATIVE_INFINITY;
        int lowestSeenValue = POSITIVE_INFINITY;
        int currentValue;
        System.out.println(board.currentPlayer().getColor() + " is THINKING with depth = " + searchDepth);

        int numMoves = board.currentPlayer().getLegalMoves().size();
        for (Move move : board.currentPlayer().getLegalMoves()) {
            MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                currentValue = board.currentPlayer().getColor().isWhite() ?
                               minimize(moveTransition.getTransitionBoard(), searchDepth - 1, POSITIVE_INFINITY, NEGATIVE_INFINITY) :
                               maximize(moveTransition.getTransitionBoard(), searchDepth - 1, NEGATIVE_INFINITY, POSITIVE_INFINITY);

                if (board.currentPlayer().getColor().isWhite() && currentValue >= highestSeenValue) {
                    highestSeenValue = currentValue;
                    bestMove = move;
                } else if (board.currentPlayer().getColor().isBlack() && currentValue <= lowestSeenValue) {
                    lowestSeenValue = currentValue;
                    bestMove = move;
                }
            }
        }

        long executionTime = System.currentTimeMillis() - startTIme;
        double executionTimeSeconds = executionTime / 1000.0;
        DecimalFormat df = new DecimalFormat("#.###");
        System.out.println(board.currentPlayer().toString() + " SELECTS " + bestMove.toString() + " [Execution Time = " + (Double.valueOf(df.format(executionTimeSeconds))) + "s]\n");
        return bestMove;
    }

    public int minimize(Board board, int depth, int alpha, int beta) {
        if (depth == 0 || isEndGameScenario(board)) {
            return this.evaluator.evaluate(board, depth);
        }

        int lowestSeenValue = POSITIVE_INFINITY;
        for (Move move : board.currentPlayer().getLegalMoves()) {
            MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                int currentValue = maximize(moveTransition.getTransitionBoard(), depth - 1, alpha, beta);
                lowestSeenValue = Math.min(lowestSeenValue, currentValue);
                beta = Math.min(beta, currentValue);
                if (beta <= alpha) {
                    break;
                }
            }
        }
        return lowestSeenValue;

    }

    public int maximize(Board board, int depth, int alpha, int beta) {
        if (depth == 0 || isEndGameScenario(board)) {
            return this.evaluator.evaluate(board, depth);
        }

        int highestSeenValue = NEGATIVE_INFINITY;
        for (Move move : board.currentPlayer().getLegalMoves()) {
            MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                int currentValue = minimize(moveTransition.getTransitionBoard(), depth - 1, alpha, beta);
                highestSeenValue = Math.max(highestSeenValue, currentValue);
                alpha = Math.max(alpha, currentValue);
                if (beta <= alpha) {
                    break;
                }
            }
        }
        return highestSeenValue;

    }

    private static boolean isEndGameScenario(Board board) {
        return board.currentPlayer().isInCheckMate() ||
               board.currentPlayer().isInStaleMate();
    }

    @Override
    public String toString() {
        return "Minimax";
    }



    
}
