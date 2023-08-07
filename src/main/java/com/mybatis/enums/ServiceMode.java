package com.mybatis.enums;

import com.mybatis.messages.MybatisSmartCodeHelpBundle;

public enum ServiceMode {
    EASY(0, "easy.mode", "easy.mode.desc"),
    FULL(1, "full.mode", "full.mode.desc"),
    PURE(-1, "pure.mode", "pure.mode.desc");
    private final Integer key;
    private final String value;
    private final String desc;

    ServiceMode(Integer key, String value, String desc) {
        this.key = key;
        this.value = value;
        this.desc = desc;
    }

    public String getValue() {
        return MybatisSmartCodeHelpBundle.message(value);
    }

    public Integer getKey() {
        return key;
    }

    public String getDesc() {
        return MybatisSmartCodeHelpBundle.message(desc);
    }
}
