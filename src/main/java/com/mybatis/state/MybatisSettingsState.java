package com.mybatis.state;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.mybatis.model.CacheModel.MybatisSettingConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
        name = "MybatisSettingsState",
        storages = {@Storage("MybatisSetting.xml")}
)
public class MybatisSettingsState implements PersistentStateComponent<MybatisSettingConfig> {
    private MybatisSettingConfig mybatisSettingConfig = new MybatisSettingConfig();

    public static MybatisSettingsState getInstance() {
        return ApplicationManager.getApplication().getService(MybatisSettingsState.class);
    }

    @Override
    public @Nullable MybatisSettingConfig getState() {
        return mybatisSettingConfig == null ? new MybatisSettingConfig() : mybatisSettingConfig;
    }

    @Override
    public void loadState(@NotNull MybatisSettingConfig state) {
        this.mybatisSettingConfig = state;
    }
}
