package com.mybatis.database.model;

import com.mybatis.database.connect.DatabaseArtifactList;
import com.mybatis.enums.DataBaseType;
import org.jetbrains.annotations.NotNull;

public class DatabaseConfig {
    private String comment;
    /**
     * The primary key in the sqlite db
     */
    private Integer id;

    private DataBaseType dataBaseType;
    /**
     * The name of the config
     */
    private String name;

    private String host;

    private int port;

    private String schema;

    private String username;

    private String password;

    private DatabaseArtifactList.ArtifactVersion artifactVersion;
    private String jarUrl;
    private String url;
    private String driverCombos;
    private String sid;

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    @NotNull
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public DataBaseType getDataBaseType() {
        return dataBaseType;
    }

    public void setDataBaseType(DataBaseType dataBaseType) {
        this.dataBaseType = dataBaseType;
    }

    public DatabaseArtifactList.ArtifactVersion getArtifactVersion() {
        return artifactVersion;
    }

    public void setArtifactVersion(DatabaseArtifactList.ArtifactVersion artifactVersion) {
        this.artifactVersion = artifactVersion;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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
