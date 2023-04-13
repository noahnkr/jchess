package board;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import player.BlackPlayer;
import player.Player;
import player.WhitePlayer;
import pieces.*;

public class Board {
    public static final int NUM_TILES = 64;
    public static final int NUM_TILES_PER_ROW = 8;

    public static final List<String> ALGEBRAIC_NOTATION = initializeAlgebraicNotation();
    public static final Map<String, Integer> POSITION_TO_COORDINATE = initializePositionToCoordinateMap();

    // Used to determine if a piece is on a certain column for move exceptions 
    public static final boolean[] FIRST_COLUMN = initColumn(0);
    public static final boolean[] SECOND_COLUMN = initColumn(1);
    public static final boolean[] SEVENTH_COLUMN = initColumn(6);
    public static final boolean[] EIGHTH_COLUMN = initColumn(7);

    public static final boolean[] FIRST_ROW = initRow(0);
    public static final boolean[] SECOND_ROW = initRow(8);
    public static final boolean[] SEVENTH_ROW = initRow(48);
    public static final boolean[] EIGHTH_ROW = initRow(56);

    private List<Tile> gameBoard;

    private Collection<Piece> whitePieces;
    private Collection<Piece> blackPieces;

    private WhitePlayer whitePlayer;
    private BlackPlayer blackPlayer;
    private Player currentPlayer;

    private Pawn enPassantPawn;

    public Board(BoardBuilder builder) {
        this.gameBoard = initializeBoard(builder);
        this.whitePieces = getActivePieces(gameBoard, Color.WHITE);
        this.blackPieces = getActivePieces(gameBoard, Color.BLACK);
        this.enPassantPawn = builder.enPassantPawn;

        // Empty Board
        if (!(whitePieces.isEmpty() && blackPieces.isEmpty())) {
            Collection<Move> whiteLegalMoves = getLegalMoves(whitePieces);
            Collection<Move> blackLegalMoves = getLegalMoves(blackPieces);
            this.whitePlayer = new WhitePlayer(this, whiteLegalMoves, blackLegalMoves);
            this.blackPlayer = new BlackPlayer(this, blackLegalMoves, whiteLegalMoves);
            this.currentPlayer = builder.nextMoveMaker.choosePlayer(this.whitePlayer, this.blackPlayer);
        }
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
            sb.append(this.gameBoard.get(i).toString() + " ");
            if ((i + 1) % NUM_TILES_PER_ROW == 0) {
                sb.append("\n");
            }
        }
        sb.append("  ------------------\n");
        sb.append("    a b c d e f g h");

        return sb.toString();
    }

    public Player currentPlayer() {
        return this.currentPlayer;
    }

    public Player blackPlayer() {
        return this.blackPlayer;
    }

    public Player whitePlayer() {
        return this.whitePlayer;
    }

    public Collection<Piece> getBlackPieces() {
        return this.blackPieces;
    }

    public Collection<Piece> getWhitePieces() {
        return this.whitePieces;
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

    public Iterable<Move> getAllLegalMoves() {
        List<Move> allLegalMoves = new ArrayList<>();
        allLegalMoves.addAll(this.whitePlayer.getLegalMoves());
        allLegalMoves.addAll(this.blackPlayer.getLegalMoves());
        return allLegalMoves;
    }

    public Tile getTile(int coordinate) {
        return gameBoard.get(coordinate);
    }

    public Pawn getEnPassantPawn() {
        return enPassantPawn;
    }

    public static int getCoordinateAtPosition(String position) {
        return POSITION_TO_COORDINATE.get(position);
    }

    public static String getPositionAtCoordinate(int coordinate) {
        return ALGEBRAIC_NOTATION.get(coordinate);
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

    public static Board createEmptyBoard() {
        return new BoardBuilder().build();
    }

    public static Board createStandardBoard() {
        BoardBuilder builder = new BoardBuilder();
        // Black Pieces
        builder.setPiece(new Rook(0, Color.BLACK));
        builder.setPiece(new Knight(1, Color.BLACK));
        builder.setPiece(new Bishop(2, Color.BLACK));
        builder.setPiece(new Queen(3, Color.BLACK));
        builder.setPiece(new King(4, Color.BLACK, true, true));
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
        builder.setPiece(new King(60, Color.WHITE, true, true));
        builder.setPiece(new Bishop(61, Color.WHITE));
        builder.setPiece(new Knight(62, Color.WHITE));
        builder.setPiece(new Rook(63, Color.WHITE));

        // White first move
        builder.setMoveMaker(Color.WHITE);

        return builder.build();
    }

    public static class BoardBuilder {
        Map<Integer, Piece> pieceMap;
        Color nextMoveMaker;
        Pawn enPassantPawn;

        public BoardBuilder() { this.pieceMap = new HashMap<>(); }

        public BoardBuilder setPiece(Piece piece) {
            pieceMap.put(piece.getPosition(), piece);
            return this;
        }

        public BoardBuilder setMoveMaker(Color nextMoveMaker) {
            this.nextMoveMaker = nextMoveMaker;
            return this;
        }

        public Board build() {
            return new Board(this);
        }

        public void setEnPassantPawn(Pawn enPassantPawn) {
            this.enPassantPawn = enPassantPawn;
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
            row[rowNumber] = true;
            rowNumber++;
        } while(rowNumber % NUM_TILES_PER_ROW != 0);
           
        return row;
    }

    private static List<String> initializeAlgebraicNotation() {
        return Arrays.asList(
                "a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8",
                "a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7",
                "a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6",
                "a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5",
                "a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4",
                "a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3",
                "a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2",
                "a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1");
    }

    private static Map<String, Integer> initializePositionToCoordinateMap() {
        Map<String, Integer> positionToCoordinate = new HashMap<>();
        for (int i = 0; i < NUM_TILES; i++) {
            positionToCoordinate.put(ALGEBRAIC_NOTATION.get(i), i);

        }
        return positionToCoordinate;
    }



}