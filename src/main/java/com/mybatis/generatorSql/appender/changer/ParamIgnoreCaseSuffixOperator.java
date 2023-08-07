package com.mybatis.generatorSql.appender.changer;

import com.mybatis.generatorSql.iftest.ConditionFieldWrapper;
import com.mybatis.generatorSql.mapping.model.TxParameter;
import com.mybatis.generatorSql.operate.suffix.SuffixOperator;
import com.mybatis.utils.JdbcTypeUtils;

import java.util.LinkedList;

/**
 * 忽略大小写
 */
public class ParamIgnoreCaseSuffixOperator implements SuffixOperator {


    @Override
    public String getTemplateText(String fieldName,
                                  LinkedList<TxParameter> parameters,
                                  ConditionFieldWrapper conditionFieldWrapper) {

        TxParameter parameter = parameters.poll();
        return "UPPER(" + fieldName + ")"
                + " "
                + "="
                + " "
                + "UPPER(" + JdbcTypeUtils.wrapperField(parameter.getName(), parameter.getCanonicalTypeText()) + ")";
    }


}
