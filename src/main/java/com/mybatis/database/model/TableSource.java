package com.mybatis.database.model;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.util.containers.JBIterable;
import com.mybatis.model.CacheModel.Dbms;
import com.mybatis.utils.DatabaseIconUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class TableSource extends DataSource {
    private List<ColumnSource> columns;
    private List<ColumnSource> primaryKeyColumns;
    private DataConfigSource dataConfigSource;
    private String schema;
    private String databaseType;
    private String tableRemark;
    private String tableType;
    private String tableName;
    private Dbms dbms;

    public TableSource(String tableName, Dbms dbms, Project project) {
        super(tableName, dbms, project);
        this.tableName = tableName;
        this.dbms = dbms;
    }

    public TableSource() {
        super(null, Dbms.UNKNOWN, null);
    }

    @Override
    public @NotNull JBIterable<? extends PsiElement> iterateChildren() {
        return null;
    }

    @Override
    public @NotNull String getName() {
        return tableName;
    }

    @Override
    public int getSize() {
        return 0;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Override
    protected Icon getBaseIcon() {
        return DatabaseIconUtils.TABLE;
    }

    @Override
    public Dbms getDbms() {
        return dbms;
    }

    public void setDbms(Dbms dbms) {
        this.dbms = dbms;
    }

    public List<ColumnSource> getColumns() {
        return columns == null ? new ArrayList<>() : columns;
    }

    public void setColumns(List<ColumnSource> columns) {
        this.columns = columns;
    }

    @Override
    public PsiElement getParent() {
        return null;
    }

    @Override
    public boolean isValid() {
        return !getProject().isDisposed();
    }

    public List<ColumnSource> getPrimaryKeyColumns() {
        return primaryKeyColumns;
    }

    public void setPrimaryKeyColumns(List<ColumnSource> primaryKeyColumns) {
        this.primaryKeyColumns = primaryKeyColumns;
    }

    public DataConfigSource getDataConfigSource() {
        return dataConfigSource;
    }

    public void setDataConfigSource(DataConfigSource dataConfigSource) {
        this.dataConfigSource = dataConfigSource;
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
