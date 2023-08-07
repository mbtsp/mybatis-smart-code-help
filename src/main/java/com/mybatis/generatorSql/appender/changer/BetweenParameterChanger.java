package com.mybatis.generatorSql.appender.changer;

import com.mybatis.generatorSql.appender.MxParameterChanger;
import com.mybatis.generatorSql.iftest.ConditionFieldWrapper;
import com.mybatis.generatorSql.mapping.model.TxParameter;
import com.mybatis.utils.JdbcTypeUtils;
import com.mybatis.utils.StringUtils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class BetweenParameterChanger implements MxParameterChanger {

    /**
     * The constant SPACE.
     */
    public static final String SPACE = " ";

    @Override
    public List<TxParameter> getParameter(TxParameter txParameter) {
        TxParameter beginParameter = TxParameter.createByOrigin(
                "begin" + StringUtils.upperCaseFirstChar(txParameter.getName()),
                txParameter.getTypeText(),
                txParameter.getCanonicalTypeText());

        TxParameter endParameter = TxParameter.createByOrigin(
                "end" + StringUtils.upperCaseFirstChar(txParameter.getName()),
                txParameter.getTypeText(),
                txParameter.getCanonicalTypeText());

        return Arrays.asList(beginParameter, endParameter);
    }

    @Override
    public String getTemplateText(String fieldName, LinkedList<TxParameter> parameters, ConditionFieldWrapper conditionFieldWrapper) {
        final TxParameter begin = parameters.poll();
        final TxParameter end = parameters.poll();
        assert begin != null;
        assert end != null;
        final String beginStr = JdbcTypeUtils.wrapperField(begin.getName(), begin.getCanonicalTypeText());
        final String endStr = JdbcTypeUtils.wrapperField(end.getName(), end.getCanonicalTypeText());
        return fieldName + SPACE + "between" + SPACE + beginStr + " and " + endStr;
    }
}
