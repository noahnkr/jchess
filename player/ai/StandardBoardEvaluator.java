package player.ai;

import java.util.ArrayList;
import java.util.Collection;
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
            pieceValueScore += piece.getPieceValue() + piece.pieceSquareBonus();

            if (piece.getPieceType() == PieceType.BISHOP) {
                bishopCount++;
            }
        }

        return pieceValueScore + (bishopCount == 2 ? DOUBLE_BISHOP_BONUS : 0);
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
