package com.mybatis.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.ReferenceSetBase;
import com.intellij.psi.xml.XmlAttributeValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ResultPropertyReferenceSet extends ReferenceSetBase<PsiReference> {
    public ResultPropertyReferenceSet(String text, @NotNull PsiElement psiElement, int offset) {
        super(text, psiElement, offset, DOT_SEPARATOR);
    }

    @Override
    protected @Nullable PsiReference createReference(TextRange range, int index) {
        XmlAttributeValue xmlAttributeValue = (XmlAttributeValue) getElement();
        return (null == xmlAttributeValue) ? null : new ContextPsiFieldReference(xmlAttributeValue, range, index);
    }
}
