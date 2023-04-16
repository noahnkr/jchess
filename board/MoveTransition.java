package board;

public class MoveTransition {

    private Board fromBoard;
    private Board toBoard;
    private Move move;
    private MoveStatus moveStatus;

    public MoveTransition(Board fromBoard, Board toBoard, Move move, MoveStatus moveStatus) {
        this.fromBoard = fromBoard;
        this.toBoard = toBoard;
        this.move = move;
        this.moveStatus = moveStatus;
    }

    public Board getFromBoard() {
        return fromBoard;
    }

    public Board getToBoard() {
        return toBoard;
    }

    public Move getMove() {
        return move;
    }

    public MoveStatus getMoveStatus() {
        return moveStatus;
    }


    
    
}
