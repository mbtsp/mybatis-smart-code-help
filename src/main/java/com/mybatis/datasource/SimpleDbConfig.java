package com.mybatis.datasource;

import com.intellij.database.model.DasTable;
import com.intellij.database.psi.DbDataSource;
import com.intellij.database.psi.DbTable;
import com.intellij.database.util.DasUtil;
import com.intellij.util.containers.JBIterable;
import com.mybatis.enums.DataBaseType;
import com.mybatis.model.CacheModel.Dbms;
import com.mybatis.utils.DataTableUtils;
import org.mybatis.generator.api.IntellijTableInfo;

public class SimpleDbConfig implements DbConfig {
    private final String schema;
    private final DbTable dbTable;
    private final DbDataSource dbDataSource;

    public SimpleDbConfig(DbTable dbTable) {
        this.schema = DasUtil.getSchema(dbTable);
        this.dbTable = dbTable;
        this.dbDataSource = dbTable.getDataSource();
    }

    @Override
    public String getTableName() {
        JBIterable<? extends DasTable> tables = DasUtil.getTables(dbDataSource);
        for (DasTable dasTable : tables) {
            if (DasUtil.getSchema(dasTable).equals(this.schema)) {
                return dbTable.getName();
            }
        }
        return null;
    }

    @Override
    public IntellijTableInfo getTableInfos() {
        IntellijTableInfo intellijTableInfo = new IntellijTableInfo();
        intellijTableInfo.setTableName(dbTable.getName());
        intellijTableInfo.setTableType(dbTable.getTypeName());
        intellijTableInfo.setColumnInfos(DataTableUtils.getColumns(dbTable, this.dbTable));
        intellijTableInfo.setPrimaryKeyColumns(DataTableUtils.getPriMaryKey(dbTable, this.dbTable));
        intellijTableInfo.setTableRemark(dbTable.getComment());
        intellijTableInfo.setDatabaseType(DataTableUtils.getDataBaseType(dbTable).toString());
        return intellijTableInfo;
    }

//    @Override
//    public DasDataSource getDataSource() {
//        return this.dbDataSource;
//    }

    @Override
    public String getSchema() {
        return this.schema;
    }

    @Override
    public DataBaseType getDataBaseType() {
        return DataTableUtils.getDataBaseType(this.dbTable);
    }

    @Override
    public String getUserName() {
        return null;
    }

    @Override
    public String getPasswd() {
        return null;
    }

    @Override
    public String getUrl() {
        return null;
    }

    @Override
    public Dbms getDbms() {
        com.intellij.database.Dbms dbms=dbTable.getDbms();
        return  Dbms.byName(dbms.getName());
    }
}
