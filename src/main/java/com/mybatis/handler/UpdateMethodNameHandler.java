package com.mybatis.handler;

import com.google.common.collect.Sets;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.mybatis.enums.MethodNameEnums;
import com.mybatis.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UpdateMethodNameHandler extends AbstractMethodNameHandler {
    private static final Set<MethodNameEnums> METHOD_NAME_SET = Sets.newHashSet(
            MethodNameEnums.BY,
            MethodNameEnums.AND);
    private final List<String> methodFields;
    private final List<String> fields;
    private final MethodNameEnums methodType;

    public UpdateMethodNameHandler() {
        this.methodFields = new ArrayList<>();
        this.fields = new ArrayList<>();
        this.methodType = MethodNameEnums.UPDATE;
    }

    @Override
    public void methodNames(@NotNull Project project, @NotNull PsiClass psiClass, String text, String prefix) {
        List<String> results = new ArrayList<>();
        if (!prefix.contains(MethodNameEnums.UPDATE.getKey())) {
            results.add(MethodNameEnums.UPDATE.getKey());
        }
        PsiField[] psiFields = psiClass.getFields();
        if (psiFields.length == 0) {
            return;
        }
        methodNames.add(MethodNameEnums.UPDATE.getKey());
        methodNames.add(MethodNameEnums.BY.getKey());
        methodNames.add(MethodNameEnums.AND.getKey());
        for (PsiField field : psiFields) {
            methodFields.add(StringUtils.upperCaseFirstChar(field.getName()));
            results.add(MethodNameEnums.UPDATE.getKey() + StringUtils.upperCaseFirstChar(field.getName()));
            results.add(StringUtils.upperCaseFirstChar(field.getName()));
            for (MethodNameEnums enums : METHOD_NAME_SET) {
                if (enums.getType() == 0) {
                    results.add(enums.getKey() + StringUtils.upperCaseFirstChar(field.getName()));
                } else if (enums.getType() == 1) {
                    results.add(StringUtils.upperCaseFirstChar(field.getName()) + enums.getKey());
                }
            }
        }
        methodNames.addAll(results);
        methodNames.addAll(methodFields);
        contributorMethodNames = filterString(results, text, prefix);
    }

    protected List<String> filterString(List<String> list, String text, String prefix) {
        if (StringUtils.isBlank(text) || list == null || list.isEmpty()) {
            return list;
        }
        if (!prefix.contains(MethodNameEnums.BY.getKey()) && prefix.contains(MethodNameEnums.UPDATE.getKey())) {
            //    list=delMethodName(prefix,list);
            if (prefix.equals(MethodNameEnums.UPDATE.getKey())) {
                //返回字符
                list = list.stream().filter(this::isMethodName).collect(Collectors.toList());
                return list;
            }
            //排除已经出现的字段
            //1.1 等到所有And开头的字段
            if (text.contains(MethodNameEnums.BY.getKey())) {
                list = list.stream().filter(str -> str.contains(MethodNameEnums.AND.getKey()) && !prefix.contains(str)).collect(Collectors.toList());
            } else {
                list = list.stream().filter(str -> (str.contains(MethodNameEnums.BY.getKey()) || str.contains(MethodNameEnums.AND.getKey()) && !prefix.contains(str))).collect(Collectors.toList());
            }
        } else if (prefix.contains(MethodNameEnums.BY.getKey())) {
            //处理编辑的时候就有By的字符
            String sub = text.substring(prefix.indexOf(MethodNameEnums.BY.getKey()) + MethodNameEnums.BY.getKey().length());
            if (StringUtils.isBlank(sub) || prefix.endsWith(MethodNameEnums.BY.getKey())) {
                //只加载基础字段
                return methodFields;
            }
            //去除基础字段
            list = delMethodAll(list);
            list = list.stream().filter(str -> !sub.contains(str) && !str.contains(MethodNameEnums.BY.getKey())).collect(Collectors.toList());
        }
        return list;
    }

    private List<String> delMethodAll(List<String> result) {
        if (result == null || result.isEmpty()) {
            return result;
        }
        return result.stream().filter(str -> !methodFields.contains(str)).collect(Collectors.toList());
    }

    private boolean isMethodName(String name) {
        if (StringUtils.isBlank(name) || methodFields.isEmpty()) {
            return false;
        }
        for (String str : methodFields) {
            if (name.equals(str)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void methodNames(String text, String prefix, List<String> fieldNames) {
        if (fieldNames == null || fieldNames.isEmpty()) {
            return;
        }
        List<String> results = new ArrayList<>();
        methodNames.add(MethodNameEnums.UPDATE.getKey());
        methodNames.add(MethodNameEnums.BY.getKey());
        methodNames.add(MethodNameEnums.AND.getKey());
        for (String field : fieldNames) {
            methodFields.add(StringUtils.upperCaseFirstChar(field));
            results.add(MethodNameEnums.UPDATE.getKey() + StringUtils.upperCaseFirstChar(field));
            results.add(StringUtils.upperCaseFirstChar(field));
            for (MethodNameEnums enums : METHOD_NAME_SET) {
                if (enums.getType() == 0) {
                    results.add(enums.getKey() + StringUtils.upperCaseFirstChar(field));
                } else if (enums.getType() == 1) {
                    results.add(StringUtils.upperCaseFirstChar(field) + enums.getKey());
                }
            }
        }
        methodNames.addAll(results);
        methodNames.addAll(methodFields);
        contributorMethodNames = filterString(results, text, prefix);
        ;
    }

    @Override
    public void methodNames(@NotNull Project project, @NotNull PsiClass psiClass, CompletionParameters parameters) {
        PsiElement psiElement = parameters.getOriginalPosition();
        if (psiElement == null) {
            return;
        }
        methodNames(project, psiClass, psiElement.getText(), CompletionUtil.findJavaIdentifierPrefix(parameters));
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
