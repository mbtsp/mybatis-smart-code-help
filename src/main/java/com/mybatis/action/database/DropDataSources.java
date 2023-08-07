package com.mybatis.action.database;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiElement;
import com.mybatis.database.model.DataConfigSource;
import com.mybatis.database.view.MybatisGenerateCodeView;
import com.mybatis.messages.MybatisSmartCodeHelpBundle;
import com.mybatis.model.CacheModel.Cache.DataConfigSourceDto;
import com.mybatis.state.MybatisDatabaseComponent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;

public class DropDataSources extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        PsiElement psiElement = e.getData(CommonDataKeys.PSI_ELEMENT);
        if (psiElement == null) {
            return;
        }
        if (!(psiElement instanceof DataConfigSource)) {
            Messages.showErrorDialog(MybatisSmartCodeHelpBundle.message("database.remove"), MybatisSmartCodeHelpBundle.message("database.choose.error"));
            return;
        }
        Map<String, DataConfigSourceDto> sourceDtoMap = Objects.requireNonNull(MybatisDatabaseComponent.getInstance(e.getProject()).getState()).getSources();
        sourceDtoMap.remove(((DataConfigSource) psiElement).getName());
        MybatisGenerateCodeView mybatisGenerateCodeView = e.getData(MybatisGenerateCodeView.DATABASE_VIEW_KEY);
        if (mybatisGenerateCodeView == null) {
            return;
        }
        mybatisGenerateCodeView.getDatabaseStructure().refreshAll();
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
