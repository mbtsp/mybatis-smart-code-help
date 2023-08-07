package com.mybatis.generator;

import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.kotlin.KotlinFile;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.config.MergeConstants;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.internal.DefaultCommentGenerator;
import org.mybatis.generator.internal.util.StringUtility;

import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.mybatis.generator.internal.util.StringUtility.isTrue;

public class MybatisCommentGenerator extends DefaultCommentGenerator {

    private final Properties properties = new Properties();

    private boolean suppressDate = false;

    private boolean interfaceComment=true;
    private boolean suppressAllComments = false;
    private String className;

    private IntrospectedTable introspectedTable;
    /**
     * If suppressAllComments is true, this option is ignored.
     */
    private boolean addRemarkComments = false;

    private SimpleDateFormat dateFormat;

    @Override
    public void addJavaFileComment(CompilationUnit compilationUnit) {
        // add no file level comments by default
        className = compilationUnit.getType().getShortName();
    }

    /**
     * Adds a suitable comment to warn users that the element was generated, and
     * when it was generated.
     *
     * @param xmlElement the xml element
     */
    @Override
    public void addComment(XmlElement xmlElement) {
        if (suppressAllComments) {
            return;
        }
        xmlElement.addElement(new TextElement("<!--" + MergeConstants.NEW_ELEMENT_TAG + " -->")); //$NON-NLS-1$
        if (xmlElement.getName().equals("resultMap")) {
            xmlElement.addElement(new TextElement("<!--@Table " + this.introspectedTable.getTableConfiguration().getTableName() + " -->")); //$NON-NLS-1$
            xmlElement.addElement(new TextElement("<!-- generated on " + new Date().toString() + " -->")); //$NON-NLS-1$
        }

    }

    @Override
    public void addRootComment(XmlElement rootElement) {
        // add no document level comments by default
    }

    @Override
    public void addConfigurationProperties(Properties properties) {
        this.properties.putAll(properties);

        suppressDate = isTrue(properties.getProperty(PropertyRegistry.COMMENT_GENERATOR_SUPPRESS_DATE));

        suppressAllComments = isTrue(properties.getProperty(PropertyRegistry.COMMENT_GENERATOR_SUPPRESS_ALL_COMMENTS));

        addRemarkComments = isTrue(properties.getProperty(PropertyRegistry.COMMENT_GENERATOR_ADD_REMARK_COMMENTS));

        interfaceComment =isTrue(properties.getProperty("interfaceComment"));
        String dateFormatString = properties.getProperty(PropertyRegistry.COMMENT_GENERATOR_DATE_FORMAT);
        if (StringUtility.stringHasValue(dateFormatString)) {
            dateFormat = new SimpleDateFormat(dateFormatString);
        }
    }

    /**
     * This method adds the custom javadoc tag for. You may do nothing if you do not
     * wish to include the Javadoc tag - however, if you do not include the Javadoc
     * tag then the Java merge capability of the eclipse plugin will break.
     *
     * @param javaElement       the java element
     * @param markAsDoNotDelete the mark as do not delete
     */
    protected void addJavadocTag(JavaElement javaElement, boolean markAsDoNotDelete) {
    }

    /**
     * Returns a formated date string to include in the Javadoc tag and XML
     * comments. You may return null if you do not want the date in these
     * documentation elements.
     *
     * @return a string representing the current timestamp, or null
     */
    protected String getDateString() {
        if (suppressDate) {
            return null;
        } else if (dateFormat != null) {
            return dateFormat.format(new Date());
        } else {
            return new Date().toString();
        }
    }

    @Override
    public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable) {
    }

    @Override
    public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable, boolean markAsDoNotDelete) {

    }

    /**
     * class 类 头部注释内容
     *
     * @param topLevelClass     topLevelClass
     * @param introspectedTable introspectedTable
     */
    @Override
    public void addModelClassComment(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        this.introspectedTable = introspectedTable;
        if (suppressAllComments || !addRemarkComments) {
            return;
        }
        String remarks = introspectedTable.getRemarks();
        if (StringUtils.isBlank(remarks)) {
            return;
        }
        topLevelClass.addJavaDocLine("/**"); //$NON-NLS-1$
        topLevelClass.addJavaDocLine("* " + remarks);
        topLevelClass.addJavaDocLine("*/");

    }

    @Override
    public void addEnumComment(InnerEnum innerEnum, IntrospectedTable introspectedTable) {
//        if (suppressAllComments) {
//            return;
//        }
//
//        StringBuilder sb = new StringBuilder();
//
//        innerEnum.addJavaDocLine("/**"); //$NON-NLS-1$
//        innerEnum.addJavaDocLine(" * This enum was generated by MyBatis Generator."); //$NON-NLS-1$
//
//        sb.append(" * This enum corresponds to the database table "); //$NON-NLS-1$
//        sb.append(introspectedTable.getFullyQualifiedTable());
//        innerEnum.addJavaDocLine(sb.toString());
//
//        addJavadocTag(innerEnum, false);
//
//        innerEnum.addJavaDocLine(" */"); //$NON-NLS-1$
    }

    /**
     * model 字段注释
     *
     * @param field              field
     * @param introspectedTable  introspectedTable
     * @param introspectedColumn introspectedColumn
     */
    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable,
                                IntrospectedColumn introspectedColumn) {
        if (suppressAllComments) {
            return;
        }
        String remarks = introspectedColumn.getRemarks();
        if (StringUtils.isBlank(remarks)) {
            return;
        }
        field.addJavaDocLine("/**");
        field.addJavaDocLine("* " + remarks);
        field.addJavaDocLine("*/");
    }

    /**
     * class model static 字段
     *
     * @param field             field
     * @param introspectedTable introspectedTable
     */
    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable) {
    }

    /**
     * model 自动生成的方法注释
     *
     * @param method            method
     * @param introspectedTable introspectedTable
     */
    @Override
    public void addGeneralMethodComment(Method method, IntrospectedTable introspectedTable) {
        if(!interfaceComment){
            return;
        }
        if (className.endsWith("Example")) {
            return;
        }
        method.addJavaDocLine("/**");
//        method.addJavaDocLine("* "+MergeConstants.NEW_ELEMENT_TAG);
        method.addJavaDocLine("* " + method.getName());
        List<Parameter> parameters = method.getParameters();
        parameters.forEach(parameter -> method.addJavaDocLine("* @param " + parameter.getName() + " " + parameter.getName()));
        Optional<FullyQualifiedJavaType> fullyQualifiedJavaType = method.getReturnType();
        fullyQualifiedJavaType.ifPresent(qualifiedJavaType -> method.addJavaDocLine("* @return " + qualifiedJavaType.getShortName() + " " + qualifiedJavaType.getShortName()));
        method.addJavaDocLine("*/");
    }

    @Override
    public void addGetterComment(Method method, IntrospectedTable introspectedTable,
                                 IntrospectedColumn introspectedColumn) {

    }

    @Override
    public void addSetterComment(Method method, IntrospectedTable introspectedTable,
                                 IntrospectedColumn introspectedColumn) {

    }

    @Override
    public void addGeneralMethodAnnotation(Method method, IntrospectedTable introspectedTable,
                                           Set<FullyQualifiedJavaType> imports) {
        imports.add(new FullyQualifiedJavaType("javax.annotation.Generated")); //$NON-NLS-1$
        String comment = "Source Table: " + introspectedTable.getFullyQualifiedTable().toString(); //$NON-NLS-1$
        method.addAnnotation(getGeneratedAnnotation(comment));
    }

    @Override
    public void addGeneralMethodAnnotation(Method method, IntrospectedTable introspectedTable,
                                           IntrospectedColumn introspectedColumn, Set<FullyQualifiedJavaType> imports) {
        imports.add(new FullyQualifiedJavaType("javax.annotation.Generated")); //$NON-NLS-1$
        String comment = "Source field: " //$NON-NLS-1$
                + introspectedTable.getFullyQualifiedTable().toString() + "." //$NON-NLS-1$
                + introspectedColumn.getActualColumnName();
        method.addAnnotation(getGeneratedAnnotation(comment));
    }

    @Override
    public void addFieldAnnotation(Field field, IntrospectedTable introspectedTable,
                                   Set<FullyQualifiedJavaType> imports) {
        imports.add(new FullyQualifiedJavaType("javax.annotation.Generated")); //$NON-NLS-1$
        String comment = "Source Table: " + introspectedTable.getFullyQualifiedTable().toString(); //$NON-NLS-1$
        field.addAnnotation(getGeneratedAnnotation(comment));
    }

    @Override
    public void addFieldAnnotation(Field field, IntrospectedTable introspectedTable,
                                   IntrospectedColumn introspectedColumn, Set<FullyQualifiedJavaType> imports) {
        imports.add(new FullyQualifiedJavaType("javax.annotation.Generated")); //$NON-NLS-1$
        String comment = "Source field: " //$NON-NLS-1$
                + introspectedTable.getFullyQualifiedTable().toString() + "." //$NON-NLS-1$
                + introspectedColumn.getActualColumnName();
        field.addAnnotation(getGeneratedAnnotation(comment));

        if (!suppressAllComments && addRemarkComments) {
            String remarks = introspectedColumn.getRemarks();
            if (addRemarkComments && StringUtility.stringHasValue(remarks)) {
                field.addJavaDocLine("/**"); //$NON-NLS-1$
                field.addJavaDocLine(" * Database Column Remarks:"); //$NON-NLS-1$
                String[] remarkLines = remarks.split(System.getProperty("line.separator")); //$NON-NLS-1$
                for (String remarkLine : remarkLines) {
                    field.addJavaDocLine(" *   " + remarkLine); //$NON-NLS-1$
                }
                field.addJavaDocLine(" */"); //$NON-NLS-1$
            }
        }
    }

    @Override
    public void addClassAnnotation(InnerClass innerClass, IntrospectedTable introspectedTable,
                                   Set<FullyQualifiedJavaType> imports) {
        imports.add(new FullyQualifiedJavaType("javax.annotation.Generated")); //$NON-NLS-1$
        String comment = "Source Table: " + introspectedTable.getFullyQualifiedTable().toString(); //$NON-NLS-1$
        innerClass.addAnnotation(getGeneratedAnnotation(comment));
    }

    private String getGeneratedAnnotation(String comment) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("@Generated("); //$NON-NLS-1$
        if (suppressAllComments) {
            buffer.append('\"');
        } else {
            buffer.append("value=\""); //$NON-NLS-1$
        }

        buffer.append(MyBatisGenerator.class.getName());
        buffer.append('\"');

        if (!suppressDate && !suppressAllComments) {
            buffer.append(", date=\""); //$NON-NLS-1$
            buffer.append(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(ZonedDateTime.now()));
            buffer.append('\"');
        }

        if (!suppressAllComments) {
            buffer.append(", comments=\""); //$NON-NLS-1$
            buffer.append(comment);
            buffer.append('\"');
        }

        buffer.append(')');
        return buffer.toString();
    }

    @Override
    public void addFileComment(KotlinFile kotlinFile) {
        if (suppressAllComments) {
            return;
        }

        kotlinFile.addFileCommentLine("/*"); //$NON-NLS-1$
        kotlinFile.addFileCommentLine(" * Auto-generated file. Created by MyBatis Generator"); //$NON-NLS-1$
        if (!suppressDate) {
            kotlinFile.addFileCommentLine(" * Generation date: " //$NON-NLS-1$
                    + DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(ZonedDateTime.now()));
        }
        kotlinFile.addFileCommentLine(" */"); //$NON-NLS-1$
    }
}
