package com.mybatis.notifier;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;

public class DatabaseNotification {

    public static void notifyError(@Nullable Project project, String content) {
        NotificationClient.notify("mybatis.smart.notification", project, "Mybatis smart code help", content, NotificationType.ERROR, null);
    }

    public static void notifyWarning(@Nullable Project project, String content) {
        NotificationClient.notify("mybatis.smart.notification", project, "Mybatis smart code help", content, NotificationType.WARNING, null);
    }
}
