package com.mybatis.dialog;

import com.intellij.database.psi.DbTable;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.psi.PsiElement;
import com.mybatis.datasource.SimpleDbConfig;
import com.mybatis.generator.Generator;
import com.mybatis.state.MybatisStateComponent;
import com.mybatis.state.ProjectState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Arrays;
import java.util.stream.Collectors;

public class MybatisDatabaseSupportDialog extends DialogWrapper {
    public static boolean isStop = false;
    private final Project project;
    private final SimpleDbConfig simpleDbConfig;
    private ProjectState projectState;
    private MybatisDatabaseSupportUiDialog mybatisDatabaseSupportUiDialog;

    public MybatisDatabaseSupportDialog(@NotNull Project project, boolean canBeParent, PsiElement psiElement) {
        super(project, canBeParent);
        this.project = project;
        projectState = MybatisStateComponent.getInstance(this.project).getState();
        if (projectState == null) {
            projectState = new ProjectState();

        }
        simpleDbConfig = new SimpleDbConfig((DbTable) psiElement);
        setTitle("Run Mybatis Generator for Database Table");
        super.init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        Module[] modules = ModuleManager.getInstance(project).getModules();
        if (modules.length > 0) {
            projectState.setJavaModules(Arrays.stream(modules).map(Module::getName).collect(Collectors.toList()));
        }
        mybatisDatabaseSupportUiDialog = new MybatisDatabaseSupportUiDialog(project, simpleDbConfig, projectState);
        return mybatisDatabaseSupportUiDialog.$$$getRootComponent$$$();
//       return new MybatisGeneratorUi().$$$getRootComponent$$$();
//        return new MybatisGeneratorCardLayoutUI().$$$getRootComponent$$$();
    }

    @Override
    protected void doOKAction() {
        this.setOKActionEnabled(false);
        mybatisDatabaseSupportUiDialog.initProjectState(this.projectState);
        Generator generator = new Generator(project, projectState, null);
        try {
            isStop = false;
            generator.buildConfig();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.setOKActionEnabled(true);
        if (!isStop) {
            Messages.showMessageDialog(project, "Generate files success", "Success", Messages.getInformationIcon());
            super.doOKAction();
        }


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

        return super.doValidate();
    }
}
