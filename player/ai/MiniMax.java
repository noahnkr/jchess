package player.ai;

import java.util.Collection;
import java.util.Comparator;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;

import board.Board;
import board.Move;
import board.MoveTransition;

public class MiniMax {

    private BoardEvaluator evaluator;
    private int searchDepth;
    private MoveSorter moveSorter;
    private int boardsEvaluated;

    public MiniMax(int searchDepth) {
        this.evaluator = new BoardEvaluator();
        this.searchDepth = searchDepth;
        this.moveSorter = MoveSorter.SORT;
        this.boardsEvaluated = 0;
    }

    public Move getBestMove(Board board) {
        long startTIme = System.currentTimeMillis();
        System.out.printf("%s is THINKING [depth = %s]%n", board.currentPlayer(), searchDepth);
        
        Move bestMove = new Move.NullMove();
        int maxScore = Integer.MIN_VALUE;
        int minScore = Integer.MAX_VALUE;
        for (Move move : moveSorter.sort(board.currentPlayer().getLegalMoves())) {
            MoveTransition transition = board.currentPlayer().makeMove(move);
            if (transition.getMoveStatus().isDone()) {
                int score = board.currentPlayer().getColor().isWhite() ? 
                        min(transition.getTransitionBoard(), searchDepth - 1, maxScore, minScore) :
                        max(transition.getTransitionBoard(), searchDepth - 1, maxScore, minScore);
        
                if (board.currentPlayer().getColor().isWhite() && score > maxScore) {
                    maxScore = score;
                    bestMove = move;

                } else if (board.currentPlayer().getColor().isBlack() && score < minScore) {
                    minScore = score;
                    bestMove = move;
                }
            }
        }

        long executionTime = System.currentTimeMillis() - startTIme;
        System.out.printf("%s SELECTS %s [#boards = %d, time taken = %d ms, rate = %.1f]%n", board.currentPlayer(),
                          bestMove, this.boardsEvaluated, executionTime, (1000 * ((double)this.boardsEvaluated/executionTime)));
        return bestMove;
    }

    public int max(Board board, int depth, int alpha, int beta) {
        if (depth == 0 || Board.gameOver(board)) {
            boardsEvaluated++;
            return evaluator.evaluate(board, depth);
        }

        int maxScore = alpha;
        for (Move move : moveSorter.sort(board.currentPlayer().getLegalMoves())) {
            MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                maxScore = Math.max(maxScore, min(moveTransition.getTransitionBoard(), depth - 1, maxScore, beta));
                if (beta <= maxScore) {
                    break;
                }
            }
        }
        return maxScore;
    }

    public int min(Board board, int depth, int alpha, int beta) {
        if (depth == 0 || Board.gameOver(board)) {
            this.boardsEvaluated++;
            return this.evaluator.evaluate(board, depth);
        }

        int minScore = beta;
        for (Move move : moveSorter.sort(board.currentPlayer().getLegalMoves())) {
            MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                minScore = Math.min(minScore, max(moveTransition.getTransitionBoard(), depth - 1, alpha, minScore));
                if (minScore <= alpha) {
                    break;
                }
            }
        }
        return minScore;
    }


    private enum MoveSorter {

        SORT {
            @Override
            Collection<Move> sort(final Collection<Move> moves) {
                return Ordering.from(SMART_SORT).immutableSortedCopy(moves);
            }
        };

        public static Comparator<Move> SMART_SORT = new Comparator<Move>() {
            @Override
            public int compare(final Move move1, final Move move2) {
                return ComparisonChain.start()
                        .compareTrueFirst(Board.isThreatenedBoardImmediate(move1.getBoard()), Board.isThreatenedBoardImmediate(move2.getBoard()))
                        .compareTrueFirst(move1.isAttackMove(), move2.isAttackMove())
                        .compareTrueFirst(move1.isCastlingMove(), move2.isCastlingMove())
                        .compare(move2.getMovedPiece().getPieceValue(), move1.getMovedPiece().getPieceValue())
                        .result();
            }
        };

        abstract Collection<Move> sort(Collection<Move> moves);
    }




}
