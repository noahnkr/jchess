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

    public int getPieceValue() {
        return this.pieceType.getValue();
    }

    public abstract List<Move> calculateLegalMoves(Board board);

    public abstract Piece movePiece(Move move);

    public enum PieceType {
        PAWN("P", 100),
        KNIGHT("N", 320),
        BISHOP("B", 330),
        ROOK("R", 500),
        QUEEN("Q", 900),
        KING("K", 20000);

        private String pieceName;

        private int pieceValue;

        PieceType(String pieceName, int pieceValue) {
            this.pieceName = pieceName;
            this.pieceValue = pieceValue;
        }

        @Override
        public String toString() {
            return this.pieceName;
        }

        public int getValue() {
            return this.pieceValue;
        }

    }
    
}
