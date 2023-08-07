package com.mybatis.model.CacheModel;

import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GenerateServiceInterfaceConfig {
    private List<ServiceInterfaceMethod> methods;
    private List<String> serviceInterfaceImport;
    private Set<FullyQualifiedJavaType> fullyQualifiedJavaTypes;
    private String serviceInterfaceFileHeader;
    private String serviceInterfaceName;
    private String serviceInterfacePackageName;
    private String serviceInterfacePath;

    public List<ServiceInterfaceMethod> getMethods() {
        return methods;
    }

    public void setMethods(List<ServiceInterfaceMethod> methods) {
        this.methods = methods;
    }

    public List<String> getServiceInterfaceImport() {
        return serviceInterfaceImport;
    }

    public void setServiceInterfaceImport(List<String> serviceInterfaceImport) {
        this.serviceInterfaceImport = serviceInterfaceImport;
        this.fullyQualifiedJavaTypes = new HashSet<>();
        if (serviceInterfaceImport != null) {
            for (String str : serviceInterfaceImport) {
                fullyQualifiedJavaTypes.add(new FullyQualifiedJavaType(str));
            }
        }
    }

    public String getServiceInterfaceFileHeader() {
        return serviceInterfaceFileHeader;
    }

    public void setServiceInterfaceFileHeader(String serviceInterfaceFileHeader) {
        this.serviceInterfaceFileHeader = serviceInterfaceFileHeader;
    }

    public String getServiceInterfaceName() {
        return serviceInterfaceName;
    }

    public void setServiceInterfaceName(String serviceInterfaceName) {
        this.serviceInterfaceName = serviceInterfaceName;
    }

    public String getServiceInterfacePackageName() {
        return serviceInterfacePackageName;
    }

    public void setServiceInterfacePackageName(String serviceInterfacePackageName) {
        this.serviceInterfacePackageName = serviceInterfacePackageName;
    }

    public String getServiceInterfacePath() {
        return serviceInterfacePath;
    }

    public void setServiceInterfacePath(String serviceInterfacePath) {
        this.serviceInterfacePath = serviceInterfacePath;
    }

    public Set<FullyQualifiedJavaType> getFullyQualifiedJavaTypes() {
        return fullyQualifiedJavaTypes;
    }

    public void setFullyQualifiedJavaTypes(Set<FullyQualifiedJavaType> fullyQualifiedJavaTypes) {
        this.fullyQualifiedJavaTypes = fullyQualifiedJavaTypes;
    }
}
