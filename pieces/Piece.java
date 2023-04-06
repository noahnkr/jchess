package pieces;

import java.util.List;

import board.Board;
import board.Move;

public abstract class Piece {

    protected PieceType pieceType;

    protected int piecePosition;

    protected Color pieceColor;

    protected boolean isFirstMove;

    private int cachedHashCode;

    public Piece(PieceType pieceType, int position, Color color, boolean isFirstMove) {
        this.pieceType = pieceType;
        this.piecePosition = position;
        this.pieceColor = color;
        this.isFirstMove = isFirstMove;
        this.cachedHashCode = computeHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof Piece)) {
            return false;
        }

        Piece otherPiece = (Piece) other;
        
        return piecePosition == otherPiece.getPosition() && pieceType == otherPiece.getPieceType() &&
               pieceColor == otherPiece.getColor() && isFirstMove == otherPiece.isFirstMove();
    }

    @Override
    public int hashCode() {
        return cachedHashCode;
    }

    public int computeHashCode() {
        int result = pieceType.hashCode();
        result = 31 * result + pieceColor.hashCode();
        result = 31 * result + piecePosition;
        result = 31 * result + (isFirstMove ? 1 : 0);
        return result;
    }

    public boolean isFirstMove() {
        return this.isFirstMove;
    }

    public PieceType getPieceType() {
        return this.pieceType;
    }

    public int getPosition() {
        return this.piecePosition;
    }

    public Color getColor() {
        return this.pieceColor;
    }

    public abstract List<Move> calculateLegalMoves(Board board);

    public abstract Piece movePiece(Move move);

    public enum PieceType {
        PAWN("P"),
        ROOK("R"),
        KNIGHT("N"),
        BISHOP("B"),
        QUEEN("Q"),
        KING("K");

        private String pieceName;

        PieceType(String pieceName) {
            this.pieceName = pieceName;
        }

        @Override
        public String toString() {
            return this.pieceName;
        }     
    }
    
}
