package com.mybatis.generator.plugin;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.internal.util.StringUtility;

import java.util.List;
import java.util.Properties;

public class LombokPlugin extends PluginAdapter {
    private boolean lombokData;
    private boolean lombokGetterSetter;
    private boolean lombokBuilder;
    private boolean lombokNoArgsConstructor;
    private boolean lombokAllArgsConstructor;

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        if (this.lombokData) {
            if (topLevelClass.getSuperClass().isPresent()) {
                topLevelClass.addImportedType("lombok.EqualsAndHashCode");
                topLevelClass.addAnnotation("@EqualsAndHashCode(callSuper = true)");
                topLevelClass.addImportedType("lombok.ToString");
                topLevelClass.addAnnotation("@ToString(callSuper = true)");
            }
            topLevelClass.addImportedType("lombok.Data");
            topLevelClass.addAnnotation("@Data");
        }
        if (this.lombokNoArgsConstructor) {
            topLevelClass.addImportedType("lombok.NoArgsConstructor");
            topLevelClass.addAnnotation("@NoArgsConstructor");
        }
        if (this.lombokAllArgsConstructor) {
            topLevelClass.addImportedType("lombok.AllArgsConstructor");
            topLevelClass.addAnnotation("@AllArgsConstructor");
        }
        if (this.lombokBuilder) {
            topLevelClass.addImportedType("lombok.Builder");
            topLevelClass.addAnnotation("@Builder");
        }
        if (this.lombokGetterSetter) {
            topLevelClass.addImportedType("lombok.Getter");
            topLevelClass.addImportedType("lombok.Setter");
            topLevelClass.addAnnotation("@Setter");
            topLevelClass.addAnnotation("@Getter");
        }
        return super.modelBaseRecordClassGenerated(topLevelClass, introspectedTable);
    }

    /**
     * This method is called after the getter, or accessor, method is generated
     * for a specific column in a table.
     *
     * @param method             the getter, or accessor, method generated for the specified
     *                           column
     * @param topLevelClass      the partially implemented model class. You can add additional
     *                           imported classes to the implementation class if necessary.
     * @param introspectedColumn The class containing information about the column related
     *                           to this field as introspected from the database
     * @param introspectedTable  The class containing information about the table as
     *                           introspected from the database
     * @param modelClassType     the type of class that the field is generated for
     * @return true if the method should be generated, false if the generated
     * method should be ignored. In the case of multiple plugins, the
     * first plugin returning false will disable the calling of further
     * plugins.
     */
    @Override
    public boolean modelGetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        return !this.lombokData && !this.lombokGetterSetter;
    }

    /**
     * This method is called after the setter, or mutator, method is generated
     * for a specific column in a table.
     *
     * @param method             the setter, or mutator, method generated for the specified
     *                           column
     * @param topLevelClass      the partially implemented model class. You can add additional
     *                           imported classes to the implementation class if necessary.
     * @param introspectedColumn The class containing information about the column related
     *                           to this field as introspected from the database
     * @param introspectedTable  The class containing information about the table as
     *                           introspected from the database
     * @param modelClassType     the type of class that the field is generated for
     * @return true if the method should be generated, false if the generated
     * method should be ignored. In the case of multiple plugins, the
     * first plugin returning false will disable the calling of further
     * plugins.
     */
    @Override
    public boolean modelSetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        return !this.lombokData && !this.lombokGetterSetter;
    }

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
        this.lombokData = StringUtility.isTrue(properties.getProperty("lombokData"));
        this.lombokGetterSetter = StringUtility.isTrue(properties.getProperty("lombokGetterSetter"));
        this.lombokBuilder = StringUtility.isTrue(properties.getProperty("lombokBuilder"));
        this.lombokNoArgsConstructor = StringUtility.isTrue(properties.getProperty("lombokNoArgsConstructor"));
        this.lombokAllArgsConstructor = StringUtility.isTrue(properties.getProperty("lombokAllArgsConstructor"));
    }
}
