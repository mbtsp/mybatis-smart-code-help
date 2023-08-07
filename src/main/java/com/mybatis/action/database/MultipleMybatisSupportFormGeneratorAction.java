package com.mybatis.action.database;

import com.intellij.database.psi.DbTable;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiElement;
import com.mybatis.datasource.SimpleDbConfig;
import com.mybatis.dialog.MultipleMybatisSupportPanelDialog;
import com.mybatis.messages.MybatisSmartCodeHelpBundle;
import com.mybatis.model.GeneratorConfig;
import com.mybatis.utils.DatabaseIconUtils;
import com.mybatis.utils.GenConfigDialog;
import com.mybatis.utils.IconUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MultipleMybatisSupportFormGeneratorAction extends AnAction {
    public MultipleMybatisSupportFormGeneratorAction() {
        super(IconUtils.JAVA_MYBATIS_ICON);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        PsiElement[] psiElements = event.getData(LangDataKeys.PSI_ELEMENT_ARRAY);
        if (psiElements == null || psiElements.length == 0) {
            Messages.showErrorDialog(MybatisSmartCodeHelpBundle.message("database.choose.table.tip.text"), MybatisSmartCodeHelpBundle.message("database.choose.error"));
            return;
        }
        List<DbTable> dbTables = new ArrayList<>();
        for (PsiElement psiElement : psiElements) {
            if (psiElement instanceof DbTable) {
                dbTables.add((DbTable) psiElement);
            }
        }
        if (dbTables.isEmpty()) {
            Messages.showErrorDialog(MybatisSmartCodeHelpBundle.message("database.choose.table.tip.text"), MybatisSmartCodeHelpBundle.message("database.choose.error"));
            return;
        }
        Project project = event.getProject();
        if (project == null) {
            return;
        }
        List<GeneratorConfig> generatorConfigList = new ArrayList<>();
        for (DbTable dbTable : dbTables) {
            SimpleDbConfig dbConfig = new SimpleDbConfig(dbTable);
            generatorConfigList.add(GenConfigDialog.convertConfig(dbConfig));
        }
        MultipleMybatisSupportPanelDialog multipleMybatisSupportPanelDialog = new MultipleMybatisSupportPanelDialog(event.getProject(), generatorConfigList);
        multipleMybatisSupportPanelDialog.showAndGet();
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        DatabaseIconUtils.updateStatus(e);
    }


}
