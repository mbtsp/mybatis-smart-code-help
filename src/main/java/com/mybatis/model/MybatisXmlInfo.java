package com.mybatis.model;

import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.mybatis.enums.MethodNameEnums;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class MybatisXmlInfo {
    private MethodNameEnums tagType;
    private String namespace;
    private Map<String, XmlTag> resultMaps;
    private String id;
    private String parameterType;
    private String resultType;
    private String resultMap;
    private List<String> models;
    private Set<XmlAttributeValue> columns;
    private Set<XmlAttributeValue> propertyList;
    private Set<XmlAttributeValue> jdbcTypes;

    public MethodNameEnums getTagType() {
        return tagType;
    }

    public void setTagType(MethodNameEnums tagType) {
        this.tagType = tagType;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public Map<String, XmlTag> getResultMaps() {
        return resultMaps;
    }

    public void setResultMaps(Map<String, XmlTag> resultMaps) {
        this.resultMaps = resultMaps;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParameterType() {
        return parameterType;
    }

    public void setParameterType(String parameterType) {
        this.parameterType = parameterType;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String getResultMap() {
        return resultMap;
    }

    public void setResultMap(String resultMap) {
        this.resultMap = resultMap;
    }

    public List<String> getModels() {
        return models;
    }

    public void setModels(List<String> models) {
        this.models = models;
    }

    public Set<XmlAttributeValue> getColumns() {
        return columns;
    }

    public void setColumns(Set<XmlAttributeValue> columns) {
        this.columns = columns;
    }

    public Set<XmlAttributeValue> getPropertyList() {
        return propertyList;
    }

    public void setPropertyList(Set<XmlAttributeValue> propertyList) {
        this.propertyList = propertyList;
    }

    public Set<XmlAttributeValue> getJdbcTypes() {
        return jdbcTypes;
    }

    public void setJdbcTypes(Set<XmlAttributeValue> jdbcTypes) {
        this.jdbcTypes = jdbcTypes;
    }
}
