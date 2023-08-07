package com.mybatis.setting;

import com.mybatis.utils.StringUtils;

import java.util.Locale;

public enum MybatisSupportLanguage {
    Built_In_System("default", Locale.getDefault(), "系统语言"),
    ZH_CN("zh", Locale.CHINESE, "中文"),
    EN("en", Locale.US, "English");
    private final String code;
    private final Locale locale;
    private final String displayName;

    MybatisSupportLanguage(String code, Locale locale, String displayName) {
        this.code = code;
        this.locale = locale;
        this.displayName = displayName;
    }

    public static MybatisSupportLanguage getLanguage(String language) {
        if (StringUtils.isBlank(language)) {
            return MybatisSupportLanguage.Built_In_System;
        }
        MybatisSupportLanguage[] mybatisSupportLanguages = MybatisSupportLanguage.values();
        for (MybatisSupportLanguage mybatisSupportLanguage : mybatisSupportLanguages) {
            if (mybatisSupportLanguage.getCode().equals(language)) {
                return mybatisSupportLanguage;
            }
        }
        return MybatisSupportLanguage.Built_In_System;
    }

    public String getCode() {
        return this.code;
    }

    public Locale getLocale() {
        return locale;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
