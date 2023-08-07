package com.mybatis.generatorSql.operate.suffix;

import com.mybatis.generatorSql.iftest.ConditionFieldWrapper;
import com.mybatis.generatorSql.mapping.model.TxParameter;

import java.util.LinkedList;

public interface SuffixOperator {

    /**
     * Gets template text.
     *
     * @param fieldName             the field name
     * @param parameters            the parameters
     * @param conditionFieldWrapper
     * @return the template text
     */
    String getTemplateText(String fieldName, LinkedList<TxParameter> parameters, ConditionFieldWrapper conditionFieldWrapper);
}
