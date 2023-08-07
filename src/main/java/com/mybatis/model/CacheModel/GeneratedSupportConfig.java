package com.mybatis.model.CacheModel;

import java.util.ArrayList;
import java.util.List;

public class GeneratedSupportConfig extends TableSupportConfig {
    private String javaModelPackage = "com.example.model";
    private String javaModelSourcePackage;
    private String javaMapperPackage = "com.example.mapper";
    private String javaMapperSourcePackage;
    private String javaXmlPackage = "mapper";
    private String javaXmlSourcePackage;
    //0 简易模式  1 完整模式  -1 纯净模式
    private Integer serviceMode;

    //去除前缀
    private String rmPrefix;
    //是否生成service impl
    private boolean generatorService;
    private String javaServicePackage = "com.example.service.impl";
    private String javaServiceSourcePackage;
    //是否生成serviceInterface
    private boolean generatorServiceInterface;
    private String javaServiceInterfacePackage = "com.example.service";
    private String javaServiceInterfaceSourcePackage;
    private List<String> primaryKeys;
    //lombok
    private boolean lombokData = true;
    private boolean lombokGetterSetter;
    private boolean lombokBuilder;
    private boolean lombokNoArgsConstructor;
    private boolean lombokAllArgsConstructor;
    //lombok end
    private boolean toString;
    private boolean equalHashCode;
    private boolean noJdbcType;
    private boolean modelStringTrim;
    //@Mapper
    private boolean annotationMapper;
    //生成注释
    private boolean comment = true;

    private boolean addSchema;
    //区分Blob
    private boolean checkBlob;

    private boolean serializable;
    private boolean generatorExample;
    //swagger  config
    private boolean swagger;
    //接口方法生成注释
    private boolean interfaceComment;
    //驼峰命名
    private boolean useActualColumnNamesCheckBox;
    //数据库命名
    private boolean userDbCheckBox;

    //tk.mappers
    private boolean userTkMapper;
    private String tkMappers;
    private boolean caseSensitive;
    private boolean forceAnnotation;

    public String getJavaModelPackage() {
        return javaModelPackage;
    }

    public void setJavaModelPackage(String javaModelPackage) {
        this.javaModelPackage = javaModelPackage;
    }

    public String getJavaModelSourcePackage() {
        return javaModelSourcePackage;
    }

    public void setJavaModelSourcePackage(String javaModelSourcePackage) {
        this.javaModelSourcePackage = javaModelSourcePackage;
    }

    public String getJavaMapperPackage() {
        return javaMapperPackage;
    }

    public void setJavaMapperPackage(String javaMapperPackage) {
        this.javaMapperPackage = javaMapperPackage;
    }

    public String getJavaMapperSourcePackage() {
        return javaMapperSourcePackage;
    }

    public void setJavaMapperSourcePackage(String javaMapperSourcePackage) {
        this.javaMapperSourcePackage = javaMapperSourcePackage;
    }

    public String getJavaXmlPackage() {
        return javaXmlPackage;
    }

    public void setJavaXmlPackage(String javaXmlPackage) {
        this.javaXmlPackage = javaXmlPackage;
    }

    public String getJavaXmlSourcePackage() {
        return javaXmlSourcePackage;
    }

    public void setJavaXmlSourcePackage(String javaXmlSourcePackage) {
        this.javaXmlSourcePackage = javaXmlSourcePackage;
    }

    public String getJavaServicePackage() {
        return javaServicePackage;
    }

    public void setJavaServicePackage(String javaServicePackage) {
        this.javaServicePackage = javaServicePackage;
    }

    public String getJavaServiceSourcePackage() {
        return javaServiceSourcePackage;
    }

    public void setJavaServiceSourcePackage(String javaServiceSourcePackage) {
        this.javaServiceSourcePackage = javaServiceSourcePackage;
    }

    public String getJavaServiceInterfacePackage() {
        return javaServiceInterfacePackage;
    }

    public void setJavaServiceInterfacePackage(String javaServiceInterfacePackage) {
        this.javaServiceInterfacePackage = javaServiceInterfacePackage;
    }

    public String getJavaServiceInterfaceSourcePackage() {
        return javaServiceInterfaceSourcePackage;
    }

    public void setJavaServiceInterfaceSourcePackage(String javaServiceInterfaceSourcePackage) {
        this.javaServiceInterfaceSourcePackage = javaServiceInterfaceSourcePackage;
    }

    public List<String> getPrimaryKeys() {
        return primaryKeys == null ? new ArrayList<>() : primaryKeys;
    }

    public void setPrimaryKeys(List<String> primaryKeys) {
        this.primaryKeys = primaryKeys;
    }

    public boolean isLombokData() {
        return lombokData;
    }

    public void setLombokData(boolean lombokData) {
        this.lombokData = lombokData;
    }

    public boolean isLombokGetterSetter() {
        return lombokGetterSetter;
    }

    public void setLombokGetterSetter(boolean lombokGetterSetter) {
        this.lombokGetterSetter = lombokGetterSetter;
    }

    public boolean isLombokNoArgsConstructor() {
        return lombokNoArgsConstructor;
    }

    public void setLombokNoArgsConstructor(boolean lombokNoArgsConstructor) {
        this.lombokNoArgsConstructor = lombokNoArgsConstructor;
    }

    public boolean isLombokAllArgsConstructor() {
        return lombokAllArgsConstructor;
    }

    public void setLombokAllArgsConstructor(boolean lombokAllArgsConstructor) {
        this.lombokAllArgsConstructor = lombokAllArgsConstructor;
    }

    public boolean isToString() {
        return toString;
    }

    public void setToString(boolean toString) {
        this.toString = toString;
    }

    public boolean isEqualHashCode() {
        return equalHashCode;
    }

    public void setEqualHashCode(boolean equalHashCode) {
        this.equalHashCode = equalHashCode;
    }

    public boolean isNoJdbcType() {
        return noJdbcType;
    }

    public void setNoJdbcType(boolean noJdbcType) {
        this.noJdbcType = noJdbcType;
    }

    public boolean isModelStringTrim() {
        return modelStringTrim;
    }

    public void setModelStringTrim(boolean modelStringTrim) {
        this.modelStringTrim = modelStringTrim;
    }

    public boolean isAnnotationMapper() {
        return annotationMapper;
    }

    public void setAnnotationMapper(boolean annotationMapper) {
        this.annotationMapper = annotationMapper;
    }

    public boolean isComment() {
        return comment;
    }

    public void setComment(boolean comment) {
        this.comment = comment;
    }

    public boolean isAddSchema() {
        return addSchema;
    }

    public void setAddSchema(boolean addSchema) {
        this.addSchema = addSchema;
    }

    public boolean isCheckBlob() {
        return checkBlob;
    }

    public void setCheckBlob(boolean checkBlob) {
        this.checkBlob = checkBlob;
    }

    public boolean isSerializable() {
        return serializable;
    }

    public void setSerializable(boolean serializable) {
        this.serializable = serializable;
    }

    public boolean isGeneratorExample() {
        return generatorExample;
    }

    public void setGeneratorExample(boolean generatorExample) {
        this.generatorExample = generatorExample;
    }

    public boolean isSwagger() {
        return swagger;
    }

    public void setSwagger(boolean swagger) {
        this.swagger = swagger;
    }

    public boolean isInterfaceComment() {
        return interfaceComment;
    }

    public void setInterfaceComment(boolean interfaceComment) {
        this.interfaceComment = interfaceComment;
    }

    public boolean isLombokBuilder() {
        return lombokBuilder;
    }

    public void setLombokBuilder(boolean lombokBuilder) {
        this.lombokBuilder = lombokBuilder;
    }

    public boolean isGeneratorServiceInterface() {
        return generatorServiceInterface;
    }

    public void setGeneratorServiceInterface(boolean generatorServiceInterface) {
        this.generatorServiceInterface = generatorServiceInterface;
    }

    public boolean isGeneratorService() {
        return generatorService;
    }

    public void setGeneratorService(boolean generatorService) {
        this.generatorService = generatorService;
    }

    public String getRmPrefix() {
        return rmPrefix;
    }

    public void setRmPrefix(String rmPrefix) {
        this.rmPrefix = rmPrefix;
    }

    public boolean isUseActualColumnNamesCheckBox() {
        return useActualColumnNamesCheckBox;
    }

    public void setUseActualColumnNamesCheckBox(boolean useActualColumnNamesCheckBox) {
        this.useActualColumnNamesCheckBox = useActualColumnNamesCheckBox;
    }

    public boolean isUserDbCheckBox() {
        return userDbCheckBox;
    }

    public void setUserDbCheckBox(boolean userDbCheckBox) {
        this.userDbCheckBox = userDbCheckBox;
    }

    public Integer getServiceMode() {
        return serviceMode;
    }

    public void setServiceMode(Integer serviceMode) {
        this.serviceMode = serviceMode;
    }

    public boolean isUserTkMapper() {
        return userTkMapper;
    }

    public void setUserTkMapper(boolean userTkMapper) {
        this.userTkMapper = userTkMapper;
    }

    public String getTkMappers() {
        return tkMappers;
    }

    public void setTkMappers(String tkMappers) {
        this.tkMappers = tkMappers;
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    public boolean isForceAnnotation() {
        return forceAnnotation;
    }

    public void setForceAnnotation(boolean forceAnnotation) {
        this.forceAnnotation = forceAnnotation;
    }
}
