package pieces;

import java.util.List;

import board.Board;
import board.Move;

public abstract class Piece {

    protected PieceType pieceType;

    protected int piecePosition;

    protected Color pieceColor;

    protected boolean isFirstMove;

    public Piece(PieceType pieceType, int position, Color color) {
        piecePosition = position;
        pieceColor = color;
        isFirstMove = true;
    }

    public boolean isFirstMove() {
        return isFirstMove;
    }

    public PieceType getPieceType() {
        return pieceType;
    }

    public int getPosition() {
        return piecePosition;
    }

    public Color getColor() {
        return pieceColor;
    }

    public abstract List<Move> calculateLegalMoves(Board board);

    public enum PieceType {
        PAWN("P"),
        ROOK("R"),
        KNIGHT("K"),
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
