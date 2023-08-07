package com.mybatis.toolWindow.bus;

import com.intellij.util.messages.Topic;

public interface MybatisLogToolWindowBus {
    public static Topic<MybatisLogToolWindowBus> MYBATIS_LOG_TOOL_WINDOW_BUS_TOPIC = Topic.create("mybatis log tool window",MybatisLogToolWindowBus.class);
    void printSelectSql(String msg);
    void printInsertSql(String msg);
    void printUpdateSql(String msg);
    void printDeleteSql(String msg);
}
