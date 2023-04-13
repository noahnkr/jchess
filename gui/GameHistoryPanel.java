package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import java.util.ArrayList;
import java.util.List;

import board.Board;
import board.Move;
import gui.Table.MoveLog;
import gui.Table.MoveLog.MoveStruct;

public class GameHistoryPanel extends JPanel {

    private DataModel model;
    private JScrollPane scrollPane;

    private static final Dimension HISTORY_PANEL_DIMENSION = new Dimension(100, 40);

    public GameHistoryPanel() {
        this.setLayout(new BorderLayout());
        this.model = new DataModel();
        JTable table = new JTable(model);
        table.setRowHeight(15);
        this.scrollPane = new JScrollPane(table);
        scrollPane.setColumnHeaderView(table.getTableHeader());
        scrollPane.setPreferredSize(HISTORY_PANEL_DIMENSION);
        this.add(scrollPane, BorderLayout.CENTER);
        this.setVisible(true);
    }

    public void redo(Board board, MoveLog moveHistory) {
        int currentRow = 0;
        this.model.clear();
        for (MoveStruct moveStruct : moveHistory.getMoves()) {
            if (moveStruct.move.getMovedPiece().getColor().isWhite()) {
                this.model.setValueAt(moveStruct.moveString, currentRow, 0);
            } else if (moveStruct.move.getMovedPiece().getColor().isBlack()) {
                this.model.setValueAt(moveStruct.moveString, currentRow, 1);
                currentRow++;
            }
        }

        if (moveHistory.getMoves().size() > 0) {
            MoveStruct lastMove = moveHistory.getMoves().get(moveHistory.size() - 1);

            if (lastMove.move.getMovedPiece().getColor().isWhite()) {
                this.model.setValueAt(lastMove.moveString, currentRow, 0);
            } else if (lastMove.move.getMovedPiece().getColor().isBlack()) {
                this.model.setValueAt(lastMove.moveString, currentRow - 1, 1);
            }
        }

        JScrollBar vertical = scrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());
    }

    

    private static class Row {

        private String whiteMove;
        private String blackMove;

        private Row() {}

        public String getWhiteMove() {
            return this.whiteMove;
        }

        public String getBlackMove() {
            return this.blackMove;
        }

        public void setWhiteMove(String move) {
            this.whiteMove = move;
        }

        public void setBlackMove(String move) {
            this.blackMove = move;
        }

    }

    private static class DataModel extends DefaultTableModel {

        private List<Row> values;
        private static final String[] NAMES = { "White", "Black" };

        public DataModel() {
            this.values = new ArrayList<>();
        }

        public void clear() {
            this.values.clear();
            setRowCount(0);
        }

        @Override
        public int getRowCount() {
            if (this.values == null) {
                return 0;
            }
            return this.values.size();
        }

        @Override
        public int getColumnCount() {
            return NAMES.length;
        }

        @Override
        public Object getValueAt(int row, int col) {
            Row currentRow = this.values.get(row);
            if (col == 0) {
                return currentRow.getWhiteMove();
            } else if (col == 1) {
                return currentRow.getBlackMove();
            }
            return null;
        }

        @Override
        public void setValueAt(Object aValue, int row, int col) {
            final Row currentRow;
            if (this.values.size() <= row) {
                currentRow = new Row();
                this.values.add(currentRow);
            } else {
                currentRow = this.values.get(row);
            }
            if (col == 0) {
                currentRow.setWhiteMove((String) aValue);
                fireTableRowsInserted(row, row);
            } else if (col == 1) {
                currentRow.setBlackMove((String) aValue);
                fireTableCellUpdated(row, col);
            }
        }

        @Override
        public Class<?> getColumnClass(int col) {
            return Move.class;
        }

        @Override
        public String getColumnName(int col) {
            return NAMES[col];
        }
    }
}
