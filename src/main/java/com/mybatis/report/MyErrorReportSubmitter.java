package com.mybatis.report;

import com.intellij.diagnostic.IdeErrorsDialog;
import com.intellij.ide.DataManager;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.idea.IdeaLogger;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.diagnostic.ErrorReportSubmitter;
import com.intellij.openapi.diagnostic.IdeaLoggingEvent;
import com.intellij.openapi.diagnostic.SubmittedReportInfo;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.util.Consumer;
import com.mybatis.utils.EmailUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class MyErrorReportSubmitter extends ErrorReportSubmitter {
    @Override
    public @NotNull String getReportActionText() {
        return "Report to Developer";
    }

    @Override
    public boolean submit(IdeaLoggingEvent @NotNull [] events, @Nullable String additionalInfo, @NotNull Component parentComponent, @NotNull Consumer<? super SubmittedReportInfo> consumer) {
        IdeaLoggingEvent ideaLoggingEvent = events[0];
        IdeaPluginDescriptor ideaPluginDescriptor = IdeErrorsDialog.getPlugin(ideaLoggingEvent);
        String actionId = IdeaLogger.ourLastActionId;
        Project project = CommonDataKeys.PROJECT.getData(DataManager.getInstance().getDataContext(parentComponent));
        new Task.Backgroundable(project, "Submitting Error Report to zoulejiu@qq.com") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                if (ideaPluginDescriptor == null) {
                    return;
                }
                EmailUtils.sendHtml("Mybatis Smart Code Help Error", EmailUtils.coverPluginErrorHtml(ideaPluginDescriptor.getName(), ideaPluginDescriptor.getVersion(), actionId, ideaLoggingEvent.getThrowableText()), "zoulejiu@qq.com");
            }
        }.queue();
        return true;
    }
}
