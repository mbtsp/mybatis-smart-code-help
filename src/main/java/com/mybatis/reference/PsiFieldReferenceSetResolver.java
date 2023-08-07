package com.mybatis.reference;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.util.PropertyUtil;
import com.intellij.psi.xml.XmlAttributeValue;
import com.mybatis.dom.utils.MapperBacktrackingUtils;
import com.mybatis.utils.JavaUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class PsiFieldReferenceSetResolver extends ContextReferenceSetResolver<XmlAttributeValue, PsiField> {
    protected PsiFieldReferenceSetResolver(XmlAttributeValue xmlAttributeValue) {
        super(xmlAttributeValue);
    }

    @Override
    public @NotNull String getText() {
        return getElement().getValue();
    }

    @Override
    public @NotNull Optional<PsiField> getStartElement(@Nullable String firstText) {
        Optional<PsiClass> clazz = MapperBacktrackingUtils.getPropertyClazz(getElement());
        if (clazz.isEmpty()) {
            return Optional.empty();
        }
        PsiClass psiClass = clazz.get();
        assert firstText != null;
//        PsiMethod propertySetter = PropertyUtil.findPropertySetter(psiClass, firstText, false, true);
        return Optional.ofNullable(PropertyUtil.findPropertyField(psiClass, firstText, false));
//        return null == propertySetter ? Optional.empty() : Optional.ofNullable(PropertyUtil.findPropertyField(psiClass, firstText, false));
    }

    @Override
    public @NotNull Optional<PsiField> resolve(@NotNull PsiField current, @NotNull String text) {
        PsiType type = current.getType();
        if (type instanceof PsiClassReferenceType && !((PsiClassReferenceType) type).hasParameters()) {
            PsiClass clazz = ((PsiClassReferenceType) type).resolve();
            if (null != clazz) {
                return JavaUtils.findSettablePsiField(clazz, text);
            }
        }
        return Optional.empty();
    }

}
