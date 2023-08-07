package com.mybatis.tip;

import com.intellij.codeInsight.completion.CompletionResultSet;
import com.mybatis.dom.model.Mapper;

public interface HashMarkTip {
    String getName();

    void tipValue(CompletionResultSet completionResultSet, Mapper mapper);
}
