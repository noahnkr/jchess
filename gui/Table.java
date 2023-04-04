package gui;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import board.Board;

public class Table {

    private JFrame gameFrame;
    private BoardPanel boardPanel;

    private static final Dimension OUTER_FRAME_DIMENSION = new Dimension(800, 800);
    private static final Dimension BOARD_PANEL_DIMENSION = new Dimension(400, 350);
    private static final Dimension TILE_PANEL_DIMENSION = new Dimension(10, 10);

    public Table() {
        this.gameFrame = new JFrame("JChess");
        JMenuBar menuBar = createTableMenuBar();
        this.gameFrame.setJMenuBar(menuBar);
        this.gameFrame.setSize(OUTER_FRAME_DIMENSION);
        this.gameFrame.setResizable(false);

        this.boardPanel = new BoardPanel();
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);

        this.gameFrame.setVisible(true);
    }

    private JMenuBar createTableMenuBar() {
        JMenuBar tableMenuBar = new JMenuBar();
        tableMenuBar.add(createFileMenu());
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

    private class BoardPanel extends JPanel {
        private ArrayList<TilePanel> boardTiles;

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

    }

    private class TilePanel extends JPanel {

        private int tileId;

        private static final Color lightTileColor = Color.decode("#2d333b");
        private static final Color darkTileColor =  Color.decode("#697484");
        
        public TilePanel(BoardPanel boardPanel, int tileId) {
            super(new GridBagLayout());
            this.tileId = tileId;
            setPreferredSize(TILE_PANEL_DIMENSION);
            assignTileColor();
            validate();
        }

        private void assignTileColor() {
            boolean isLight = ((tileId + tileId / 8) % 2 == 0);
            setBackground(isLight ? lightTileColor : darkTileColor);
        }
    }
    
}
