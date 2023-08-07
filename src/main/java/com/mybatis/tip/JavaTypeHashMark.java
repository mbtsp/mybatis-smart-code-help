package com.mybatis.tip;

import com.intellij.codeInsight.completion.CompletionResultSet;
import com.mybatis.dom.model.Mapper;

public class JavaTypeHashMark implements HashMarkTip {
    @Override
    public String getName() {
        return "javaType";
    }

    /**
     * java 类如此多，所以就不提示了
     *
     * @param completionResultSet
     * @param mapper
     */
    @Override
    public void tipValue(CompletionResultSet completionResultSet, Mapper mapper) {

    }
}
