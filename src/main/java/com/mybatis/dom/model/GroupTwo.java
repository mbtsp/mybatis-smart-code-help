package com.mybatis.dom.model;

import com.intellij.psi.PsiClass;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;
import com.mybatis.dom.converter.AliasConverter;
import com.mybatis.dom.converter.ParameterMapConverter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface GroupTwo extends GroupOne, IdDomElement {
    @SubTagList("bind")
    List<Bind> getBinds();

    @Attribute("parameterMap")
    @Convert(ParameterMapConverter.class)
    @NotNull
    GenericAttributeValue<XmlTag> getParameterMap();

    @Attribute("id")
    @Convert(DaoMethodConverter.class)
    GenericAttributeValue<String> getId();

    @Attribute("parameterType")
    @Convert(AliasConverter.class)
    @NotNull
    GenericAttributeValue<PsiClass> getParameterType();
}
