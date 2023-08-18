package com.mybatis.dom.converter;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.*;
import com.mybatis.alias.AliasClassReference;
import com.mybatis.alias.AliasFacade;
import com.mybatis.utils.MybatisConstants;
import com.mybatis.utils.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AliasConverter extends ConverterAdapter<PsiClass> implements CustomReferenceConverter<PsiClass> {
    private final PsiClassConverter delegate = new PsiClassConverter();

    @Override
    public @NotNull PsiReference[] createReferences(GenericDomValue<PsiClass> value, PsiElement element, ConvertContext context) {
        if (((XmlAttributeValue) element).getValue().contains(MybatisConstants.DOT_SEPARATOR)) {
            return this.delegate.createReferences(value, element, context);
        }
        return new PsiReference[]{new AliasClassReference((XmlAttributeValue) element)};
    }

    @Override
    public @Nullable PsiClass fromString(@Nullable String text, ConvertContext context) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        if (!text.contains(MybatisConstants.DOT_SEPARATOR)) {
            return AliasFacade.getInstance(context.getProject()).findPsiClass(context.getXmlElement(), text).orElse(null);
        }
        return DomJavaUtil.findClass(text.trim(), context.getFile(), context.getModule(), GlobalSearchScope.allScope(context.getProject()));
    }

    @Override
    public @Nullable String toString(@Nullable PsiClass psiClass, ConvertContext context) {
        try {
            return this.delegate.toString(psiClass, context);
        } catch (Exception e) {
            return null;
        }

    }
}
