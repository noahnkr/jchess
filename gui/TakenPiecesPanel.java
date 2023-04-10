package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import board.Move;
import board.Move.AttackMove;
import pieces.*;
import gui.Table.MoveLog;

public class TakenPiecesPanel extends JPanel {

    private JPanel northPanel;
    private JPanel southPanel;

    private static final EtchedBorder PANEL_BORDER = new EtchedBorder(EtchedBorder.RAISED);
    private static final Color PANEL_COLOR = Color.decode("#ffffff");
    private static final Dimension TAKEN_PIECES_DIMENSION = new Dimension(40, 80);

    public TakenPiecesPanel() {
        super(new BorderLayout());
        setBackground(PANEL_COLOR);
        setBorder(PANEL_BORDER);
        this.northPanel = new JPanel(new GridLayout(8, 2));
        this.southPanel = new JPanel(new GridLayout(8, 2));
        northPanel.setBackground(PANEL_COLOR);
        southPanel.setBackground(PANEL_COLOR);
        add(northPanel, BorderLayout.NORTH);
        add(southPanel, BorderLayout.SOUTH);
        setPreferredSize(TAKEN_PIECES_DIMENSION);
    }

    public void redo(MoveLog moveLog) {
        this.northPanel.removeAll();
        this.southPanel.removeAll();

        List<Piece> whiteTakenPieces = new ArrayList<>();
        List<Piece> blackTakenPieces = new ArrayList<>();

        for (Move move : moveLog.getMoves()) {
            if (move.isAttackMove()) {
                Piece takenPiece = ((AttackMove) move).getAttackedPiece();
                if (takenPiece.getColor().isWhite()) {
                    whiteTakenPieces.add(takenPiece);
                } else {
                    blackTakenPieces.add(takenPiece);
                }
            }
        }

        Collections.sort(whiteTakenPieces, new Comparator<Piece>() {

            @Override
            public int compare(Piece o1, Piece o2) {
                return Integer.compare(o1.getPieceValue(), o2.getPieceValue());
            }
        });

        Collections.sort(blackTakenPieces, new Comparator<Piece>() {

            @Override
            public int compare(Piece o1, Piece o2) {
                return Integer.compare(o1.getPieceValue(), o2.getPieceValue());
            }
        });

        for (Piece takenPiece : whiteTakenPieces) {
            try {
                String takenPieceIconPath = takenPiece.getColor().name().toLowerCase() + "_" + 
                                           takenPiece.getClass().getSimpleName().toLowerCase() + ".png";
                BufferedImage takenPieceImage = ImageIO.read(new File("./gui/assets/piece_icons/" + takenPieceIconPath));
                Image takenPieceIcon = takenPieceImage.getScaledInstance(25, 25, Image.SCALE_SMOOTH);
                JLabel takenPieceLabel = new JLabel(new ImageIcon(takenPieceIcon));
                this.southPanel.add(takenPieceLabel);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (Piece takenPiece : blackTakenPieces) {
            try {
                String takenPieceIconPath = takenPiece.getColor().name().toLowerCase() + "_" + 
                                           takenPiece.getClass().getSimpleName().toLowerCase() + ".png";
                BufferedImage takenPieceImage = ImageIO.read(new File("./gui/assets/piece_icons/" + takenPieceIconPath));
                Image takenPieceIcon = takenPieceImage.getScaledInstance(25, 25, Image.SCALE_SMOOTH);
                JLabel takenPieceLabel = new JLabel(new ImageIcon(takenPieceIcon));
                this.northPanel.add(takenPieceLabel);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        validate();
    }
    
}
