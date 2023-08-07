package com.mybatis.action.console;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.ui.Messages;
import com.intellij.util.messages.Topic;
import com.mybatis.action.console.bus.MybatisConsoleLogBus;
import com.mybatis.enums.DataTypeEnums;
import com.mybatis.notifier.MybatisConfigNotification;
import com.mybatis.utils.IconUtils;
import com.mybatis.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MybatisConsoleLog extends DumbAwareAction implements Disposable {
    public final static String preparingStr = "Preparing: ";
    public final static String parametersStr = "Parameters: ";
    public static Topic<MybatisConsoleLogBus> MYBATIS_CONSOLE_LOG=Topic.create("mybatis console log",MybatisConsoleLogBus.class);
    public MybatisConsoleLog() {
        super(IconUtils.JAVA_MYBATIS_ICON);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        DataContext dataContext = event.getDataContext();
        ConsoleViewImpl consoleView = (ConsoleViewImpl) dataContext.getData("consoleView");
        if (consoleView == null)
            return;
        Editor editor = consoleView.getEditor();
        if (editor == null)
            return;
        String mybatisLogAll = editor.getSelectionModel().getSelectedText();
        if (StringUtils.isBlank(mybatisLogAll)) {
            MybatisConfigNotification.notifyWarning(event.getProject(), "Choose a mybatis sql output statement");
            return;
        }
        if (!mybatisLogAll.contains(preparingStr)) {
            MybatisConfigNotification.notifyWarning(event.getProject(), "Choose a mybatis sql output statement");
            return;
        }
        if (mybatisLogAll.contains("?") && !mybatisLogAll.contains(parametersStr)) {
            MybatisConfigNotification.notifyWarning(event.getProject(), "Please select the log containing mybatis sql statements and parameters");
            return;
        }

        String[] mybatisLogs = mybatisLogAll.split("\n");
        if (mybatisLogs.length == 0) {
            MybatisConfigNotification.notifyWarning(event.getProject(), "Choose a mybatis sql output statement");
            return;
        }
        List<String> logs = Arrays.stream(mybatisLogs).filter(str -> (str.contains(parametersStr) || str.contains(preparingStr))).collect(Collectors.toList());
        if (logs.isEmpty()) {
            MybatisConfigNotification.notifyWarning(event.getProject(), "Choose a mybatis sql output statement");
            return;
        }
        String preparatorySql = null;
        String preparatoryParameters = null;
        for (String log : logs) {
            if (log.contains(preparingStr)) {
                preparatorySql = log;
                continue;
            }
            if (preparatorySql != null && log.contains(parametersStr)) {
                preparatoryParameters = log;
                break;
            }
        }
        if (preparatorySql == null) {
            MybatisConfigNotification.notifyWarning(event.getProject(), "Failed to parse mybatis sql log");
            return;
        }

        preparatorySql = preparatorySql.substring(preparatorySql.indexOf(preparingStr) + preparingStr.length());
        if (preparatoryParameters != null) {
            preparatoryParameters = preparatoryParameters.substring(preparatoryParameters.indexOf(parametersStr) + parametersStr.length());
            String[] parameters = preparatoryParameters.split(", ");
            List<Object> parameterObjectList = getObjectList(parameters);
            List<SQLStatement> sqlStatementList = SQLUtils.parseStatements(preparatorySql, DbType.mysql);
            preparatorySql = SQLUtils.toSQLString(sqlStatementList, DbType.mysql, parameterObjectList);
        }
        preparatorySql = SQLUtils.format(preparatorySql, DbType.mysql, SQLUtils.DEFAULT_FORMAT_OPTION);
        Messages.showMessageDialog(preparatorySql, "the Complete Sql Statement", Messages.getInformationIcon());
    }

    private List<Object> getObjectList(String[] parameters) {
        List<Object> parameterObjectList = new ArrayList<>(parameters.length);
        for (String parameter : parameters) {
            parameterObjectList.add(DataTypeEnums.getDataType(parameter));
        }
        return parameterObjectList;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setVisible(!e.getPresentation().getText().equals("Mybatis Log Convert to Sql"));
    }

    @Override
    public void dispose() {

    }
}
