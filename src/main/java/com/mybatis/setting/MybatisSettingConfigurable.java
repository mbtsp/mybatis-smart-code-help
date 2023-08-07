package com.mybatis.setting;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class MybatisSettingConfigurable implements Configurable {
    private MybatisSettingPanel mybatisSettingPanel;

    public MybatisSettingConfigurable() {
        mybatisSettingPanel = new MybatisSettingPanel();
    }

    @Override
    public String getDisplayName() {
        return "Mybatis Smart Code Help";
    }

    @Override
    public @Nullable JComponent createComponent() {
        return mybatisSettingPanel.$$$getRootComponent$$$();
    }

    @Override
    public boolean isModified() {
        return mybatisSettingPanel.isModified();
    }

    @Override
    public void apply() throws ConfigurationException {
        mybatisSettingPanel.apply();
    }


    @Override
    public void reset() {
        mybatisSettingPanel.reset();
    }

    @Override
    public void disposeUIResources() {
        mybatisSettingPanel = null;
    }

}
