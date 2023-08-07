package com.mybatis.generatorSql.iftest;

import com.intellij.openapi.project.Project;
import com.mybatis.dom.model.Mapper;
import com.mybatis.generatorSql.MapperClassGenerateFactory;
import com.mybatis.generatorSql.mapping.model.TxField;
import com.mybatis.generatorSql.operate.EmptyGenerator;
import com.mybatis.generatorSql.operate.Generator;
import com.mybatis.generatorSql.operate.MybatisXmlGenerator;
import com.mybatis.utils.JdbcTypeUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class ConditionIfTestWrapper implements ConditionFieldWrapper {
    public static final int DEFAULT_NEWLINE_VALUE = 3;
    private Project project;
    private Set<String> selectedWrapFields;
    private String allFieldsStr;
    private String resultMap;
    private boolean resultType;
    private String resultTypeClass;
    private List<String> resultFields;
    private Map<String, TxField> txFieldMap;
    private List<TxField> allFields;
    /**
     * 默认字段的关键字：  oracle: SYSDATE, mysql: NOW()
     */
    private String defaultDateWord;
    private Mapper mapper;
    private List<String> defaultDateList;

    /**
     * Instantiates a new Condition if test wrapper.
     *
     * @param project
     * @param selectedWrapFields the wrapper fields
     * @param resultFields
     * @param allFields
     * @param defaultDateWord
     */
    public ConditionIfTestWrapper(@NotNull Project project,
                                  Set<String> selectedWrapFields,
                                  List<String> resultFields,
                                  List<TxField> allFields,
                                  String defaultDateWord) {
        this.project = project;
        this.selectedWrapFields = selectedWrapFields;
        this.resultFields = resultFields;
        txFieldMap = allFields.stream().collect(Collectors.toMap(TxField::getFieldName, x -> x, (a, b) -> a));
        this.allFields = allFields;
        this.defaultDateWord = defaultDateWord;
    }

    @Override
    public String wrapConditionText(String fieldName, String templateText) {
        if (selectedWrapFields.contains(fieldName)) {
            templateText = "<if test=\"" + getConditionField(fieldName) + "\">" +
                    "\n" + templateText +
                    "\n" + "</if>";
        }
        return templateText;
    }

    @Override
    public String wrapWhere(String content) {
        return "<where>\n" + content + "\n</where>";
    }

    @Override
    public String getAllFields() {
        return "<include refid=\"Base_Column_List\" />";
    }

    /**
     * Sets all fields.
     *
     * @param allFieldsStr the all fields str
     */
    public void setAllFields(String allFieldsStr) {
        this.allFieldsStr = allFieldsStr;
    }

    @Override
    public String getResultMap() {
        return "BaseResultMap";
    }

    /**
     * Sets result map.
     *
     * @param resultMap the result map
     */
    public void setResultMap(String resultMap) {
        this.resultMap = resultMap;
    }

    @Override
    public String getResultType() {
        return resultTypeClass;
    }

    /**
     * Sets result type.
     *
     * @param resultType the result type
     */
    public void setResultType(boolean resultType) {
        this.resultType = resultType;
    }

    @Override
    public Boolean isResultType() {
        return resultType;
    }

    @Override
    public Generator getGenerator(MapperClassGenerateFactory mapperClassGenerateFactory) {
        if (mapper == null) {
            return new EmptyGenerator();
        }
        return new MybatisXmlGenerator(mapperClassGenerateFactory, mapper, project);
    }

    private String getConditionField(String fieldName) {
        TxField txField = txFieldMap.get(fieldName);
        String appender = "";
        if (Objects.equals(txField.getFieldType(), "java.lang.String")) {
            appender = " and " + fieldName + " != ''";
        }
        return fieldName + " != null" + appender;
    }

    /**
     * Sets result type class.
     *
     * @param resultTypeClass the result type class
     */
    public void setResultTypeClass(String resultTypeClass) {
        this.resultTypeClass = resultTypeClass;
    }


    @Override
    public void setMapper(Mapper mapper) {
        this.mapper = mapper;
    }

    /**
     * 对于默认值 create_time,update_time, 在 更新和插入的时候替换为数据库默认值的关键字
     * MYSQL默认时间: NOW()
     * ORACLE默认时间: SYSDATE
     *
     * @param columnName 字段名
     * @param fieldValue
     * @return
     */
    @Override
    public String wrapDefaultDateIfNecessary(String columnName, String fieldValue) {
        if (defaultDateList.contains(columnName)) {
            return defaultDateWord;
        }
        return fieldValue;
    }

    @Override
    public List<String> getDefaultDateList() {
        return Collections.emptyList();
    }

    @Override
    public List<TxField> getResultTxFields() {
        Set<String> addedFields = new HashSet<>();
        return allFields.stream().filter(field -> resultFields.contains(field.getFieldName()) && addedFields.add(field.getFieldName())).collect(Collectors.toList());
    }

    private int newLine;

    @Override
    public int getNewline() {
        return newLine;
    }

    @Override
    public String wrapperField(String originName, String name, String canonicalTypeText) {
        TxField txField = txFieldMap.get(originName);
        if (txField != null) {
            String jdbcType = txField.getJdbcType();
            if (jdbcType != null) {
                return "#{" + name +
                        ",jdbcType=" + jdbcType +
                        "}";
            }
        }
        return JdbcTypeUtils.wrapperField(name, canonicalTypeText);
    }

    public void setDefaultDateList(List<String> defaultDateList) {
        this.defaultDateList = defaultDateList;
    }


    public void setNewLine(int newLine) {
        // 如果设置错误的值, 给一个合适的默认值
        if (newLine <= 0) {
            newLine = DEFAULT_NEWLINE_VALUE;
        }
        this.newLine = newLine;
    }
}
