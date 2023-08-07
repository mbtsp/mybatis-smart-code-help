package com.mybatis.dom.converter;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.CustomReferenceConverter;
import com.intellij.util.xml.GenericDomValue;
import com.mybatis.dom.model.IdDomElement;
import com.mybatis.dom.model.Mapper;
import com.mybatis.utils.MapperUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public class SelectConverter extends ConverterAdapter<XmlAttributeValue> implements CustomReferenceConverter<XmlAttributeValue> {

    @Override
    public PsiReference @NotNull [] createReferences(GenericDomValue<XmlAttributeValue> value, PsiElement element, ConvertContext context) {
        String text = value.getStringValue();
        if (StringUtils.isBlank(text)) {
            return PsiReference.EMPTY_ARRAY;
        }
        return new PsiReference[]{new SelectIdPsiReferenceBase((XmlAttributeValue) element, context)};
    }


    @Override
    public @Nullable String toString(@Nullable XmlAttributeValue xmlAttributeValue, ConvertContext context) {
        return super.toString(xmlAttributeValue, context);
    }

    @Override
    public @NotNull Collection<? extends XmlAttributeValue> getVariants(ConvertContext context) {
        return super.getVariants(context);
    }

    @Override
    public @Nullable XmlAttributeValue fromString(@Nullable String text, ConvertContext context) {
        return super.fromString(text, context);
    }


    private static class SelectIdPsiReferenceBase extends PsiReferenceBase<XmlAttributeValue> {
        private final ConvertContext context;

        public SelectIdPsiReferenceBase(@NotNull XmlAttributeValue xmlAttributeValue, ConvertContext context) {
            super(xmlAttributeValue, false);
            this.context = context;
        }

        @Override
        public @Nullable PsiElement resolve() {
            XmlAttributeValue attributeValue = getElement();
            String selectStr = attributeValue.getValue();
            if (StringUtils.isBlank(selectStr)) {
                return null;
            }
            if (selectStr.contains(".")) {
                selectStr = selectStr.substring(0, selectStr.lastIndexOf("."));
                Collection<Mapper> mappers = MapperUtils.findMappers(context.getProject(), selectStr);
                if (!mappers.isEmpty()) {
                    List<IdDomElement> idDomElements = mappers.stream().findFirst().get().getDaoElements();
                    if (!idDomElements.isEmpty()) {
                        String selectMethod = attributeValue.getValue().substring(attributeValue.getValue().lastIndexOf(".") + 1, attributeValue.getValue().length());
                        if (StringUtils.isNotBlank(selectMethod)) {
                            for (IdDomElement idDomElement : idDomElements) {
                                String value = idDomElement.getId().getRawText();
                                if (com.mybatis.utils.StringUtils.isNotBlank(value) && value.equals(selectMethod)) {
                                    return idDomElement.getId().getXmlAttribute();
                                }
                            }
                        }
                    }
                }
            }
            Mapper mapper = MapperUtils.getMapper(context.getInvocationElement());
            List<IdDomElement> idDomElements = mapper.getDaoElements();
            if (idDomElements.isEmpty()) {
                return null;
            }
            for (IdDomElement idDomElement : idDomElements) {
                String value = idDomElement.getId().getRawText();
                if (com.mybatis.utils.StringUtils.isNotBlank(value) && value.equals(attributeValue.getValue())) {
                    return idDomElement.getId().getXmlAttribute();
                }
            }
            return null;
        }
    }

}
