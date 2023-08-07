package com.mybatis.console;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.intellij.execution.filters.Filter;
import com.intellij.openapi.project.Project;
import com.mybatis.action.console.MybatisConsoleLog;
import com.mybatis.enums.DataTypeEnums;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MybatisLogFilter implements Filter {
    private boolean sync = false;
    private final List<String> sqlList = new ArrayList<>();
    private Project project;
    private MyMybatisLogToolWindowBus myMybatisLogToolWindowBus;

    public MybatisLogFilter(Project project) {
        this.project = project;
        this.myMybatisLogToolWindowBus = new MyMybatisLogToolWindowBus(this.project);
    }

    @Override
    public @Nullable Result applyFilter(@NotNull String line, int entireLength) {
        if (sqlList.isEmpty() && line.contains(MybatisConsoleLog.preparingStr)) {
            sqlList.add(line);
            if (!line.contains("?")) {
                sync = true;
            }
        } else if (sqlList.size() > 0 && sqlList.get(0).contains(MybatisConsoleLog.preparingStr) && line.contains(MybatisConsoleLog.parametersStr)) {
            sqlList.add(line);
            sync = true;
        }
        if (sync) {
            try {
                String preparatorySql = null;
                String preparatoryParameters = null;
                for (String log : sqlList) {
                    if (log.contains(MybatisConsoleLog.preparingStr)) {
                        preparatorySql = log;
                        continue;
                    }
                    if (preparatorySql != null && log.contains(MybatisConsoleLog.parametersStr)) {
                        preparatoryParameters = log;
                        break;
                    }
                }
                if (preparatorySql == null) {
                    sqlList.clear();
                    return null;
                }
                preparatorySql = preparatorySql.substring(preparatorySql.indexOf(MybatisConsoleLog.preparingStr) + MybatisConsoleLog.preparingStr.length());
                if (preparatoryParameters != null) {
                    preparatoryParameters = preparatoryParameters.substring(preparatoryParameters.indexOf(MybatisConsoleLog.parametersStr) + MybatisConsoleLog.parametersStr.length());
                    String[] parameters = preparatoryParameters.split(", ");
                    List<Object> parameterObjectList = getObjectList(parameters);
                    List<SQLStatement> sqlStatementList = SQLUtils.parseStatements(preparatorySql, DbType.mysql);
                    preparatorySql = SQLUtils.toSQLString(sqlStatementList, DbType.mysql, parameterObjectList);
                }
                preparatorySql = SQLUtils.format(preparatorySql, DbType.mysql, SQLUtils.DEFAULT_FORMAT_OPTION);
                if (preparatorySql.startsWith("s") || preparatorySql.startsWith("S")) {
                    myMybatisLogToolWindowBus.printSelectSql("====================================================" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "=============================================================================\n");
                    myMybatisLogToolWindowBus.printSelectSql(preparatorySql + "\n");
                    myMybatisLogToolWindowBus.printSelectSql("=============================================================================================================================================\n");
                } else if (preparatorySql.startsWith("i") || preparatorySql.startsWith("I")) {
                    myMybatisLogToolWindowBus.printInsertSql("====================================================" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "==============================================================================\n");
                    myMybatisLogToolWindowBus.printInsertSql(preparatorySql + "\n");
                    myMybatisLogToolWindowBus.printInsertSql("===============================================================================================================================================\n");
                } else if (preparatorySql.startsWith("u") || preparatorySql.startsWith("U")) {
                    myMybatisLogToolWindowBus.printUpdateSql("====================================================" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "==============================================================================\n");
                    myMybatisLogToolWindowBus.printUpdateSql(preparatorySql + "\n");
                    myMybatisLogToolWindowBus.printUpdateSql("================================================================================================================================================\n");
                } else if (preparatorySql.startsWith("d") || preparatorySql.startsWith("D")) {
                    myMybatisLogToolWindowBus.printDeleteSql("====================================================" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "==============================================================================\n");
                    myMybatisLogToolWindowBus.printDeleteSql(preparatorySql + "\n");
                    myMybatisLogToolWindowBus.printDeleteSql("=================================================================================================================================================\n");
                }

            } finally {
                sqlList.clear();
                this.sync = false;
            }

        }
        return null;
    }

    private List<Object> getObjectList(String[] parameters) {
        List<Object> parameterObjectList = new ArrayList<>(parameters.length);
        for (String parameter : parameters) {
            parameter = parameter.replace("\n", "");
            parameterObjectList.add(DataTypeEnums.getDataType(parameter));
        }
        return parameterObjectList;
    }


}
