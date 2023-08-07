package com.mybatis.notifier;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MybatisConfigNotification {
    public static void notifySuccess(@Nullable Project project, String content) {
        NotificationClient.notify("mybatis.config.notification", project, "Mybatis smart code help", content, NotificationType.INFORMATION, null);

    }

    public static void notifySuccess(@Nullable Project project, String title, String content, List<? extends AnAction> actions) {
        NotificationClient.notify("mybatis.config.notification", project, title, content, NotificationType.INFORMATION, actions);

    }

    public static void notifyError(@Nullable Project project, String content) {
        NotificationClient.notify("mybatis.config.notification", project, "Mybatis smart code help", content, NotificationType.ERROR, null);

    }

    public static void notifyWarning(@Nullable Project project, String content) {
        NotificationClient.notify("mybatis.config.notification", project, "Mybatis smart code help", content, NotificationType.WARNING, null);
    }

    public static void notifyWarning(@Nullable Project project, String content, List<? extends AnAction> actions) {
        NotificationClient.notify("mybatis.config.notification", project, "Mybatis smart code help", content, NotificationType.WARNING, actions);
    }

    public static void notifyWarning(@Nullable Project project, @NotNull String title, String content, List<? extends AnAction> actions) {
        NotificationClient.notify("mybatis.config.notification", project, title, content, NotificationType.WARNING, actions);

    }

    public static void notifyWarning(@Nullable Project project, @NotNull String title, String content) {
        NotificationClient.notify("mybatis.config.notification", project, title, content, NotificationType.WARNING, null);

    }
}
