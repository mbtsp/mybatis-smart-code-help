package com.mybatis.dom.model;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.mybatis.dom.converter.SelectConverter;
import org.jetbrains.annotations.NotNull;
public interface SelectAttributeGroup extends DomElement{
    @Attribute("select")
    @Convert(SelectConverter.class)
    @NotNull
    GenericAttributeValue<XmlTag> getSelect();
}
