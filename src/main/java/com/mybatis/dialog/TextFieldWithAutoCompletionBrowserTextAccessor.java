package com.mybatis.dialog;

import com.intellij.openapi.ui.TextComponentAccessor;
import com.intellij.ui.TextFieldWithAutoCompletion;
import org.jetbrains.annotations.NotNull;

public class TextFieldWithAutoCompletionBrowserTextAccessor implements TextComponentAccessor<TextFieldWithAutoCompletion<String>> {

    @Override
    public String getText(TextFieldWithAutoCompletion<String> component) {
        return component.getText();
    }

    @Override
    public void setText(TextFieldWithAutoCompletion<String> component, @NotNull String text) {
        component.setText(text);
    }
}
