package com.mybatis.generator.plugin;

import com.mybatis.utils.StringUtils;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;

public class GeneratorSwagger2Doc extends PluginAdapter {

    /**
     * This method is called after all the setXXX methods are called, but before
     * any other method is called. This allows the plugin to determine whether
     * it can run or not. For example, if the plugin requires certain properties
     * to be set, and the properties are not set, then the plugin is invalid and
     * will not run.
     *
     * @param warnings add strings to this list to specify warnings. For example, if
     *                 the plugin is invalid, you should specify why. Warnings are
     *                 reported to users after the completion of the run.
     * @return true if the plugin is in a valid state. Invalid plugins will not
     * be called
     */
    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean modelFieldGenerated(Field field, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        String tableRemarks = removeSpecialCharacters(introspectedTable.getRemarks());
        if (StringUtils.isBlank(tableRemarks)) {
            tableRemarks = "";
        }
        String classAnnotation = "@ApiModel(value=\"" + tableRemarks + "\")";
        if (!topLevelClass.getAnnotations().contains(classAnnotation)) {
            topLevelClass.addAnnotation(classAnnotation);
        }
        String apiModelAnnotationPackage = this.properties.getProperty("apiModelAnnotationPackage");
        String apiModelPropertyAnnotationPackage = this.properties.getProperty("apiModelPropertyAnnotationPackage");
        if (null == apiModelAnnotationPackage) {
            apiModelAnnotationPackage = "io.swagger.annotations.ApiModel";
        }
        if (null == apiModelPropertyAnnotationPackage) {
            apiModelPropertyAnnotationPackage = "io.swagger.annotations.ApiModelProperty";
        }
        topLevelClass.addImportedType(apiModelAnnotationPackage);
        topLevelClass.addImportedType(apiModelPropertyAnnotationPackage);
        String remarks = removeSpecialCharacters(introspectedColumn.getRemarks());
        if (StringUtils.isBlank(remarks)) {
            remarks = "";
        }
        field.addAnnotation("@ApiModelProperty(value=\"" + remarks + "\")");
        return super.modelFieldGenerated(field, topLevelClass, introspectedColumn, introspectedTable, modelClassType);
    }
    private String removeSpecialCharacters(String remark){
        if(StringUtils.isBlank(remark)){
            return null;
        }
        return remark.replace("\n","");
    }

}
