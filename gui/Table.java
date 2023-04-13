package gui;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutionException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import board.Board;
import board.Move;
import board.MoveTransition;
import board.Tile;
import board.Move.MoveFactory;
import pieces.Piece;
import pieces.Piece.PieceType;
import player.ai.MiniMax;
import player.ai.MoveStrategy;

public class Table extends Observable {

    private Board gameBoard;

    private JFrame gameFrame;
    private BoardPanel boardPanel;
    private GameHistoryPanel gameHistoryPanel;
    private TakenPiecesPanel takenPiecesPanel;
    private GameSetup gameSetup;

    private MoveLog moveLog;
    private Move lastMove;
    private Move computerMove;
    private Tile lastEnteredTile;

    private Tile sourceTile;
    private Tile destinationTile;
    private Piece movedPiece;

    private boolean highlightLegalMoves;
    private boolean gameStarted;

    private static final Dimension OUTER_FRAME_DIMENSION = new Dimension(1000, 800);
    private static final Dimension BOARD_PANEL_DIMENSION = new Dimension(400, 350);
    private static final Dimension TILE_PANEL_DIMENSION = new Dimension(100, 100);
    private static final Map<String, Image> PIECE_ICON_MAP = setupPieceIconMap();

    private static final Color LIGHT_TILE_COLOR = Color.decode("#2d333b");
    private static final Color DARK_TILE_COLOR =  Color.decode("#697484");
    private static final Color SOURCE_TILE_COLOR = Color.decode("#63839c");
    private static final Color DESTINATION_TILE_COLOR = Color.decode("#405e75");
    private static final Color CHECK_COLOR = Color.decode("#ff9640");
    private static final Color CHECK_MATE_COLOR = Color.decode("#ff4040");

    private static Table INSTANCE = new Table();

    private Table() {
        this.gameBoard = Board.createStandardBoard();
        this.gameFrame = new JFrame("JChess");
        this.gameFrame.setJMenuBar(createTableMenuBar());
        this.gameHistoryPanel = new GameHistoryPanel();
        this.takenPiecesPanel = new TakenPiecesPanel();
        this.highlightLegalMoves = true;
        this.gameStarted = false;
        this.gameFrame.setSize(OUTER_FRAME_DIMENSION);
        this.gameFrame.setResizable(false);
        this.boardPanel = new BoardPanel();
        this.moveLog = new MoveLog();
        this.addObserver(new TableGameAIWatcher());
        this.gameSetup = new GameSetup(this.gameFrame, true);
        this.gameFrame.add(this.takenPiecesPanel, BorderLayout.WEST);
        this.gameFrame.add(this.gameHistoryPanel, BorderLayout.EAST);
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
        this.gameFrame.setVisible(true);
    }

    public static Table get() {
        return INSTANCE;
    }

    public void show() {
        Table.get().getMoveLog().clear();
        Table.get().getGameHistoryPanel().redo(this.gameBoard, Table.get().getMoveLog());
        Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
        Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
    }

    private Board getGameBoard() {
        return this.gameBoard;
    }

    private JMenuBar createTableMenuBar() {
        JMenuBar tableMenuBar = new JMenuBar();
        tableMenuBar.add(createFileMenu());
        tableMenuBar.add(createPreferencesMenu());
        return tableMenuBar;
    }

    private JMenu createFileMenu() {
        JMenu fileMenu = new JMenu("File");

        JMenuItem newGameMenuItem = new JMenuItem("New Game");
        newGameMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Table.get().getGameSetup().promptUser();
                Table.get().setupUpdate(Table.get().getGameSetup());
                Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
            }
            
        });

        JMenuItem openPGNMenuItem = new JMenuItem("Load PGN File");
        openPGNMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Opening PGN File.");
            }
        });

        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Exiting Game.");
                System.exit(0);
            }
            
        });

        fileMenu.add(newGameMenuItem);
        fileMenu.add(openPGNMenuItem);
        fileMenu.add(exitMenuItem);
        return fileMenu;
    }

    private JMenu createPreferencesMenu() {
        JMenu preferencesMenu = new JMenu("Preferences");

        JCheckBoxMenuItem highlightLegalMovesMenuItem = new JCheckBoxMenuItem("Highlight Legal Moves", true);
        highlightLegalMovesMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                highlightLegalMoves = highlightLegalMovesMenuItem.isSelected();
            }
            
        });

        preferencesMenu.add(highlightLegalMovesMenuItem);
        return preferencesMenu;
    }

    private static Map<String, Image> setupPieceIconMap() {
        Map<String, Image> pieceIconMap = new HashMap<>();
        try {
            pieceIconMap.put("white_pawn", ImageIO.read(new URL("https://i.imgur.com/9eI3GGO.png")));
            pieceIconMap.put("white_knight", ImageIO.read(new URL("https://i.imgur.com/QCZ4y53.png")));
            pieceIconMap.put("white_bishop", ImageIO.read(new URL("https://i.imgur.com/pHOTxDH.png")));
            pieceIconMap.put("white_rook", ImageIO.read(new URL("https://i.imgur.com/b3qm61t.png")));
            pieceIconMap.put("white_queen", ImageIO.read(new URL("https://i.imgur.com/bCx4z4T.png")));
            pieceIconMap.put("white_king", ImageIO.read(new URL("https://i.imgur.com/rXDoxhx.png")));

            pieceIconMap.put("black_pawn", ImageIO.read(new URL("https://i.imgur.com/13UNpni.png")));
            pieceIconMap.put("black_knight", ImageIO.read(new URL("https://i.imgur.com/nFQG8uI.png")));
            pieceIconMap.put("black_bishop", ImageIO.read(new URL("https://i.imgur.com/GJL1Lpw.png")));
            pieceIconMap.put("black_rook", ImageIO.read(new URL("https://i.imgur.com/q31CI9i.png")));
            pieceIconMap.put("black_queen", ImageIO.read(new URL("https://i.imgur.com/fO0AqnA.png")));
            pieceIconMap.put("black_king", ImageIO.read(new URL("https://i.imgur.com/6I3mOgs.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return pieceIconMap;
    }

    public void startGame() {
        this.gameStarted = true;

    }

    public void stopGame() {
        this.gameStarted = true;
    }

    public void updateGameBoard(Board board) {
        this.gameBoard = board;
    }

    public void updateComputerMove(Move move) {
        this.computerMove = move;
    }

    private void setLastMove(Move move) {
        this.lastMove = move;
    }

    private BoardPanel getBoardPanel() {
        return this.boardPanel;
    }

    private MoveLog getMoveLog() {
        return this.moveLog;
    }

    private GameHistoryPanel getGameHistoryPanel() {
        return this.gameHistoryPanel;
    }

    private TakenPiecesPanel getTakenPiecesPanel() {
        return this.takenPiecesPanel;
    }

    private void moveMadeUpdate(PlayerType playerType) {
        setChanged();
        notifyObservers(playerType);
    }

    private GameSetup getGameSetup() {
        return this.gameSetup;
    }

    private void setupUpdate(GameSetup gameSetup) {
        setChanged();
        notifyObservers(gameSetup);
        
    }

    private static class TableGameAIWatcher implements Observer {

        @Override
        public void update(Observable o, Object arg) {
            if (Table.get().getGameSetup().isAIPlayer(Table.get().getGameBoard().currentPlayer()) &&
                                      !Table.get().getGameBoard().currentPlayer().isInCheckMate() &&
                                      !Table.get().getGameBoard().currentPlayer().isInStaleMate()) {
                AIThinkTank thinkTank = new AIThinkTank();
                thinkTank.execute();
            }

            if (Table.get().getGameBoard().currentPlayer().isInCheckMate()) {
                System.out.println("Game Over, " + Table.get().getGameBoard().currentPlayer().getColor() + " is in checkmate.");
                JOptionPane.showMessageDialog(Table.get().getBoardPanel(), ("Game Over, " + Table.get().getGameBoard().currentPlayer().getColor() + " is in checkmate."));
            }

            if (Table.get().getGameBoard().currentPlayer().isInStaleMate()) {
                System.out.println("Game Over, stalemate.");
                JOptionPane.showMessageDialog(Table.get().getBoardPanel(), "Game Over, stalemate.");
            }
        }
    }

    private static class AIThinkTank extends SwingWorker<Move, String> {

        private AIThinkTank() {}

        @Override
        protected Move doInBackground() throws Exception {
            MoveStrategy minimax = new MiniMax(Table.get().getGameSetup().getSearchDepth());
            Move bestMove = minimax.execute(Table.get().getGameBoard());
            return bestMove;
        }

        @Override
        public void done() {
            try {
                Move bestMove = get();
                Table.get().updateComputerMove(bestMove);
                Table.get().setLastMove(bestMove);
                Table.get().updateGameBoard(Table.get().getGameBoard().currentPlayer().makeMove(bestMove).getTransitionBoard());
                Table.get().getMoveLog().addMove(bestMove);
                Table.get().getGameHistoryPanel().redo(Table.get().getGameBoard(), Table.get().getMoveLog());
                Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
                Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
                Table.get().moveMadeUpdate(PlayerType.COMPUTER);

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    
    private class BoardPanel extends JPanel {
        private List<TilePanel> boardTiles;

        public BoardPanel() {
            super(new GridLayout(8, 8));
            this.boardTiles = new ArrayList<>();
            for (int i = 0; i < Board.NUM_TILES; i++) {
                TilePanel tilePanel = new TilePanel(this, i);
                this.boardTiles.add(tilePanel);
                add(tilePanel);
            }
            setPreferredSize(BOARD_PANEL_DIMENSION);
            validate();
        }

        public void drawBoard(Board board) {
            removeAll();
            for (TilePanel tilePanel : boardTiles) {
                tilePanel.drawTile(board);
                add(tilePanel);
            }
            validate();
            repaint();
        }

        public TilePanel getTilePanel(int tileId) {
            return boardTiles.get(tileId);
        }

    }
    
    private class TilePanel extends JLayeredPane {

        private BoardPanel boardPanel;

        private int tileId;

        public TilePanel(BoardPanel boardPanel, int tileId) {
            this.boardPanel = boardPanel;
            this.tileId = tileId;
            setLayout(null);
            setPreferredSize(TILE_PANEL_DIMENSION);
            setOpaque(true);

            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {}

                @Override
                public void mousePressed(MouseEvent e) {
                    if (!gameSetup.isAIPlayer(gameBoard.currentPlayer()) && gameStarted) {                    
                        if (sourceTile == null) {
                            sourceTile = gameBoard.getTile(tileId);
                            movedPiece = sourceTile.getPiece();
                            if (movedPiece == null) {
                                clearTileState();
                            } else {
                                setBackground(SOURCE_TILE_COLOR);
                                boardPanel.drawBoard(gameBoard);
                            } 
                        } else if (sourceTile.getTileCoordinate() == tileId) {
                            clearTileState();
                        } else {
                            destinationTile = gameBoard.getTile(tileId);
                        }
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (!gameSetup.isAIPlayer(gameBoard.currentPlayer()) && gameStarted) {    
                        if (sourceTile != null) {
                            if (sourceTile.getTileCoordinate() != tileId) {
                                Move move = MoveFactory.createMove(gameBoard, 
                                                            sourceTile.getTileCoordinate(), 
                                                            destinationTile.getTileCoordinate());
                                MoveTransition transition = gameBoard.currentPlayer().makeMove(move);
                                if (transition.getMoveStatus().isDone()) {
                                    gameBoard = transition.getTransitionBoard();
                                    moveLog.addMove(move); 
                                    setBackground(DESTINATION_TILE_COLOR);
                                    lastMove = move;
                                }
                                clearTileState();
                            } else if (sourceTile.getTileCoordinate() != lastEnteredTile.getTileCoordinate()) {
                                destinationTile = lastEnteredTile;
                                Move move = MoveFactory.createMove(gameBoard, 
                                                            sourceTile.getTileCoordinate(), 
                                                            destinationTile.getTileCoordinate());
                                MoveTransition transition = gameBoard.currentPlayer().makeMove(move);
                                if (transition.getMoveStatus().isDone()) {
                                    gameBoard = transition.getTransitionBoard();
                                    moveLog.addMove(move); 
                                    setBackground(DESTINATION_TILE_COLOR);
                                    lastMove = move;
                                }
                                clearTileState();
                            }
                        }
                            
                        SwingUtilities.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                gameHistoryPanel.redo(gameBoard, moveLog);
                                takenPiecesPanel.redo(moveLog);
                                if (gameSetup.isAIPlayer(gameBoard.currentPlayer())) {
                                    Table.get().moveMadeUpdate(PlayerType.HUMAN);
                                }

                                boardPanel.drawBoard(gameBoard);
                            }
                        });
                    }
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    lastEnteredTile = gameBoard.getTile(tileId);
                }

                @Override
                public void mouseExited(MouseEvent e) {}
            });

            addMouseMotionListener(new MouseAdapter() {

                @Override
                public void mouseDragged(MouseEvent e) {}

                @Override
                public void mouseMoved(MouseEvent e) {}

            });
            validate();
        }


        public void drawTile(Board board) {
            assignTileColor(board);
            if (gameStarted) {
                assignPiece(board);
                highlightLegalMoves(board);
                highlightLastMove(board);
                highlightCheck(board);
                highlightCheckMate(board);

            }
            
            validate();
            repaint();
        }

        private void assignTileColor(Board board) {
            boolean isLight = ((tileId + tileId / 8) % 2 == 0);
            setBackground(isLight ? LIGHT_TILE_COLOR : DARK_TILE_COLOR);
            if (movedPiece != null && movedPiece.getColor() == board.currentPlayer().getColor()) {
                boardPanel.getTilePanel(sourceTile.getTileCoordinate()).setBackground(SOURCE_TILE_COLOR);
            }
        }


        private void highlightLegalMoves(Board board) {
            if (highlightLegalMoves) {
                for (Move move : pieceLegalMoves(board)) {
                    if (move.getDestinationCoordinate() == this.tileId) {
                        try {
                            Image legalMoveImage;
                            if (move.isAttackMove()) {
                                legalMoveImage = ImageIO.read(new File("./gui/assets/move_highlighting/attack_move.png")).getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                                //legalMoveImage = ImageIO.read(new URL("https://i.imgur.com/oE30v3z.png")).getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                            } else {
                                legalMoveImage = ImageIO.read(new File("./gui/assets/move_highlighting/basic_move.png")).getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                                //legalMoveImage = ImageIO.read(new URL("https://i.imgur.com/XuhdJv8.png")).getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                            }
                            JLabel legalMoves = new JLabel(new ImageIcon(legalMoveImage));
                            legalMoves.setBounds(0, 0, 100, 100);
                            add(legalMoves, Integer.valueOf(1));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (!board.currentPlayer().getCastlingMoves().isEmpty()) {
                    for (Move move : board.currentPlayer().getCastlingMoves()) {
                        if (move.getDestinationCoordinate() == this.tileId && sourceTile != null
                                                                           && sourceTile.getPiece().getPieceType() == PieceType.KING) {
                            try {
                                Image legalMoveImage = ImageIO.read(new File("./gui/assets/move_highlighting/basic_move.png")).getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                                //Image legalMoveImage = ImageIO.read(new URL("https://i.imgur.com/XuhdJv8.png")).getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                                JLabel legalMoves = new JLabel(new ImageIcon(legalMoveImage));
                                legalMoves.setBounds(0, 0, 100, 100);
                                add(legalMoves, Integer.valueOf(1));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }

        private Collection<Move> pieceLegalMoves(Board board) {
            if (movedPiece != null && movedPiece.getColor() == board.currentPlayer().getColor()) {
                return movedPiece.calculateLegalMoves(board);
            }
            return Collections.emptyList();
        }

        private void highlightLastMove(Board board) {
            if (lastMove != null) {
                boardPanel.getTilePanel(lastMove.getCurrentCoordinate()).setBackground(SOURCE_TILE_COLOR);
                boardPanel.getTilePanel(lastMove.getDestinationCoordinate()).setBackground(DESTINATION_TILE_COLOR);
            }
        }

        private void highlightCheck(Board board) {
            if (board.currentPlayer().isInCheck()) {
                boardPanel.getTilePanel(board.currentPlayer().getPlayerKing().getPosition()).setBackground(CHECK_COLOR);
            }
        }

        private void highlightCheckMate(Board board) {
            if (board.currentPlayer().isInCheckMate()) {
                boardPanel.getTilePanel(board.currentPlayer().getPlayerKing().getPosition()).setBackground(CHECK_MATE_COLOR);
            }
        }


        private void assignPiece(Board board) {
            this.removeAll();
            if (board.getTile(tileId).isOccupied()) {
                try {
                    String pieceIconPath = board.getTile(tileId).getPiece().getColor().name().toLowerCase() + "_" + 
                                            board.getTile(tileId).getPiece().getClass().getSimpleName().toLowerCase() + ".png";
                    Image pieceIconImage = ImageIO.read(new File("./gui/assets/piece_icons/" + pieceIconPath)).getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                    /*String pieceName = board.getTile(tileId).getPiece().getColor().name().toLowerCase() + "_" +
                                        board.getTile(tileId).getPiece().getClass().getSimpleName().toLowerCase();
                    Image pieceIconImage = PIECE_ICON_MAP.get(pieceName).getScaledInstance(100, 100, Image.SCALE_SMOOTH);*/
                    JLabel pieceIcon = new JLabel(new ImageIcon(pieceIconImage));
                    pieceIcon.setBounds(0, 0, 100, 100);
                    add(pieceIcon, Integer.valueOf(0));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private void clearTileState() {
        sourceTile = null;
        destinationTile = null;
        movedPiece = null;
    }

    public static class MoveLog {

        public class MoveStruct {
            Move move;
            String moveString;

            public MoveStruct(Move move, String moveString) {
                this.move = move;
                this.moveString = moveString;
            }
        }

        private List<MoveStruct> moves;

        public MoveLog() {
            this.moves = new ArrayList<>();
        }

        public List<MoveStruct> getMoves() {
            return moves;
        }

        public void addMove(Move move) {
            this.moves.add(new MoveStruct(move, move.toString() + calculateCheckAndCheckMateHash(Table.get().getGameBoard())));
        }

        private static String calculateCheckAndCheckMateHash(Board board) {
            if (board.currentPlayer().isInCheckMate()) {
                return "#";
            } else if (board.currentPlayer().isInCheck()) {
                return "+";
            }
            return "";
        }

        public int size() {
            return this.moves.size();
        }

        public void clear() {
            this.moves.clear();
        }

        public void removeMove(int index) {
            this.moves.remove(index);
        }

    }

    public enum PlayerType {
        HUMAN,
        COMPUTER
    }
    
}
