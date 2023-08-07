package com.mybatis.dom.converter;

import com.intellij.psi.ElementManipulators;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.*;
import com.mybatis.reference.ResultPropertyReferenceSet;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PropertyConverter extends ConverterAdapter<XmlAttributeValue> implements CustomReferenceConverter<XmlAttributeValue> {

    @Override
    public @Nullable XmlAttributeValue fromString(@Nullable String s, ConvertContext context) {
        DomElement ctxElement = context.getInvocationElement();
        return ctxElement instanceof GenericAttributeValue ? ((GenericAttributeValue) ctxElement).getXmlAttributeValue() : null;
    }

    @Override
    public @NotNull PsiReference @NotNull [] createReferences(GenericDomValue<XmlAttributeValue> value, PsiElement element, ConvertContext context) {
        String text = value.getStringValue();
        if (StringUtils.isBlank(text)) {
            return PsiReference.EMPTY_ARRAY;
        }
        return new ResultPropertyReferenceSet(text, element, ElementManipulators.getOffsetInElement(element)).getPsiReferences();
    }
}
