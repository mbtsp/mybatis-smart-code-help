package com.mybatis.action.database;

import com.intellij.database.psi.DbTable;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.mybatis.datasource.SimpleDbConfig;
import com.mybatis.utils.GenConfigDialog;
import com.mybatis.utils.IconUtils;
import org.jetbrains.annotations.NotNull;

public class MybatisSupportFormGeneratorAction extends AnAction {
    public MybatisSupportFormGeneratorAction() {
        super(IconUtils.JAVA_MYBATIS_ICON);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        PsiElement psiElement = event.getData(LangDataKeys.PSI_ELEMENT);
        if (!(psiElement instanceof DbTable)) {
            return;
        }
        Project project = event.getProject();
        if (project == null) {
            return;
        }
        SimpleDbConfig dbConfig = new SimpleDbConfig((DbTable) psiElement);
        GenConfigDialog.showConfigDialog(project, dbConfig);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        PsiElement psiElement = e.getData(LangDataKeys.PSI_ELEMENT);
        if (!(psiElement instanceof DbTable)) {
            e.getPresentation().setEnabled(false);
        }
    }
}
