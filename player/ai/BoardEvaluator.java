package player.ai;

import board.Board;

public interface BoardEvaluator {

    public int evaluate(Board board, int depth);
    
}
