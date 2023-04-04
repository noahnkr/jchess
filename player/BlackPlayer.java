package player;

import java.util.Collection;
import board.Board;
import board.Move;
import pieces.Color;
import pieces.Piece;

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
    
}
