package com.mybatis.model.CacheModel;

import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GenerateServiceConfig {
    private List<ServiceMethod> methods;
    private List<String> imports;
    private Set<FullyQualifiedJavaType> fullyQualifiedJavaTypes;
    private boolean useServiceInterface;
    private String serviceInterfaceFullName;
    private String serviceFileHeader;
    private String daoType;
    private String daoName;
    private String serviceInterfaceName;
    private String serviceName;
    private String servicePackageName;
    private String servicePath;

    public List<ServiceMethod> getMethods() {
        return methods;
    }

    public void setMethods(List<ServiceMethod> methods) {
        this.methods = methods;
    }

    public List<String> getImports() {
        return imports;
    }

    public void setImports(List<String> imports) {
        this.imports = imports;
        this.fullyQualifiedJavaTypes = new HashSet<>();
        if (imports != null) {
            for (String str : imports) {
                fullyQualifiedJavaTypes.add(new FullyQualifiedJavaType(str));
            }
        }
    }

    public boolean isUseServiceInterface() {
        return useServiceInterface;
    }

    public void setUseServiceInterface(boolean useServiceInterface) {
        this.useServiceInterface = useServiceInterface;
    }

    public String getServiceInterfaceFullName() {
        return serviceInterfaceFullName;
    }

    public void setServiceInterfaceFullName(String serviceInterfaceFullName) {
        this.serviceInterfaceFullName = serviceInterfaceFullName;
    }

    public String getServiceFileHeader() {
        return serviceFileHeader;
    }

    public void setServiceFileHeader(String serviceFileHeader) {
        this.serviceFileHeader = serviceFileHeader;
    }

    public String getDaoType() {
        return daoType;
    }

    public void setDaoType(String daoType) {
        this.daoType = daoType;
    }

    public String getDaoName() {
        return daoName;
    }

    public void setDaoName(String daoName) {
        this.daoName = daoName;
    }

    public String getServiceInterfaceName() {
        return serviceInterfaceName;
    }

    public void setServiceInterfaceName(String serviceInterfaceName) {
        this.serviceInterfaceName = serviceInterfaceName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServicePackageName() {
        return servicePackageName;
    }

    public void setServicePackageName(String servicePackageName) {
        this.servicePackageName = servicePackageName;
    }

    public String getServicePath() {
        return servicePath;
    }

    public void setServicePath(String servicePath) {
        this.servicePath = servicePath;
    }

    public Set<FullyQualifiedJavaType> getFullyQualifiedJavaTypes() {
        return fullyQualifiedJavaTypes;
    }

    public void setFullyQualifiedJavaTypes(Set<FullyQualifiedJavaType> fullyQualifiedJavaTypes) {
        this.fullyQualifiedJavaTypes = fullyQualifiedJavaTypes;
    }
}
