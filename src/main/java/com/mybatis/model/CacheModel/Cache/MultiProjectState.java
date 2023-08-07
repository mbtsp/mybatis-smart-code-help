package com.mybatis.model.CacheModel.Cache;

import com.mybatis.state.ProjectState;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MultiProjectState {
    private Map<String, ProjectState> projectStates;

    public MultiProjectState() {
        this.projectStates = new ConcurrentHashMap<>();
    }

    public Map<String, ProjectState> getProjectStates() {
        return projectStates == null ? new ConcurrentHashMap<>() : projectStates;
    }

    public void setProjectStates(Map<String, ProjectState> projectStates) {
        this.projectStates = projectStates;
    }
}
