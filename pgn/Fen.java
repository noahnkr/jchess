package pgn;

import board.Board;

public class Fen {

    private Fen() {}

    public static Board createBoardFromFEN(String fen) {
        return null;
    }

    public static String createFENFromBoard(Board board) {
        return boardText(board) + " " +
               currentPlayerText(board) + " " + 
               castleText(board) + " " +
               enPassantSquareText(board) + 
               "0 1";
    }

    private static String boardText(Board board) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < Board.NUM_TILES; i++) {
            sb.append(board.getTile(i).toString());
        }

        sb.insert(8, "/");
        sb.insert(17, "/");
        sb.insert(26, "/");
        sb.insert(35, "/");
        sb.insert(44, "/");
        sb.insert(53, "/");
        sb.insert(62, "/");

        return sb.toString().replaceAll("--------", "8")
                            .replaceAll("-------", "7")
                            .replaceAll("------", "6")
                            .replaceAll("-----", "5")
                            .replaceAll("----", "4")
                            .replaceAll("---", "3")
                            .replaceAll("--", "2")
                            .replaceAll("-", "1");
    }

    private static String currentPlayerText(Board board) {
        return board.currentPlayer().toString().substring(0, 1).toLowerCase();
    }

    

    private static String castleText(Board board) {
        StringBuilder sb = new StringBuilder();
        if (board.whitePlayer().isKingSideCastleCapable()) {
            sb.append("K");

        }
        if (board.whitePlayer().isKingSideCastleCapable()) {
            sb.append("Q");
            
        }
        if (board.blackPlayer().isKingSideCastleCapable()) {
            sb.append("k");
            
        }
        if (board.blackPlayer().isQueenSideCastleCapable()) {
            sb.append("q");
        }

        return sb.toString().isEmpty() ? "-" : sb.toString();
    }

    private static String enPassantSquareText(Board board) {
        if (board.getEnPassantPawn() != null) {
            return Board.getPositionAtCoordinate(board.getEnPassantPawn().getPosition());
        }
        return "-";

    }

}
