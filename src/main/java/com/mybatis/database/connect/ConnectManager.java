package com.mybatis.database.connect;


import com.intellij.openapi.project.Project;
import com.mybatis.database.model.DatabaseConfig;
import com.mybatis.enums.DataBaseType;
import org.jetbrains.annotations.NotNull;
import org.mybatis.generator.api.IntellijTableInfo;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ConnectManager {

    public static ConnectManager getConnectManager(@NotNull DataBaseType dataBaseType) {
        ConnectManager connectManager = null;
        if (dataBaseType.equals(DataBaseType.MySql) || dataBaseType.equals(DataBaseType.MySQL_5)) {
            connectManager = new MysqlConnectManager();
        } else if (dataBaseType.equals(DataBaseType.Oracle)) {
            connectManager = new OracleConnectManager();
        }
        return connectManager;
    }

    public boolean checkConnect(DatabaseConfig databaseConfig, Project project) throws SQLException, MalformedURLException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException;

    public Optional<Connection> getConnection(DatabaseConfig databaseConfig, Project project) throws SQLException, MalformedURLException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException;

    public Driver loadDriver(DatabaseConfig databaseConfig) throws MalformedURLException, ClassNotFoundException, SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException;

    public Optional<List<IntellijTableInfo>> getTables(DatabaseConfig databaseConfig, Project myProject) throws MalformedURLException, SQLException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException;

    public void uninstall(DatabaseConfig databaseConfig);
}
