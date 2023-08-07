package com.mybatis.handler;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.mybatis.enums.MethodNameEnums;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface BaseMethodNameHandler {
    public void methodNames(@NotNull Project project, @NotNull PsiClass psiClass, String text, String prefix);

    public void methodNames(String text, String prefix, List<String> fields);

    public void methodNames(@NotNull Project project, @NotNull PsiClass psiClass, CompletionParameters parameters);

    public List<String> getMethodNames();

    public List<String> getContributorNames();

    public MethodNameEnums getMethodType();
}
