package com.mybatis.handler;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.mybatis.enums.MethodNameEnums;
import com.mybatis.utils.StringUtils;
import org.apache.commons.compress.utils.Sets;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DeleteMethodNameHandler extends AbstractMethodNameHandler {
    private static final Set<MethodNameEnums> METHOD_NAME_SET = Sets.newHashSet(
            MethodNameEnums.BY,
            MethodNameEnums.AND, MethodNameEnums.OR);
    private final List<String> methodFields;
    private final List<String> fields;
    private final MethodNameEnums methodType;

    public DeleteMethodNameHandler() {
        methodFields = new ArrayList<>();
        fields = new ArrayList<>();
        methodType = MethodNameEnums.DELETE;
    }

    @Override
    public void methodNames(@NotNull Project project, @NotNull PsiClass psiClass, String text, String prefix) {
        List<String> results = new ArrayList<>();
        if (!prefix.contains(MethodNameEnums.DELETE.getKey())) {
            results.add(MethodNameEnums.DELETE.getKey());
        }
        PsiField[] psiFields = psiClass.getFields();
        if (psiFields.length == 0) {
            return;
        }
        methodNames.add(MethodNameEnums.DELETE.getKey());
        methodNames.add(MethodNameEnums.BY.getKey());
        methodNames.add(MethodNameEnums.DELETE.getKey() + MethodNameEnums.BY.getKey());
        methodNames.add(MethodNameEnums.AND.getKey());
        for (PsiField field : psiFields) {
            methodFields.add(StringUtils.upperCaseFirstChar(field.getName()));
            results.add(MethodNameEnums.DELETE.getKey() + MethodNameEnums.BY.getKey() + StringUtils.upperCaseFirstChar(field.getName()));
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
        if (!prefix.contains(MethodNameEnums.BY.getKey()) && prefix.contains(MethodNameEnums.DELETE.getKey())) {
            //    list=delMethodName(prefix,list);
            if (prefix.equals(MethodNameEnums.DELETE.getKey())) {
                //返回字符
                list = new ArrayList<>();
                list.add(MethodNameEnums.BY.getKey());
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

    @Override
    public void methodNames(String text, String prefix, List<String> fields) {
        List<String> results = new ArrayList<>();
        if (!prefix.contains(MethodNameEnums.DELETE.getKey())) {
            results.add(MethodNameEnums.DELETE.getKey());
        }
        if (fields == null || fields.isEmpty()) {
            return;
        }
        methodNames.add(MethodNameEnums.DELETE.getKey());
        methodNames.add(MethodNameEnums.BY.getKey());
        methodNames.add(MethodNameEnums.DELETE.getKey() + MethodNameEnums.BY.getKey());
        methodNames.add(MethodNameEnums.AND.getKey());
        for (String field : fields) {
            methodFields.add(StringUtils.upperCaseFirstChar(field));
            results.add(MethodNameEnums.DELETE.getKey() + MethodNameEnums.BY.getKey() + StringUtils.upperCaseFirstChar(field));
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
