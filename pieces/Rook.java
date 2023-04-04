package pieces;

import java.util.ArrayList;
import java.util.List;

import board.Board;
import board.Move;
import board.Tile;
import board.Move.AttackMove;
import board.Move.BasicMove;

public class Rook extends Piece {

    private static final int[] POSSIBLE_MOVE_OFFSET = { -8, -1, 1, 8 };

    public Rook(int position, Color color) {
        super(position, color);
    }

    @Override
    public String toString() {
        return PieceType.ROOK.toString();
    }

    @Override
    public List<Move> calculateLegalMoves(Board board) {
        List<Move> legalMoves = new ArrayList<>();

        for (int currentMoveOffset : POSSIBLE_MOVE_OFFSET) {
            int destinationCoordinate = this.piecePosition;
            while (Board.isValidTileCoordinate(destinationCoordinate)) {
                if (isFirstColumnExclusion(destinationCoordinate, currentMoveOffset) ||
                        isEighthColumnExclusion(destinationCoordinate, currentMoveOffset)) {
                    break;
                }

                destinationCoordinate += currentMoveOffset;
                if (Board.isValidTileCoordinate(destinationCoordinate)) {
                    Tile destinationTile = board.getTile(destinationCoordinate);
                    if (!destinationTile.isOccupied()) {
                        legalMoves.add(new BasicMove(board, this, destinationCoordinate));

                    } else {
                        Piece occupiedPiece = destinationTile.getPiece();
                        if (this.pieceColor != occupiedPiece.pieceColor) {
                            legalMoves.add(new AttackMove(board, this, occupiedPiece, destinationCoordinate));
                        }
                        break;
                    }
                }
            }
        }

        return legalMoves;
    }

    /* Exceptions to knight move offset when its position is on the first or eighth column. */

    private static boolean isFirstColumnExclusion(int position, int offset) {
        return Board.FIRST_COLUMN[position] && offset == -1;
    }

    private static boolean isEighthColumnExclusion(int position, int offset) {
        return Board.EIGHTH_COLUMN[position] && offset == 1;
    }
    
}
