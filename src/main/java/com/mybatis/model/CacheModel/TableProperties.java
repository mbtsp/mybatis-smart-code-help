package com.mybatis.model.CacheModel;

import com.mybatis.model.CacheModel.Cache.TableColumnOverride;
import com.mybatis.model.CacheModel.Cache.TableIgnoredColumn;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public class TableProperties {
    private String tableName;
    private List<TableColumnOverride> columnOverrides;
    private List<TableIgnoredColumn> ignoredColumns;

    public TableProperties() {
        this.columnOverrides = Lists.newArrayList();
        this.ignoredColumns = Lists.newArrayList();
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<TableColumnOverride> getColumnOverrides() {
        return columnOverrides;
    }

    public void setColumnOverrides(List<TableColumnOverride> columnOverrides) {
        this.columnOverrides = columnOverrides;
    }

    public List<TableIgnoredColumn> getIgnoredColumns() {
        return ignoredColumns;
    }

    public void setIgnoredColumns(List<TableIgnoredColumn> ignoredColumns) {
        this.ignoredColumns = ignoredColumns;
    }
}
