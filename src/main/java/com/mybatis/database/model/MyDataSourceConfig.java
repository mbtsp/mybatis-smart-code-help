package com.mybatis.database.model;

import com.intellij.openapi.diagnostic.Logger;
import com.mybatis.database.util.TableInfoConverter;
import com.mybatis.datasource.DbConfig;
import com.mybatis.enums.DataBaseType;
import com.mybatis.model.CacheModel.Dbms;
import org.mybatis.generator.api.IntellijColumnInfo;
import org.mybatis.generator.api.IntellijTableInfo;

import java.util.ArrayList;
import java.util.List;

public class MyDataSourceConfig implements DbConfig {
    private static final Logger log = Logger.getInstance(MyDataSourceConfig.class);
    private TableSource tableSource;

    public MyDataSourceConfig(TableSource tableSource) {
        this.tableSource = tableSource;
        log.info("init config:" + tableSource);
    }

    public String getTableName() {
        return tableSource.getTableName();
    }

    public IntellijTableInfo getTableInfos() {
        IntellijTableInfo intellijTableInfo = new IntellijTableInfo();
        intellijTableInfo.setTableName(tableSource.getTableName());
        intellijTableInfo.setTableRemark(tableSource.getTableRemark());
        intellijTableInfo.setTableType(tableSource.getTableType());
        intellijTableInfo.setDatabaseType(tableSource.getDatabaseType());
        List<IntellijColumnInfo> columnInfos = new ArrayList<>();
        List<ColumnSource> columnSources = tableSource.getColumns();
        TableInfoConverter.coverColumns(columnInfos, columnSources);
        intellijTableInfo.setColumnInfos(columnInfos);
        List<IntellijColumnInfo> primaryColumns = new ArrayList<>();
        List<ColumnSource> primaryKeyColumns = tableSource.getPrimaryKeyColumns();
        TableInfoConverter.coverColumns(primaryColumns, primaryKeyColumns);
        intellijTableInfo.setPrimaryKeyColumns(primaryColumns);
        return intellijTableInfo;
    }


    public String getSchema() {
        return tableSource.getSchema();
    }

    public DataBaseType getDataBaseType() {
        return tableSource.getDataConfigSource().getDataBaseType();
    }

    public String getUserName() {
        return tableSource.getDataConfigSource().getUserName();
    }

    public String getPasswd() {
        return tableSource.getDataConfigSource().getPassword();
    }

    @Override
    public String getUrl() {
        return tableSource.getDataConfigSource().getUrl();
    }

    @Override
    public Dbms getDbms() {
        return tableSource.getDataConfigSource().getDbms();
    }

}
