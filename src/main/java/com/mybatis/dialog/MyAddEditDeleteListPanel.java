package com.mybatis.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.ui.AddEditDeleteListPanel;
import com.intellij.ui.ListSpeedSearch;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MyAddEditDeleteListPanel extends AddEditDeleteListPanel<String> {
    private final Project project;
    private final List<String> methodNameList;
    private final String title;

    public MyAddEditDeleteListPanel(@Nls String title, Project project, List<String> initialList, List<String> methodNameList) {
        super(title, initialList);
        this.project = project;
        this.methodNameList = methodNameList;
        this.title = title;
        new ListSpeedSearch<>(this.myList);
    }

    @Override
    protected @Nullable String editSelectedItem(String item) {
        return null;
    }

    @Override
    protected @Nullable String findItemToAdd() {
        MethodNameForSqlDialogWrapper methodNameForSqlDialogWrapper = new MethodNameForSqlDialogWrapper(this.project, methodNameList, "");
        boolean show = methodNameForSqlDialogWrapper.showAndGet();
        if (show) {
            return methodNameForSqlDialogWrapper.getValue();
        }
        return "";
    }

}
