package com.mybatis.action.database;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.mybatis.database.model.MyDataSourceConfig;
import com.mybatis.database.model.TableSource;
import com.mybatis.dialog.MultipleMybatisSupportPanelDialog;
import com.mybatis.model.GeneratorConfig;
import com.mybatis.state.MybatisSettingsState;
import com.mybatis.utils.GenConfigDialog;
import com.mybatis.utils.IconUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MultipleMybatisCommunitySupportFormGeneratorAction extends AnAction {
    public MultipleMybatisCommunitySupportFormGeneratorAction() {
        super(IconUtils.JAVA_MYBATIS_ICON);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        PsiElement[] psiElements = event.getData(LangDataKeys.PSI_ELEMENT_ARRAY);
        if (psiElements == null || psiElements.length == 0) {
            return;
        }
        List<TableSource> dbTables = new ArrayList<>();
        for (PsiElement psiElement : psiElements) {
            if (psiElement instanceof TableSource) {
                dbTables.add((TableSource) psiElement);
            }
        }
        if (dbTables.isEmpty()) {
            return;
        }
        Project project = event.getProject();
        if (project == null) {
            return;
        }
        List<GeneratorConfig> generatorConfigList = new ArrayList<>();
        for (TableSource dbTable : dbTables) {
            MyDataSourceConfig dbConfig = new MyDataSourceConfig((TableSource) dbTable);
            generatorConfigList.add(GenConfigDialog.convertConfig(dbConfig));
        }
        MultipleMybatisSupportPanelDialog multipleMybatisSupportPanelDialog = new MultipleMybatisSupportPanelDialog(event.getProject(), generatorConfigList);
        multipleMybatisSupportPanelDialog.showAndGet();
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        updateStatus(e);
    }

    public static void updateStatus(@NotNull AnActionEvent e) {
        PsiElement[] psiElements = e.getData(LangDataKeys.PSI_ELEMENT_ARRAY);
        if (psiElements == null || psiElements.length == 0) {
            e.getPresentation().setEnabled(false);
            return;
        }
        for (PsiElement psiElement : psiElements) {
            if ((psiElement instanceof TableSource)) {
                e.getPresentation().setEnabled(true);
                return;
            }
        }
        e.getPresentation().setEnabled(false);
    }
}
