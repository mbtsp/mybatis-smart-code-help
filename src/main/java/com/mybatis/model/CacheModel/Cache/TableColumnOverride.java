package com.mybatis.model.CacheModel.Cache;

public class TableColumnOverride {
    private String columnName;

    private String javaProperty;

    private String jdbcType;

    private String javaType;

    private String typeHandler;

    private boolean isColumnNameDelimited;

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getJavaProperty() {
        return javaProperty;
    }

    public void setJavaProperty(String javaProperty) {
        this.javaProperty = javaProperty;
    }

    public String getJdbcType() {
        return jdbcType;
    }

    public void setJdbcType(String jdbcType) {
        this.jdbcType = jdbcType;
    }

    public String getJavaType() {
        return javaType;
    }

    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }

    public String getTypeHandler() {
        return typeHandler;
    }

    public void setTypeHandler(String typeHandler) {
        this.typeHandler = typeHandler;
    }

    public boolean isColumnNameDelimited() {
        return isColumnNameDelimited;
    }

    public void setColumnNameDelimited(boolean columnNameDelimited) {
        isColumnNameDelimited = columnNameDelimited;
    }
}
