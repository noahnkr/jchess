package player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import board.Board;
import board.Move;
import board.Tile;
import board.Move.KingSideCastleMove;
import board.Move.QueenSideCastleMove;
import pieces.Color;
import pieces.Piece;
import pieces.Rook;
import pieces.Piece.PieceType;

public class BlackPlayer extends Player {

    public BlackPlayer(Board board, Collection<Move> whiteLegalMoves, Collection<Move> blackLegalMoves) {
        super(board, whiteLegalMoves, blackLegalMoves);
    }

    @Override
    public Collection<Piece> getActivePieces() {
        return board.getBlackPieces();
    }

    @Override
    public Color getColor() {
        return Color.WHITE;
    }

    @Override
    public Player getOpponent() {
        return this.board.whitePlayer();
    }

    @Override
    public Collection<Move> calculateKingCastles(Collection<Move> playerLegals, Collection<Move> opponentsLegals) {
        List<Move> kingCastles = new ArrayList<>();

        if (this.playerKing.isFirstMove() && !this.isInCheck()) {
            // King side castle
            if (!this.board.getTile(5).isOccupied() && !this.board.getTile(6).isOccupied()) {
                Tile rookTile = this.board.getTile(7);
                if (rookTile.isOccupied() && rookTile.getPiece().isFirstMove()) {
                    if (Player.calculateAttacksOnTile(5, opponentsLegals).isEmpty() &&
                        Player.calculateAttacksOnTile(6, opponentsLegals).isEmpty() &&
                        rookTile.getPiece().getPieceType() == PieceType.ROOK) {
                            kingCastles.add(new KingSideCastleMove(this.board,
                                                                   this.playerKing,
                                                                   6,
                                                                   (Rook) rookTile.getPiece(),
                                                                   rookTile.getTileCoordinate(),
                                                                   5));
                    }
                }
            }

            // Queen side castle
            if (!this.board.getTile(1).isOccupied() && !this.board.getTile(2).isOccupied() &&
                !this.board.getTile(3).isOccupied()) {
                Tile rookTile = this.board.getTile(0);
                if (rookTile.isOccupied() && rookTile.getPiece().isFirstMove() &&
                    Player.calculateAttacksOnTile(2, opponentsLegals).isEmpty() &&
                    Player.calculateAttacksOnTile(3, opponentsLegals).isEmpty() &&
                    rookTile.getPiece().getPieceType() == PieceType.ROOK) {
                    kingCastles.add(new QueenSideCastleMove(this.board,
                                                            this.playerKing,
                                                            2,
                                                            (Rook) rookTile.getPiece(),
                                                            rookTile.getTileCoordinate(),
                                                            3));
                }
            }
        }
        return kingCastles;
    }
    
}
