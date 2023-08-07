package com.mybatis.handler;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.mybatis.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MethodNameFactory {
    public BaseMethodNameHandler searchMethodName(@NotNull Project project, @NotNull PsiClass psiClass, @NotNull CompletionParameters parameters) {
        PsiElement psiElement = parameters.getOriginalPosition();
        if (psiElement == null) {
            return new DefaultMethodNameHandler();
        }
        String text = psiElement.getText();
        if (StringUtils.isBlank(text)) {
            return new DefaultMethodNameHandler();
        }
        char firstChar = text.charAt(0);
        if (firstChar == 's' || firstChar == 'f') {
            SelectMethodNameHandler selectMethodNameHandler = new SelectMethodNameHandler();
            selectMethodNameHandler.methodNames(project, psiClass, parameters);
            return selectMethodNameHandler;
        } else if (firstChar == 'u') {
            UpdateMethodNameHandler updateMethodNameHandler = new UpdateMethodNameHandler();
            updateMethodNameHandler.methodNames(project, psiClass, parameters);
            return updateMethodNameHandler;
        } else if (firstChar == 'i') {
            InsertMethodNameHandler insertMethodNameHandler = new InsertMethodNameHandler();
            insertMethodNameHandler.methodNames(project, psiClass, parameters);
            return insertMethodNameHandler;
        } else if (firstChar == 'd') {
            DeleteMethodNameHandler deleteMethodNameHandler = new DeleteMethodNameHandler();
            deleteMethodNameHandler.methodNames(project, psiClass, parameters);
            return deleteMethodNameHandler;
        }
        return new DefaultMethodNameHandler();
    }

    public BaseMethodNameHandler searchMethodName(String text, String prefix, List<String> fields) {
        if (StringUtils.isBlank(text)) {
            return new DefaultMethodNameHandler();
        }
        char firstChar = text.charAt(0);
        if (firstChar == 's' || firstChar == 'f') {
            SelectMethodNameHandler selectMethodNameHandler = new SelectMethodNameHandler();
            selectMethodNameHandler.methodNames(text, prefix, fields);
            return selectMethodNameHandler;
        } else if (firstChar == 'u') {
            UpdateMethodNameHandler updateMethodNameHandler = new UpdateMethodNameHandler();
            updateMethodNameHandler.methodNames(text, prefix, fields);
            return updateMethodNameHandler;
        } else if (firstChar == 'i') {
            InsertMethodNameHandler insertMethodNameHandler = new InsertMethodNameHandler();
            insertMethodNameHandler.methodNames(text, prefix, fields);
            return insertMethodNameHandler;
        } else if (firstChar == 'd') {
            DeleteMethodNameHandler deleteMethodNameHandler = new DeleteMethodNameHandler();
            deleteMethodNameHandler.methodNames(text, prefix, fields);
            return deleteMethodNameHandler;
        }
        return new DefaultMethodNameHandler();
    }
}
