package board;

import pieces.*;

public abstract class Move {

    protected Board board;

    protected Piece movedPiece;

    protected int destinationCoordinate;

    public Move(Board board, Piece movedPiece, int destinationCoordinate) {
        this.board = board;
        this.movedPiece = movedPiece;
        this.destinationCoordinate = destinationCoordinate;
    }

    public static class BasicMove extends Move {

        public BasicMove(Board board, Piece movedPiece, int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }

    }

    public static class AttackMove extends Move {

        private Piece attackedPiece; 

        public AttackMove(Board board, Piece movedPiece, Piece attackedPiece, int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
            this.attackedPiece = attackedPiece;
        }
        
    }
    
}
