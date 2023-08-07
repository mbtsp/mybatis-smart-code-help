package com.mybatis.dom.model;

import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.GenericAttributeValue;
import com.mybatis.dom.converter.PropertyConverter;

public interface Id extends PropertyGroup {
    @Attribute("property")
    @Convert(PropertyConverter.class)
    GenericAttributeValue<XmlAttributeValue> getProperty();
}
