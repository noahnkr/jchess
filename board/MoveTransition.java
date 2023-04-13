package board;

public class MoveTransition {

    private Board transitionBoard;
    private MoveStatus moveStatus;

    public MoveTransition(Board transitionBoard, MoveStatus moveStatus) {
        this.transitionBoard = transitionBoard;
        this.moveStatus = moveStatus;
    }

    public Board getTransitionBoard() {
        return transitionBoard;
    }

    public MoveStatus getMoveStatus() {
        return moveStatus;
    }


    
    
}
