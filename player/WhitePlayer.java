package player;

import java.util.Collection;

import board.Board;
import board.Move;
import pieces.Color;
import pieces.Piece;

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
    
}
