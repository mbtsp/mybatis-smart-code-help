package com.mybatis.listener;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.util.text.HtmlChunk;
import com.mybatis.dialog.SupportDialog;
import com.mybatis.messages.MybatisSmartCodeHelpBundle;
import com.mybatis.notifier.MybatisConfigNotification;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CheckMybatisPlugin implements StartupActivity, DumbAware {
    private static final List<String> plugins = Arrays.asList("cn.wuzhizhan.plugin.mybatis", "com.baomidou.plugin.idea.mybatisx", "com.ccnode.codegenerator.MyBatisCodeHelperPro", "com.baomidou.plugin.idea.mybatisPlus");

    private static void disablePlugin(@NotNull Notification notification) {
        for (String id : plugins) {
            PluginId pluginId = PluginId.getId(id);
            if (pluginId != null) {
                PluginManagerCore.disablePlugin(pluginId);
            }
        }
        notification.expire();
    }

    @Override
    public void runActivity(@NotNull Project project) {
        new Task.Backgroundable(project, "Detect plugins that may cause conflicts") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                boolean flag = false;
                StringBuilder stringBuilder = new StringBuilder();
                for (String id : plugins) {
                    PluginId pluginId = PluginId.getId(id);
                    IdeaPluginDescriptor ideaPluginDescriptor = PluginManagerCore.getPlugin(pluginId);
                    if (ideaPluginDescriptor != null && ideaPluginDescriptor.isEnabled()) {
                        stringBuilder.append(ideaPluginDescriptor.getName());
                        stringBuilder.append(",");
                        flag = true;
                    }
                }
                if (stringBuilder.length() > 0) {
                    stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
                }
                if (flag) {
                    MybatisConfigNotification.notifyWarning(project, "<html>The plug-ins are below [" + stringBuilder + "] These plug-ins may conflict, please uninstall or disable for maximum experience</html>", Arrays.asList(new DisablePluginAction(MybatisSmartCodeHelpBundle.message("disable.plugin.text")), new DisableAndRestartPluginAction(MybatisSmartCodeHelpBundle.message("disable.plugin.and.restart.text"))));
                }

            }
        }.queue();
        for (String id : getUpdatedPlugins()) {
            if(id.equals("com.zoulejiu.mybatis.smart.plugin")){
                NotificationAction notificationAction =   NotificationAction.createSimple("支持/捐赠",()->{
                    new SupportDialog(project).show();
                });
               MybatisConfigNotification.notifySuccess(project,"Mybatis smart code help","Welcome to the mybatis plugin", List.of(notificationAction));
            }
        }

    }


    private static Set<String> getUpdatedPlugins() {
        try {
            Path file = getUpdatedPluginsFile();
            if (Files.isRegularFile(file)) {
                List<String> list = Files.readAllLines(file);
                Files.delete(file);
                return new HashSet<>(list);
            }
        }
        catch (IOException ignored) {

        }
        return new HashSet<>();
    }

    private static Path getUpdatedPluginsFile() {
        return Path.of(PathManager.getConfigPath(), ".updated_plugins_list");
    }


    private static class DisablePluginAction extends NotificationAction {

        public DisablePluginAction(String text) {
            super(text);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {
            disablePlugin(notification);
        }
    }

    private static class DisableAndRestartPluginAction extends NotificationAction {

        public DisableAndRestartPluginAction(String text) {
            super(text);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {
            disablePlugin(notification);
            ApplicationManager.getApplication().exit(true, false, true);
        }
    }
}
