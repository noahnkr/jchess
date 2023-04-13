package pieces;

import board.Board;
import player.BlackPlayer;
import player.Player;
import player.WhitePlayer;

public enum Color {
    WHITE {
        @Override
        public int getDirection() {
            return -1;
        }

        @Override
        public int getOppositeDirection() {
            return 1;
        }

        @Override
        public boolean isWhite() {
            return true;
        }

        @Override
        public boolean isBlack() {
            return false;
        }

        @Override
        public boolean isPawnPromotionTile(int position) {
            return Board.FIRST_ROW[position];
        }

        @Override
        public Player choosePlayer(WhitePlayer whitePlayer, BlackPlayer blackPlayer) {
            return whitePlayer;
        }

        @Override
        public int pawnBonus(int position) {
            return WHITE_PAWN_PIECE_TABLE[position];
        }

        @Override
        public int knightBonus(int position) {
            return WHITE_KNIGHT_PIECE_TABLE[position];
        }

        @Override
        public int bishopBonus(int position) {
           return WHITE_BISHOP_PIECE_TABLE[position];
        }

        @Override
        public int rookBonus(int position) {
           return WHITE_ROOK_PIECE_TABLE[position];
        }

        @Override
        public int queenBonus(int position) {
            return WHITE_QUEEN_PIECE_TABLE[position];
        }

        @Override
        public int kingBonus(int position) {
            return WHITE_KING_PIECE_TABLE[position];
        }

    },
    BLACK {
        @Override
        public int getDirection() {
            return 1;
        }

        @Override
        public int getOppositeDirection() {
            return -1;
        }

        @Override
        public boolean isWhite() {
            return false;
        }

        @Override
        public boolean isBlack() {
            return true;
        }

        @Override
        public boolean isPawnPromotionTile(int position) {
            return Board.EIGHTH_ROW[position];
        }

        @Override
        public Player choosePlayer(WhitePlayer whitePlayer, BlackPlayer blackPlayer) {
            return blackPlayer;
        }

        @Override
        public int pawnBonus(int position) {
            return BLACK_PAWN_PIECE_TABLE[position];
        }

        @Override
        public int knightBonus(int position) {
            return BLACK_KNIGHT_PIECE_TABLE[position];
        }

        @Override
        public int bishopBonus(int position) {
           return BLACK_BISHOP_PIECE_TABLE[position];
        }

        @Override
        public int rookBonus(int position) {
           return BLACK_ROOK_PIECE_TABLE[position];
        }

        @Override
        public int queenBonus(int position) {
            return BLACK_QUEEN_PIECE_TABLE[position];
        }

        @Override
        public int kingBonus(int position) {
            return BLACK_KING_PIECE_TABLE[position];
        }

        
    };

    public abstract int getDirection();
    public abstract int getOppositeDirection();
    public abstract boolean isWhite();
    public abstract boolean isBlack();
    public abstract boolean isPawnPromotionTile(int position);
    public abstract Player choosePlayer(WhitePlayer whitePlayer, BlackPlayer blackPlayer);

    public abstract int pawnBonus(int position);
    public abstract int knightBonus(int position);
    public abstract int bishopBonus(int position);
    public abstract int rookBonus(int position);
    public abstract int queenBonus(int position);
    public abstract int kingBonus(int position);


    private final static int[] WHITE_PAWN_PIECE_TABLE = {
        0,  0,  0,  0,  0,  0,  0,  0,
        90, 90, 90, 90, 90, 90, 90, 90,
        30, 30, 40, 60, 60, 40, 30, 30,
        10, 10, 20, 40, 40, 20, 10, 10,
        5,  5, 10, 20, 20, 10,  5,  5,
        0,  0,  0,-10,-10,  0,  0,  0,
        5, -5,-10,  0,  0,-10, -5,  5,
        0,  0,  0,  0,  0,  0,  0,  0
    };

    private final static int[] BLACK_PAWN_PIECE_TABLE = {
            0,  0,  0,  0,  0,  0,  0,  0,
            5, -5,-10,  0,  0,-10, -5,  5,
            0,  0,  0,-10,-10,  0,  0,  0,
            5,  5, 10, 20, 20, 10,  5,  5,
            10, 10, 20, 40, 40, 20, 10, 10,
            30, 30, 40, 60, 60, 40, 30, 30,
            90, 90, 90, 90, 90, 90, 90, 90,
            0,  0,  0,  0,  0,  0,  0,  0
    };

    private final static int[] WHITE_KNIGHT_PIECE_TABLE = {
            -50,-40,-30,-30,-30,-30,-40,-50,
            -40,-20,  0,  5,  5,  0,-20,-40,
            -30,  5, 10, 15, 15, 10,  5,-30,
            -30,  5, 15, 20, 20, 15,  5,-30,
            -30,  5, 15, 20, 20, 15,  5,-30,
            -30,  5, 10, 15, 15, 10,  5,-30,
            -40,-20,  0,  0,  0,  0,-20,-40,
            -50,-40,-30,-30,-30,-30,-40,-50
    };

    private final static int[] BLACK_KNIGHT_PIECE_TABLE = {
            -50,-40,-30,-30,-30,-30,-40,-50,
            -40,-20,  0,  0,  0,  0,-20,-40,
            -30,  5, 10, 15, 15, 10,  5,-30,
            -30,  5, 15, 20, 20, 15,  5,-30,
            -30,  5, 15, 20, 20, 15,  5,-30,
            -30,  5, 10, 15, 15, 10,  5,-30,
            -40,-20,  0,  5,  5,  0,-20,-40,
            -50,-40,-30,-30,-30,-30,-40,-50,
    };

    private final static int[] WHITE_BISHOP_PIECE_TABLE = {
            -20,-10,-10,-10,-10,-10,-10,-20,
            -10,  0,  0,  0,  0,  0,  0,-10,
            -10,  0,  5, 10, 10,  5,  0,-10,
            -10,  5,  5, 10, 10,  5,  5,-10,
            -10,  0, 10, 15, 15, 10,  0,-10,
            -10, 10, 10, 10, 10, 10, 10,-10,
            -10,  5,  0,  0,  0,  0,  5,-10,
            -20,-10,-10,-10,-10,-10,-10,-20
    };

    private final static int[] BLACK_BISHOP_PIECE_TABLE = {
            -20,-10,-10,-10,-10,-10,-10,-20,
            -10,  5,  0,  0,  0,  0,  5,-10,
            -10, 10, 10, 10, 10, 10, 10,-10,
            -10,  0, 10, 15, 15, 10,  0,-10,
            -10,  5, 10, 15, 15, 10,  5,-10,
            -10,  0, 10, 10, 10, 10,  0,-10,
            -10,  0,  0,  0,  0,  0,  0,-10,
            -20,-10,-10,-10,-10,-10,-10,-20
    };

    private final static int[] WHITE_ROOK_PIECE_TABLE = {
            0,  0,  0,  0,  0,  0,  0,  0,
            5, 20, 20, 20, 20, 20, 20,  5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            0,  0,  0,  5,  5,  0,  0,  0
    };

    private final static int[] BLACK_ROOK_PIECE_TABLE = {
            0,  0,  0,  5,  5,  0,  0,  0,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            5, 20, 20, 20, 20, 20, 20,  5,
            0,  0,  0,  0,  0,  0,  0,  0,
    };

    private final static int[] WHITE_QUEEN_PIECE_TABLE = {
            -20,-10,-10, -5, -5,-10,-10,-20,
            -10,  0,  0,  0,  0,  0,  0,-10,
            -10,  0,  5,  5,  5,  5,  0,-10,
            -5,  0,  5, 10, 10,  5,  0, -5,
            -5,  0,  5, 10, 10,  5,  0, -5,
            -10,  0,  5,  5,  5,  5,  0,-10,
            -10,  0,  0,  0,  0,  0,  0,-10,
            -20,-10,-10, -5, -5,-10,-10,-20
    };

    private final static int[] BLACK_QUEEN_PIECE_TABLE = {
            -20,-10,-10, -5, -5,-10,-10,-20,
            -10,  0,  5,  0,  0,  0,  0,-10,
            -10,  5,  5,  5,  5,  5,  0,-10,
            -5,  0,  5, 10, 10,  5,  0, -5,
            -5,  0,  5, 10, 10,  5,  0, -5,
            -10,  0,  5,  5,  5,  5,  0,-10,
            -10,  0,  0,  0,  0,  0,  0,-10,
            -20,-10,-10, -5, -5,-10,-10,-20
    };

    private final static int[] WHITE_KING_PIECE_TABLE = {
            -50,-30,-30,-30,-30,-30,-30,-50,
            -30,-30,  0,  0,  0,  0,-30,-30,
            -30,-10, 20, 30, 30, 20,-10,-30,
            -30,-10, 30, 40, 40, 30,-10,-30,
            -30,-10, 30, 40, 40, 30,-10,-30,
            -30,-10, 20, 30, 30, 20,-10,-30,
            -30,-20,-10,  0,  0,-10,-20,-30,
            -50,-40,-30,-20,-20,-30,-40,-50
    };

    private final static int[] BLACK_KING_PIECE_TABLE = {
            -50,-40,-30,-20,-20,-30,-40,-50,
            -30,-20,-10,  0,  0,-10,-20,-30,
            -30,-10, 20, 30, 30, 20,-10,-30,
            -30,-10, 30, 40, 40, 30,-10,-30,
            -30,-10, 30, 40, 40, 30,-10,-30,
            -30,-10, 20, 30, 30, 20,-10,-30,
            -30,-30,  0,  0,  0,  0,-30,-30,
            -50,-30,-30,-30,-30,-30,-30,-50
    };

}
