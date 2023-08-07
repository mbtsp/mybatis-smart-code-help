package com.mybatis.state;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

@State(name = "MybatisStateComponent", storages = {@Storage(value = "MybatisStateComponent.xml")})
public class MybatisStateComponent implements PersistentStateComponent<ProjectState> {

    private ProjectState projectState = new ProjectState();

    public static MybatisStateComponent getInstance(Project project) {
        return ServiceManager.getService(project, MybatisStateComponent.class);
    }

    @Override
    public @Nullable ProjectState getState() {
        if (this.projectState == null) {
            this.projectState = new ProjectState();
            this.projectState.setJavaModules(new ArrayList<>());
            this.projectState.setPrimaryKeys(new ArrayList<>());
        }
        if (this.projectState.getJavaModules() == null || this.projectState.getJavaModules().isEmpty()) {
            this.projectState.setJavaModules(new ArrayList<>());
        }
        if (this.projectState.getPrimaryKeys() == null || this.projectState.getPrimaryKeys().isEmpty()) {
            this.projectState.setPrimaryKeys(new ArrayList<>());
        }
        return projectState;
    }

    @Override
    public void loadState(@NotNull ProjectState state) {
        this.projectState = state;
    }
}
