package com.mybatis.utils;

import com.intellij.database.psi.DbTable;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.util.IconLoader;
import com.intellij.psi.PsiElement;
import com.intellij.util.ReflectionUtil;
import com.mybatis.database.model.TableSource;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Objects;

public class DatabaseIconUtils {
    public static final Icon OBJECT_GROUP = IconLoader.getIcon("/icon/objectGroup.svg", Objects.requireNonNull(ReflectionUtil.getGrandCallerClass()));
    public static final Icon TABLE = IconLoader.getIcon("/icon/table.svg", Objects.requireNonNull(ReflectionUtil.getGrandCallerClass()));
    public static final Icon SCHEMA = IconLoader.getIcon("/icon/schema.svg", Objects.requireNonNull(ReflectionUtil.getGrandCallerClass()));
    public static final Icon CLOUMN = IconLoader.getIcon("/icon/colIndex.svg", Objects.requireNonNull(ReflectionUtil.getGrandCallerClass()));
    public static final Icon MANAGE_DATA_SOURCES = IconLoader.getIcon("/icon/manageDataSources.svg", Objects.requireNonNull(ReflectionUtil.getGrandCallerClass()));
    public static final Icon MANAGE_DATA_SOURCES_DARK = IconLoader.getIcon("/icon/manageDataSources_dark.svg", Objects.requireNonNull(ReflectionUtil.getGrandCallerClass()));

    public static void updateStatus(@NotNull AnActionEvent e) {
        PsiElement[] psiElements = e.getData(LangDataKeys.PSI_ELEMENT_ARRAY);
        if (psiElements == null || psiElements.length == 0) {
            e.getPresentation().setEnabled(false);
            return;
        }
        for (PsiElement psiElement : psiElements) {
            if ((psiElement instanceof TableSource) || (psiElement instanceof DbTable)) {
                e.getPresentation().setEnabled(true);
                return;
            }
        }
        e.getPresentation().setEnabled(false);
    }
}
