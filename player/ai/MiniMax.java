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
                int score = board.currentPlayer().getColor().isWhite() ?
                                minimize(transition.getTransitionBoard(), searchDepth - 1, maxScore, minScore) :
                                maximize(transition.getTransitionBoard(), searchDepth - 1, maxScore, minScore);
                
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

    public int maximize(Board board, int depth, int alpha, int beta) {
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

        table.put(key, new Entry(max, depth, TranspositionTable.EXACT));
        return max;
    }

    public int minimize(Board board, int depth, int alpha, int beta) {
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
        table.put(key, new Entry(min, depth, TranspositionTable.EXACT));
        return min;
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
