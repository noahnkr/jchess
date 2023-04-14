package player.ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import board.Board;
import board.Move;
import board.Move.AttackMove;
import pieces.Piece;
import pieces.Piece.PieceType;
import player.Player;

public class StandardBoardEvaluator implements BoardEvaluator {

    private static final int CHECK_BONUS = 50;
    private static final int CHECK_MATE_BONUS = 10000;
    private static final int DEPTH_BONUS = 100;
    private static final int CASTLE_BONUS = 25;
    private static final int DOUBLE_BISHOP_BONUS = 25;

    private static final int MOBILITY_WEIGHT = 5;
    private static final int ATTACKS_WEIGHT = 1;

    private final static List<Integer> PAWN_PIECE_SQUARE_TABLE = Arrays.asList(
        0,  0,  0,  0,  0,  0,  0,  0,
        50, 50, 50, 50, 50, 50, 50, 50,
        10, 10, 20, 30, 30, 20, 10, 10,
        5,  5, 10, 25, 25, 10,  5,  5,
        0,  0,  0, 20, 20,  0,  0,  0,
        5, -5,-10,  0,  0,-10, -5,  5,
        5, 10, 10,-20,-20, 10, 10,  5,
        0,  0,  0,  0,  0,  0,  0,  0
    );


    private final static List<Integer> KNIGHT_PIECE_SQUARE_TABLE = Arrays.asList(
        -50,-40,-30,-30,-30,-30,-40,-50,
        -40,-20,  0,  5,  5,  0,-20,-40,
        -30,  0, 10, 15, 15, 10,  0,-30,
        -30,  5, 15, 20, 20, 15,  5,-30,
        -30,  0, 15, 20, 20, 15,  0,-30,
        -30,  5, 10, 15, 15, 10,  5,-30,
        -40,-20,  0,  0,  0,  0,-20,-40,
        -50,-40,-30,-30,-30,-30,-40,-50
    );


    private final static List<Integer> BISHOP_PIECE_SQUARE_TABLE = Arrays.asList(
        -20,-10,-10,-10,-10,-10,-10,-20,
        -10,  0,  0,  0,  0,  0,  0,-10,
        -10,  0,  5, 10, 10,  5,  0,-10,
        -10,  5,  5, 10, 10,  5,  5,-10,
        -10,  0, 10, 15, 15, 10,  0,-10,
        -10, 10, 10, 10, 10, 10, 10,-10,
        -10,  5,  0,  0,  0,  0,  5,-10,
        -20,-10,-10,-10,-10,-10,-10,-20
    );


    private final static List<Integer> ROOK_PIECE_SQUARE_TABLE = Arrays.asList(
        0,  0,  0,  0,  0,  0,  0,  0,
        5, 10, 10, 10, 10, 10, 10,  5,
        5,  0,  0,  0,  0,  0,  0, -5,
       -5,  0,  0,  0,  0,  0,  0, -5,
       -5,  0,  0,  0,  0,  0,  0, -5,
       -5,  0,  0,  0,  0,  0,  0, -5,
       -5,  0,  0,  0,  0,  0,  0, -5,
        0,  0,  0,  5,  5,  0,  0,  0
    );


    private final static List<Integer> QUEEN_PIECE_SQUARE_TABLE = Arrays.asList(
        -20,-10,-10, -5, -5,-10,-10,-20,
        -10,  0,  0,  0,  0,  0,  0,-10,
        -10,  0,  5,  5,  5,  5,  0,-10,
         -5,  0,  5,  5,  5,  5,  0, -5,
          0,  0,  5,  5,  5,  5,  0, -5,
        -10,  5,  5,  5,  5,  5,  0,-10,
        -10,  0,  5,  0,  0,  0,  0,-10,
        -20,-10,-10, -5, -5,-10,-10,-20
    );


    private final static List<Integer> KING_PIECE_SQUARE_TABLE_MIDGAME = Arrays.asList(
        -30,-40,-40,-50,-50,-40,-40,-30,
        -30,-40,-40,-50,-50,-40,-40,-30,
        -30,-40,-40,-50,-50,-40,-40,-30,
        -30,-40,-40,-50,-50,-40,-40,-30,
        -20,-30,-30,-40,-40,-30,-30,-20,
        -10,-20,-20,-20,-20,-20,-20,-10,
         20, 20,  0,  0,  0,  0, 20, 20,
         20, 30, 10,  0,  0, 10, 30, 20
    );

    private final static List<Integer> KING_PIECE_SQUARE_TABLE_ENDGAME = Arrays.asList(
        -50,-30,-30,-30,-30,-30,-30,-50,
        -30,-30,  0,  0,  0,  0,-30,-30,
        -30,-10, 20, 30, 30, 20,-10,-30,
        -30,-10, 30, 40, 40, 30,-10,-30,
        -30,-10, 30, 40, 40, 30,-10,-30,
        -30,-10, 20, 30, 30, 20,-10,-30,
        -30,-20,-10,  0,  0,-10,-20,-30,
        -50,-40,-30,-20,-20,-30,-40,-50
    );

    private enum GamePhase {
        OPENING,
        MIDGAME,
        ENDGAME
    }

    @Override
    public int evaluate(Board board, int depth) {
        return scorePlayer(board, board.whitePlayer(), depth) -
                scorePlayer(board, board.blackPlayer(), depth);
    }

    public int scorePlayer(Board board, Player player, int depth) {
         return pieceEvaluation(player) +
                mobility(player) +
                attacks(player) +
                check(player) +
                checkmate(player, depth) +
                castled(player) + 
                pawnStructure(player);
    }

    private static int pieceEvaluation(Player player) {
        int pieceValueScore = 0;
        int bishopCount = 0;
        for (Piece piece : player.getActivePieces()) {
            pieceValueScore += piece.getPieceValue() + pieceSquareBonus(piece, determineGamePhase(player));

            if (piece.getPieceType() == PieceType.BISHOP) {
                bishopCount++;
            }
        }

        return pieceValueScore + (bishopCount == 2 ? DOUBLE_BISHOP_BONUS : 0);
    }

    private static int pieceSquareBonus(Piece piece, GamePhase gamePhase) {
        switch (piece.getPieceType()) {
            case PAWN:
                return piece.getColor().isWhite() ? PAWN_PIECE_SQUARE_TABLE.get(piece.getPosition()) :
                                                    blackPieceSquareTable(PAWN_PIECE_SQUARE_TABLE).get(piece.getPosition());
            case KNIGHT:
                return piece.getColor().isWhite() ? KNIGHT_PIECE_SQUARE_TABLE.get(piece.getPosition()) :
                                                    blackPieceSquareTable(KNIGHT_PIECE_SQUARE_TABLE).get(piece.getPosition());
            case BISHOP:
                return piece.getColor().isWhite() ? BISHOP_PIECE_SQUARE_TABLE.get(piece.getPosition()) :
                                                    blackPieceSquareTable(BISHOP_PIECE_SQUARE_TABLE).get(piece.getPosition());
            case ROOK:
                return piece.getColor().isWhite() ? ROOK_PIECE_SQUARE_TABLE.get(piece.getPosition()) :
                                                    blackPieceSquareTable(ROOK_PIECE_SQUARE_TABLE).get(piece.getPosition());
            case QUEEN:
                return piece.getColor().isWhite() ? QUEEN_PIECE_SQUARE_TABLE.get(piece.getPosition()) :
                                                    blackPieceSquareTable(QUEEN_PIECE_SQUARE_TABLE).get(piece.getPosition());
            case KING:
                return piece.getColor().isWhite() ? (gamePhase == GamePhase.ENDGAME ? KING_PIECE_SQUARE_TABLE_ENDGAME.get(piece.getPosition()) : 
                                                                                      KING_PIECE_SQUARE_TABLE_MIDGAME.get(piece.getPosition())) : 
                                                    (gamePhase == GamePhase.ENDGAME ? blackPieceSquareTable(KING_PIECE_SQUARE_TABLE_ENDGAME).get(piece.getPosition()) : 
                                                                                      blackPieceSquareTable(KING_PIECE_SQUARE_TABLE_MIDGAME).get(piece.getPosition()));
            default:
                return 0;
        }
    }

    private static List<Integer> blackPieceSquareTable(List<Integer> pieceSquareTable) {
        List<Integer> temp = pieceSquareTable;
        Collections.reverse(pieceSquareTable);
        return temp;
    }

    private static int mobility(Player player) {
        return ((int) ((player.getLegalMoves().size() / 10.0f) /
                player.getOpponent().getLegalMoves().size())) *
                MOBILITY_WEIGHT;
    }

    private static int attacks(Player player) {
        int attackScore = 0;
        for (Move move : player.getLegalMoves()) {
            if (move.isAttackMove()) {
                if (move.getMovedPiece().getPieceValue() <= (((AttackMove) move).getAttackedPiece().getPieceValue())) {
                    attackScore++;
                }
            }
        }

        return attackScore * ATTACKS_WEIGHT;
    }

    private static int check(Player player) {
        return player.getOpponent().isInCheck() ? CHECK_BONUS : 0;
    }

    private static int checkmate(Player player, int depth) {
        return player.getOpponent().isInCheckMate() ? CHECK_MATE_BONUS * depthBonus(depth) : 0;
    }

    private static int depthBonus(int depth) {
        return depth == 0 ? 1 : DEPTH_BONUS * depth;
    }

    private static int castled(Player player) {
        return player.isCastled() ? CASTLE_BONUS : 0;
    }

    private static int pawnStructure(Player player) {
        return PawnStructureAnalyzer.pawnStructureScore(player);
    }

    private static GamePhase determineGamePhase(Player player) {
        List<Piece> allPieces = new ArrayList<>();
        allPieces.addAll(player.getActivePieces());
        allPieces.addAll(player.getOpponent().getActivePieces());

        int npm = 0;
        for (Piece piece : allPieces) {
            if (piece.getPieceType() != PieceType.PAWN) {
                npm++;
            }
        }
        return npm < 7 ? GamePhase.ENDGAME : GamePhase.MIDGAME;
    }

    


    private final class PawnStructureAnalyzer {

        private static final int ISOLATED_PAWN_PENALTY = -10;
        private static final int DOUBLED_PAWN_PENALTY = -10;

        private PawnStructureAnalyzer() {}

        public static int pawnStructureScore(Player player) {
            int[] pawnsOnColumnTable = createPawnColumnTable(calculatePlayerPawns(player));
            return calculatePawnColumnStack(pawnsOnColumnTable) + calculateIsolatedPawnPenalty(pawnsOnColumnTable);
        }

        private static Collection<Piece> calculatePlayerPawns(Player player) {
            List<Piece> playerPawns = new ArrayList<Piece>();
            for (Piece piece : player.getActivePieces()) {
                if (piece.getPieceType() == PieceType.PAWN) {
                    playerPawns.add(piece);
                }
            }
            return playerPawns;
        }

        private static int calculatePawnColumnStack(int[] pawnsOnColumnTable) {
            int pawnStackPenalty = 0;
            for (int pawnStack : pawnsOnColumnTable) {
                if (pawnStack > 1) {
                    pawnStackPenalty += pawnStack;
                }
            }
            return pawnStackPenalty * DOUBLED_PAWN_PENALTY;
        }

        private static int calculateIsolatedPawnPenalty(int[] pawnsOnColumnTable) {
            int numIsolatedPawns = 0;
            if (pawnsOnColumnTable[0] > 0 && pawnsOnColumnTable[1] == 0) {
                numIsolatedPawns += pawnsOnColumnTable[0];
            }
            if (pawnsOnColumnTable[7] > 0 && pawnsOnColumnTable[6] == 0) {
                numIsolatedPawns += pawnsOnColumnTable[7];
            }
            for (int i = 1; i < pawnsOnColumnTable.length - 1; i++) {
                if ((pawnsOnColumnTable[i - 1] == 0 && pawnsOnColumnTable[i + 1] == 0)) {
                    numIsolatedPawns += pawnsOnColumnTable[i];
                }
            }
            return numIsolatedPawns * ISOLATED_PAWN_PENALTY;
        }

        private static int[] createPawnColumnTable(Collection<Piece> playerPawns) {
            int[] table = new int[8];
            for (Piece playerPawn : playerPawns) {
                table[playerPawn.getPosition() % 8]++;
            }
            return table;
        }
    }
}
