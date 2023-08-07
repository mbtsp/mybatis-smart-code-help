package com.mybatis.model;

import org.mybatis.generator.api.IntellijColumnInfo;

import java.io.Serializable;

public class ColumnInfo extends IntellijColumnInfo implements Serializable {
    private int id;
    private String javaType;
    //是否是主键
    private boolean isKey;
    private boolean isIgnore;
    private String javaName;
    private boolean update;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJavaType() {
        return javaType;
    }

    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }

    public boolean isKey() {
        return isKey;
    }

    public void setKey(boolean key) {
        isKey = key;
    }

    public boolean isIgnore() {
        return isIgnore;
    }

    public void setIgnore(boolean ignore) {
        isIgnore = ignore;
    }

    public String getJavaName() {
        return javaName;
    }

    public void setJavaName(String javaName) {
        this.javaName = javaName;
    }

    public boolean isUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }
}
