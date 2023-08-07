package com.mybatis.dom.model;

import com.intellij.psi.PsiClass;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.GenericAttributeValue;
import com.mybatis.dom.converter.AliasConverter;
import org.jetbrains.annotations.NotNull;

public interface Select extends GroupTwo, ResultMapGroup {

    @Attribute("resultType")
    @Convert(AliasConverter.class)
    @NotNull
    GenericAttributeValue<PsiClass> getResultType();
}
