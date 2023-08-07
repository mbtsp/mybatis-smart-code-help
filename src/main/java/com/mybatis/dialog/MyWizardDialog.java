package com.mybatis.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.wizard.WizardDialog;
import com.intellij.ui.wizard.WizardModel;
import com.intellij.ui.wizard.WizardStep;
import com.mybatis.common.Common;
import com.mybatis.generator.Generator;
import com.mybatis.messages.MybatisSmartCodeHelpBundle;
import com.mybatis.model.GeneratorConfig;
import com.mybatis.notifier.MybatisConfigNotification;
import com.mybatis.state.ProjectState;

import javax.swing.*;
import java.awt.*;

public class MyWizardDialog extends WizardDialog<WizardModel> {
    private final ProjectState projectState;
    private final Project project;
    private final GeneratorConfig generatorConfig;
    private final GeneratorConfigPanel generatorConfigPanel;
    private final StepTwoPanel stepTwoPanel;
    private final ConfigPanel configPanel;

    public MyWizardDialog(Project project, ProjectState projectState, GeneratorConfig generatorConfig, WizardModel wizardModel, GeneratorConfigPanel generatorConfigPanel, StepTwoPanel stepTwoPanel, ConfigPanel configPanel) {
        super(project, true, wizardModel);
        this.project = project;
        this.projectState = projectState;
        this.generatorConfig = generatorConfig;
        this.generatorConfigPanel = generatorConfigPanel;
        this.stepTwoPanel = stepTwoPanel;
        this.configPanel = configPanel;
    }


    @Override
    protected JComponent createSouthPanel() {
        JComponent component = super.createSouthPanel();
        Component[] components = component.getComponents();
        JPanel jPanel = (JPanel) components[0];
        components = jPanel.getComponents();
        jPanel.remove(components.length - 1);
        return component;
    }

    @Override
    public void onStepChanged() {
        WizardStep wizardStep = myModel.getCurrentStep();
        int i = myModel.getStepIndex(wizardStep);
        if (i == 1) {
            if (!generatorConfigPanel.doValidate()) {
                myModel.previous();
                return;
            }
        }
        super.onStepChanged();
    }

    @Override
    protected void doOKAction() {
        //加载所有的panel 变动
        this.setOKActionEnabled(false);
        if (!generatorConfigPanel.doValidate()) {
            Messages.showErrorDialog(MybatisSmartCodeHelpBundle.message("base.info.config.is.not.full"), MybatisSmartCodeHelpBundle.message("error"));
            return;
        }
        if (!configPanel.doValidate()) {
            Messages.showErrorDialog(MybatisSmartCodeHelpBundle.message("config.panel.is.not.full"), MybatisSmartCodeHelpBundle.message("error"));
            return;
        }
        Common.isStop = false;
        generatorConfigPanel.initProjectState(projectState);
        stepTwoPanel.initProjectState(projectState);
        configPanel.initProjectState(projectState);
        Generator generator = new Generator(project, projectState, generatorConfig);
        try {
            generator.buildConfig();
            if (!generator.getWarnings().isEmpty()) {
                for (String str : generator.getWarnings()) {
                    MybatisConfigNotification.notifyWarning(project, MybatisSmartCodeHelpBundle.message("fail.to.generate.text", generatorConfig.getTableName(), str));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            MybatisConfigNotification.notifyError(project, e.getMessage());
            return;
        }
        this.setOKActionEnabled(true);
        if (!Common.isStop) {
            MybatisConfigNotification.notifySuccess(project, MybatisSmartCodeHelpBundle.message("generate.file.success"));
            super.doOKAction();
        }

    }

}
