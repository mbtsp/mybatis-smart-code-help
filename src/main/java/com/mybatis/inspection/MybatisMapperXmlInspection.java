package com.mybatis.inspection;

import com.intellij.codeHighlighting.HighlightDisplayLevel;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericDomValue;
import com.intellij.util.xml.highlighting.BasicDomElementsInspection;
import com.intellij.util.xml.highlighting.DomElementAnnotationHolder;
import com.intellij.util.xml.highlighting.DomHighlightingHelper;
import org.jetbrains.annotations.NotNull;

public class MybatisMapperXmlInspection extends BasicDomElementsInspection<DomElement> {
    public MybatisMapperXmlInspection() {
        super(DomElement.class);
    }


    @Override
    protected void checkDomElement(DomElement element, DomElementAnnotationHolder holder, DomHighlightingHelper helper) {
    }

    @Override
    public @NotNull HighlightDisplayLevel getDefaultLevel() {
        return HighlightDisplayLevel.ERROR;
    }

    @Override
    protected boolean shouldCheckResolveProblems(GenericDomValue value) {
        return super.shouldCheckResolveProblems(value);
    }
}
