package player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import board.Board;
import board.Move;
import board.MoveStatus;
import board.MoveTransition;
import pieces.Color;
import pieces.King;
import pieces.Piece;
import pieces.Piece.PieceType;

public abstract class Player {

    protected Board board;
    protected King playerKing;
    protected Collection<Move> legalMoves;
    protected boolean isInCheck;

    public Player(Board board, Collection<Move> legalMoves, Collection<Move> opponentMoves) {
        this.board = board;
        this.playerKing = establishKing();
        this.isInCheck = !calculateAttacksOnTile(this.playerKing.getPosition(), opponentMoves).isEmpty();
        legalMoves.addAll(calculateKingCastles(legalMoves, opponentMoves));
        this.legalMoves = legalMoves;
    }

    private King establishKing() {
        for (Piece piece : getActivePieces()) {
            if (piece.getPieceType() == PieceType.KING) {
                return (King) piece;
            }
        }

        throw new RuntimeException("No King on board");
    }

    public King getPlayerKing() {
        return this.playerKing;
    }

    public Collection<Move> getLegalMoves() {
        return legalMoves;
    }

    public Collection<Move> getCaptureMoves() {
        Collection<Move> captureMoves = new ArrayList<Move>();
        for (Move move : legalMoves) {
            if (move.isAttackMove()) {
                captureMoves.add(move);
            }
        }
        return captureMoves;
    }

    public Collection<Move> getCastlingMoves() {
        Collection<Move> castlingMoves = new ArrayList<Move>();
        for (Move move : legalMoves) {
            if (move.isCastlingMove()) {
                castlingMoves.add(move);
            }
        }
        return castlingMoves;
    }

    protected static Collection<Move> calculateAttacksOnTile(int piecePosition, Collection<Move> opponentMoves) {
        List<Move> attackMoves = new ArrayList<>();
        for (Move move : opponentMoves) {
            if (piecePosition == move.getDestinationCoordinate()) {
                attackMoves.add(move);
            }
        }
        return attackMoves;
    }

    public boolean isMoveLegal(Move move) {
        return this.legalMoves.contains(move);
    }

    public boolean isInCheck() {
        return this.isInCheck;
    }

    public boolean isInCheckMate() {
        return this.isInCheck && !hasEscapeMoves();
    }

    public boolean isInStaleMate() {
        return !this.isInCheck && !hasEscapeMoves();
    }

    public boolean isKingSideCastleCapable() {
        return playerKing.isKingSideCastleCapable();

    }

    public boolean isQueenSideCastleCapable() {
        return playerKing.isQueenSideCastleCapable();
    }

    protected boolean hasEscapeMoves() {
        for (Move move : this.legalMoves) {
            MoveTransition transition = makeMove(move);
            if (transition.getMoveStatus().isDone()) {
                return true;
            }
        }
        return false;
    }

    public boolean isCastled() {
        return false;
    }

    public MoveTransition makeMove(Move move) {

        if (!isMoveLegal(move)) {
            return new MoveTransition(this.board, move, MoveStatus.ILLEGAL_MOVE);
        }

        Board transitionBoard = move.execute();
        Collection<Move> kingAttacks = Player.calculateAttacksOnTile(transitionBoard.currentPlayer().getOpponent().getPlayerKing().getPosition(),
                                                                    transitionBoard.currentPlayer().getLegalMoves());

        if (!kingAttacks.isEmpty()) {
            return new MoveTransition(this.board, move, MoveStatus.LEAVES_PLAYER_IN_CHECK);
        }

        return new MoveTransition(transitionBoard, move, MoveStatus.DONE);
    }

    public abstract Collection<Piece> getActivePieces();

    public abstract Color getColor();

    public abstract Player getOpponent();

    public abstract Collection<Move> calculateKingCastles(Collection<Move> playerLegals, Collection<Move> opponentsLegals);
    
}
