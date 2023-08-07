package com.mybatis.model.CacheModel.Cache;

import com.mybatis.enums.DataBaseType;

import java.util.ArrayList;
import java.util.List;

public class DataConfigSourceDto {
    private String myUniqueId;
    private String databaseName;
    private String host;
    private int port;
    private String driverClass;
    private String userName;
    private List<String> schemas;
    private String Database;
    private String url;
    private String comment;
    private SchemaSourceDto schemaSourceDto;
    private DataBaseType dataBaseType;
    private String jarUrl;
    private String driverCombos;
    private String sid;

    public DataConfigSourceDto() {
        schemas = new ArrayList<>();
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public DataBaseType getDataBaseType() {
        return dataBaseType;
    }

    public void setDataBaseType(DataBaseType dataBaseType) {
        this.dataBaseType = dataBaseType;
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


    public List<String> getSchemas() {
        return schemas;
    }

    public void setSchemas(List<String> schemas) {
        this.schemas = schemas;
    }

    public String getDatabase() {
        return Database;
    }

    public void setDatabase(String database) {
        Database = database;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public SchemaSourceDto getSchemaSourceDto() {
        return schemaSourceDto;
    }

    public void setSchemaSourceDto(SchemaSourceDto schemaSourceDto) {
        this.schemaSourceDto = schemaSourceDto;
    }

    public String getMyUniqueId() {
        return myUniqueId;
    }

    public void setMyUniqueId(String myUniqueId) {
        this.myUniqueId = myUniqueId;
    }

    public String getJarUrl() {
        return jarUrl;
    }

    public void setJarUrl(String jarUrl) {
        this.jarUrl = jarUrl;
    }

    public String getDriverCombos() {
        return driverCombos;
    }

    public void setDriverCombos(String driverCombos) {
        this.driverCombos = driverCombos;
    }
}
