package com.mybatis.model.CacheModel.Cache;

import java.util.ArrayList;
import java.util.List;

public class TableSourceDto {

    private List<ColumnSourceDto> columns;

    private List<ColumnSourceDto> primaryKeyColumns;

    private String tableName;
    private String schema;
    private String databaseType;
    private String tableRemark;
    private String tableType;

    public TableSourceDto() {
        this.columns = new ArrayList<>();
        this.primaryKeyColumns = new ArrayList<>();
    }

    public List<ColumnSourceDto> getColumns() {
        return columns;
    }

    public void setColumns(List<ColumnSourceDto> columns) {
        this.columns = columns;
    }

    public List<ColumnSourceDto> getPrimaryKeyColumns() {
        return primaryKeyColumns;
    }

    public void setPrimaryKeyColumns(List<ColumnSourceDto> primaryKeyColumns) {
        this.primaryKeyColumns = primaryKeyColumns;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getDatabaseType() {
        return databaseType;
    }

    public void setDatabaseType(String databaseType) {
        this.databaseType = databaseType;
    }

    public String getTableRemark() {
        return tableRemark;
    }

    public void setTableRemark(String tableRemark) {
        this.tableRemark = tableRemark;
    }

    public String getTableType() {
        return tableType;
    }

    public void setTableType(String tableType) {
        this.tableType = tableType;
    }
}
