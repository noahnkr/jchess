package board;

import board.Board.BoardBuilder;
import pieces.Piece;

public abstract class Move {

    protected Board board;

    protected Piece movedPiece;

    protected int destinationCoordinate;

    public Move(Board board, Piece movedPiece, int destinationCoordinate) {
        this.board = board;
        this.movedPiece = movedPiece;
        this.destinationCoordinate = destinationCoordinate;
    }

    public int getDestinationCoordinate() {
        return this.destinationCoordinate;
    }

    public abstract Board execute();

    public static class BasicMove extends Move {

        public BasicMove(Board board, Piece movedPiece, int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }

        @Override
        public Board execute() {
            BoardBuilder builder = new BoardBuilder();

            for (Piece piece : this.board.currentPlayer().getActivePieces()) {
                if (!this.movedPiece.equals(piece)) {
                    builder.setPiece(piece);
                }
            }

            for (Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()) {
                builder.setPiece(piece);
            }

            builder.setPiece(null);
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getColor());
            return builder.build();
        }

    }

    public static class AttackMove extends Move {

        private Piece attackedPiece; 

        public AttackMove(Board board, Piece movedPiece, Piece attackedPiece, int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
            this.attackedPiece = attackedPiece;
        }

        @Override
        public Board execute() {
            return null;
        }
        
    }
    
}
