package com.mybatis.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class SupportDialog extends DialogWrapper {
    private final SupportForm supportForm;

    public SupportDialog(@Nullable Project project) {
        super(project, true);
        setTitle("支持/捐赠");
        supportForm = new SupportForm();
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return supportForm.$$$getRootComponent$$$();
    }

}
