package player.ai;

import board.Board;
import board.Move;
import board.MoveTransition;
import player.Player;

public class MiniMax implements MoveStrategy {

    private BoardEvaluator evaluator;
    private int searchDepth;
    private long boardsEvaluated;

    private static final int NEGATIVE_INFINITY = Integer.MIN_VALUE;
    private static final int POSITIVE_INFINITY = Integer.MAX_VALUE;

    public MiniMax(int searchDepth) {
        evaluator = new StandardBoardEvaluator();
        this.searchDepth = searchDepth;
        this.boardsEvaluated = 0;
    }

    @Override
    public Move execute(Board board) {
        long startTIme = System.currentTimeMillis();
        Player currentPlayer = board.currentPlayer();

        Move bestMove = new Move.NullMove();
        int highestSeenValue = NEGATIVE_INFINITY;
        int lowestSeenValue = POSITIVE_INFINITY;
        int currentValue;
        System.out.printf("%s is THINKING [depth = %s]%n", board.currentPlayer(), searchDepth);

        int numMoves = board.currentPlayer().getLegalMoves().size();
        for (Move move : board.currentPlayer().getLegalMoves()) {
            MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                currentValue = currentPlayer.getColor().isWhite() ?
                               minimize(moveTransition.getTransitionBoard(), this.searchDepth - 1, highestSeenValue, lowestSeenValue) :
                               maximize(moveTransition.getTransitionBoard(), this.searchDepth - 1, highestSeenValue, lowestSeenValue);

                if (currentPlayer.getColor().isWhite() && currentValue > highestSeenValue) {
                    highestSeenValue = currentValue;
                    bestMove = move;
                } else if (currentPlayer.getColor().isBlack() && currentValue < lowestSeenValue) {
                    lowestSeenValue = currentValue;
                    bestMove = move;
                }
            }
        }

        long executionTime = System.currentTimeMillis() - startTIme;
        System.out.printf("%s SELECTS %s [#boards = %d, time taken = %d ms, rate = %.1f]%n", board.currentPlayer(),
                          bestMove, this.boardsEvaluated, executionTime, (1000 * ((double)this.boardsEvaluated/executionTime)));
        return bestMove;
    }

    public int maximize(Board board, int depth, int alpha, int beta) {
        if (depth == 0 || gameOver(board)) {
            this.boardsEvaluated++;
            return this.evaluator.evaluate(board, depth);
        }

        int max = alpha;
        for (Move move : board.currentPlayer().getLegalMoves()) {
            MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                max = Math.max(max, minimize(moveTransition.getTransitionBoard(), depth - 1, max, beta));
                if (beta <= max) {
                    break;
                }
            }
        }
        return max;
    }

    public int minimize(Board board, int depth, int alpha, int beta) {
        if (depth == 0 || gameOver(board)) {
            this.boardsEvaluated++;
            return this.evaluator.evaluate(board, depth);
        }

        int min = beta;
        for (Move move : board.currentPlayer().getLegalMoves()) {
            MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                min = Math.min(min, maximize(moveTransition.getTransitionBoard(), depth - 1, alpha, min));
                if (min <= alpha) {
                    break;
                }
            }
        }
        return min;
    }
    
    private static boolean gameOver(Board board) {
        return board.currentPlayer().isInCheckMate() ||
               board.currentPlayer().isInStaleMate();
    }

    @Override
    public String toString() {
        return "Minimax";
    }

}
