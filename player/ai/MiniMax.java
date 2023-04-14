package player.ai;

import board.Board;
import board.Move;
import board.MoveTransition;
import player.ai.TranspositionTable.Entry;

public class MiniMax {

    private BoardEvaluator evaluator;
    private int searchDepth;
    private long boardsEvaluated;
    private TranspositionTable table;

    public MiniMax(int searchDepth) {
        this.evaluator = new BoardEvaluator();
        this.searchDepth = searchDepth;
        this.boardsEvaluated = 0;
        this.table = new TranspositionTable(0);
        Zobrist.generateZobristKeys();
    }

    public Move getBestMove(Board board) {
        long startTIme = System.currentTimeMillis();
        System.out.printf("%s is THINKING [depth = %s]%n", board.currentPlayer(), searchDepth);
        
        Move bestMove = new Move.NullMove();
        int maxScore = Integer.MIN_VALUE;
        int minScore = Integer.MAX_VALUE;
        for (Move move : board.currentPlayer().getLegalMoves()) {
            MoveTransition transition = board.currentPlayer().makeMove(move);
            if (transition.getMoveStatus().isDone()) {
                boolean isMaximizingPlayer = board.currentPlayer().getColor().isWhite();
                int score = minimax(transition.getTransitionBoard(), searchDepth - 1, maxScore, minScore, isMaximizingPlayer);
        
                if (isMaximizingPlayer && score > maxScore) {
                    maxScore = score;
                    bestMove = move;

                } else if (!isMaximizingPlayer && score < minScore) {
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

    public int minimax(Board board, int depth, int alpha, int beta, boolean maximize) {
        long key = Zobrist.hash(board);

        // check if board already evaluated at deeper or same depth
        if (table.contains(key) && table.get(key).getDepth() >= depth) {
            Entry entry = table.get(key);
            if (entry.getFlag() == TranspositionTable.EXACT) {
                return entry.getScore();
            } else if (entry.getFlag() == TranspositionTable.LOWER) {
                alpha =  Math.max(alpha, entry.getScore());
            } else {
                beta = Math.min(beta, entry.getScore());
            }
            
            // prune
            if (alpha >= beta) {
                return entry.getScore();
            }
        }

        if (depth == 0 || gameOver(board)) {
            int score = evaluator.evaluate(board, depth);
            table.put(key, new Entry(score, depth, TranspositionTable.EXACT));
            boardsEvaluated++;
            return score;
        }

        if (maximize) {
            int max = alpha;
            for (Move move : board.currentPlayer().getLegalMoves()) {
                MoveTransition moveTransition = board.currentPlayer().makeMove(move);
                if (moveTransition.getMoveStatus().isDone()) {
                    max = Math.max(max, minimax(moveTransition.getTransitionBoard(), depth - 1, max, beta, false));
                    if (beta <= max) {
                        break;
                    }
                }
            }
            table.put(key, new Entry(max, depth, TranspositionTable.EXACT));
            return max;
        } else {
            int min = beta;
            for (Move move : board.currentPlayer().getLegalMoves()) {
                MoveTransition moveTransition = board.currentPlayer().makeMove(move);
                if (moveTransition.getMoveStatus().isDone()) {
                    min = Math.min(min, minimax(moveTransition.getTransitionBoard(), depth - 1, alpha, min, true));
                    if (min <= alpha) {
                        break;
                    }
                }
            }
            table.put(key, new Entry(min, depth, TranspositionTable.EXACT));
            return min;
        }
    }

   
    private int quiescene(Board board, int alpha, int beta) {
        int standingPat = (board.currentPlayer().getColor().isWhite() ? 1 : -1) * 
                           evaluator.evaluate(board, 0);
        


        return 0;
    }

    private static boolean gameOver(Board board) {
        return board.currentPlayer().isInCheckMate() ||
               board.currentPlayer().isInStaleMate();
    }

}
