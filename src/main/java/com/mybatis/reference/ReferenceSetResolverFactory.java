package com.mybatis.reference;

import com.intellij.psi.PsiField;
import com.intellij.psi.xml.XmlAttributeValue;

public class ReferenceSetResolverFactory {

    public static <F extends XmlAttributeValue> ContextReferenceSetResolver<XmlAttributeValue, PsiField> createPsiFieldResolver(F xmlAttributeValue) {
        return new PsiFieldReferenceSetResolver(xmlAttributeValue);
    }
}
