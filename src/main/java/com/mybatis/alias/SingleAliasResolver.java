package com.mybatis.alias;

import com.google.common.collect.Sets;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.mybatis.utils.MapperUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class SingleAliasResolver extends AliasResolver {
    public SingleAliasResolver(@NotNull Project project) {
        super(project);
    }

    @Override
    public @NotNull Set<AliasDesc> getClassAliasDescriptions(@Nullable PsiElement psiElement) {
        Set<AliasDesc> result = Sets.newHashSet();
        MapperUtils.processConfiguredTypeAliases(this.project, typeAlias -> {
            addAliasDesc(result, typeAlias.getType().getValue(), typeAlias.getAlias().getStringValue());
            return true;
        });
        return result;
    }
}
