package board;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pieces.Color;
import player.BlackPlayer;
import player.WhitePlayer;
import pieces.*;

public class Board {

    protected List<Tile> gameBoard;
    protected Collection<Piece> whitePieces;
    protected Collection<Piece> blackPieces;

    public static final int NUM_TILES = 64;
    public static final int NUM_TILES_PER_ROW = 8;

    // Used to determine if a piece is on a certain column for move exceptions 
    public static final boolean[] FIRST_COLUMN = initColumn(0);
    public static final boolean[] SECOND_COLUMN = initColumn(1);
    public static final boolean[] SEVENTH_COLUMN = initColumn(6);
    public static final boolean[] EIGHTH_COLUMN = initColumn(7);

    public static final boolean[] SECOND_ROW = initRow(1);
    public static final boolean[] SEVENTH_ROW = initRow(6);

    public Board(BoardBuilder builder) {
        this.gameBoard = initializeBoard(builder);
        this.whitePieces = getActivePieces(gameBoard, Color.WHITE);
        this.blackPieces = getActivePieces(gameBoard, Color.BLACK);

        Collection<Move> whiteLegalMoves = getLegalMoves(whitePieces);
        Collection<Move> blackLegalMoves = getLegalMoves(blackPieces);

        WhitePlayer whitePlayer = new WhitePlayer(this, whiteLegalMoves, blackLegalMoves);
        BlackPlayer blackPlayer = new BlackPlayer(this, whiteLegalMoves, blackLegalMoves);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        int rowCounter = 8;
        for (int i = 0; i < NUM_TILES; i++) {
            if ((NUM_TILES - i) % NUM_TILES_PER_ROW == 0) {
                sb.append(rowCounter + " | ");
                rowCounter--;
            }
            sb.append(prettyPrint(this.gameBoard.get(i)) + " ");
            if ((i + 1) % NUM_TILES_PER_ROW == 0) {
                sb.append("\n");
            }
        }
        sb.append("  ------------------\n");
        sb.append("    a b c d e f g h");

        return sb.toString();
    }

    private String prettyPrint(Tile tile) {
        if (tile.isOccupied()) {
            return tile.getPiece().getColor().isBlack() ? tile.getPiece().toString().toLowerCase() :
                                                          tile.getPiece().toString();
        }
        return tile.toString();
    }

    private static Collection<Piece> getActivePieces(List<Tile> gameBoard, Color color) {
        List<Piece> activePieces = new ArrayList<>();
        for (Tile tile : gameBoard) {
            if (tile.isOccupied()) {
                Piece piece = tile.getPiece();
                if (piece.getColor() == color) {
                    activePieces.add(piece);
                }
            }
        }
        return activePieces;
    }

    private Collection<Move> getLegalMoves(Collection<Piece> pieces) {
        List<Move> legalMoves = new ArrayList<>();
        for (Piece piece : pieces) {
            legalMoves.addAll(piece.calculateLegalMoves(this));
        }
        return legalMoves;
    }

    public Tile getTile(int coordinate) {
        return gameBoard.get(coordinate);
    }

    public static boolean isValidTileCoordinate(int coordinate) {
        return coordinate >= 0 && coordinate < NUM_TILES;
    }

    public static List<Tile> initializeBoard(BoardBuilder builder) {
        List<Tile> emptyBoard = new ArrayList<>();
        for (int i = 0; i < NUM_TILES; i++) {
            emptyBoard.add(Tile.createTile(i, builder.pieceMap.get(i)));
        }
        return emptyBoard;
    }

    public static Board createStandardBoard() {
        BoardBuilder builder = new BoardBuilder();
        // Black Pieces
        builder.setPiece(new Rook(0, Color.BLACK));
        builder.setPiece(new Knight(1, Color.BLACK));
        builder.setPiece(new Bishop(2, Color.BLACK));
        builder.setPiece(new Queen(3, Color.BLACK));
        builder.setPiece(new King(4, Color.BLACK));
        builder.setPiece(new Bishop(5, Color.BLACK));
        builder.setPiece(new Knight(6, Color.BLACK));
        builder.setPiece(new Rook(7, Color.BLACK));
        builder.setPiece(new Pawn(8, Color.BLACK));
        builder.setPiece(new Pawn(9, Color.BLACK));
        builder.setPiece(new Pawn(10, Color.BLACK));
        builder.setPiece(new Pawn(11, Color.BLACK));
        builder.setPiece(new Pawn(12, Color.BLACK));
        builder.setPiece(new Pawn(13, Color.BLACK));
        builder.setPiece(new Pawn(14, Color.BLACK));
        builder.setPiece(new Pawn(15, Color.BLACK));

        // White Pieces
        builder.setPiece(new Pawn(48, Color.WHITE));
        builder.setPiece(new Pawn(49, Color.WHITE));
        builder.setPiece(new Pawn(50, Color.WHITE));
        builder.setPiece(new Pawn(51, Color.WHITE));
        builder.setPiece(new Pawn(52, Color.WHITE));
        builder.setPiece(new Pawn(53, Color.WHITE));
        builder.setPiece(new Pawn(54, Color.WHITE));
        builder.setPiece(new Pawn(55, Color.WHITE));
        builder.setPiece(new Rook(56, Color.WHITE));
        builder.setPiece(new Knight(57, Color.WHITE));
        builder.setPiece(new Bishop(58, Color.WHITE));
        builder.setPiece(new Queen(59, Color.WHITE));
        builder.setPiece(new King(60, Color.WHITE));
        builder.setPiece(new Bishop(61, Color.WHITE));
        builder.setPiece(new Knight(62, Color.WHITE));
        builder.setPiece(new Rook(63, Color.WHITE));

        // White first move
        builder.setTurn(Color.WHITE);

        return builder.build();
    }

    public static class BoardBuilder {
        Map<Integer, Piece> pieceMap;
        Color playerTurn;

        public BoardBuilder() { this.pieceMap = new HashMap<>(); }

        public BoardBuilder setPiece(Piece piece) {
            pieceMap.put(piece.getPosition(), piece);
            return this;
        }

        public BoardBuilder setTurn(Color playerTurn) {
            this.playerTurn = playerTurn;
            return this;
        }

        public Board build() {
            return new Board(this);
        }
    }

    private static boolean[] initColumn(int columnNumber) {
        boolean[] column = new boolean[NUM_TILES];
        do {
            column[columnNumber] = true;
            columnNumber += NUM_TILES_PER_ROW;

        } while (columnNumber < NUM_TILES);

        return column;
    }

    private static boolean[] initRow(int rowNumber) {
        boolean[] row = new boolean[NUM_TILES];
         do {
            row[rowNumber++] = true;
         } while(rowNumber % NUM_TILES_PER_ROW != 0);
           

        return row;
    }


}