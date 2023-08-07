package com.mybatis.database.connect;

import com.intellij.openapi.project.Project;
import com.mybatis.database.model.ColumnIndexInfo;
import com.mybatis.database.model.ColumnInfo;
import com.mybatis.database.model.DatabaseConfig;
import com.mybatis.database.model.TableInfo;
import com.mybatis.notifier.DatabaseNotification;
import com.mybatis.utils.StringUtils;
import org.mybatis.generator.api.IntellijTableInfo;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class MysqlConnectManager extends AbstractConnectManager {

    @Override
    public Optional<List<IntellijTableInfo>> getTables(DatabaseConfig databaseConfig, Project myProject) throws MalformedURLException, SQLException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Optional<Connection> connection = Optional.empty();
        try {
            connection = getConnection(databaseConfig, myProject);
            if (connection.isEmpty()) {
                DatabaseNotification.notifyWarning(myProject, "Failed to get database connection,schema:" + databaseConfig.getSchema());
                return Optional.empty();
            }
            Optional<List<TableInfo>> tableInfo = getTables(connection.get(), databaseConfig);
            if (tableInfo.isEmpty()) {
                DatabaseNotification.notifyWarning(myProject, "Failed to obtain database table information,schema:" + databaseConfig.getSchema());
                return Optional.empty();
            }
            Optional<List<ColumnInfo>> columnInfos = getColumns(connection.get(), databaseConfig);
            if (columnInfos.isEmpty()) {
                DatabaseNotification.notifyWarning(myProject, "Failed to get database table field information,schema:" + databaseConfig.getSchema());
                return Optional.empty();
            }
            Optional<List<ColumnIndexInfo>> columnIndexInfos = getColumnIndex(connection.get(), databaseConfig);
            if (columnIndexInfos.isEmpty()) {
                DatabaseNotification.notifyWarning(myProject, "Failed to get the index information of the database table field,schema:" + databaseConfig.getSchema());
                return Optional.empty();
            }
            return Optional.of(coverIntellijTableInfo(databaseConfig, tableInfo.get(), columnInfos.get(), columnIndexInfos.get()));
        } finally {
            if (connection.isPresent()) {
                connection.get().close();
                uninstall(databaseConfig);
            }
        }
    }

    public Optional<List<TableInfo>> getTables(Connection connection, DatabaseConfig databaseConfig) throws MalformedURLException, SQLException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        if (checkSchema(connection, databaseConfig)) return Optional.empty();
        String sql = "select table_name, table_type, table_comment, engine, table_collation\n" +
                "        from information_schema.tables\n" +
                "        where table_schema = '" + databaseConfig.getSchema() + "';";
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            List<TableInfo> tableInfos = new ArrayList<>();
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                TableInfo tableInfo = new TableInfo();
                tableInfo.setTableType(resultSet.getString("TABLE_TYPE"));
                tableInfo.setComment(resultSet.getString("table_comment"));
                tableInfo.setTableCollation(resultSet.getString("table_collation"));
                tableInfo.setEngine(resultSet.getString("engine"));
                tableInfo.setTableName(resultSet.getString("table_name"));
                tableInfos.add(tableInfo);
            }
            return Optional.of(tableInfos);
        } finally {
            if (resultSet != null) {
                resultSet.isClosed();
            }
            if (preparedStatement != null) {
                preparedStatement.isClosed();
            }
        }

    }

    public Optional<List<ColumnInfo>> getColumns(Connection connection, DatabaseConfig databaseConfig) throws MalformedURLException, SQLException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        if (checkSchema(connection, databaseConfig)) return Optional.empty();
        String sql = "select\n" +
                "    ordinal_position,\n" +
                "    column_name,\n" +
                "    column_type,\n" +
                "    data_type,\n" +
                "    column_default,\n" +
//                "    generation_expression,\n" +
                "    table_name,\n" +
                "    column_comment,\n" +
                "    is_nullable,\n" +
                "    extra,\n" +
                "    collation_name\n" +
                "from information_schema.columns\n" +
                "where table_schema = '" + databaseConfig.getSchema() + "'\n" +
                "order by table_name, ordinal_position;";
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            List<ColumnInfo> columnInfos = new ArrayList<>();
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                ColumnInfo columnInfo = new ColumnInfo();
                columnInfo.setOrdinalPosition(resultSet.getInt("ordinal_position"));
                columnInfo.setColumnName(resultSet.getString("column_name"));
                columnInfo.setColumnType(resultSet.getString("column_type"));
                columnInfo.setColumnDefault(resultSet.getString("column_default"));
                columnInfo.setTableName(resultSet.getString("table_name"));
                columnInfo.setColumnComment(resultSet.getString("column_comment"));
                columnInfo.setNullable(resultSet.getString("is_nullable").equals("YES"));
                columnInfo.setExtra(resultSet.getString("extra"));
                columnInfo.setCollationName(resultSet.getString("collation_name"));
                String dataType = resultSet.getString("data_type");
                if (StringUtils.isNotBlank(dataType)) {
                    dataType = dataType.toUpperCase(Locale.ROOT);
                }

                columnInfo.setDataType(dataType);
                columnInfos.add(columnInfo);
            }
            return Optional.of(columnInfos);
        } finally {
            if (resultSet != null) {
                resultSet.isClosed();
            }
            if (preparedStatement != null) {
                preparedStatement.isClosed();
            }
        }
    }

    public Optional<List<ColumnIndexInfo>> getColumnIndex(Connection connection, DatabaseConfig databaseConfig) throws MalformedURLException, SQLException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        if (checkSchema(connection, databaseConfig)) return Optional.empty();
        String sql = "select\n" +
                "    constraint_name,\n" +
                "    table_name,\n" +
                "    column_name,\n" +
                "    referenced_table_schema,\n" +
                "    referenced_table_name,\n" +
                "    referenced_column_name\n" +
                "from information_schema.key_column_usage\n" +
                "where table_schema = '" + databaseConfig.getSchema() + "' and CONSTRAINT_NAME='PRIMARY'\n" +
                "order by\n" +
                "    table_name\n" +
                "       , constraint_name\n" +
                "       , ordinal_position";
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            List<ColumnIndexInfo> columnInfos = new ArrayList<>();
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                ColumnIndexInfo columnInfo = new ColumnIndexInfo();
                columnInfo.setColumnName(resultSet.getString("column_name"));
                columnInfo.setConstraintName(resultSet.getString("constraint_name"));
                columnInfo.setTableName(resultSet.getString("table_name"));
                columnInfos.add(columnInfo);
            }
            return Optional.of(columnInfos);
        } finally {
            if (resultSet != null) {
                resultSet.isClosed();
            }
            if (preparedStatement != null) {
                preparedStatement.isClosed();
            }
        }
    }

    private boolean checkSchema(Connection connection, DatabaseConfig databaseConfig) throws SQLException, MalformedURLException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        if (com.mybatis.utils.StringUtils.isBlank(databaseConfig.getSchema())) {
            Optional<List<String>> schemas = getSchemas(connection);
            if (schemas.isEmpty()) {
                return true;
            }
            if (schemas.get().isEmpty()) {
                return true;
            }
            databaseConfig.setSchema(schemas.get().get(0));
        }
        return false;
    }

    public Optional<List<String>> getSchemas(Connection connection) throws SQLException, MalformedURLException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            String sql = "select schema_name, default_collation_name\n" +
                    "        from information_schema.schemata\n" +
                    "        order by if(schema() = schema_name, 1, 2), schema_name;";
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            List<String> strings = new ArrayList<>();
            while (resultSet.next()) {
                String name = resultSet.getString("schema_name");
                if (!name.equals("information_schema")) {
                    strings.add(name);
                }
            }
            return Optional.of(strings);
        } finally {
            if (resultSet != null) {
                resultSet.isClosed();
            }
            if (preparedStatement != null) {
                preparedStatement.isClosed();
            }
        }
    }
}
