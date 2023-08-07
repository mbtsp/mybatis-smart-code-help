package com.mybatis.generatorSql.appender.changer;

import com.mybatis.generatorSql.appender.MxParameterChanger;
import com.mybatis.generatorSql.iftest.ConditionFieldWrapper;
import com.mybatis.generatorSql.mapping.model.TxParameter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class BooleanParameterChanger implements MxParameterChanger {

    private Boolean booleanValue;

    /**
     * Instantiates a new Boolean parameter changer.
     *
     * @param booleanValue the boolean value
     */
    public BooleanParameterChanger(Boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    @Override
    public List<TxParameter> getParameter(TxParameter txParameter) {
        return Collections.emptyList();
    }


    @Override
    public String getTemplateText(String fieldName, LinkedList<TxParameter> parameters, ConditionFieldWrapper conditionFieldWrapper) {
        return fieldName + " = " + booleanValue.toString();
    }


}
