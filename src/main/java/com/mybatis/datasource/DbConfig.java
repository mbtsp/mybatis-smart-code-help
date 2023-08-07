package com.mybatis.datasource;

import com.mybatis.enums.DataBaseType;
import com.mybatis.model.CacheModel.Dbms;
import org.mybatis.generator.api.IntellijTableInfo;

public interface DbConfig {
    /**
     * 获取表名
     *
     * @return list
     */
    String getTableName();

    /**
     * 获取表信息
     *
     * @return List
     */
    IntellijTableInfo getTableInfos();


    /**
     * 获取Schema
     *
     * @return String
     */
    String getSchema();

    DataBaseType getDataBaseType();

    String getUserName();

    String getPasswd();

    String getUrl();

    Dbms  getDbms();
}
