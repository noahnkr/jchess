package player.ai;

import board.Board;
import board.Move;
import board.MoveTransition;

public class MiniMax implements MoveStrategy {

    private BoardEvaluator evaluator;

    private static final int NEGATIVE_INFINITY = Integer.MIN_VALUE;
    private static final int POSITIVE_INFINITY = Integer.MAX_VALUE;

    public MiniMax() {

    }

    @Override
    public Move execute(Board board, int depth) {
        return null;
    }

    public int minimize(Board board, int depth) {
        if (depth == 0 /*|| board.gameOver() */) {
            return this.evaluator.evaluate(board, depth);
        }

        int lowestSeenValue = POSITIVE_INFINITY;
        for (Move move : board.currentPlayer().getLegalMoves()) {
            MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                int currentValue = maximize(moveTransition.getTransitionBoard(), depth - 1);
                if (currentValue <= lowestSeenValue) {
                    lowestSeenValue = currentValue;
                }
            }
        }
        return lowestSeenValue;

    }

    public int maximize(Board board, int depth) {
        if (depth == 0 /*|| board.gameOver() */) {
            return this.evaluator.evaluate(board, depth);
        }

        int highestSeenValue = NEGATIVE_INFINITY;
        for (Move move : board.currentPlayer().getLegalMoves()) {
            MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                int currentValue = minimize(moveTransition.getTransitionBoard(), depth - 1);
                if (currentValue >= highestSeenValue) {
                    highestSeenValue = currentValue;
                }
            }
        }
        return highestSeenValue;

    }

    @Override
    public String toString() {
        return "Minimax";
    }
    
}
