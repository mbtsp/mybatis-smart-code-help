package com.mybatis.generatorSql.factory;

import com.intellij.psi.PsiClass;
import com.mybatis.generatorSql.appender.CustomFieldAppender;
import com.mybatis.generatorSql.appender.SyntaxAppender;
import com.mybatis.generatorSql.iftest.ConditionFieldWrapper;
import com.mybatis.generatorSql.mapping.AreaSequence;
import com.mybatis.generatorSql.mapping.model.TxField;
import com.mybatis.generatorSql.mapping.model.TxParameter;
import com.mybatis.generatorSql.util.SyntaxAppenderWrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ResultAppenderFactory extends BaseAppenderFactory {
    /**
     * The constant RESULT.
     */
// 区域类型
    public static final String RESULT = "Result";
    /**
     * 区域前缀
     */
    private final String areaPrefix;

    private final List<SyntaxAppender> syntaxAppenderList = new ArrayList<>();

    /**
     * Instantiates a new Result appender factory.
     *
     * @param areaPrefix the area prefix
     */
    public ResultAppenderFactory(final String areaPrefix) {
        this.areaPrefix = areaPrefix;
    }

    /**
     * Register appender.
     *
     * @param syntaxAppender the syntax appender
     */
    public void registerAppender(final SyntaxAppender syntaxAppender) {
        this.syntaxAppenderList.add(syntaxAppender);
    }


    @Override
    public List<TxParameter> getMxParameter(PsiClass entityClass, LinkedList<SyntaxAppenderWrapper> jpaStringList) {
        return Collections.emptyList();
    }

    @Override
    public List<SyntaxAppender> getSyntaxAppenderList() {
        return this.syntaxAppenderList;
    }


    @Override
    public String getTipText() {
        return this.areaPrefix;
    }


    @Override
    protected AreaSequence getAreaSequence() {
        return AreaSequence.RESULT;
    }


    public static class WrapDateCustomFieldAppender extends CustomFieldAppender {


        /**
         * Instantiates a new Custom field appender.
         *
         * @param field        the field
         * @param areaSequence the area sequence
         */
        public WrapDateCustomFieldAppender(TxField field, AreaSequence areaSequence) {
            super(field, areaSequence);
        }


        @Override
        protected String wrapFieldValueInTemplateText(String columnName, ConditionFieldWrapper conditionFieldWrapper, String fieldValue) {
            return conditionFieldWrapper.wrapDefaultDateIfNecessary(columnName, fieldValue);
        }
    }
}
