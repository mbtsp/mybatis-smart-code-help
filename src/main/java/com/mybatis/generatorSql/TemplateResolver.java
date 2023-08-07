package com.mybatis.generatorSql;

import com.intellij.psi.PsiClass;
import com.mybatis.generatorSql.appender.CompositeAppender;
import com.mybatis.generatorSql.appender.SyntaxAppender;
import com.mybatis.generatorSql.iftest.ConditionFieldWrapper;
import com.mybatis.generatorSql.mapping.model.TxParameter;
import com.mybatis.generatorSql.util.SyntaxAppenderWrapper;

import java.util.LinkedList;

public class TemplateResolver {

    /**
     * Gets template text.
     *
     * @param current               the current
     * @param tableName             the table name
     * @param entityClass           the entity class
     * @param parameters            the parameters
     * @param collector             the collector
     * @param conditionFieldWrapper the condition field wrapper
     * @return the template text
     */
    public String getTemplateText(LinkedList<SyntaxAppender> current,
                                  String tableName, PsiClass entityClass,
                                  LinkedList<TxParameter> parameters,
                                  LinkedList<SyntaxAppenderWrapper> collector,
                                  ConditionFieldWrapper conditionFieldWrapper) {
        SyntaxAppender syntaxAppender = null;
        if (current.size() == 1) {
            syntaxAppender = current.poll();
        } else if (current.size() > 1) {
            syntaxAppender = new CompositeAppender(current.toArray(new SyntaxAppender[0]));
        } else {
            return "";
        }
        return syntaxAppender.getTemplateText(tableName, entityClass, parameters, collector, conditionFieldWrapper);

    }


}
