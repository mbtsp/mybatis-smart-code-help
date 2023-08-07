package com.mybatis.model.CacheModel;

import org.apache.commons.compress.utils.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TableSupportConfig {
    private String schema;
    private String tableName;
    private String modelName;
    private String key;
    private List<String> javaModules;
    private Map<String, TableProperties> tablePropertiesMap;
    private boolean useActualColumnNames;

    public TableSupportConfig() {
        this.javaModules = Lists.newArrayList();
        this.tablePropertiesMap = new ConcurrentHashMap<>();
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public List<String> getJavaModules() {
        return javaModules == null ? new ArrayList<>() : javaModules;
    }

    public void setJavaModules(List<String> javaModules) {
        this.javaModules = javaModules;
    }

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

    public Map<String, TableProperties> getTablePropertiesMap() {
        return tablePropertiesMap;
    }

    public void setTablePropertiesMap(Map<String, TableProperties> tablePropertiesMap) {
        this.tablePropertiesMap = tablePropertiesMap;
    }

    public boolean isUseActualColumnNames() {
        return useActualColumnNames;
    }

    public void setUseActualColumnNames(boolean useActualColumnNames) {
        this.useActualColumnNames = useActualColumnNames;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
