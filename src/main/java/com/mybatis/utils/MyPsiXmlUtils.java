package com.mybatis.utils;

import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MyPsiXmlUtils {
    @Nullable
    public final GlobalSearchScope a(@NotNull PsiElement psiElement) {
        com.intellij.openapi.module.Module module = ModuleUtilCore.findModuleForPsiElement(psiElement);
        if(module==null){
            return null;
        }
        return GlobalSearchScope.moduleScope(module);
    }

    @NotNull
    public final GlobalSearchScope b(@NotNull PsiElement psiElement) {
        return GlobalSearchScope.allScope(psiElement.getProject());
    }


}
