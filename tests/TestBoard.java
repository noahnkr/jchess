package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import board.Board;
import board.Move;
import board.MoveTransition;
import player.ai.MiniMax;
import player.ai.MoveStrategy;

public class TestBoard {

    @Test
    public void testFoolsMate() {

        Board board = Board.createStandardBoard();

        MoveTransition t1 = board.currentPlayer().makeMove(Move.MoveFactory.createMove(board, Board.getCoordinateAtPosition("f2"), Board.getCoordinateAtPosition("f3")));
        assertTrue(t1.getMoveStatus().isDone());

        MoveTransition t2 = t1.getTransitionBoard().currentPlayer().makeMove(Move.MoveFactory.createMove(t1.getTransitionBoard(), Board.getCoordinateAtPosition("e7"), Board.getCoordinateAtPosition("e5")));
        assertTrue(t2.getMoveStatus().isDone());

        MoveTransition t3 = t2.getTransitionBoard().currentPlayer().makeMove(Move.MoveFactory.createMove(t2.getTransitionBoard(), Board.getCoordinateAtPosition("g2"), Board.getCoordinateAtPosition("g4")));
        assertTrue(t3.getMoveStatus().isDone());

        MoveStrategy strategy = new MiniMax(4);
        Move aiMove = strategy.execute(t3.getTransitionBoard());
        Move bestMove = Move.MoveFactory.createMove(t3.getTransitionBoard(), Board.getCoordinateAtPosition("d8"),  Board.getCoordinateAtPosition("h4"));
        assertEquals(aiMove, bestMove);
    }
    
}
