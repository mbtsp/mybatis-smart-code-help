package com.mybatis.generatorSql.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.mybatis.messages.MybatisSmartCodeHelpBundle;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;
import java.util.Set;

public class ChooseIfTestDialog extends DialogWrapper {
    private final List<String> conditionFields;
    private final ChooseIfTestParams chooseIfTestParams;

    public ChooseIfTestDialog(@Nullable Project project, boolean canBeParent, List<String> conditionFields) {
        super(project, canBeParent);
        this.conditionFields = conditionFields;
        chooseIfTestParams = new ChooseIfTestParams(this.conditionFields);
        super.init();
        setTitle(MybatisSmartCodeHelpBundle.message("choose.if.test.params"));
        setSize(640, 400);

    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return chooseIfTestParams.$$$getRootComponent$$$();
    }

    public Set<String> getConditionFields() {
        return chooseIfTestParams.getConditionFields();
    }
}
