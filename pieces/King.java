package pieces;

import java.util.ArrayList;
import java.util.List;

import board.Board;
import board.Move;
import board.Tile;
import board.Move.BasicAttackMove;
import board.Move.BasicMove;

public class King extends Piece {

    private static final int[] POSSIBLE_MOVE_OFFSET = { -9, -8, -7, -1, 1, 7, 8, 9 };

    private boolean isCastled;
    private boolean kingSideCastleCapable;
    private boolean queenSideCastleCapable;

    public King(int position, Color color, boolean kingSideCastleCapable, boolean queenSideCastleCapable) {
        super(PieceType.KING, position, color, true);
        this.isCastled = false;
        this.kingSideCastleCapable = kingSideCastleCapable;
        this.queenSideCastleCapable = queenSideCastleCapable;
    }

    public King(int position, Color color, boolean isFirstMove, boolean isCastled, boolean kingSideCastleCapable, boolean queenSideCastleCapable) {
        super(PieceType.KING, position, color, isFirstMove);
        this.isCastled = isCastled;
        this.kingSideCastleCapable = kingSideCastleCapable;
        this.queenSideCastleCapable = queenSideCastleCapable;

    }

    @Override
    public String toString() {
        return PieceType.KING.toString();
    }

    @Override
    public List<Move> calculateLegalMoves(Board board) {
        List<Move> legalMoves = new ArrayList<>();

        for (int currentMoveOffset : POSSIBLE_MOVE_OFFSET) {
            int destinationCoordinate = this.piecePosition + currentMoveOffset;

            if (isFirstColumnExclusion(this.piecePosition, currentMoveOffset) ||
                isEighthColumnExclusion(this.piecePosition, currentMoveOffset)) {
                continue;
            }

            if (Board.isValidTileCoordinate(destinationCoordinate)) {
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

    public boolean isCastled() {
        return this.isCastled;
    }

    public boolean isKingSideCastleCapable() {
        return this.kingSideCastleCapable;

    }

    public boolean isQueenSideCastleCapable() {
        return this.queenSideCastleCapable;

    }


    private static boolean isFirstColumnExclusion(int position, int offset) {
        return Board.FIRST_COLUMN[position] && (offset == -9 || offset == -1 || offset == 7);
    }

    private static boolean isEighthColumnExclusion(int position, int offset) {
        return Board.EIGHTH_COLUMN[position] && (offset == -7 || offset == 1 || offset == 9);
    }

    @Override
    public King movePiece(Move move) {
        return new King(move.getDestinationCoordinate(), move.getMovedPiece().getColor(), false, move.isCastlingMove(), false, false);
    }
    
}
