package com.mybatis.generatorSql.util;

import com.intellij.psi.PsiClass;
import com.mybatis.generatorSql.appender.SyntaxAppender;
import com.mybatis.generatorSql.mapping.model.TxParameter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SyntaxAppenderWrapper {
    /**
     * 缓存不可变更的
     */
    private SyntaxAppender syntaxAppender;
    /**
     * 为了扩展符号追加器的内容的集合
     */
    private LinkedList<SyntaxAppenderWrapper> collector;

    /**
     * Instantiates a new Syntax appender wrapper.
     *
     * @param syntaxAppender the syntax appender
     */
    public SyntaxAppenderWrapper(SyntaxAppender syntaxAppender) {
        this.syntaxAppender = syntaxAppender;
        this.collector = new LinkedList<>();
    }


    /**
     * Instantiates a new Syntax appender wrapper.
     *
     * @param syntaxAppender the syntax appender
     * @param collector      the collector
     */
    public SyntaxAppenderWrapper(SyntaxAppender syntaxAppender, LinkedList<SyntaxAppenderWrapper> collector) {
        this.syntaxAppender = syntaxAppender;
        this.collector = collector;
    }

    /**
     * Add wrapper.
     *
     * @param syntaxAppenderWrapper the syntax appender wrapper
     */
    public void addWrapper(SyntaxAppenderWrapper syntaxAppenderWrapper) {
        this.collector.add(syntaxAppenderWrapper);
    }

    /**
     * Gets appender.
     *
     * @return the appender
     */
    public SyntaxAppender getAppender() {
        return syntaxAppender;
    }

    /**
     * Gets collector.
     *
     * @return the collector
     */
    public LinkedList<SyntaxAppenderWrapper> getCollector() {
        return collector;
    }

    public List<TxParameter> getMxParameter(PsiClass entityClass) {

        List<TxParameter> list = new ArrayList<>();
        if (syntaxAppender != null) {
            list.addAll(syntaxAppender.getMxParameter(collector, entityClass));
        } else {
            for (SyntaxAppenderWrapper syntaxAppenderWrapper : collector) {
                list.addAll(syntaxAppenderWrapper.getMxParameter(entityClass));
            }
        }
        return list;
    }
}
