package com.mybatis.model;

import javax.swing.*;
import java.util.List;

public class MyListModel<T> extends AbstractListModel<T> {
    private final List<T> data;

    public MyListModel(List<T> data) {
        this.data = data;
    }

    @Override
    public int getSize() {
        return data.size();
    }

    @Override
    public T getElementAt(int index) {
        return data.get(index);
    }

    public int getIndex(T t) {
        if (data == null || data.isEmpty()) {
            return -1;
        }
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).equals(t)) {
                return i;
            }
        }
        return -1;
    }

}
