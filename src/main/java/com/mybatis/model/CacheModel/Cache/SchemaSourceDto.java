package com.mybatis.model.CacheModel.Cache;

import java.util.ArrayList;
import java.util.List;

public class SchemaSourceDto {
    private String name;
    private List<TableSourceDto> tableSources;

    public SchemaSourceDto() {
        this.tableSources = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TableSourceDto> getTableSources() {
        return tableSources;
    }

    public void setTableSources(List<TableSourceDto> tableSources) {
        this.tableSources = tableSources;
    }
}
