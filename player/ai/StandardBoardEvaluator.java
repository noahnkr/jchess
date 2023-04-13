package player.ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import board.Board;
import pieces.Piece;
import pieces.Piece.PieceType;
import player.Player;

public class StandardBoardEvaluator implements BoardEvaluator {

    private static final int CHECK_BONUS = 50;
    private static final int CHECK_MATE_BONUS = 1000;
    private static final int DEPTH_BONUS = 100;
    private static final int CASTLE_BONUS = 60;
    private static final int DOUBLE_BISHOP_BONUS = 50;

 

    @Override
    public int evaluate(Board board, int depth) {
        return scorePlayer(board, board.whitePlayer(), depth) -
               scorePlayer(board, board.blackPlayer(), depth);
    }

    public int scorePlayer(Board board, Player player, int depth) {
        return pieceValue(player) + 
               mobility(player) + 
               check(player) +
               checkmate(player, depth) +
               doubleBishopBonus(player) +
               castled(player);
    }

    private static int pieceValue(Player player) {
        int pieceValueScore = 0;
        for (Piece piece : player.getActivePieces()) {
            pieceValueScore += piece.getPieceValue() +
                               pieceSquareBonus(piece, false);
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
        return player.getOpponent().isInCheckMate() ? CHECK_MATE_BONUS + depthBonus(depth) : 0;
    }

    private static int depthBonus(int depth) {
        return depth == 0 ? 1 : DEPTH_BONUS * depth;
    }

    private static int doubleBishopBonus(Player player) {
        int bishopCount = 0;
        for (Piece piece : player.getActivePieces()) {
            if (piece.getPieceType() == PieceType.BISHOP) {
                bishopCount++;
            }
        }
        return bishopCount == 2 ? DOUBLE_BISHOP_BONUS : 0;
    }

    private static int castled(Player player) {
        return player.isCastled() ? CASTLE_BONUS : 0;
    }

    private static int pieceSquareBonus(Piece piece, boolean endgame) {
        List<Integer> pieceSquare;
        switch (piece.getPieceType()) {
            case PAWN:
                pieceSquare = Arrays.asList(
                    0, 0, 0, 0, 0, 0, 0, 0,
                    50, 50, 50, 50, 50, 50, 50, 50,
                    10, 10, 20, 30, 30, 20, 10, 10,
                    5, 5, 10, 25, 25, 10, 5, 5,
                    0, 0, 0, 20, 20, 0, 0, 0,
                    5, -5, -10, 0, 0, -10, -5, 5,
                    5, 10, 10, -20, -20, 10, 10, 5,
                    0, 0, 0, 0, 0, 0, 0, 0
                );
                break;
            case KNIGHT:
                pieceSquare = Arrays.asList(
                    -50, -40, -30, -30, -30, -30, -40, -50,
                    -40, -20, 0, 0, 0, 0, -20, -40,
                    -30, 0, 10, 15, 15, 10, 0, -30,
                    -30, 5, 15, 20, 20, 15, 5, -30,
                    -30, 0, 15, 20, 20, 15, 0, -30,
                    -30, 5, 10, 15, 15, 10, 5, -30,
                    -40, -20, 0, 5, 5, 0, -20, -40,
                    -50, -40, -30, -30, -30, -30, -40, -50
                );
                break;
            case BISHOP:
                pieceSquare = Arrays.asList(
                    -20, -10, -10, -10, -10, -10, -10, -20,
                    -10, 0, 0, 0, 0, 0, 0, -10,
                    -10, 0, 5, 10, 10, 5, 0, -10,
                    -10, 5, 5, 10, 10, 5, 5, -10,
                    -10, 0, 10, 10, 10, 10, 0, -10,
                    -10, 10, 10, 10, 10, 10, 10, -10,
                    -10, 5, 0, 0, 0, 0, 5, -10,
                    -20, -10, -10, -10, -10, -10, -10, -20
                );
                break;
            case ROOK: 
                pieceSquare = Arrays.asList(
                    0, 0, 0, 0, 0, 0, 0, 0,
                    5, 10, 10, 10, 10, 10, 10, 5,
                    -5, 0, 0, 0, 0, 0, 0, -5,
                    -5, 0, 0, 0, 0, 0, 0, -5,
                    -5, 0, 0, 0, 0, 0, 0, -5,
                    -5, 0, 0, 0, 0, 0, 0, -5,
                    -5, 0, 0, 0, 0, 0, 0, -5,
                    0, 0, 0, 5, 5, 0, 0, 0
                );
                break;
            case QUEEN: 
                pieceSquare = Arrays.asList(
                    -20, -10, -10, -5, -5, -10, -10, -20,
                    -10, 0, 0, 0, 0, 0, 0, -10,
                    -10, 0, 5, 5, 5, 5, 0, -10,
                    -5, 0, 5, 5, 5, 5, 0, -5,
                    0, 0, 5, 5, 5, 5, 0, -5,
                    -10, 5, 5, 5, 5, 5, 0, -10,
                    -10, 0, 5, 0, 0, 0, 0, -10,
                    -20, -10, -10, -5, -5, -10, -10, -20
                );
                break;
            case KING: 
                if (endgame) {
                    pieceSquare = Arrays.asList(
                        -50, -40, -30, -20, -20, -30, -40, -50,
                        -30, -20, -10, 0, 0, -10, -20, -30,
                        -30, -10, 20, 30, 30, 20, -10, -30,
                        -30, -10, 30, 40, 40, 30, -10, -30,
                        -30, -10, 30, 40, 40, 30, -10, -30,
                        -30, -10, 20, 30, 30, 20, -10, -30,
                        -30, -30, 0, 0, 0, 0, -30, -30,
                        -50, -30, -30, -30, -30, -30, -30, -50
                    );
                } else {
                    pieceSquare = Arrays.asList(
                        -30, -40, -40, -50, -50, -40, -40, -30,
                        -30, -40, -40, -50, -50, -40, -40, -30,
                        -30, -40, -40, -50, -50, -40, -40, -30,
                        -30, -40, -40, -50, -50, -40, -40, -30,
                        -20, -30, -30, -40, -40, -30, -30, -20,
                        -10, -20, -20, -20, -20, -20, -20, -10,
                        20, 20, 0, 0, 0, 0, 20, 20,
                        20, 30, 10, 0, 0, 10, 30, 20
                    );
                }
                break;
            default: 
                pieceSquare = new ArrayList<>(Board.NUM_TILES);
                break;
        }

        if (piece.getColor().isBlack()) {
            Collections.reverse(pieceSquare);
        }

        return pieceSquare.get(piece.getPosition());
    }


}
