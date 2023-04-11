package board;

import board.Board.BoardBuilder;
import pieces.Pawn;
import pieces.Piece;
import pieces.Rook;

public abstract class Move {

    protected Board board;

    protected Piece movedPiece;

    protected int destinationCoordinate;

    protected boolean isFirstMove;


    public static final Move NULL_MOVE = new NullMove();

    public Move(Board board, Piece movedPiece, int destinationCoordinate) {
        this.board = board;
        this.movedPiece = movedPiece;
        this.destinationCoordinate = destinationCoordinate;
        this.isFirstMove = movedPiece.isFirstMove();
    }

    public Move(Board board, int destinationCoordinate) {
        this.board = board;
        this.destinationCoordinate = destinationCoordinate;
        this.movedPiece = null;
        this.isFirstMove = false;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + this.destinationCoordinate;
        result = 31 * result + this.movedPiece.hashCode();
        result = 31 * result + this.movedPiece.getPosition(); 
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof Move)) {
            return false;
        }

        Move otherMove = (Move) other;
        return getCurrentCoordinate() == otherMove.getCurrentCoordinate() &&
               destinationCoordinate == otherMove.getDestinationCoordinate() &&
               movedPiece.equals(otherMove.getMovedPiece());
    }

    public String disambiguationFile() {
        for(Move move : this.board.currentPlayer().getLegalMoves()) {
            if(move.getDestinationCoordinate() == this.destinationCoordinate && !this.equals(move) &&
               this.movedPiece.getPieceType().equals(move.getMovedPiece().getPieceType())) {
                return Board.getPositionAtCoordinate(this.movedPiece.getPosition()).substring(0, 1);
            }
        }
        return "";
    }

    public int getCurrentCoordinate() {
        return this.movedPiece.getPosition();
    }

    public int getDestinationCoordinate() {
        return this.destinationCoordinate;
    }

    public Piece getMovedPiece() {
        return movedPiece;
    }

    public Piece getAttackedPiece() {
        return null;
    }

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

        builder.setPiece(this.movedPiece.movePiece(this));
        builder.setMoveMaker(this.board.currentPlayer().getOpponent().getColor());
        return builder.build();
    }

    public abstract boolean isAttackMove();

    public static class BasicMove extends Move {

        public BasicMove(Board board, Piece movedPiece, int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }

        @Override
        public boolean equals(Object other) {
            return this == other || other instanceof BasicMove && super.equals(other);
        }

        @Override
        public String toString() {
            return movedPiece.getPieceType().toString() + disambiguationFile() +
                   Board.getPositionAtCoordinate(this.destinationCoordinate);
        }

        @Override
        public boolean isAttackMove() {
            return false;
        }
    }

    public static class AttackMove extends Move {

        private Piece attackedPiece; 

        public AttackMove(Board board, Piece movedPiece, Piece attackedPiece, int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
            this.attackedPiece = attackedPiece;
        }

        @Override
        public int hashCode() {
            return this.attackedPiece.hashCode() + super.hashCode();
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }

            if (!(other instanceof AttackMove)) {
                return false;
            }

            AttackMove otherAttackMove = (AttackMove) other;
            return super.equals(otherAttackMove) && getAttackedPiece().equals(otherAttackMove.getAttackedPiece());
        }

        @Override
        public String toString() {
            return movedPiece.getPieceType().toString() + disambiguationFile() + "x" + 
                   Board.getPositionAtCoordinate(this.destinationCoordinate);
        }
  

        public Piece getAttackedPiece() {
            return this.attackedPiece;
        }

        @Override
        public boolean isAttackMove() {
            return true;
        }
    }

    public static class BasicAttackMove extends AttackMove {

        public BasicAttackMove(Board board, Piece movedPiece, Piece attackedPiece, int destinationCoordinate) {
            super(board, movedPiece, attackedPiece, destinationCoordinate);
        }

        @Override
        public boolean equals(Object other) {
            return this == other || other instanceof BasicAttackMove && super.equals(other);
        }
        
    }

    public static class PawnMove extends Move {

        public PawnMove(Board board, Piece movedPiece, int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }

        @Override
        public boolean isAttackMove() {
            return false;
        }

        @Override
        public String toString() {
            return Board.getPositionAtCoordinate(this.destinationCoordinate);
        }
        
    }

    public static class PawnPromotion extends PawnMove {

        Pawn promotedPawn;

        public PawnPromotion(Board board, Piece movedPiece, int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
            this.promotedPawn = (Pawn) movedPiece;
        }

        @Override
        public Board execute() {
            BoardBuilder builder = new BoardBuilder();

            for (Piece piece : board.currentPlayer().getActivePieces()) {
                if (!this.promotedPawn.equals(piece)) {
                    builder.setPiece(piece);
                }
            }

            for (Piece piece : board.currentPlayer().getOpponent().getActivePieces()) {
                builder.setPiece(piece);
            }

            builder.setPiece(this.promotedPawn.getPromotedPiece().movePiece(this));
            builder.setMoveMaker(board.currentPlayer().getOpponent().getColor());
            return builder.build();
            
        }

        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof PawnPromotion && (super.equals(other));
        }

        @Override
        public boolean isAttackMove() {
            return false;
        }

        @Override
        public String toString() {
            return Board.getPositionAtCoordinate(destinationCoordinate) + "=Q";
        }
        
    }

    public static class PawnAttackPromotion extends PawnAttackMove {

        Pawn promotedPawn;

        public PawnAttackPromotion(Board board, Piece movedPiece, Piece attackedPiece, int destinationCoordinate) {
            super(board, movedPiece, attackedPiece, destinationCoordinate);
            this.promotedPawn = (Pawn) movedPiece;
        }

        @Override
        public Board execute() {
            BoardBuilder builder = new BoardBuilder();

            for (Piece piece : board.currentPlayer().getActivePieces()) {
                if (!this.promotedPawn.equals(piece)) {
                    builder.setPiece(piece);
                }
            }

            for (Piece piece : board.currentPlayer().getOpponent().getActivePieces()) {
                if (!super.getAttackedPiece().equals(piece)) {
                    builder.setPiece(piece);
                }
                
            }

            builder.setPiece(this.promotedPawn.getPromotedPiece().movePiece(this));
            builder.setMoveMaker(board.currentPlayer().getOpponent().getColor());
            return builder.build();
            
        }

        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof PawnAttackPromotion && (super.equals(other));
        }

        @Override
        public boolean isAttackMove() {
            return true;
        }

        @Override
        public Piece getAttackedPiece() {
            return super.getAttackedPiece();
        }

        @Override
        public String toString() {
            return Board.getPositionAtCoordinate(destinationCoordinate) + "x=Q";
        }


        
    }

    public static class PawnJump extends Move {

        public PawnJump(Board board, Piece movedPiece, int destinationCoordinate) {
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

            Pawn movedPawn = (Pawn) this.movedPiece.movePiece(this);
            builder.setPiece(movedPawn);
            builder.setEnPassantPawn(movedPawn);
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getColor());
            return builder.build();
        }

        @Override
        public boolean isAttackMove() {
            return false;
        }

        @Override
        public String toString() {
            return Board.getPositionAtCoordinate(this.destinationCoordinate);
        }
        
    }

    public static class PawnAttackMove extends AttackMove {

        public PawnAttackMove(Board board, Piece movedPiece, Piece attackedPiece, int destinationCoordinate) {
            super(board, movedPiece, attackedPiece, destinationCoordinate);
        }

        @Override
        public boolean equals(Object other) {
            return this == other || other instanceof PawnAttackMove && super.equals(other);
        }

        @Override
        public boolean isAttackMove() {
            return true;
        }

        @Override
        public String toString() {
            return Board.getPositionAtCoordinate(getCurrentCoordinate()).substring(0, 1) + "x" + 
                   Board.getPositionAtCoordinate(this.destinationCoordinate);
        }

    }

    public static class PawnEnPassantAttackMove extends PawnAttackMove {

        public PawnEnPassantAttackMove(Board board, Piece movedPiece, Piece attackedPiece, int destinationCoordinate) {
            super(board, movedPiece, attackedPiece, destinationCoordinate);
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
                if (!getAttackedPiece().equals(piece)) {
                    builder.setPiece(piece);
                }
            }

            Pawn movedPawn = (Pawn) this.movedPiece.movePiece(this);
            builder.setPiece(movedPawn);
            builder.setEnPassantPawn(movedPawn);
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getColor());
            return builder.build();
        }

        @Override
        public boolean isAttackMove() {
            return true;
        }

        @Override
        public boolean equals(Object other) {
            return this == other || other instanceof PawnEnPassantAttackMove && super.equals(other);
        }
        

    }

    public static abstract class CastleMove extends Move {

        protected Rook castleRook;
        protected int castleRookStart;
        protected int castleRookDestination;

        public CastleMove(Board board, Piece movedPiece, int destinationCoordinate,
                          Rook castleRook, int castleRookStart, int castleRookDestination) {
            super(board, movedPiece, destinationCoordinate);
            this.castleRook = castleRook;
            this.castleRookStart = castleRookStart;
            this.castleRookDestination = castleRookDestination;
        }

        public Rook getCastleRook() {
            return this.castleRook;
        }

        public boolean isCastlingMove() {
            return true;
        }

        @Override
        public Board execute() {
            BoardBuilder builder = new BoardBuilder();
            for (Piece piece : this.board.currentPlayer().getActivePieces()) {
                if (!this.movedPiece.equals(piece) && !this.castleRook.equals(piece)) {
                    builder.setPiece(piece);
                }
            }

            for (Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()) {
                builder.setPiece(piece);
            }

            builder.setPiece(this.movedPiece.movePiece(this));
            builder.setPiece(new Rook(this.castleRookDestination, this.castleRook.getColor()));
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getColor());
            return builder.build();
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + this.castleRook.hashCode();
            result = 31 * result * this.castleRookDestination;
            return result;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }

            if (!(other instanceof CastleMove)) {
                return false;
            }

            CastleMove otherCastleMove = (CastleMove) other;
            return super.equals(otherCastleMove) && this.castleRook.equals(otherCastleMove.getCastleRook());
        }



        @Override
        public boolean isAttackMove() {
            return false;
        }

    }

    public static class KingSideCastleMove extends CastleMove {

        public KingSideCastleMove(Board board, Piece movedPiece, int destinationCoordinate,
                                  Rook castleRook, int castleRookStart, int castleRookDestination) {
            super(board, movedPiece, destinationCoordinate, castleRook, castleRookStart, castleRookDestination);
        }

        @Override
        public boolean equals(Object other) {
            return this == other || other instanceof KingSideCastleMove && super.equals(other);
        }

        @Override
        public String toString() {
            return "O-O";
        }

        @Override
        public boolean isAttackMove() {
            return false;
        }
        
    }

    public static class QueenSideCastleMove extends CastleMove {

        public QueenSideCastleMove(Board board, Piece movedPiece, int destinationCoordinate,
                                   Rook castleRook, int castleRookStart, int castleRookDestination) {
            super(board, movedPiece, destinationCoordinate, castleRook, castleRookStart, castleRookDestination);
        }

        @Override
        public boolean equals(Object other) {
            return this == other || other instanceof QueenSideCastleMove && super.equals(other);
        }

        @Override
        public String toString() {
            return "O-O-O";
        }

        @Override
        public boolean isAttackMove() {
            return false;
        }

    }

    public static class NullMove extends Move {

        public NullMove() {
            super(null, -1);
        }

        @Override
        public Board execute() {
            throw new RuntimeException("Cannot execute null move.");
        }

        @Override
        public int getCurrentCoordinate() {
            return -1;
        }

        @Override
        public boolean isAttackMove() {
            return false;
        }
        
    }

    public static class MoveFactory {

        private MoveFactory() {}

        public static Move createMove(Board board, int currentCoordinate, int destinationCoordinate) {
            for (Move move : board.getAllLegalMoves()) {
                if (move.getCurrentCoordinate() == currentCoordinate &&
                    move.getDestinationCoordinate() == destinationCoordinate) {
                        return move;
                    }

            }
            return NULL_MOVE;
        }
    }


    
}
