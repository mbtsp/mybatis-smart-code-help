package com.mybatis.utils;

import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.ui.PopupHandler;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.lang.reflect.Method;

public class PopupMenuUtil {
    public static void showPopupMenu(@NotNull final JComponent myTree, @NotNull String groupId, @NotNull final String place) {
        if (ApplicationInfo.getInstance().getBuild().getBaselineVersion() <= 212) {
            PopupMenuUtil212.showPopupMenu(myTree, groupId, place);
        } else {
            PopupMenuUtilGt212.showPopupMenu(myTree, groupId, place);
        }
    }

    private static class PopupMenuUtil212 {
        public static void showPopupMenu(JComponent myTree, String groupId, String place) {
            try {
                Method installPopupHandlerMethod = PopupHandler.class.getMethod("installPopupHandler", JComponent.class, String.class, String.class);
                installPopupHandlerMethod.invoke(null, myTree, groupId, place);
            } catch (NoSuchMethodException | java.lang.reflect.InvocationTargetException |
                     IllegalAccessException ignored) {

            }
        }
    }

    private static class PopupMenuUtilGt212 {
        public static void showPopupMenu(JComponent myTree, String groupId, String place) {
            try {
                Method installPopupHandlerMethod = PopupHandler.class.getMethod("installPopupMenu", JComponent.class, String.class, String.class);
                installPopupHandlerMethod.invoke(null, myTree, groupId, place);
            } catch (NoSuchMethodException | java.lang.reflect.InvocationTargetException |
                     IllegalAccessException ignored) {

            }
        }
    }
}
