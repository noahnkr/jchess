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
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import board.Board;
import board.Move;
import board.MoveTransition;
import board.Tile;
import board.Move.MoveFactory;
import pieces.Piece;

public class Table {

    private Board gameBoard;

    private JFrame gameFrame;
    private BoardPanel boardPanel;
    private GameHistoryPanel gameHistoryPanel;
    private TakenPiecesPanel takenPiecesPanel;

    private Tile sourceTile;
    private Tile destinationTile;
    private Piece movedPiece;

    private MoveLog moveHistory;
    private Move lastMove;
    private Tile lastEnteredTile;

    private boolean highlightLegalMoves;

    private static final Dimension OUTER_FRAME_DIMENSION = new Dimension(1000, 800);
    private static final Dimension BOARD_PANEL_DIMENSION = new Dimension(400, 350);
    private static final Dimension TILE_PANEL_DIMENSION = new Dimension(100, 100);

    private static final Color lightTileColor = Color.decode("#2d333b");
    private static final Color darkTileColor =  Color.decode("#697484");
    private static final Color sourceTileColor = Color.decode("#63839c");
    private static final Color destinationTileColor = Color.decode("#405e75");

    public Table() {
        this.gameBoard = Board.createStandardBoard();
        this.gameFrame = new JFrame("JChess");
        this.gameFrame.setJMenuBar(createTableMenuBar());
        this.gameHistoryPanel = new GameHistoryPanel();
        this.takenPiecesPanel = new TakenPiecesPanel();
        this.highlightLegalMoves = true;
        this.gameFrame.setSize(OUTER_FRAME_DIMENSION);
        this.gameFrame.setResizable(false);
        this.boardPanel = new BoardPanel();
        this.moveHistory = new MoveLog();
        this.gameFrame.add(this.takenPiecesPanel, BorderLayout.WEST);
        this.gameFrame.add(this.gameHistoryPanel, BorderLayout.EAST);
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
        this.gameFrame.setVisible(true);
    }

    private JMenuBar createTableMenuBar() {
        JMenuBar tableMenuBar = new JMenuBar();
        tableMenuBar.add(createFileMenu());
        tableMenuBar.add(createPreferencesMenu());
        return tableMenuBar;
    }

    private JMenu createFileMenu() {
        JMenu fileMenu = new JMenu("File");
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

        protected boolean drawPiece;

        public TilePanel(BoardPanel boardPanel, int tileId) {
            this.boardPanel = boardPanel;
            this.tileId = tileId;
            this.drawPiece = true;
            setLayout(null);
            setPreferredSize(TILE_PANEL_DIMENSION);
            drawTile(gameBoard);
            setOpaque(true);

            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {}

                @Override
                public void mousePressed(MouseEvent e) {
                    if (sourceTile == null) {
                        sourceTile = gameBoard.getTile(tileId);
                        movedPiece = sourceTile.getPiece();
                        if (movedPiece == null) {
                            clearTileState();
                        } else {
                            setBackground(sourceTileColor);
                            boardPanel.drawBoard(gameBoard);
                            /*try {
                                String movedPieceImagePath = movedPiece.getColor().name().toLowerCase() + "_" + 
                                                   movedPiece.getClass().getSimpleName().toLowerCase() + ".png";
                                BufferedImage movedPieceBufferedImage = ImageIO.read(new File("./gui/assets/piece_icons/" + movedPieceImagePath));
                                Image movedPieceImage = movedPieceBufferedImage.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }*/
                        } 
                    } else if (sourceTile.getTileCoordinate() == tileId) {
                        clearTileState();
                    } else {
                        destinationTile = gameBoard.getTile(tileId);
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (sourceTile != null) {
                        if (sourceTile.getTileCoordinate() != tileId) {
                            Move move = MoveFactory.createMove(gameBoard, 
                                                        sourceTile.getTileCoordinate(), 
                                                        destinationTile.getTileCoordinate());
                            MoveTransition transition = gameBoard.currentPlayer().makeMove(move);
                            if (transition.getMoveStatus().isDone()) {
                                gameBoard = transition.getTransitionBoard();
                                moveHistory.addMove(move); 
                                setBackground(destinationTileColor);
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
                                moveHistory.addMove(move); 
                                setBackground(destinationTileColor);
                                lastMove = move;
                            }
                            clearTileState();
                        }
                    }
                        
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            gameHistoryPanel.redo(gameBoard, moveHistory);
                            takenPiecesPanel.redo(moveHistory);
                            boardPanel.drawBoard(gameBoard);
                        }
                    });
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
                public void mouseDragged(MouseEvent e) {
                    /*if (movedPiece != null) {
                        drawPiece = false;

                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                boardPanel.drawBoard(gameBoard);
                                
                            }
                        });
                    }*/      
                }

                @Override
                public void mouseMoved(MouseEvent e) {}

                

            });
            validate();
        }


        public void drawTile(Board board) {
            assignTileColor(board);
            assignPiece(board);
            highlightLegalMoves(board);
            //highlightSelectedTile(board);
            highlightLastMove(board);
            validate();
            repaint();
        }

        private void highlightLegalMoves(Board board) {
            if (highlightLegalMoves) {
                for (Move move : pieceLegalMoves(board)) {
                    if (move.getDestinationCoordinate() == this.tileId) {
                        try {
                            BufferedImage legalMoveImage;
                            if (move.isAttackMove()) {
                                legalMoveImage = ImageIO.read(new File("./gui/assets/move_highlighting/attack_move.png"));
                            } else {
                                legalMoveImage = ImageIO.read(new File("./gui/assets/move_highlighting/basic_move.png"));
                            }
                            Image scaledLegalMoveImage = legalMoveImage.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                            JLabel legalMoves = new JLabel(new ImageIcon(scaledLegalMoveImage));
                            legalMoves.setBounds(0, 0, 100, 100);
                            add(legalMoves, Integer.valueOf(1));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        private void highlightLastMove(Board board) {
            // TODO: fix hgighlighting with flipped board
            if (lastMove != null) {
                boardPanel.getTilePanel(lastMove.getCurrentCoordinate()).setBackground(sourceTileColor);
                boardPanel.getTilePanel(lastMove.getDestinationCoordinate()).setBackground(destinationTileColor);
            }
        }

        
        private Collection<Move> pieceLegalMoves(Board board) {
            if (movedPiece != null && movedPiece.getColor() == board.currentPlayer().getColor()) {
                return movedPiece.calculateLegalMoves(board);
            }
            return Collections.emptyList();
        }

        private void highlightSelectedTile(Board board) {
            try {
                BufferedImage hoveredTileIcon = ImageIO.read(new File("./gui/assets/move_highlighting/hovered_tile.png"));
                Image scaledHoveredTileIcon = hoveredTileIcon.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                JLabel hoveredtile = new JLabel(new ImageIcon(scaledHoveredTileIcon));
                add(hoveredtile, Integer.valueOf(2));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        private void assignTileColor(Board board) {
            boolean isLight = ((tileId + tileId / 8) % 2 == 0);
            setBackground(isLight ? lightTileColor : darkTileColor);
            if (movedPiece != null && movedPiece.getColor() == board.currentPlayer().getColor()) {
                boardPanel.getTilePanel(sourceTile.getTileCoordinate()).setBackground(sourceTileColor);
            }
        }

        private void assignPiece(Board board) {
            this.removeAll();
            if (board.getTile(tileId).isOccupied()) {
                try {
                    String pieceIconPath = board.getTile(tileId).getPiece().getColor().name().toLowerCase() + "_" + 
                                           board.getTile(tileId).getPiece().getClass().getSimpleName().toLowerCase() + ".png";
                    BufferedImage pieceIconImage = ImageIO.read(new File("./gui/assets/piece_icons/" + pieceIconPath));
                    Image scaledPieceIconImage = pieceIconImage.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                    JLabel pieceIcon = new JLabel(new ImageIcon(scaledPieceIconImage));
                    pieceIcon.setBounds(0, 0, 100, 100);
                    if (drawPiece) {
                        add(pieceIcon, Integer.valueOf(0));
                    }
                    
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

        private List<Move> moves;

        public MoveLog() {
            this.moves = new ArrayList<>();
        }

        public List<Move> getMoves() {
            return moves;
        }

        public void addMove(Move move) {
            this.moves.add(move);
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

        public boolean removeMove(Move move) {
            return moves.remove(move);
        }
    }
    
}
