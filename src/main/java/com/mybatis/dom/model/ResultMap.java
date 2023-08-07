package com.mybatis.dom.model;

import com.intellij.psi.PsiClass;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.GenericAttributeValue;
import com.mybatis.dom.converter.AliasConverter;
import com.mybatis.dom.converter.ResultMapConverter;
import org.jetbrains.annotations.NotNull;

public interface ResultMap extends GroupFour, IdDomElement {
    @Attribute("extends")
    @Convert(ResultMapConverter.class)
    @NotNull
    GenericAttributeValue<XmlAttributeValue> getExtends();

    @Attribute("type")
    @Convert(AliasConverter.class)
    @NotNull
    GenericAttributeValue<PsiClass> getType();
}
