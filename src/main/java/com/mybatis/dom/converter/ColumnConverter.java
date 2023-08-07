package com.mybatis.dom.converter;

import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.*;
import com.mybatis.alias.AliasFacade;
import com.mybatis.database.util.DatabaseUtils;
import com.mybatis.reference.ResultColumnReferenceSet;
import com.mybatis.utils.CommonDataTableUtils;
import com.mybatis.utils.JavaUtils;
import com.mybatis.utils.StringUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ColumnConverter extends ConverterAdapter<PsiClass> implements CustomReferenceConverter<PsiClass> {
    public static final String NAMESPACE = "namespace";
    private final PsiClassConverter psiClassConverter = new PsiClassConverter();

    @NotNull
    @Override
    public PsiReference[] createReferences(GenericDomValue<PsiClass> value, PsiElement element, ConvertContext context) {
        String stringValue = value.getStringValue();
        if (stringValue == null) {
            return PsiReference.EMPTY_ARRAY;
        }
        // 社区版就不需要跳转到数据库的列了
        if (!CommonDataTableUtils.isIU()) {
            return PsiReference.EMPTY_ARRAY;
        }
        int offsetInElement = ElementManipulators.getOffsetInElement(element);

        Optional<PsiClass> mapperClassOptional = findMapperClass(context);
        PsiClass mapperClass = mapperClassOptional.orElse(null);
        return new ResultColumnReferenceSet(stringValue, element, offsetInElement, mapperClass).getPsiReferences();
    }

    private Optional<PsiClass> findMapperClass(ConvertContext context) {
        XmlTag rootTag = context.getFile().getRootTag();
        if (rootTag != null) {
            XmlAttribute namespace = rootTag.getAttribute(NAMESPACE);
            if (namespace != null) {
                String value = namespace.getValue();
                if (!StringUtils.isEmpty(value)) {
                    return JavaUtils.findClazz(context.getProject(), value);
                }
            }
        }
        return Optional.empty();
    }

    @Nullable
    @Override
    public PsiClass fromString(@Nullable @NonNls String paramString, ConvertContext context) {
        if (StringUtils.isBlank(paramString)) {
            return null;
        }
        if (!paramString.contains(".")) {
            return AliasFacade.getInstance(context.getProject()).findPsiClass(context.getXmlElement(), paramString).orElse(null);
        }
//        DomElement ctxElement = context.getInvocationElement();
//        return ctxElement instanceof GenericAttributeValue ? ((GenericAttributeValue) ctxElement).getXmlAttributeValue() : null;
        return DomJavaUtil.findClass(paramString.trim(), context.getFile(), context.getModule(), GlobalSearchScope.allScope(context.getProject()));
    }

}
