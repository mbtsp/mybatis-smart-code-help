package com.mybatis.toolWindow.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import org.jetbrains.annotations.NotNull;

public class ClearAction extends DumbAwareAction {
    public ClearAction() {
        super(AllIcons.Actions.GC);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

    }
}
