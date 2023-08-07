package com.mybatis.model;

import com.mybatis.messages.MybatisSmartCodeHelpBundle;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class MyTableModel extends AbstractTableModel {
    String[] columnNames = new String[]{MybatisSmartCodeHelpBundle.message("table.column.id"), MybatisSmartCodeHelpBundle.message("table.column.name"),
            MybatisSmartCodeHelpBundle.message("table.column.jdbc.type"), MybatisSmartCodeHelpBundle.message("table.column.java.name"),
            MybatisSmartCodeHelpBundle.message("table.column.java.type"), MybatisSmartCodeHelpBundle.message("table.column.is.pk"),
            MybatisSmartCodeHelpBundle.message("table.column.is.ignore")};
    private List<ColumnInfo> columnInfos;

    public MyTableModel() {
    }

    public MyTableModel(List<ColumnInfo> columnInfos) {
        this.columnInfos = columnInfos;
    }

    /**
     * Returns the number of rows in the model. A
     * <code>JTable</code> uses this method to determine how many rows it
     * should display.  This method should be quick, as it
     * is called frequently during rendering.
     *
     * @return the number of rows in the model
     * @see #getColumnCount
     */
    @Override
    public int getRowCount() {
        return columnInfos.size();
    }

    /**
     * Returns a default name for the column using spreadsheet conventions:
     * A, B, C, ... Z, AA, AB, etc.  If <code>column</code> cannot be found,
     * returns an empty string.
     *
     * @param column the column being queried
     * @return a string containing the default name of <code>column</code>
     */
    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    /**
     * Returns the number of columns in the model. A
     * <code>JTable</code> uses this method to determine how many columns it
     * should create and display by default.
     *
     * @return the number of columns in the model
     * @see #getRowCount
     */
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    /**
     * Returns the value for the cell at <code>columnIndex</code> and
     * <code>rowIndex</code>.
     *
     * @param rowIndex    the row whose value is to be queried
     * @param columnIndex the column whose value is to be queried
     * @return the value Object at the specified cell
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnInfos == null || columnInfos.isEmpty()) {
            return null;
        }
        ColumnInfo columnInfo = columnInfos.get(rowIndex);
        if (columnIndex == 0) {
            return columnInfo.getId();
        } else if (columnIndex == 1) {
            return columnInfo.getName();
        } else if (columnIndex == 2) {
            return columnInfo.getTypeName();
        } else if (columnIndex == 3) {
            return columnInfo.getJavaName();
        } else if (columnIndex == 4) {
            return columnInfo.getJavaType();
        } else if (columnIndex == 5) {
            return columnInfo.isKey();
        } else if (columnIndex == 6) {
            return columnInfo.isIgnore();
        }
        return null;
    }

    /**
     * This empty implementation is provided so users don't have to implement
     * this method if their data model is not editable.
     *
     * @param aValue      value to assign to cell
     * @param rowIndex    row of cell
     * @param columnIndex column of cell
     */
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        ColumnInfo columnInfo = columnInfos.get(rowIndex);
        if (columnIndex == 0) {
            columnInfo.setId((Integer) aValue);
        } else if (columnIndex == 1) {
            columnInfo.setName((String) aValue);
        } else if (columnIndex == 2) {
            columnInfo.setTypeName((String) aValue);
        } else if (columnIndex == 3) {
            columnInfo.setJavaName((String) aValue);
        } else if (columnIndex == 4) {
            columnInfo.setJavaType((String) aValue);
        } else if (columnIndex == 5) {
            columnInfo.setKey((Boolean) aValue);
            reduction(columnInfo.getId());
        } else if (columnIndex == 6) {
            columnInfo.setIgnore((Boolean) aValue);
        }
        columnInfo.setUpdate(true);
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    private void reduction(int id) {
        if (columnInfos == null || columnInfos.isEmpty()) {
            return;
        }
        columnInfos.forEach(columnInfo -> {
            if (columnInfo.getId() != id) {
                columnInfo.setKey(false);
            }
        });
    }


    public void setColumnInfos(List<ColumnInfo> columnInfos) {
        this.columnInfos = columnInfos;
    }

    /**
     * Returns <code>Object.class</code> regardless of <code>columnIndex</code>.
     *
     * @param columnIndex the column being queried
     * @return the Object.class
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return getValueAt(0, columnIndex).getClass();
    }

    /**
     * Returns false.  This is the default implementation for all cells.
     *
     * @param rowIndex    the row being queried
     * @param columnIndex the column being queried
     * @return false
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex > 1;
    }

    /**
     * Notifies all listeners that the value of the cell at
     * <code>[row, column]</code> has been updated.
     *
     * @param row    row of cell which has been updated
     * @param column column of cell which has been updated
     */
    @Override
    public void fireTableCellUpdated(int row, int column) {
        super.fireTableCellUpdated(row, column);
    }

    /**
     * Notifies all listeners that rows in the range
     * <code>[firstRow, lastRow]</code>, inclusive, have been updated.
     *
     * @param firstRow the first row
     * @param lastRow  the last row
     */
    @Override
    public void fireTableRowsUpdated(int firstRow, int lastRow) {
        super.fireTableRowsUpdated(firstRow, lastRow);
    }
}
