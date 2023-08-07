package com.mybatis.action.database;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.psi.PsiElement;
import com.mybatis.database.view.AddDatabaseDialog;
import com.mybatis.database.view.DatabaseStructure;
import org.jetbrains.annotations.NotNull;

public class AddDataSources extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        AddDatabaseDialog addDatabaseDialog = new AddDatabaseDialog(e.getProject(), false, e);
        addDatabaseDialog.showAndGet();
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        PsiElement psiElement = e.getData(CommonDataKeys.PSI_ELEMENT);
        if (psiElement == null) {
            e.getPresentation().setEnabled(false);
            return;
        }
        if (!(psiElement instanceof DatabaseStructure.DbRootGroup)) {
            e.getPresentation().setEnabled(false);
        }
    }
}
