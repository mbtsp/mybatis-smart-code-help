package com.mybatis.model.CacheModel;

import com.mybatis.database.util.DatabaseUtils;
import com.mybatis.utils.CommonDataTableUtils;

import java.util.Locale;

public class MybatisSettingConfig {
    private boolean enableCustomDatabaseTools = !CommonDataTableUtils.isIU();
    private String language = Locale.getDefault().getLanguage();
    private boolean enableMapperJumpXml = true;

    private boolean showFileBirdIcon = true;

    public boolean isEnableCustomDatabaseTools() {
        return enableCustomDatabaseTools;
    }

    public void setEnableCustomDatabaseTools(boolean enableCustomDatabaseTools) {
        this.enableCustomDatabaseTools = enableCustomDatabaseTools;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public boolean isEnableMapperJumpXml() {
        return enableMapperJumpXml;
    }

    public void setEnableMapperJumpXml(boolean enableMapperJumpXml) {
        this.enableMapperJumpXml = enableMapperJumpXml;
    }

    public boolean isShowFileBirdIcon() {
        return showFileBirdIcon;
    }

    public void setShowFileBirdIcon(boolean showFileBirdIcon) {
        this.showFileBirdIcon = showFileBirdIcon;
    }
}
