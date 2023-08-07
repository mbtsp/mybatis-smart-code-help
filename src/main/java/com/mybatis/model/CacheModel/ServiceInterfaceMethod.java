package com.mybatis.model.CacheModel;

import java.util.List;

public class ServiceInterfaceMethod {
    List<ServiceInterfaceMethodParameter> parameters;
    private String shortName;
    private String name;

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

    public List<ServiceInterfaceMethodParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<ServiceInterfaceMethodParameter> parameters) {
        this.parameters = parameters;
    }
}
