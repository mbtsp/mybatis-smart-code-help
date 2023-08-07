package com.mybatis.generatorSql.operate.suffix;

import com.mybatis.generatorSql.iftest.ConditionFieldWrapper;
import com.mybatis.generatorSql.mapping.model.TxParameter;
import com.mybatis.utils.JdbcTypeUtils;
import org.apache.commons.lang3.Validate;

import java.util.LinkedList;

/**
 * 字段比较
 */
public class ParamAroundSuffixOperator implements SuffixOperator {
    /**
     * 比较符号
     */
    private String prefix;

    private String suffix;

    /**
     * Instantiates a new Param around suffix operator.
     *
     * @param prefix the prefix
     * @param suffix the suffix
     */
    public ParamAroundSuffixOperator(final String prefix, final String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    @Override
    public String getTemplateText(String fieldName, LinkedList<TxParameter> parameters, ConditionFieldWrapper conditionFieldWrapper) {

        TxParameter parameter = parameters.poll();
        Validate.notNull(parameter, "parameter must not be null");
        return fieldName
                + " "
                + prefix
                + " "
                + JdbcTypeUtils.wrapperField(parameter.getName(), parameter.getCanonicalTypeText())
                + suffix;
    }
}
