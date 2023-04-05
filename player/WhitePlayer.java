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

public class WhitePlayer extends Player {

    public WhitePlayer(Board board, Collection<Move> whiteLegalMoves, Collection<Move> blackLegalMoves) {
        super(board, whiteLegalMoves, blackLegalMoves);
    }

    @Override
    public Collection<Piece> getActivePieces() {
        return board.getWhitePieces();
    }

    @Override
    public Color getColor() {
        return Color.BLACK;
    }

    @Override
    public Player getOpponent() {
        return this.board.blackPlayer();
    }

    @Override
    public Collection<Move> calculateKingCastles(Collection<Move> playerLegals, Collection<Move> opponentsLegals) {
        List<Move> kingCastles = new ArrayList<>();

        if (this.playerKing.isFirstMove() && !this.isInCheck()) {
            // King side castle
            if (!this.board.getTile(61).isOccupied() && !this.board.getTile(62).isOccupied()) {
                Tile rookTile = this.board.getTile(63);
                if (rookTile.isOccupied() && rookTile.getPiece().isFirstMove()) {
                    if (Player.calculateAttacksOnTile(61, opponentsLegals).isEmpty() &&
                        Player.calculateAttacksOnTile(62, opponentsLegals).isEmpty() &&
                        rookTile.getPiece().getPieceType() == PieceType.ROOK) {
                            kingCastles.add(new KingSideCastleMove(this.board, 
                                                                   this.playerKing, 
                                                                   62, 
                                                                   (Rook) rookTile.getPiece(), 
                                                                   rookTile.getTileCoordinate(), 
                                                                   61));
                    }
                }
            }   

            // Queen side castle
            if (!this.board.getTile(59).isOccupied() && !this.board.getTile(58).isOccupied() &&
                !this.board.getTile(57).isOccupied()) {
                Tile rookTile = this.board.getTile(56);
                if (rookTile.isOccupied() && rookTile.getPiece().isFirstMove() &&
                    Player.calculateAttacksOnTile(58, opponentsLegals).isEmpty() &&
                    Player.calculateAttacksOnTile(59, opponentsLegals).isEmpty() &&
                    rookTile.getPiece().getPieceType() == PieceType.ROOK) {
                    kingCastles.add(new QueenSideCastleMove(this.board,
                                                            this.playerKing,
                                                            58,
                                                            (Rook) rookTile.getPiece(),
                                                            rookTile.getTileCoordinate(),
                                                            59));
                }
            }
        }
        return kingCastles;
    }
    
}
