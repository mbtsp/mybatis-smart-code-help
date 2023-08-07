package com.mybatis.alias;

import com.google.common.collect.Lists;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.mybatis.utils.MapperUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class ConfigPackageAliasResolver extends PackageAliasResolver {
    public ConfigPackageAliasResolver(@NotNull Project project) {
        super(project);
    }

    @Override
    protected @NotNull Collection<String> getPackages(@NotNull PsiElement psiElement) {
        List<String> result = Lists.newArrayList();
        MapperUtils.processConfiguredPackage(this.project, pkg -> {
            result.add(pkg.getName().getStringValue());
            return true;
        });
        return result;
    }
}
