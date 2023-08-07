package com.mybatis.generatorSql.operate.dialect.oracle;

import com.intellij.psi.PsiClass;
import com.mybatis.generatorSql.appender.CustomSuffixAppender;
import com.mybatis.generatorSql.appender.SyntaxAppender;
import com.mybatis.generatorSql.iftest.ConditionFieldWrapper;
import com.mybatis.generatorSql.mapping.AreaSequence;
import com.mybatis.generatorSql.mapping.model.TxParameter;
import com.mybatis.generatorSql.operate.suffix.SuffixOperator;
import com.mybatis.generatorSql.util.SyntaxAppenderWrapper;

import java.util.LinkedList;

/**
 * 插入后缀处理器
 */
public class InsertCustomSuffixAppender extends CustomSuffixAppender {

    /**
     * Instantiates a new Insert custom suffix appender.
     *
     * @param tipName        the tip name
     * @param suffixOperator the suffix operator
     * @param areaSequence   the area sequence
     */
    public InsertCustomSuffixAppender(String tipName, SuffixOperator suffixOperator, AreaSequence areaSequence) {
        super(tipName, suffixOperator, areaSequence);
    }

    /**
     * Create insert by suffix operator syntax appender.
     *
     * @param all            the all
     * @param suffixOperator the suffix operator
     * @param areaSequence   the area sequence
     * @return the syntax appender
     */
    public static SyntaxAppender createInsertBySuffixOperator(String all, SuffixOperator suffixOperator, AreaSequence areaSequence) {
        return new InsertCustomSuffixAppender(all, suffixOperator, areaSequence);
    }

    @Override
    public void toTree(LinkedList<SyntaxAppender> jpaStringList, SyntaxAppenderWrapper syntaxAppenderWrapper) {
        syntaxAppenderWrapper.addWrapper(new SyntaxAppenderWrapper(InsertCustomSuffixAppender.this));
    }

    @Override
    protected String getFieldTemplateText(String tableName, PsiClass entityClass, LinkedList<TxParameter> parameters, LinkedList<SyntaxAppenderWrapper> collector, ConditionFieldWrapper conditionFieldWrapper, SyntaxAppender appender) {
        InsertCustomSuffixAppender suffixAppender = (InsertCustomSuffixAppender) appender;
        return suffixAppender.getSuffixOperator().getTemplateText(tableName, parameters, conditionFieldWrapper);
    }
}
