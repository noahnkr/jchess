package player.ai;

import java.util.Collection;
import java.util.Comparator;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;

import board.Board;
import board.Move;
import board.MoveTransition;
import player.ai.TranspositionTable.Entry;

public class MiniMax {

    private BoardEvaluator evaluator;
    private TranspositionTable transpositionTable;
    private int searchDepth;
    private MoveSorter moveSorter;
    private int boardsEvaluated;

    public MiniMax(int searchDepth) {
        this.evaluator = new BoardEvaluator();
        this.transpositionTable = new TranspositionTable(searchDepth);
        this.searchDepth = searchDepth;
        this.moveSorter = MoveSorter.SORT;
        this.boardsEvaluated = 0;
        Zobrist.generateZobristKeys();
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
                        minimax(transition.getToBoard(), searchDepth - 1, maxScore, minScore, false) :
                        minimax(transition.getToBoard(), searchDepth - 1, maxScore, minScore, true);
        
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

    public int minimax(Board board, int depth, int alpha, int beta, boolean isMaximizingPlayer) {
        long key = Zobrist.hash(board);

        // if board alredy evaluated at same or a deeper depth
        if (transpositionTable.contains(key) && transpositionTable.get(key).getDepth() >= depth) {
            Entry entry = transpositionTable.get(key);
            if (entry.getFlag() == TranspositionTable.EXACT) {
                return entry.getScore();
            } else if (entry.getFlag() == TranspositionTable.LOWER) {
                alpha = Math.max(alpha, entry.getScore());
            } else {
                beta = Math.min(beta, entry.getScore());
            }

            if (alpha >= beta) {
                return entry.getScore();
            }
        }

        if (depth == 0 || board.gameOver()) {
            boardsEvaluated++;
            return evaluator.evaluate(board, depth);
        }

        if (isMaximizingPlayer) {
            int maxScore = alpha;
            for (Move move : moveSorter.sort(board.currentPlayer().getLegalMoves())) {
                MoveTransition moveTransition = board.currentPlayer().makeMove(move);
                if (moveTransition.getMoveStatus().isDone()) {
                    maxScore = Math.max(maxScore, minimax(moveTransition.getToBoard(), depth - 1, maxScore, beta, false));
                    if (beta <= maxScore) {
                        break;
                    }
                }
            }

            int flag = maxScore <= alpha ? TranspositionTable.EXACT : TranspositionTable.LOWER;
            transpositionTable.put(key, new Entry(maxScore, depth, flag));
            return maxScore;
        } else {
            int minScore = beta;
            for (Move move : moveSorter.sort(board.currentPlayer().getLegalMoves())) {
                MoveTransition moveTransition = board.currentPlayer().makeMove(move);
                if (moveTransition.getMoveStatus().isDone()) {
                    minScore = Math.min(minScore, minimax(moveTransition.getToBoard(), depth - 1, alpha, minScore, true));
                    if (minScore <= alpha) {
                        break;
                    }
                }
            }

            int flag = minScore >= beta ? TranspositionTable.EXACT : TranspositionTable.UPPER;
            transpositionTable.put(key, new Entry(minScore, depth, flag));
            return minScore;
        }
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
                        .compareTrueFirst(move1.getBoard().isKingThreatened(), move2.getBoard().isKingThreatened())
                        .compareTrueFirst(move1.isAttackMove(), move2.isAttackMove())
                        .compareTrueFirst(move1.isCastlingMove(), move2.isCastlingMove())
                        .compare(move2.getMovedPiece().getPieceValue(), move1.getMovedPiece().getPieceValue())
                        .result();
            }
        };

        abstract Collection<Move> sort(Collection<Move> moves);
    }
}
