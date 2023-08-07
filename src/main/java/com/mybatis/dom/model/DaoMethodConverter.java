package com.mybatis.dom.model;

import com.intellij.psi.PsiMethod;
import com.intellij.util.xml.ConvertContext;
import com.mybatis.dom.converter.ConverterAdapter;
import com.mybatis.utils.JavaUtils;
import com.mybatis.utils.MapperUtils;
import org.jetbrains.annotations.Nullable;

public class DaoMethodConverter extends ConverterAdapter<PsiMethod> {
    @Override
    public @Nullable PsiMethod fromString(@Nullable String text, ConvertContext context) {
        Mapper mapper = MapperUtils.getMapper(context.getInvocationElement());
        return JavaUtils.findMethod(context.getProject(), MapperUtils.getNamespace(mapper), text).orElse(null);
    }
}
