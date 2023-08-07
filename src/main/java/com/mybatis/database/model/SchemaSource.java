package com.mybatis.database.model;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.util.containers.JBIterable;
import com.mybatis.model.CacheModel.Dbms;
import com.mybatis.utils.DatabaseIconUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;

public class SchemaSource extends DataSource {
    private String schemaName;
    private List<TableSource> tableSources;
    private DataConfigSource dataConfigSource;

    public SchemaSource(String name, Dbms dbms, Project project) {
        super(name, dbms, project);
        this.schemaName = name;
    }

    @Override
    public @NotNull JBIterable<? extends PsiElement> iterateChildren() {
        return null;
    }

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public PsiElement getParent() {
        return null;
    }

    @Override
    public boolean isValid() {
        return !getProject().isDisposed();
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public List<TableSource> getTableSources() {
        return tableSources;
    }

    public void setTableSources(List<TableSource> tableSources) {
        this.tableSources = tableSources;
    }

    @Override
    public @NotNull String getName() {
        return this.schemaName;
    }

    public DataConfigSource getDataConfigSource() {
        return dataConfigSource;
    }

    public void setDataConfigSource(DataConfigSource dataConfigSource) {
        this.dataConfigSource = dataConfigSource;
    }

    @Override
    protected Icon getBaseIcon() {
        return DatabaseIconUtils.SCHEMA;
    }
}
