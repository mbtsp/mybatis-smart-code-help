package com.mybatis.database.view;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.mybatis.database.connect.ConnectManager;
import com.mybatis.database.model.DatabaseConfig;
import com.mybatis.database.util.TableInfoConverter;
import com.mybatis.messages.MybatisSmartCodeHelpBundle;
import com.mybatis.model.CacheModel.Cache.DataConfigSourceDto;
import com.mybatis.model.CacheModel.DatabaseSource;
import com.mybatis.notifier.DatabaseNotification;
import com.mybatis.state.MybatisDatabaseComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mybatis.generator.api.IntellijTableInfo;

import javax.swing.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AddDatabaseDialog extends DialogWrapper {
    private final DatabaseSource databaseSource;
    private final Project project;
    private final Map<String, DataConfigSourceDto> dataConfigSources;
    private AddDatabase addDatabase;
    private AnActionEvent actionEvent;
    private MyPasswordSafe myPasswordSafe;

    public AddDatabaseDialog(@Nullable Project project, boolean canBeParent, AnActionEvent actionEvent) {
        super(project, canBeParent);
        this.project = project;
        this.actionEvent = actionEvent;
        this.myPasswordSafe = new MyPasswordSafe();
        databaseSource = MybatisDatabaseComponent.getInstance(project).getState();
        assert databaseSource != null;
        this.dataConfigSources = databaseSource.getSources();
        setTitle(MybatisSmartCodeHelpBundle.message("add.database.config"));
        super.init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        addDatabase = new AddDatabase(project, null);
        return addDatabase.$$$getRootComponent$$$();
    }

    @Override
    protected @Nullable ValidationInfo doValidate() {
        if (!addDatabase.check()) {
            return null;
        }
        return super.doValidate();
    }

    @Override
    protected void doOKAction() {
        this.setOKActionEnabled(false);
        if (!addDatabase.check()) {
            this.setOKActionEnabled(true);
            return;
        }

        DatabaseConfig databaseConfig = addDatabase.getDatabaseConfig();
        ConnectManager connectManager = ConnectManager.getConnectManager(databaseConfig.getDataBaseType());
        Task task = new Task.Backgroundable(project, MybatisSmartCodeHelpBundle.message("load.database.config")) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                try {
                    Optional<List<IntellijTableInfo>> tableInfos = connectManager.getTables(databaseConfig, myProject);
                    tableInfos.ifPresent(intellijTableInfos -> TableInfoConverter.loadState(intellijTableInfos, databaseConfig, dataConfigSources));
                    MybatisGenerateCodeView mybatisGenerateCodeView = actionEvent.getData(MybatisGenerateCodeView.DATABASE_VIEW_KEY);
                    if (mybatisGenerateCodeView == null) {
                        return;
                    }
                    mybatisGenerateCodeView.getDatabaseStructure().refreshAll();
                } catch (Exception e) {
                    DatabaseNotification.notifyError(project, e.getMessage());
                }
            }
        };
        task.queue();
        this.setOKActionEnabled(true);
        super.doOKAction();
    }


}
