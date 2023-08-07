package com.mybatis.action.tools;

import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import com.intellij.util.PlatformUtils;
import com.mybatis.database.view.MybatisGenerateCodeView;
import com.mybatis.state.MybatisSettingsState;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class DatabaseToolFactory implements DumbAware, ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        toolWindow.setToHideOnEmptyContent(true);
        MybatisGenerateCodeView mybatisGenerateCodeView = new MybatisGenerateCodeView(project);
        mybatisGenerateCodeView.setupToolWindow(toolWindow);
        ContentManager contentManager = toolWindow.getContentManager();
        Content content = contentManager.getFactory().createContent(mybatisGenerateCodeView.getComponent(), null, false);
        content.setPreferredFocusableComponent(mybatisGenerateCodeView.getMyTree());
        contentManager.addContent(content);
        contentManager.setSelectedContent(content, true);
    }

    @Override
    public void init(@NotNull ToolWindow toolWindow) {
        ToolWindowFactory.super.init(toolWindow);
    }

    @Override
    public boolean shouldBeAvailable(@NotNull Project project) {
        return Objects.requireNonNull(MybatisSettingsState.getInstance().getState()).isEnableCustomDatabaseTools();
    }

}
