package com.mybatis.database.util;

import com.intellij.database.model.DasColumn;
import com.intellij.database.model.DasObject;
import com.intellij.database.psi.DbDataSource;
import com.intellij.database.psi.DbElement;
import com.intellij.database.psi.DbPsiFacade;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

public class DatabaseUtils {


    public static Optional<DbElement> getDbElement(@NotNull Project project, @NotNull DbDataSource dataSource, @NotNull DasColumn column) {
        DbPsiFacade dbPsiFacade = DbPsiFacade.getInstance(project);
        if (ApplicationInfo.getInstance().getBuild().getBaselineVersion() <= 221) {
            return getDbElementLe212(dbPsiFacade, column);
        }
        return getDbElement212(dataSource, column);
    }

    private static Optional<DbElement> getDbElement212(DbDataSource dataSource, DasColumn column) {
        try {
            Method createNotificationMethod = dataSource.getClass().getMethod("findElement", DasObject.class);
            Object object = createNotificationMethod.invoke(dataSource, column);
            return object == null ? Optional.empty() : Optional.of((DbElement) object);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ignored) {
            return Optional.empty();
        }
    }

    private static Optional<DbElement> getDbElementLe212(DbPsiFacade dbPsiFacade, DasColumn column) {
        try {
            Method createNotificationMethod = dbPsiFacade.getClass().getMethod("findElement", DasObject.class);
            Object object = createNotificationMethod.invoke(dbPsiFacade, column);
            return object == null ? Optional.empty() : Optional.of((DbElement) object);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ignored) {
            return Optional.empty();
        }
    }
}
