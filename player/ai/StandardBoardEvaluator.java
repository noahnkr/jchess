package player.ai;

import board.Board;
import pieces.Piece;
import player.Player;

public class StandardBoardEvaluator implements BoardEvaluator {

    private static final int CHECK_BONUS = 50;
    private static final int CHECK_MATE_BONUS = 500;
    private static final int DEPTH_BONUS = 100;
    private static final int CASTLE_BONUS = 60;

    private static final int PIECE_VALUE_WEIGHT = 1;
    private static final int MOBILITY_WEIGHT = 1;
 

    @Override
    public int evaluate(Board board, int depth) {
        return scorePlayer(board, board.whitePlayer(), depth) -
               scorePlayer(board, board.blackPlayer(), depth);
    }

    public int scorePlayer(Board board, Player player, int depth) {
        return (pieceValue(player) * PIECE_VALUE_WEIGHT) + 
               (mobility(player) * MOBILITY_WEIGHT) +
                check(player) + 
                checkmate(player, depth) + 
                castled(player);
    }

    private static int pieceValue(Player player) {
        int pieceValueScore = 0;
        for (Piece piece : player.getActivePieces()) {
            pieceValueScore += piece.getPieceValue();
        }
        return pieceValueScore;
    }

    private static int mobility(Player player) {
        return player.getLegalMoves().size();
    }

    private static int check(Player player) {
        return player.getOpponent().isInCheck() ? CHECK_BONUS : 0;
    }

    private static int checkmate(Player player, int depth) {
        return player.getOpponent().isInCheckMate() ? CHECK_MATE_BONUS * depthBonus(depth) : 0;
    }

    private static int depthBonus(int depth) {
        return depth == 0 ? 1 : DEPTH_BONUS * depth;
    }

    private static int castled(Player player) {
        return player.isCastled() ? CASTLE_BONUS : 0;
    }

}
