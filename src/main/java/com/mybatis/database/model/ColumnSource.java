package com.mybatis.database.model;

import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.util.containers.JBIterable;
import com.mybatis.model.CacheModel.Dbms;
import com.mybatis.utils.DatabaseIconUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class ColumnSource extends DataSource {
    private String columnName;
    private int dataType;
    private boolean generatedColumn;
    private boolean autoIncrement;
    private int size;
    private int decimalDigits;
    private String remarks;
    private String columnDefaultValue;
    private Boolean nullable;
    private short keySeq;
    private String typeName;

    public ColumnSource(String columnName, Dbms dbms, Project project) {
        super(columnName, dbms, project);
        this.columnName = columnName;
    }

    @Override
    public @NotNull JBIterable<? extends PsiElement> iterateChildren() {
        return null;
    }

    @Override
    public @NotNull String getName() {
        return columnName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public boolean isGeneratedColumn() {
        return generatedColumn;
    }

    public void setGeneratedColumn(boolean generatedColumn) {
        this.generatedColumn = generatedColumn;
    }

    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    public void setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    @Override
    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getDecimalDigits() {
        return decimalDigits;
    }

    public void setDecimalDigits(int decimalDigits) {
        this.decimalDigits = decimalDigits;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getColumnDefaultValue() {
        return columnDefaultValue;
    }

    public void setColumnDefaultValue(String columnDefaultValue) {
        this.columnDefaultValue = columnDefaultValue;
    }

    public Boolean getNullable() {
        return nullable;
    }

    public void setNullable(Boolean nullable) {
        this.nullable = nullable;
    }

    public short getKeySeq() {
        return keySeq;
    }

    public void setKeySeq(short keySeq) {
        this.keySeq = keySeq;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public PsiElement getParent() {
        return null;
    }

    @Override
    protected Icon getBaseIcon() {
        return DatabaseIconUtils.CLOUMN;
    }

    @Override
    public boolean isValid() {
        return !getProject().isDisposed();
    }

    @Override
    public @NotNull ItemPresentation getPresentation() {
        this.presentation.clear();
        this.presentation.setIcon(getIcon(false));
        this.presentation.addText(getName(), SimpleTextAttributes.REGULAR_ATTRIBUTES);
        int theSize = getSize();
        if (theSize > 0) {
            this.presentation.addText("  " + typeName.toLowerCase() + " " + size, SimpleTextAttributes.GRAYED_SMALL_ATTRIBUTES);
        }
        return this.presentation;
    }
}
