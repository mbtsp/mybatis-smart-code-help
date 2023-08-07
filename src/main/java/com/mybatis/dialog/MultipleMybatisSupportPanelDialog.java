package com.mybatis.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.mybatis.generator.Generator;
import com.mybatis.messages.MybatisSmartCodeHelpBundle;
import com.mybatis.model.CacheModel.Cache.MultiProjectState;
import com.mybatis.model.GeneratorConfig;
import com.mybatis.notifier.MybatisConfigNotification;
import com.mybatis.state.ProjectState;
import com.mybatis.utils.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

public class MultipleMybatisSupportPanelDialog extends DialogWrapper {
    private final Project project;
    private final List<GeneratorConfig> configList;
    private final MultipleMybatisSupportPanel multipleMybatisSupportPanel;

    public MultipleMybatisSupportPanelDialog(@Nullable Project project, @NotNull List<GeneratorConfig> configList) {
        super(project, true);
        this.project = project;
        this.configList = configList;
        setTitle(MybatisSmartCodeHelpBundle.message("multiple.panel.title", configList.size()));
        multipleMybatisSupportPanel = new MultipleMybatisSupportPanel(project, configList);
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return multipleMybatisSupportPanel.$$$getRootComponent$$$();
    }

    @Override
    protected @Nullable ValidationInfo doValidate() {
        return super.doValidate();
    }

    @Override
    protected void doOKAction() {
        if (!multipleMybatisSupportPanel.doValidate()) {
            Messages.showErrorDialog(MybatisSmartCodeHelpBundle.message("multiple.panel.validate.error"), MybatisSmartCodeHelpBundle.message("error"));
            return;
        }
        if (configList == null || configList.isEmpty()) {
            MybatisConfigNotification.notifyWarning(project, MybatisSmartCodeHelpBundle.message("multiple.table.number.validate.error"));
            super.doOKAction();
            return;
        }
        MultiProjectState multiProjectState = multipleMybatisSupportPanel.getMultiProjectState();
        for (GeneratorConfig generatorConfig : configList) {
            ProjectState projectState = getProjectState(generatorConfig.getTableName(), multiProjectState);
            if (projectState == null) {
                MybatisConfigNotification.notifyError(project, MybatisSmartCodeHelpBundle.message("fail.to.generate.text", generatorConfig.getTableName(), "projectState is null"));
                return;
            }
            try {
                Generator generator = new Generator(project, projectState, generatorConfig);
                generator.setRefresh(false);
                generator.buildConfig();
                if (!generator.getWarnings().isEmpty()) {
                    for (String str : generator.getWarnings()) {
                        MybatisConfigNotification.notifyWarning(project, MybatisSmartCodeHelpBundle.message("fail.to.generate.text", generatorConfig.getTableName(), str));
                    }
                }
            } catch (Exception e) {
                MybatisConfigNotification.notifyError(project, MybatisSmartCodeHelpBundle.message("fail.to.generate.text", generatorConfig.getTableName(), e));
            }
        }
        VirtualFileManager.getInstance().refreshWithoutFileWatcher(true);
        MybatisConfigNotification.notifySuccess(project, MybatisSmartCodeHelpBundle.message("generate.file.success"));
        super.doOKAction();
    }

    public ProjectState getProjectState(String name, MultiProjectState multiProjectState) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        if (multiProjectState.getProjectStates().isEmpty()) {
            return null;
        }
        if (multiProjectState.getProjectStates().containsKey(name)) {
            return multiProjectState.getProjectStates().get(name);
        }
        return null;
    }
}
