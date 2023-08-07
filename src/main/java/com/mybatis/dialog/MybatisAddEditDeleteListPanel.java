package com.mybatis.dialog;

import com.intellij.ui.AddEditDeleteListPanel;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MybatisAddEditDeleteListPanel<T> extends AddEditDeleteListPanel<T> {
    public MybatisAddEditDeleteListPanel(String title, List<T> initialList) {
        super(title, initialList);
    }

    @Override
    protected @Nullable T editSelectedItem(T item) {
        return null;
    }

    @Override
    protected @Nullable T findItemToAdd() {
        return null;
    }

}
