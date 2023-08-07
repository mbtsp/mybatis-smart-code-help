package com.mybatis.action.tools;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

public class MybatisSmartPluginGenerateCode extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent event) {
        // TODO: insert action logic here
        Project project = event.getProject();
        if (project == null) {
            return;
        }
        Messages.showMessageDialog(project, "Function is under development", "Success", Messages.getInformationIcon());
    }
}
