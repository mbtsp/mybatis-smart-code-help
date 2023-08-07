package com.mybatis.notifier;

import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.project.Project;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

public class NotificationClient {
    public static void notify(String groupId, Project project, String title, String content, NotificationType notificationType, List<? extends AnAction> actions) {
        NotificationGroup notificationGroup = NotificationGroupManager.getInstance().getNotificationGroup(groupId);
        if (ApplicationInfo.getInstance().getBuild().getBaselineVersion() >= 212) {
            NotificationGroup211.notify(notificationGroup, project, title, content, notificationType, actions);
        } else {
            NotificationGroupLt211.notify(notificationGroup, project, title, content, notificationType, actions);
        }
    }

    private static class NotificationGroup211 {
        public static void notify(NotificationGroup notificationGroup, Project project, String title, String content, NotificationType notificationType, Collection<? extends AnAction> actions) {
            try {
                Method createNotificationMethod = notificationGroup.getClass().getMethod("createNotification", String.class, String.class, NotificationType.class);
                Object object = createNotificationMethod.invoke(notificationGroup, title, content, notificationType);
                if (actions != null && actions.size() > 0) {
                    Method addActionsMethod = object.getClass().getMethod("addActions", Collection.class);
                    addActionsMethod.invoke(object, actions);
                }
                Method notify = object.getClass().getMethod("notify", Project.class);
                notify.invoke(object, project);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ignored) {

            }
        }
    }

    private static class NotificationGroupLt211 {
        public static void notify(NotificationGroup notificationGroup, Project project, String title, String content, NotificationType notificationType, List<? extends AnAction> actions) {
            try {
                Method createNotificationMethod = notificationGroup.getClass().getMethod("createNotification", String.class, String.class, NotificationType.class, NotificationListener.class, String.class);
                Object object = createNotificationMethod.invoke(notificationGroup, title, content, notificationType, null, null);
                if (actions != null && actions.size() > 0) {
                    Method addActionsMethod = object.getClass().getMethod("addActions", List.class);
                    addActionsMethod.invoke(object, actions);
                }
                Method notify = object.getClass().getMethod("notify", Project.class);
                notify.invoke(object, project);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ignored) {

            }
        }
    }
}
