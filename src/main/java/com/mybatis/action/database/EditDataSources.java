package com.mybatis.action.database;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiElement;
import com.mybatis.database.model.DataConfigSource;
import com.mybatis.database.view.EditDatabaseDialog;
import com.mybatis.messages.MybatisSmartCodeHelpBundle;
import com.mybatis.utils.DatabaseIconUtils;
import org.jetbrains.annotations.NotNull;

public class EditDataSources extends AnAction {
    public EditDataSources() {
        super(DatabaseIconUtils.MANAGE_DATA_SOURCES);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        PsiElement psiElement = e.getData(CommonDataKeys.PSI_ELEMENT);
        if (psiElement == null) {
            return;
        }
        if (!(psiElement instanceof DataConfigSource)) {
            Messages.showErrorDialog(MybatisSmartCodeHelpBundle.message("database.edit"), MybatisSmartCodeHelpBundle.message("database.choose.error"));
            return;
        }
        EditDatabaseDialog editDatabaseDialog = new EditDatabaseDialog(e.getProject(), false, e, (DataConfigSource) psiElement);
        editDatabaseDialog.showAndGet();
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        PsiElement psiElement = e.getData(CommonDataKeys.PSI_ELEMENT);
        if (psiElement == null) {
            e.getPresentation().setEnabled(false);
            return;
        }
        if (!(psiElement instanceof DataConfigSource)) {
            e.getPresentation().setEnabled(false);
        }
    }
}
