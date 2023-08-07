package com.mybatis.model.CacheModel;

import java.util.List;

public class ServiceMethod {
    private boolean override;
    private String shortName;
    private String name;
    private List<ServiceMethodParameter> parameters;

    public boolean isOverride() {
        return override;
    }

    public void setOverride(boolean override) {
        this.override = override;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ServiceMethodParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<ServiceMethodParameter> parameters) {
        this.parameters = parameters;
    }
}
