package com.mybatis.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface Package extends DomElement {
    @Attribute("name")
    @NotNull
    GenericAttributeValue<String> getName();
}
