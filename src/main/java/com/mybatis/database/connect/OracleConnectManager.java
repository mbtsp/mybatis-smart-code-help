package com.mybatis.database.connect;

import com.intellij.openapi.project.Project;
import com.mybatis.database.model.ColumnIndexInfo;
import com.mybatis.database.model.DatabaseConfig;
import com.mybatis.database.model.TableInfo;
import com.mybatis.notifier.DatabaseNotification;
import org.jetbrains.annotations.NotNull;
import org.mybatis.generator.api.IntellijColumnInfo;
import org.mybatis.generator.api.IntellijTableInfo;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class OracleConnectManager extends AbstractConnectManager {

    public Optional<List<TableInfo>> getTables(Connection connection, DatabaseConfig databaseConfig) throws SQLException {
        try (ResultSet re = connection.getMetaData().getTables(databaseConfig.getSchema(), databaseConfig.getSchema(), null, new String[]{"TABLE"})) {
            List<TableInfo> tableInfos = new ArrayList<>();
            while (re.next()) {
                TableInfo tableInfo = new TableInfo();
                tableInfo.setTableName(re.getString("TABLE_NAME"));
                tableInfo.setTableType(re.getString("TABLE_TYPE"));
                tableInfo.setComment(re.getString("REMARKS"));
                tableInfos.add(tableInfo);
            }
            return Optional.of(tableInfos);
        }

    }

    @NotNull
    private Optional<List<ColumnIndexInfo>> getPrimaryKeyColumnList(Connection connection, DatabaseConfig databaseConfig, String tableName) throws SQLException {
        try (ResultSet primaryKeysResultSet = connection.getMetaData().getPrimaryKeys(databaseConfig.getSchema(), databaseConfig.getSchema(), tableName)) {
            List<ColumnIndexInfo> primaryKeyColumnList = new ArrayList<>();
            Map<Short, ColumnIndexInfo> keyColumns = new TreeMap<>();
            while (primaryKeysResultSet.next()) {
                String tableName1 = primaryKeysResultSet.getString("TABLE_NAME");
                String columnName = primaryKeysResultSet.getString("COLUMN_NAME"); //$NON-NLS-1$
                ColumnIndexInfo intellijColumnInfo = new ColumnIndexInfo();
                short keySeq = primaryKeysResultSet.getShort("KEY_SEQ");
                intellijColumnInfo.setColumnName(columnName);
                intellijColumnInfo.setTableName(tableName1);
                keyColumns.put(keySeq, intellijColumnInfo);
            }
            for (Map.Entry<Short, ColumnIndexInfo> entry : keyColumns.entrySet()) {
                ColumnIndexInfo intellijColumnInfo = entry.getValue();
                primaryKeyColumnList.add(intellijColumnInfo);
            }
            return Optional.of(primaryKeyColumnList);
        }
    }

    @NotNull
    private Map<String, List<IntellijColumnInfo>> getIntellijColumnInfos(Connection connection, DatabaseConfig databaseConfig) throws SQLException {
        try (ResultSet columnResultSet = connection.getMetaData().getColumns(databaseConfig.getSchema(), databaseConfig.getSchema(), null, null)) {
            ResultSetMetaData resultSetMetaData = columnResultSet.getMetaData();
            boolean supportsIsAutoIncrement = false;
            boolean supportsIsGeneratedColumn = false;
            int colCount = resultSetMetaData.getColumnCount();
            for (int i = 1; i <= colCount; i++) {
                if ("IS_AUTOINCREMENT".equals(resultSetMetaData.getColumnName(i))) { //$NON-NLS-1$
                    supportsIsAutoIncrement = true;
                }
                if ("IS_GENERATEDCOLUMN".equals(resultSetMetaData.getColumnName(i))) { //$NON-NLS-1$
                    supportsIsGeneratedColumn = true;
                }
            }
            Map<String, List<IntellijColumnInfo>> stringListMap = new ConcurrentHashMap<>();
            while (columnResultSet.next()) {
                String tableName = columnResultSet.getString("TABLE_NAME");
                if (stringListMap.containsKey(tableName)) {
                    stringListMap.get(tableName).add(getIntellijColumnInfo(columnResultSet, supportsIsAutoIncrement, supportsIsGeneratedColumn));
                } else {
                    List<IntellijColumnInfo> columnInfos = new ArrayList<>();
                    columnInfos.add(getIntellijColumnInfo(columnResultSet, supportsIsAutoIncrement, supportsIsGeneratedColumn));
                    stringListMap.put(tableName, columnInfos);
                }
            }
            return stringListMap;
        }
    }


    @NotNull
    private IntellijColumnInfo getIntellijColumnInfo(ResultSet columnResultSet, boolean supportsIsAutoIncrement, boolean supportsIsGeneratedColumn) throws SQLException {
        IntellijColumnInfo intellijColumnInfo = new IntellijColumnInfo();
        if (supportsIsAutoIncrement) {
            intellijColumnInfo.setAutoIncrement(
                    "YES".equals(columnResultSet.getString("IS_AUTOINCREMENT"))); //$NON-NLS-1$ //$NON-NLS-2$
        }
        if (supportsIsGeneratedColumn) {
            intellijColumnInfo.setGeneratedColumn(
                    "YES".equals(columnResultSet.getString("IS_GENERATEDCOLUMN"))); //$NON-NLS-1$ //$NON-NLS-2$
        }
        intellijColumnInfo.setDataType(columnResultSet.getInt("DATA_TYPE"));
        intellijColumnInfo.setColumnDefaultValue(columnResultSet.getString("COLUMN_DEF"));
        intellijColumnInfo.setDecimalDigits(columnResultSet.getInt("DECIMAL_DIGITS"));
        intellijColumnInfo.setName(columnResultSet.getString("COLUMN_NAME"));
        intellijColumnInfo.setSize(columnResultSet.getInt("COLUMN_SIZE"));
        intellijColumnInfo.setTypeName(columnResultSet.getString("TYPE_NAME"));
        intellijColumnInfo.setNullable(columnResultSet.getInt("NULLABLE") == DatabaseMetaData.columnNullable);
        intellijColumnInfo.setRemarks(columnResultSet.getString("REMARKS"));
        return intellijColumnInfo;
    }


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
            Map<String, List<IntellijColumnInfo>> columnInfos = getIntellijColumnInfos(connection.get(), databaseConfig);
            if (columnInfos.isEmpty()) {
                DatabaseNotification.notifyWarning(myProject, "Failed to get database table field information,schema:" + databaseConfig.getSchema());
                return Optional.empty();
            }
            List<TableInfo> tableInfos = tableInfo.get();
            List<ColumnIndexInfo> columnIndexInfos = new ArrayList<>();
            for (TableInfo info : tableInfos) {
                Optional<List<ColumnIndexInfo>> columnIndexInfoList = getPrimaryKeyColumnList(connection.get(), databaseConfig, info.getTableName());
                columnIndexInfoList.ifPresent(columnIndexInfos::addAll);
            }
            if (columnIndexInfos.isEmpty()) {
                DatabaseNotification.notifyWarning(myProject, "Failed to get the index information of the database table field,schema:" + databaseConfig.getSchema());
                return Optional.empty();
            }
            return Optional.of(coverIntellijTableInfo(databaseConfig, tableInfo.get(), columnInfos, columnIndexInfos));
        } finally {
            if (connection.isPresent()) {
                connection.get().close();
                uninstall(databaseConfig);
            }
        }
    }
}
