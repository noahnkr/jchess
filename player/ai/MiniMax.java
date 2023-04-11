package player.ai;

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

        Move bestMove = null;
        int highestSeenValue = NEGATIVE_INFINITY;
        int lowestSeenValue = POSITIVE_INFINITY;
        int currentValue;
        System.out.println(board.currentPlayer() + " THINKING with depth = " + searchDepth);

        int numMoves = board.currentPlayer().getLegalMoves().size();
        for (Move move : board.currentPlayer().getLegalMoves()) {
            MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                currentValue = board.currentPlayer().getColor().isWhite() ?
                               minimize(moveTransition.getTransitionBoard(), searchDepth - 1) :
                               maximize(moveTransition.getTransitionBoard(), searchDepth - 1);

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
        return bestMove;
    }

    public int minimize(Board board, int depth) {
        if (depth == 0 || isEndGameScenario(board)) {
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
        if (depth == 0 || isEndGameScenario(board)) {
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

    private static boolean isEndGameScenario(Board board) {
        return board.currentPlayer().isInCheckMate() ||
               board.currentPlayer().isInStaleMate();
    }

    @Override
    public String toString() {
        return "Minimax";
    }
    
}
