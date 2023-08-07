package com.mybatis.alias;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.mybatis.utils.JavaUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;

public abstract class AliasResolver {
    protected Project project;

    public AliasResolver(Project project) {
        this.project = project;
    }

    protected Optional<AliasDesc> addAliasDesc(@NotNull Set<AliasDesc> descSet, @Nullable PsiClass psiClass, @Nullable String alias) {
        if (StringUtils.isBlank(alias) || JavaUtils.isModelClazz(psiClass)) {
            return Optional.empty();
        }
        AliasDesc aliasDesc = new AliasDesc();
        aliasDesc.setAlias(alias);
        aliasDesc.setPsiClass(psiClass);
        descSet.add(aliasDesc);
        return Optional.of(aliasDesc);
    }

    @NotNull
    public abstract Set<AliasDesc> getClassAliasDescriptions(@Nullable PsiElement psiElement);

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
