package com.mybatis.handler;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.mybatis.enums.MethodNameEnums;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DefaultMethodNameHandler implements BaseMethodNameHandler {

    @Override
    public void methodNames(@NotNull Project project, @NotNull PsiClass psiClass, String text, String prefix) {

    }

    @Override
    public void methodNames(String text, String prefix, @NotNull List<String> fields) {

    }

    @Override
    public void methodNames(@NotNull Project project, @NotNull PsiClass psiClass, CompletionParameters parameters) {

    }

    @Override
    public List<String> getMethodNames() {
        return null;
    }

    @Override
    public List<String> getContributorNames() {
        return null;
    }

    @Override
    public MethodNameEnums getMethodType() {
        return null;
    }
}
