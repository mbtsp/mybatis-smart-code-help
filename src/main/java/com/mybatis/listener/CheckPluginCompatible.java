package com.mybatis.listener;

import com.intellij.ide.plugins.*;
import com.intellij.ide.startup.StartupActionScriptManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.application.ex.ApplicationEx;
import com.intellij.openapi.application.ex.ApplicationManagerEx;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class CheckPluginCompatible extends PluginReplacement {
    private static final Logger logger = Logger.getInstance(CheckPluginCompatible.class);

    protected CheckPluginCompatible() {
        super("");
        PluginStateManager.addStateListener(new PluginStateListener() {
            @Override
            public void install(@NotNull IdeaPluginDescriptor descriptor) {
                if (descriptor.getPluginId().getIdString().equals("com.mybatis.smart.pro.plugin")) {
                    PluginId pluginId = PluginId.findId("com.zoulejiu.mybatis.smart.plugin");
                    IdeaPluginDescriptor ideaPluginDescriptor = PluginManagerCore.getPlugin(pluginId);
                    if (ideaPluginDescriptor != null) {
                        String pluginPath = PathManager.getPluginsPath() + File.separator + ideaPluginDescriptor.getName();
                        try {
                            StartupActionScriptManager.addActionCommand(new StartupActionScriptManager.DeleteCommand(Path.of(pluginPath)));
                        } catch (IOException e) {
                            logger.error(e);
                        }
                    }
                }else if(descriptor.getPluginId().getIdString().equals("com.zoulejiu.mybatis.smart.plugin")){
                    PluginId pluginId = PluginId.findId("com.mybatis.smart.pro.plugin");
                    IdeaPluginDescriptor ideaPluginDescriptor = PluginManagerCore.getPlugin(pluginId);
                    if (ideaPluginDescriptor != null) {
                        String pluginPath = PathManager.getPluginsPath() + File.separator + ideaPluginDescriptor.getName();
                        try {
                            StartupActionScriptManager.addActionCommand(new StartupActionScriptManager.DeleteCommand(Path.of(pluginPath)));
                        } catch (IOException e) {
                            logger.error(e);
                        }
                    }
                }
                if (PluginManagerConfigurable.showRestartDialog() == Messages.YES) {
                    ApplicationManagerEx.getApplicationEx().restart(true);
                }
            }

            @Override
            public void uninstall(@NotNull IdeaPluginDescriptor descriptor) {

            }
        });
    }

    @Override
    public @NotNull @Nls String getReplacementMessage(@NotNull IdeaPluginDescriptor oldPlugin, @NotNull IdeaPluginDescriptor newPlugin) {
        return super.getReplacementMessage(oldPlugin, newPlugin);
    }
}
