package com.mybatis.generatorSql.operate.suffix;

import com.mybatis.generatorSql.iftest.ConditionFieldWrapper;
import com.mybatis.generatorSql.mapping.model.TxParameter;
import com.mybatis.utils.JdbcTypeUtils;

import java.util.LinkedList;

public class ParamBeforeSuffixOperator implements SuffixOperator {
    /**
     * 比较符号
     */
    private String operatorName;

    /**
     * Instantiates a new Param before suffix operator.
     *
     * @param operatorName the operator name
     */
    public ParamBeforeSuffixOperator(final String operatorName) {
        this.operatorName = operatorName;
    }

    @Override
    public String getTemplateText(String columnName, LinkedList<TxParameter> parameters, ConditionFieldWrapper conditionFieldWrapper) {

        TxParameter parameter = parameters.poll();
        return columnName
                + " "
                + operatorName
                + " "
                + JdbcTypeUtils.wrapperField(parameter.getName(), parameter.getCanonicalTypeText());
    }
}
