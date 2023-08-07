package com.mybatis.dom.model;

import com.intellij.psi.PsiClass;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface TypeAlias extends DomElement {
    @Attribute("type")
    @NotNull
    GenericAttributeValue<PsiClass> getType();

    @Attribute("alias")
    @NotNull
    GenericAttributeValue<String> getAlias();
}


