package com.mybatis.toolWindow;

import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import com.intellij.util.messages.MessageBusConnection;
import com.mybatis.toolWindow.action.ClearAction;
import com.mybatis.toolWindow.bus.MybatisLogToolWindowBus;
import org.jetbrains.annotations.NotNull;

import javax.swing.event.AncestorListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MybatisLogTooLWindow implements ToolWindowFactory, DumbAware, Disposable {
    private ConsoleView console;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        toolWindow.setToHideOnEmptyContent(true);
        TextConsoleBuilder consoleBuilder = TextConsoleBuilderFactory.getInstance().createBuilder(project);
        console = consoleBuilder.getConsole();
        SimpleToolWindowPanel simpleToolWindowPanel = new SimpleToolWindowPanel(false, true);
        simpleToolWindowPanel.setContent(console.getComponent());
        simpleToolWindowPanel.setToolbar(createToolBar().getComponent());
        ContentManager contentManager = toolWindow.getContentManager();
        Content content = contentManager.getFactory().createContent(simpleToolWindowPanel, null, false);
        content.setPreferredFocusableComponent(simpleToolWindowPanel);
        contentManager.addContent(content);
        contentManager.setSelectedContent(content, true);
        MessageBusConnection messageBusConnection = project.getMessageBus().connect(this);
        messageBusConnection.subscribe(MybatisLogToolWindowBus.MYBATIS_LOG_TOOL_WINDOW_BUS_TOPIC, new MybatisLogToolWindowBus() {
            @Override
            public void printSelectSql(String msg) {
                console.print(msg, ConsoleViewContentType.LOG_INFO_OUTPUT);
            }

            @Override
            public void printInsertSql(String msg) {
                console.print(msg, ConsoleViewContentType.LOG_WARNING_OUTPUT);
            }

            @Override
            public void printUpdateSql(String msg) {
                console.print(msg, ConsoleViewContentType.LOG_DEBUG_OUTPUT);
            }

            @Override
            public void printDeleteSql(String msg) {
                console.print(msg, ConsoleViewContentType.LOG_ERROR_OUTPUT);
            }
        });
    }

    private ActionToolbar createToolBar() {
        DefaultActionGroup defaultActionGroup = new DefaultActionGroup();
        defaultActionGroup.add(new ClearAction());
        return ActionManager.getInstance().createActionToolbar("MybatisLog", defaultActionGroup, false);
    }


    @Override
    public boolean shouldBeAvailable(@NotNull Project project) {
        return ToolWindowFactory.super.shouldBeAvailable(project);
    }

    @Override
    public boolean isApplicable(@NotNull Project project) {
        return ToolWindowFactory.super.isApplicable(project);
    }

    @Override
    public void dispose() {

    }
}
