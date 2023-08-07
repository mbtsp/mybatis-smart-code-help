package com.mybatis.dom.model;

import com.intellij.psi.PsiClass;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;
import com.mybatis.dom.converter.AliasConverter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ParameterMap extends IdDomElement {
    @Attribute("type")
    @Convert(AliasConverter.class)
    @NotNull
    GenericAttributeValue<PsiClass> getType();

    @SubTagList("parameter")
    List<Parameter> getParameters();
}
