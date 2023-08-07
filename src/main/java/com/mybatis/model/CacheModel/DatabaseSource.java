package com.mybatis.model.CacheModel;

import com.mybatis.model.CacheModel.Cache.DataConfigSourceDto;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DatabaseSource {
    private Map<String, DataConfigSourceDto> sources;

    public DatabaseSource() {
        this.sources = new ConcurrentHashMap<>();
    }

    public Map<String, DataConfigSourceDto> getSources() {
        return sources;
    }

    public void setSources(Map<String, DataConfigSourceDto> sources) {
        this.sources = sources;
    }
}
