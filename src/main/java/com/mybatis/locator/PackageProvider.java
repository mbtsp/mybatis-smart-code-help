package com.mybatis.locator;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiPackage;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public abstract class PackageProvider {
    /**
     * Gets packages.
     *
     * @param project the project
     * @return the packages
     */
    @NotNull
    public abstract Set<PsiPackage> getPackages(@NotNull Project project);
}
