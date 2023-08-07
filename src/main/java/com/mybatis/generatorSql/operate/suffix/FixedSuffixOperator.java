package com.mybatis.generatorSql.operate.suffix;

import com.mybatis.generatorSql.iftest.ConditionFieldWrapper;
import com.mybatis.generatorSql.mapping.model.TxField;
import com.mybatis.generatorSql.mapping.model.TxParameter;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class FixedSuffixOperator implements SuffixOperator {
    /**
     * 比较符号
     */
    private final String operatorName;
    private final List<TxField> mappingField;

    /**
     * Instantiates a new Fixed suffix operator.
     *
     * @param operatorName the operator name
     * @param mappingField
     */
    public FixedSuffixOperator(final String operatorName, List<TxField> mappingField) {
        this.operatorName = operatorName;
        this.mappingField = mappingField;
    }

    /**
     * 通过字段名称找到表的列名, 然后拼接列名和操作符，例如  username is null
     *
     * @param fieldName  the field name 字段名称
     * @param parameters
     */
    @Override
    public String getTemplateText(String fieldName,
                                  LinkedList<TxParameter> parameters,
                                  ConditionFieldWrapper conditionFieldWrapper) {
        return mappingField.stream()
                .filter(field -> field.getFieldName().equals(fieldName))
                .map(field -> field.getColumnName() + " " + operatorName)
                .collect(Collectors.joining());
    }
}
