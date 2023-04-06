package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.List;
import java.util.ArrayList;


import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import board.Move;
import board.Move.AttackMove;
import pieces.*;
import gui.Table.MoveLog;

public class TakenPiecesPanel extends JPanel {

    private JPanel northPanel;
    private JPanel southPanel;

    private static final EtchedBorder PANEL_BORDER = new EtchedBorder(EtchedBorder.RAISED);
    private static final Color PANEL_COLOR = Color.decode("0xFDFE6");
    private static final Dimension TAKEN_PIECES_DIMENSION = new Dimension(40, 80);

    public TakenPiecesPanel() {
        super(new BorderLayout());
        setBackground(Color.decode("0xFDF5E6"));
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
        List<Piece> blackTakenpieces = new ArrayList<>();

        for (Move move : moveLog.getMoves()) {
            if (move.isAttackMove()) {
                Piece takenPiece = ((AttackMove) move).getAttackedPiece();
                if (takenPiece.getColor().isWhite()) {
                    whiteTakenPieces.add(takenPiece);
                } else {
                    blackTakenpieces.add(takenPiece);

                }
            }
            
        }

    }
    
}
