package com.mybatis.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComponentWithBrowseButton;
import com.intellij.ui.TextAccessor;
import com.intellij.ui.TextFieldWithAutoCompletion;

import java.util.Collection;
import java.util.Collections;

public class MyTextFieldWithAutoCompletionWithBrowserButton extends ComponentWithBrowseButton<TextFieldWithAutoCompletion<String>> implements TextAccessor {

    public MyTextFieldWithAutoCompletionWithBrowserButton(Project project) {
        super(TextFieldWithAutoCompletion.create(project, Collections.emptyList(), true, null), null);

    }

    public void setAutoCompletionItems(Collection<String> items) {
        getChildComponent().setVariants(items);
    }

    @Override
    public String getText() {
        return getChildComponent().getText();
    }

    @Override
    public void setText(String text) {
        getChildComponent().setText(text);
    }

}
