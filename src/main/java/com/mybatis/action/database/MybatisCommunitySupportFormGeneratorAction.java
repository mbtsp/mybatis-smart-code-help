package com.mybatis.action.database;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiElement;
import com.mybatis.database.model.MyDataSourceConfig;
import com.mybatis.database.model.TableSource;
import com.mybatis.messages.MybatisSmartCodeHelpBundle;
import com.mybatis.state.MybatisSettingsState;
import com.mybatis.utils.GenConfigDialog;
import com.mybatis.utils.IconUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MybatisCommunitySupportFormGeneratorAction extends AnAction {
    public MybatisCommunitySupportFormGeneratorAction() {
        super(IconUtils.JAVA_MYBATIS_ICON);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        PsiElement psiElement = event.getData(LangDataKeys.PSI_ELEMENT);
        if (!(psiElement instanceof TableSource)) {
            Messages.showErrorDialog(MybatisSmartCodeHelpBundle.message("database.choose.table.tip.text"), MybatisSmartCodeHelpBundle.message("database.choose.error"));
            return;
        }
        Project project = event.getProject();
        if (project == null) {
            return;
        }
        MyDataSourceConfig dbConfig = new MyDataSourceConfig((TableSource) psiElement);
        GenConfigDialog.showConfigDialog(project, dbConfig);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        PsiElement psiElement = e.getData(LangDataKeys.PSI_ELEMENT);
        if (!(psiElement instanceof TableSource)) {
            e.getPresentation().setEnabled(false);
        }
    }

}
