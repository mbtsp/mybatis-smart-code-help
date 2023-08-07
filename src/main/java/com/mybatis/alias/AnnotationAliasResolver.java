package com.mybatis.alias;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.AnnotatedElementsSearch;
import com.mybatis.annotation.Annotation;
import com.mybatis.utils.JavaUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class AnnotationAliasResolver extends AliasResolver {
    public AnnotationAliasResolver(@NotNull Project project) {
        super(project);
    }

    public static AnnotationAliasResolver getInstance(@NotNull Project project) {
        return project.getComponent(AnnotationAliasResolver.class);
    }

    @Override
    public @NotNull Set<AliasDesc> getClassAliasDescriptions(@Nullable PsiElement psiElement) {
        Optional<PsiClass> clazz = Annotation.ALIAS.toPsiClass(this.project);
        if (clazz.isPresent()) {
            Collection<PsiClass> res = AnnotatedElementsSearch.searchPsiClasses(clazz.get(), GlobalSearchScope.allScope(this.project)).findAll();
            return res.stream().map(psiClass -> {
                Optional<String> txt = JavaUtils.getAnnotationValueText(psiClass, Annotation.ALIAS);
                if (!txt.isPresent()) return null;
                AliasDesc ad = new AliasDesc();
                ad.setAlias(txt.get());
                ad.setPsiClass(psiClass);
                return ad;
            }).collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }
}
