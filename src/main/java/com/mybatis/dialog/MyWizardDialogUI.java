package com.mybatis.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.ui.wizard.WizardModel;
import com.intellij.ui.wizard.WizardNavigationState;
import com.intellij.ui.wizard.WizardStep;
import com.mybatis.messages.MybatisSmartCodeHelpBundle;
import com.mybatis.model.CacheModel.Cache.MultiProjectState;
import com.mybatis.model.GeneratorConfig;
import com.mybatis.state.MultipleMybatisStateComponent;
import com.mybatis.state.MybatisStateComponent;
import com.mybatis.state.ProjectState;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Objects;

public class MyWizardDialogUI {
    private final MyWizardDialog wizardDialog;
    private final ProjectState projectState;
    private final MultiProjectState multiProjectState;
    private final GeneratorConfigPanel generatorConfigPanel;
    private final StepTwoPanel stepTwoPanel;
    private final ConfigPanel configPanel;

    public MyWizardDialogUI(Project project, @NotNull GeneratorConfig generatorConfig) {
        ProjectState tempState = MybatisStateComponent.getInstance(project).getState();
        multiProjectState = MultipleMybatisStateComponent.getInstance(project).getState();
        if (multiProjectState != null && multiProjectState.getProjectStates() != null && multiProjectState.getProjectStates().containsKey(generatorConfig.getTableName())) {
            tempState = multiProjectState.getProjectStates().get(generatorConfig.getTableName());
        }
        this.generatorConfigPanel = new GeneratorConfigPanel(project, Objects.requireNonNull(tempState), generatorConfig);
        this.stepTwoPanel = new StepTwoPanel(project, tempState, generatorConfig);
        this.configPanel = new ConfigPanel(project, tempState, generatorConfig);
        this.projectState = tempState;
        WizardModel wizardModel = new WizardModel(MybatisSmartCodeHelpBundle.message("single.table.generated.title", generatorConfig.getTableName()));
        WizardStep<WizardModel> one = new WizardStep<>(MybatisSmartCodeHelpBundle.message("base.info.panel.title")) {
            @Override
            public JComponent prepare(WizardNavigationState state) {
                state.FINISH.setEnabled(true);
                return generatorConfigPanel.$$$getRootComponent$$$();
            }
        };

        WizardStep<WizardModel> two = new WizardStep<>(MybatisSmartCodeHelpBundle.message("step.two.panel.title")) {
            @Override
            public JComponent prepare(WizardNavigationState state) {
                state.FINISH.setEnabled(true);
                return stepTwoPanel.$$$getRootComponent$$$();
            }
        };
        WizardStep<WizardModel> end = new WizardStep<>(MybatisSmartCodeHelpBundle.message("config.panel.title")) {
            @Override
            public JComponent prepare(WizardNavigationState state) {
                return configPanel.$$$getRootComponent$$$();
            }
        };
        wizardModel.add(one);
        wizardModel.add(two);
        wizardModel.add(end);
        wizardDialog = new MyWizardDialog(project, projectState, generatorConfig, wizardModel, generatorConfigPanel, stepTwoPanel, configPanel);
    }

    public void showAndGet() {
        if (this.wizardDialog != null) {
            this.wizardDialog.showAndGet();
        }
    }
}
