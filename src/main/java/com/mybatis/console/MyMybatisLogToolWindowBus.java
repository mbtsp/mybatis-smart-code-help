package com.mybatis.console;

import com.intellij.openapi.project.Project;
import com.mybatis.toolWindow.bus.MybatisLogToolWindowBus;

public class MyMybatisLogToolWindowBus implements MybatisLogToolWindowBus {
    private final Project project;

    public MyMybatisLogToolWindowBus(Project project) {
        this.project = project;
    }

    @Override
    public void printSelectSql(String msg) {
        project.getMessageBus().syncPublisher(MybatisLogToolWindowBus.MYBATIS_LOG_TOOL_WINDOW_BUS_TOPIC).printSelectSql(msg);
    }

    @Override
    public void printInsertSql(String msg) {
        project.getMessageBus().syncPublisher(MybatisLogToolWindowBus.MYBATIS_LOG_TOOL_WINDOW_BUS_TOPIC).printSelectSql(msg);
    }

    @Override
    public void printUpdateSql(String msg) {
        project.getMessageBus().syncPublisher(MybatisLogToolWindowBus.MYBATIS_LOG_TOOL_WINDOW_BUS_TOPIC).printSelectSql(msg);
    }

    @Override
    public void printDeleteSql(String msg) {
        project.getMessageBus().syncPublisher(MybatisLogToolWindowBus.MYBATIS_LOG_TOOL_WINDOW_BUS_TOPIC).printSelectSql(msg);
    }
}
