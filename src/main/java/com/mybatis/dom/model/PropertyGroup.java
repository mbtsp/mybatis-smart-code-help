package com.mybatis.dom.model;

import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.mybatis.dom.converter.ColumnConverter;
import com.mybatis.dom.converter.JdbcTypeConverter;
import com.mybatis.dom.converter.PropertyConverter;
import com.mybatis.dom.converter.TypeHandlerConverter;

public interface PropertyGroup extends DomElement {
    @Attribute("property")
    @Convert(PropertyConverter.class)
    GenericAttributeValue<XmlAttributeValue> getProperty();

    /**
     * column
     *
     * @return
     */
    @Attribute("column")
    @Convert(value = ColumnConverter.class, soft = true)
    GenericAttributeValue<XmlAttributeValue> getColumn();

    /**
     * jdbcType
     *
     * @return
     */
    @Attribute("jdbcType")
    @Convert(JdbcTypeConverter.class)
    GenericAttributeValue<XmlAttributeValue> getJdbcType();

    /**
     * jdbcType
     *
     * @return
     */
    @Attribute("typeHandler")
    @Convert(TypeHandlerConverter.class)
    GenericAttributeValue<XmlAttributeValue> getTypeHandler();
}
