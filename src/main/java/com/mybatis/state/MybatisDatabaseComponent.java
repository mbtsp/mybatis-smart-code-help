package com.mybatis.state;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.mybatis.model.CacheModel.DatabaseSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;

@State(name = "MybatisDatabase", storages = {@Storage(value = "MybatisDatabase.xml")})
public class MybatisDatabaseComponent implements PersistentStateComponent<DatabaseSource> {
    private DatabaseSource tableSource = new DatabaseSource();

    public static MybatisDatabaseComponent getInstance(Project project) {
        return ServiceManager.getService(project, MybatisDatabaseComponent.class);
    }

    @Override
    public @Nullable DatabaseSource getState() {
        if (tableSource == null) {
            tableSource = new DatabaseSource();
            tableSource.setSources(new ConcurrentHashMap<>());
        }
        return tableSource;
    }

    @Override
    public void loadState(@NotNull DatabaseSource state) {
        this.tableSource = state;
    }
}
