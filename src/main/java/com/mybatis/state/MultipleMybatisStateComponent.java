package com.mybatis.state;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.mybatis.model.CacheModel.Cache.MultiProjectState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "MultipleMybatisStateComponent", storages = {@Storage(value = "MultipleMybatisStateComponent.xml")})
public class MultipleMybatisStateComponent implements PersistentStateComponent<MultiProjectState> {

    private MultiProjectState multiProjectState = new MultiProjectState();

    public static MultipleMybatisStateComponent getInstance(Project project) {
        return ServiceManager.getService(project, MultipleMybatisStateComponent.class);
    }

    @Override
    public @Nullable MultiProjectState getState() {
        return multiProjectState == null ? new MultiProjectState() : multiProjectState;
    }

    @Override
    public void loadState(@NotNull MultiProjectState state) {
        this.multiProjectState = state;
    }
}
