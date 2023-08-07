package com.mybatis.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.util.textCompletion.TextFieldWithCompletion;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

public class MethodNameForSqlDialogWrapper extends DialogWrapper {
    private final Project project;
    private final List<String> methodNames;
    private JPanel jPanel;
    private String value;
    private TextFieldWithCompletion textFieldWithCompletion;

    protected MethodNameForSqlDialogWrapper(@NotNull Project project, List<String> methodNames, String value) {
        super(project, true);
        this.project = project;
        this.methodNames = methodNames;
        this.value = value;
        this.setTitle("Method Name for Sql");
        this.init();
    }

    /**
     * Factory method. It creates panel with dialog options. Options panel is located at the
     * center of the dialog's content pane. The implementation can return {@code null}
     * value. In this case there will be no options panel.
     */
    @Override
    protected @Nullable JComponent createCenterPanel() {
        MethodNameForSqlDialog methodNameForSqlDialog = new MethodNameForSqlDialog(this.project, value, methodNames);
        this.textFieldWithCompletion = methodNameForSqlDialog.getTextFieldWithCompletion();
        this.jPanel = methodNameForSqlDialog.getPanel();
        return this.jPanel;
    }

    /**
     * Validates user input and returns {@code null} if everything is fine
     * or validation description with component where problem has been found.
     *
     * @return {@code null} if everything is OK or validation descriptor
     * @see <a href="https://jetbrains.design/intellij/principles/validation_errors/">Validation errors guidelines</a>
     */
    @Override
    protected @Nullable ValidationInfo doValidate() {
        if (StringUtils.isBlank(this.textFieldWithCompletion.getText())) {
            return new ValidationInfo("Method name should not be blank", this.textFieldWithCompletion);
        }
        return super.doValidate();
    }

    /**
     * This method is invoked by default implementation of "OK" action. It just closes dialog
     * with {@code OK_EXIT_CODE}. This is convenient place to override functionality of "OK" action.
     * Note that the method does nothing if "OK" action isn't enabled.
     */
    @Override
    protected void doOKAction() {
        this.value = this.textFieldWithCompletion.getText().trim();
        super.doOKAction();
    }

    /**
     * This method is invoked by default implementation of "Cancel" action. It just closes dialog
     * with {@code CANCEL_EXIT_CODE}. This is convenient place to override functionality of "Cancel" action.
     * Note that the method does nothing if "Cancel" action isn't enabled.
     */
    @Override
    public void doCancelAction() {
        super.doCancelAction();
    }

    public String getValue() {
        return value;
    }

    public JPanel getjPanel() {
        return jPanel;
    }

    public void setjPanel(JPanel jPanel) {
        this.jPanel = jPanel;
    }
}
