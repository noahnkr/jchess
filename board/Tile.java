package board;

import pieces.*;

public abstract class Tile {

    protected final int tileCoordinate;

    public Tile(int coordinate) {
        tileCoordinate = coordinate;
    }

    public static Tile createTile(int coordinate, Piece piece) {
        return piece != null ? new OccupiedTile(coordinate, piece) : new EmptyTile(coordinate);
    }

    public abstract boolean isOccupied();

    public abstract Piece getPiece();
    
    public static final class EmptyTile extends Tile {

        public EmptyTile(int coordinate) {
            super(coordinate);
        }

        @Override
        public String toString() {
            return "·";
        }
    
        @Override
        public boolean isOccupied() {
            return false;
        }
    
        @Override
        public Piece getPiece() {
            return null;
        }
    }

    public static final class OccupiedTile extends Tile {

        protected Piece tilePiece;

        public OccupiedTile(int coordinate, Piece piece) {
            super(coordinate);
            tilePiece = piece;
        }

        @Override
        public String toString() {
            return tilePiece.toString();
        }

        @Override
        public boolean isOccupied() {
            return true;
        }

        @Override
        public Piece getPiece() {
            return tilePiece;
        }
    }

    

}
