package pieces;

import java.util.ArrayList;
import java.util.List;

import board.Board;
import board.Tile;
import board.Move.AttackMove;
import board.Move.BasicAttackMove;
import board.Move.BasicMove;
import board.Move;


public class Knight extends Piece {

    private static final int[] POSSIBLE_MOVE_OFFSET = { -17, -15, -10, -6, 6, 10, 15, 17 };

    public Knight(int position, Color color) {
        super(PieceType.KNIGHT, position, color, true);
    }

    public Knight(int position, Color color, boolean isFirstMove) {
        super(PieceType.KNIGHT, position, color, isFirstMove);
    }

    @Override
    public String toString() {
        return PieceType.KNIGHT.toString();
    }

    @Override
    public List<Move> calculateLegalMoves(Board board) {
        List<Move> legalMoves = new ArrayList<>();

        for (int currentMoveOffset : POSSIBLE_MOVE_OFFSET) {
            int destinationCoordinate = this.piecePosition + currentMoveOffset;

            if (Board.isValidTileCoordinate(destinationCoordinate)) {

                if (isFirstColumnExclusion(this.piecePosition, currentMoveOffset) ||
                        isSecondColumnExclusion(this.piecePosition, currentMoveOffset) ||
                        isSeventhColumnExclusion(this.piecePosition, currentMoveOffset) ||
                        isEighthColumnExclusion(this.piecePosition, currentMoveOffset)) {
                    continue;
                }

                Tile destinationTile = board.getTile(destinationCoordinate);

                if (!destinationTile.isOccupied()) {
                    legalMoves.add(new BasicMove(board, this, destinationCoordinate));

                } else {
                    Piece occupiedPiece = destinationTile.getPiece();

                    if (this.pieceColor != occupiedPiece.pieceColor) {
                        legalMoves.add(new BasicAttackMove(board, this, occupiedPiece, destinationCoordinate));
                    }
                }
            }
        }

        return legalMoves;
    }


    /* Exceptions to knight move offset when its position is on the outer two columns. */

    private static boolean isFirstColumnExclusion(int position, int offset) {
        return Board.FIRST_COLUMN[position] && (offset == -17 || offset == -10
                                            || offset == 6 || offset == 15);
    }

    private static boolean isSecondColumnExclusion(int position, int offset) {
        return Board.SECOND_COLUMN[position] && (offset == -10 || offset == 6);                                    
    }

    private static boolean isSeventhColumnExclusion(int position, int offset) {
        return Board.SEVENTH_COLUMN[position] && (offset == -6 || offset == 10);                                  
    }

    private static boolean isEighthColumnExclusion(int position, int offset) {
        return Board.EIGHTH_COLUMN[position] && (offset == -15 || offset == -6
                                             || offset == 10 || offset == 17);
    }

    @Override
    public Knight movePiece(Move move) {
        return new Knight(move.getDestinationCoordinate(), move.getMovedPiece().getColor());
    }
    
}
