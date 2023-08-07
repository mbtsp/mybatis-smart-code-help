package com.mybatis.dialog;

import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.util.TextFieldCompletionProvider;
import com.mybatis.handler.BaseMethodNameHandler;
import com.mybatis.utils.JavaUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MyTextFieldCompletionProvider extends TextFieldCompletionProvider {
    private List<String> methodNames;
    private Project project;
    private Editor editor;

    public MyTextFieldCompletionProvider(Project project, Editor editor, List<String> methodNames) {
        this.methodNames = methodNames;
        this.project = project;
        this.editor = editor;
    }

    @Override
    protected void addCompletionVariants(@NotNull String text, int offset, @NotNull String prefix, @NotNull CompletionResultSet result) {
        BaseMethodNameHandler baseMethodNameHandler = JavaUtils.buildMethodName(text, prefix, methodNames);
        JavaUtils.buildNewCompletionResult(result, prefix, baseMethodNameHandler.getMethodNames());
        JavaUtils.completionAddElements(result, baseMethodNameHandler, project, editor);
    }
}
