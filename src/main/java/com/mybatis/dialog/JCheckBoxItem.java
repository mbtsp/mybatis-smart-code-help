package com.mybatis.dialog;

public class JCheckBoxItem {
    private Integer key;
    private String value;

    @Override
    public String toString() {
        return value;
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
