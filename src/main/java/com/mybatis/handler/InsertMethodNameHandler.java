package com.mybatis.handler;

import com.google.common.collect.Sets;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.mybatis.enums.MethodNameEnums;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class InsertMethodNameHandler extends AbstractMethodNameHandler {
    private static final Set<MethodNameEnums> METHOD_NAME_SET = Sets.newHashSet(
            MethodNameEnums.BY,
            MethodNameEnums.AND);
    private final List<String> methodFields;
    private final List<String> fields;
    private final MethodNameEnums methodType;

    public InsertMethodNameHandler() {
        methodFields = new ArrayList<>();
        fields = new ArrayList<>();
        methodType = MethodNameEnums.INSERT;
    }

    @Override
    public void methodNames(@NotNull Project project, @NotNull PsiClass psiClass, String text, String prefix) {
        contributorMethodNames.add("insertAll");
        contributorMethodNames.add("insertBatch");
        contributorMethodNames.add("insertSelective");
    }

    @Override
    public void methodNames(String text, String prefix, List<String> fields) {
        contributorMethodNames.add("insertAll");
        contributorMethodNames.add("insertBatch");
        contributorMethodNames.add("insertSelective");
    }

    @Override
    public void methodNames(@NotNull Project project, @NotNull PsiClass psiClass, CompletionParameters parameters) {
        contributorMethodNames.add("insertAll");
        contributorMethodNames.add("insertBatch");
        contributorMethodNames.add("insertSelective");
    }

    @Override
    public List<String> getMethodNames() {
        return methodNames;
    }

    @Override
    public List<String> getContributorNames() {
        return contributorMethodNames;
    }

    @Override
    public MethodNameEnums getMethodType() {
        return methodType;
    }
}
