package pieces;

import java.util.ArrayList;
import java.util.List;

import board.Board;
import board.Move;
import board.Tile;
import board.Move.AttackMove;
import board.Move.BasicAttackMove;
import board.Move.BasicMove;

public class Queen extends Piece {

    private static final int[] POSSIBLE_MOVE_OFFSET = { -9, -8, -7, -1, 1, 7, 8, 9 };

    public Queen(int position, Color color) {
        super(PieceType.QUEEN, position, color, true);
    }

    public Queen(int position, Color color, boolean isFirstMove) {
        super(PieceType.QUEEN, position, color, isFirstMove);
    }

    @Override
    public String toString() {
        return PieceType.QUEEN.toString();
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
                            legalMoves.add(new BasicAttackMove(board, this, occupiedPiece, destinationCoordinate));
                        }
                        break;
                    }
                }
            }
        }

        return legalMoves;
    }

    /* Exceptions to queen move offset when its position is on the first or eighth column. */

    private static boolean isFirstColumnExclusion(int position, int offset) {
        return Board.FIRST_COLUMN[position] && (offset == -9 || offset == -1 || offset == 7);
    }

    private static boolean isEighthColumnExclusion(int position, int offset) {
        return Board.EIGHTH_COLUMN[position] && (offset == -7 || offset == 1 || offset == 9);
    }

    @Override
    public Queen movePiece(Move move) {
        return new Queen(move.getDestinationCoordinate(), move.getMovedPiece().getColor());
    }
    
}
