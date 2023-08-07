package com.mybatis.tip;

import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.mybatis.dom.model.Mapper;

public class NumericScaleHashMarkTip implements HashMarkTip {
    @Override
    public String getName() {
        return "numericScale";
    }

    /**
     * 是数字,可以不用提示。<br/>
     * 提示默认以常见的保留2位和4位小数
     */
    @Override
    public void tipValue(CompletionResultSet completionResultSet, Mapper mapper) {
        completionResultSet.addElement(LookupElementBuilder.create("2"));
        completionResultSet.addElement(LookupElementBuilder.create("4"));
    }
}
