package com.mybatis.model.CacheModel.Cache;

public class TableIgnoredColumn {
    private String columnName;
    private boolean isColumnNameDelimited;

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public boolean isColumnNameDelimited() {
        return isColumnNameDelimited;
    }

    public void setColumnNameDelimited(boolean columnNameDelimited) {
        isColumnNameDelimited = columnNameDelimited;
    }
}
