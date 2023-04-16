package player.ai;

import java.util.Random;

import board.Board;
import pieces.Pawn;
import pieces.Piece;

public class Zobrist {

    private static long[][][] piecesKey;
    private static long[] castlingKey;
    private static long[] enPassantKey;
    private static long blackMoveKey;

    public static void generateZobristKeys() {
        Random rand = new Random(1234567890L);
        piecesKey = new long[2][6][64];
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 6; j++) {
                for (int k = 0; k < 64; k++) {
                    piecesKey[i][j][k] = rand.nextLong();
                }
            }
        }
        
        castlingKey = new long[4];
        for (int i = 0; i < 4; i++) {
            castlingKey[i] = rand.nextLong();
        }

        enPassantKey = new long[8];
        for (int i = 0; i < 8; i++) {   
            enPassantKey[i] = rand.nextLong();
        }

        blackMoveKey = rand.nextLong();
    }

    public static long hash(Board board) {
        long hashKey = 0L;
        for (int i = 0; i < Board.NUM_TILES; i++) {
            if (board.getTile(i).isOccupied()) {
                Piece piece = board.getTile(i).getPiece();
                switch (piece.getPieceType()) {
                    case PAWN:
                        hashKey ^= piece.getColor().isWhite() ? piecesKey[0][0][i] : piecesKey[1][0][i];
                        break;
                    case KNIGHT:
                        hashKey ^= piece.getColor().isWhite() ? piecesKey[0][1][i] : piecesKey[1][1][i];
                        break;
                    case BISHOP: 
                        hashKey ^= piece.getColor().isWhite() ? piecesKey[0][2][i] : piecesKey[1][2][i];
                        break;
                    case ROOK: 
                        hashKey ^= piece.getColor().isWhite() ? piecesKey[0][3][i] : piecesKey[1][3][i];
                        break;
                    case QUEEN:
                        hashKey ^= piece.getColor().isWhite() ? piecesKey[0][4][i] : piecesKey[1][4][i];
                        break;
                    case KING:
                        hashKey ^= piece.getColor().isWhite() ? piecesKey[0][5][i] : piecesKey[1][5][i];
                        break;
                }
            }
        }

        for (int i = 0; i < 4; i++) {
            if (board.getCastlingRights().get(i)) {
                hashKey ^= castlingKey[i];
            }
        }

        for (int i = 0; i < 8; i++) {
            Pawn enPassantPawn = board.getEnPassantPawn();
            if (enPassantPawn != null) {
                hashKey ^= enPassantKey[enPassantPawn.getPosition() / Board.NUM_TILES_PER_ROW];
            }
        }

        if (board.currentPlayer().getColor().isBlack()) {
            hashKey ^= blackMoveKey;
        }

        return hashKey;       
    }
   
    
}