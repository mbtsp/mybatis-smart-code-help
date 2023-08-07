package com.mybatis.codeInsight;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.mybatis.annotation.Annotation;
import com.mybatis.dom.model.Mapper;
import com.mybatis.utils.IconUtils;
import com.mybatis.utils.JavaUtils;
import com.mybatis.utils.MapperUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

public class InjectionLineMarkerProvider extends RelatedItemLineMarkerProvider {

    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
        if (!(element instanceof PsiField))
            return;
        PsiField field = (PsiField) element;
        if (!isTargetField(field))
            return;
        PsiType type = field.getType();
        if (!(type instanceof PsiClassReferenceType))
            return;
        Optional<PsiClass> clazz = JavaUtils.findClazz(element.getProject(), type.getCanonicalText());
        if (clazz.isEmpty())
            return;
        PsiClass psiClass = clazz.get();
        Optional<Mapper> mapper = MapperUtils.findFirstMapper(element.getProject(), psiClass);
        if (mapper.isEmpty()) {
            return;
        }
        NavigationGutterIconBuilder<PsiElement> builder = NavigationGutterIconBuilder.create(IconUtils.JAVA_MYBATIS_ICON).setAlignment(GutterIconRenderer.Alignment.CENTER).setTarget(psiClass).setTooltipTitle("Mapper:" + psiClass.getQualifiedName());
        result.add(builder.createLineMarkerInfo(field.getNameIdentifier()));
    }

    private boolean isTargetField(PsiField field) {
        if (JavaUtils.isAnnotationPresent(field, Annotation.AUTOWIRED)) {
            return true;
        }
        Optional<PsiAnnotation> resourceAnnotation = JavaUtils.getPsiAnnotation(field, Annotation.RESOURCE);
        if (resourceAnnotation.isPresent()) {
            PsiAnnotationMemberValue nameValue = resourceAnnotation.get().findAttributeValue("name");
            assert nameValue != null;
            String name = nameValue.getText().replaceAll("\"", "");
            return (StringUtils.isBlank(name) || name.equals(field.getName()));
        } else {
            String name = field.getType().getCanonicalText();
            return StringUtils.isNotBlank(name);
        }
    }
}
