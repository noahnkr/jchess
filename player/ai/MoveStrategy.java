package player.ai;

import board.Board;
import board.Move;

public interface MoveStrategy {

    public Move execute(Board board);
    
}
