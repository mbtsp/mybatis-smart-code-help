package com.mybatis.model;

import com.mybatis.enums.DataBaseType;
import com.mybatis.model.CacheModel.Dbms;
import org.jetbrains.annotations.NotNull;
import org.mybatis.generator.api.IntellijTableInfo;

public class GeneratorConfig {
    private String tableName;
    private String schema;
    private IntellijTableInfo intellijTableInfo;
    private DataBaseType dataBaseType;
    private String userName;
    private String password;
    private String url;
    private String displayName;
    private Dbms dbms;
    private boolean verify;

    @NotNull
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

    public IntellijTableInfo getIntellijTableInfo() {
        return intellijTableInfo;
    }

    public void setIntellijTableInfo(IntellijTableInfo intellijTableInfo) {
        this.intellijTableInfo = intellijTableInfo;
    }

    public DataBaseType getDataBaseType() {
        return dataBaseType;
    }

    public void setDataBaseType(DataBaseType dataBaseType) {
        this.dataBaseType = dataBaseType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isVerify() {
        return verify;
    }

    public void setVerify(boolean verify) {
        this.verify = verify;
    }

    public Dbms getDbms() {
        return dbms;
    }

    public void setDbms(Dbms dbms) {
        this.dbms = dbms;
    }
}
