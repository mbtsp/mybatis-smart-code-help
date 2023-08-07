package com.mybatis.utils;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class ApplicationUtils {
    public static int showRestartDialog(@NotNull String title, @NotNull Function<? super String, @Nls String> message) {
        String action = ApplicationManager.getApplication().isRestartCapable() ? "Restart" : "Shutdown";
        return Messages
                .showYesNoDialog(message.apply(action), title, action, "Not Now", Messages.getQuestionIcon());
    }

}
