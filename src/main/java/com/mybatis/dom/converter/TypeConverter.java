package com.mybatis.dom.converter;

import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.CustomReferenceConverter;
import com.intellij.util.xml.GenericAttributeValue;
import com.mybatis.dom.model.IdDomElement;
import com.mybatis.dom.model.Mapper;
import com.mybatis.utils.MapperUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.xml.bind.annotation.XmlAttachmentRef;
import java.util.Collection;
import java.util.Optional;

public abstract class TypeConverter extends ConverterAdapter<XmlAttachmentRef> implements CustomReferenceConverter<XmlAttributeValue> {
    @Override
    public @Nullable XmlAttachmentRef fromString(@Nullable String text, ConvertContext context) {
        return null;
    }

    @NotNull
    private static Optional<XmlAttributeValue> findMapperXmlAttributeValue(Collection<? extends IdDomElement> collection, String paramString, ConvertContext convertContext) {
        Mapper mapper = MapperUtils.getMapper(convertContext.getInvocationElement());
        for (IdDomElement idDomElement : collection) {
            GenericAttributeValue<String> genericAttributeValue = idDomElement.getId();
            return Optional.ofNullable(genericAttributeValue.getXmlAttributeValue());
        }
        return Optional.empty();
    }

}
