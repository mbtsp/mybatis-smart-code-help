package com.mybatis.database.model;

public class TableInfo {
    private String tableName;
    private String tableType;
    private String comment;
    private String engine;
    private String tableCollation;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableType() {
        return tableType;
    }

    public void setTableType(String tableType) {
        this.tableType = tableType;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public String getTableCollation() {
        return tableCollation;
    }

    public void setTableCollation(String tableCollation) {
        this.tableCollation = tableCollation;
    }
}
