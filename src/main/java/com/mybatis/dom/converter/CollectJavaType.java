package com.mybatis.dom.converter;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.xml.*;
import com.mybatis.utils.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CollectJavaType extends ConverterAdapter<PsiClass> implements CustomReferenceConverter<PsiClass> {
    PsiClassConverter psiClassConverter = new PsiClassConverter();

    @Override
    public PsiReference @NotNull [] createReferences(GenericDomValue<PsiClass> value, PsiElement psiElement, ConvertContext context) {
        return psiClassConverter.createReferences(value, psiElement, context);
    }

    @Override
    public @Nullable String toString(@Nullable PsiClass psiClass, ConvertContext context) {
        try {
            return psiClassConverter.toString(psiClass, context);
        } catch (Exception e) {
            return null;
        }

    }

    @Override
    public @Nullable PsiClass fromString(@Nullable String text, ConvertContext context) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        return DomJavaUtil.findClass(text, context.getFile(), context.getModule(), context.getXmlElement() == null ? null : GlobalSearchScope.allScope(context.getXmlElement().getProject()));
    }
}
