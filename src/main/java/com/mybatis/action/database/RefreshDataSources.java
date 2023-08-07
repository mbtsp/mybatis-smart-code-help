package com.mybatis.action.database;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.psi.PsiElement;
import com.mybatis.database.connect.ConnectManager;
import com.mybatis.database.model.DataConfigSource;
import com.mybatis.database.model.DatabaseConfig;
import com.mybatis.database.model.SchemaSource;
import com.mybatis.database.model.TableSource;
import com.mybatis.database.util.TableInfoConverter;
import com.mybatis.database.view.DatabaseStructure;
import com.mybatis.database.view.MybatisGenerateCodeView;
import com.mybatis.messages.MybatisSmartCodeHelpBundle;
import com.mybatis.notifier.DatabaseNotification;
import com.mybatis.state.MybatisDatabaseComponent;
import org.jetbrains.annotations.NotNull;
import org.mybatis.generator.api.IntellijTableInfo;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class RefreshDataSources extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        PsiElement psiElement = e.getData(CommonDataKeys.PSI_ELEMENT);
        if (psiElement == null) {
            return;
        }
        MybatisGenerateCodeView mybatisGenerateCodeView = e.getData(MybatisGenerateCodeView.DATABASE_VIEW_KEY);
        if (mybatisGenerateCodeView == null) {
            return;
        }
        if (psiElement instanceof DatabaseStructure.DbRootGroup) {
            Task task = new Task.Backgroundable(e.getProject(), MybatisSmartCodeHelpBundle.message("database.refresh"), false) {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    mybatisGenerateCodeView.getDatabaseStructure().refreshAll();
                }
            };
            task.queue();
            return;
        }
        if (psiElement instanceof DataConfigSource) {
            //刷新
            DataConfigSource dataConfigSource = (DataConfigSource) psiElement;
            refresh(e, dataConfigSource.getDatabase(), mybatisGenerateCodeView, dataConfigSource);
        }
        if (psiElement instanceof SchemaSource) {
            //刷新
            DataConfigSource dataConfigSource = ((SchemaSource) psiElement).getDataConfigSource();
            refresh(e, ((SchemaSource) psiElement).getSchemaName(), mybatisGenerateCodeView, dataConfigSource);
        }
        if (psiElement instanceof TableSource) {
            DataConfigSource dataConfigSource = ((TableSource) psiElement).getDataConfigSource();
            refresh(e, dataConfigSource.getDatabase(), mybatisGenerateCodeView, dataConfigSource);
        }
    }

    private void refresh(@NotNull AnActionEvent e, String schemaName, MybatisGenerateCodeView mybatisGenerateCodeView, DataConfigSource dataConfigSource) {
        ConnectManager connectManager = ConnectManager.getConnectManager(dataConfigSource.getDataBaseType());
        DatabaseConfig databaseConfig = new DatabaseConfig();
        databaseConfig.setJarUrl(dataConfigSource.getJarUrl());
        databaseConfig.setDataBaseType(dataConfigSource.getDataBaseType());
        databaseConfig.setName(dataConfigSource.getName());
        databaseConfig.setHost(dataConfigSource.getHost());
        databaseConfig.setPort(dataConfigSource.getPort());
        databaseConfig.setSchema(schemaName);
        databaseConfig.setUsername(dataConfigSource.getUserName());
        databaseConfig.setPassword(dataConfigSource.getPassword());
        databaseConfig.setUrl(dataConfigSource.getUrl());
        Task task = new Task.Backgroundable(e.getProject(), MybatisSmartCodeHelpBundle.message("database.refresh"), true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                try {
                    Optional<List<IntellijTableInfo>> tableInfos = connectManager.getTables(databaseConfig, myProject);
                    tableInfos.ifPresent(intellijTableInfos -> TableInfoConverter.loadState(intellijTableInfos, databaseConfig, Objects.requireNonNull(MybatisDatabaseComponent.getInstance(e.getProject()).getState()).getSources()));
                    mybatisGenerateCodeView.getDatabaseStructure().refreshAll();
                } catch (Exception exception) {
                    DatabaseNotification.notifyError(e.getProject(), exception.getMessage());
                }
            }
        };
        task.queue();
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        PsiElement psiElement = e.getData(CommonDataKeys.PSI_ELEMENT);
        if (psiElement == null) {
            e.getPresentation().setEnabled(false);
        }
    }
}
