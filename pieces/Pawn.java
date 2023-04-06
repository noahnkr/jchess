package pieces;

import java.util.ArrayList;
import java.util.List;

import board.Board;
import board.Move;
import board.Tile;
import board.Move.BasicMove;
import board.Move.PawnAttackMove;
import board.Move.PawnJump;
import board.Move.PawnMove;

public class Pawn extends Piece {

    private static final int[] POSSIBLE_MOVE_OFFSET = { 7, 8, 9, 16 };

    public Pawn(int position, Color color) {
        super(PieceType.PAWN, position, color, true);
    }

    public Pawn(int position, Color color, boolean isFirstMove) {
        super(PieceType.PAWN, position, color, isFirstMove);
    }

    @Override
    public String toString() {
        return PieceType.PAWN.toString();
    }

    @Override
    public List<Move> calculateLegalMoves(Board board) {
        List<Move> legalMoves = new ArrayList<>();

        for (int currentMoveOffset : POSSIBLE_MOVE_OFFSET) {
            int destinationCoordinate = this.piecePosition + (currentMoveOffset * this.pieceColor.getDirection());
            Tile destinationTile = board.getTile(destinationCoordinate);

            if (!Board.isValidTileCoordinate(destinationCoordinate)) {
                continue;
            }

            // Basic Pawn Move
            if (currentMoveOffset == 8 && !board.getTile(destinationCoordinate).isOccupied()) {
                legalMoves.add(new PawnMove(board, this, destinationCoordinate));
            // Pawn Jump
            } else if (currentMoveOffset == 16 && this.isFirstMove() &&
                      ((Board.SECOND_ROW[this.piecePosition] && this.pieceColor.isBlack()) ||
                      (Board.SEVENTH_ROW[this.piecePosition] && this.pieceColor.isWhite()))) {
                int behindDestinationCoordinate = this.piecePosition + (this.pieceColor.getDirection() * 8);
                if (!board.getTile(behindDestinationCoordinate).isOccupied() &&
                    !board.getTile(destinationCoordinate).isOccupied()) {
                    
                    legalMoves.add(new PawnJump(board, this, destinationCoordinate));
                }
            } else if (currentMoveOffset == 7 && destinationTile.isOccupied() &&
                      !((Board.EIGHTH_COLUMN[this.piecePosition] && this.pieceColor.isWhite()) ||
                      (Board.FIRST_COLUMN[this.piecePosition] && this.pieceColor.isBlack()))) {
                    if (this.pieceColor != destinationTile.getPiece().pieceColor) {
                        legalMoves.add(new PawnAttackMove(board, this, destinationTile.getPiece(), destinationCoordinate));
                    }
                    
            } else if (currentMoveOffset == 9 && destinationTile.isOccupied() &&
                      !((Board.FIRST_COLUMN[this.piecePosition] && this.pieceColor.isWhite()) || 
                      (Board.EIGHTH_COLUMN[this.piecePosition] && this.pieceColor.isBlack()))) {
                    if (this.pieceColor != destinationTile.getPiece().pieceColor) {
                        legalMoves.add(new PawnAttackMove(board, this, destinationTile.getPiece(), destinationCoordinate));
                    }
            }
        }
        
        return legalMoves;
    }

    @Override
    public Pawn movePiece(Move move) {
        return new Pawn(move.getDestinationCoordinate(), move.getMovedPiece().getColor());
    }
    
}
