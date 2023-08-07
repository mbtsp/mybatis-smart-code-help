package com.mybatis.dialog;

import com.intellij.ui.wizard.WizardModel;
import com.intellij.ui.wizard.WizardStep;

public class MyWizardModel extends WizardModel {

    public MyWizardModel(String title) {
        super(title);
    }

    @Override
    public boolean isLast(WizardStep step) {
        return true;
    }

}
