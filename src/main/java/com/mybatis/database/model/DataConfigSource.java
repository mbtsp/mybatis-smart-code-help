package com.mybatis.database.model;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.util.containers.JBIterable;
import com.mybatis.enums.DataBaseType;
import com.mybatis.model.CacheModel.Dbms;
import com.mybatis.utils.DatabaseIconUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;

public class DataConfigSource extends DataSource {
    private String myUniqueId;
    private String databaseName;
    private String host;
    private int port;
    private String driverClass;
    private String userName;
    private String password;
    private List<String> schemas;
    private String database;
    private String url;
    private String comment;
    private String jarUrl;
    private SchemaSource schemaSources;
    private DataBaseType dataBaseType;
    private String driverCombos;
    private String sid;

    public DataConfigSource(String databaseName, Dbms dbms, Project project) {
        super(databaseName, dbms, project);
        this.databaseName = databaseName;
    }

    public DataConfigSource() {
        super(null, Dbms.UNKNOWN, null);
    }

    @Override
    public @NotNull String getName() {
        return databaseName;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }


    public String getDriverClass() {
        return driverClass;
    }

    public void setDriverClass(String driverClass) {
        this.driverClass = driverClass;
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

    public List<String> getSchemas() {
        return schemas;
    }

    public void setSchemas(List<String> schemas) {
        this.schemas = schemas;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
    protected Icon getBaseIcon() {
        return DatabaseIconUtils.MANAGE_DATA_SOURCES_DARK;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public SchemaSource getSchemaSources() {
        return schemaSources;
    }

    public void setSchemaSources(SchemaSource schemaSources) {
        this.schemaSources = schemaSources;
    }

    public String getMyUniqueId() {
        return myUniqueId;
    }

    public void setMyUniqueId(String myUniqueId) {
        this.myUniqueId = myUniqueId;
    }

    @Override
    public boolean isValid() {
        return !getProject().isDisposed();
    }

    public String getJarUrl() {
        return jarUrl;
    }

    public void setJarUrl(String jarUrl) {
        this.jarUrl = jarUrl;
    }

    public DataBaseType getDataBaseType() {
        return dataBaseType;
    }

    public void setDataBaseType(DataBaseType dataBaseType) {
        this.dataBaseType = dataBaseType;
    }

    public String getDriverCombox() {
        return driverCombos;
    }

    public void setDriverCombox(String driverCombox) {
        this.driverCombos = driverCombox;
    }

    public String getDriverCombos() {
        return driverCombos;
    }

    public void setDriverCombos(String driverCombos) {
        this.driverCombos = driverCombos;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }
}
