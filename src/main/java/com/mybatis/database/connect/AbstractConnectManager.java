package com.mybatis.database.connect;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.Messages;
import com.mybatis.database.model.ColumnIndexInfo;
import com.mybatis.database.model.ColumnInfo;
import com.mybatis.database.model.DatabaseConfig;
import com.mybatis.database.model.TableInfo;
import com.mybatis.notifier.DatabaseNotification;
import com.mybatis.utils.JdbcUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.mybatis.generator.api.IntellijColumnInfo;
import org.mybatis.generator.api.IntellijTableInfo;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public abstract class AbstractConnectManager implements ConnectManager {
    Map<String, Driver> tmpDriverMap = new HashMap<>();

    public static boolean isNumeric(String string) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(string).matches();
    }

    @Override
    public boolean checkConnect(DatabaseConfig databaseConfig, Project project) throws SQLException, MalformedURLException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Optional<Connection> connection = Optional.empty();
        try {
            connection = getConnection(databaseConfig, project);
            return connection.isPresent();
        } finally {
            if (connection != null && connection.isPresent()) {
                connection.get().close();
                uninstall(databaseConfig);
            }
        }
    }

    @Override
    public Optional<Connection> getConnection(DatabaseConfig databaseConfig, Project project) throws SQLException, MalformedURLException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Driver driver = loadDriver(databaseConfig);
        if (driver == null) {
            return Optional.empty();
        }
        Properties props = new Properties();
        if (StringUtils.isBlank(databaseConfig.getUsername())) {
            String userName = Messages.showInputDialog("Please enter the database account", "Data Lost", null, "root", new InputValidator() {
                @Override
                public boolean checkInput(String inputString) {
                    return true;
                }

                @Override
                public boolean canClose(String inputString) {
                    return true;
                }
            });
            databaseConfig.setUsername(userName);
        }
        if (StringUtils.isBlank(databaseConfig.getPassword())) {
            String password = Messages.showInputDialog("Please enter the database password", "Data Lost", null, "root", new InputValidator() {
                @Override
                public boolean checkInput(String inputString) {
                    return true;
                }

                @Override
                public boolean canClose(String inputString) {
                    return true;
                }
            });
            databaseConfig.setPassword(password);
        }
        if (StringUtils.isBlank(databaseConfig.getUsername())) {
            DatabaseNotification.notifyError(project, "The database connection account is empty");
            return Optional.empty();
        }
        if (StringUtils.isBlank(databaseConfig.getPassword())) {
            DatabaseNotification.notifyError(project, "The database connection passwrod is empty");
            return Optional.empty();
        }
        props.setProperty("user", databaseConfig.getUsername()); //$NON-NLS-1$
        props.setProperty("password", databaseConfig.getPassword()); //$NON-NLS-1$
        return Optional.of(driver.connect(databaseConfig.getUrl(), props));
    }

    @Override
    public Driver loadDriver(DatabaseConfig databaseConfig) throws MalformedURLException, ClassNotFoundException, SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        {
            File newFile = null;
            if (StringUtils.isBlank(databaseConfig.getJarUrl())) {
                Optional<DatabaseArtifactList.Item> optional = databaseConfig.getArtifactVersion().items.stream().filter(item -> item.type.equals(DatabaseArtifactList.Item.Type.JAR)).findFirst();
                if (optional.isEmpty()) {
                    return null;
                }
                DatabaseArtifactList.Item item = optional.get();
                File file = DatabaseArtifactList.getArtifactDir(databaseConfig.getArtifactVersion());
                if (!file.exists()) {
                    return null;
                }
                newFile = new File(file.getAbsolutePath() + "\\" + item.name);
                databaseConfig.setJarUrl(newFile.getAbsolutePath());
            } else {
                newFile = new File(databaseConfig.getJarUrl());
            }
            if (!newFile.exists()) {
                return null;
            }
            URL u = newFile.toURI().toURL();
            URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{u});
            Driver driver = (Driver) Class.forName(databaseConfig.getDataBaseType().getDriverClass(), true, urlClassLoader).getDeclaredConstructor().newInstance();
            tmpDriverMap.put(newFile.getAbsolutePath(), driver);
            return driver;
        }
    }

    @Override
    public void uninstall(DatabaseConfig databaseConfig) {
        File newFile = null;
        if (StringUtils.isBlank(databaseConfig.getJarUrl())) {
            Optional<DatabaseArtifactList.Item> optional = databaseConfig.getArtifactVersion().items.stream().filter(item -> item.type.equals(DatabaseArtifactList.Item.Type.JAR)).findFirst();
            if (optional.isEmpty()) {
                return;
            }
            DatabaseArtifactList.Item item = optional.get();
            File file = DatabaseArtifactList.getArtifactDir(databaseConfig.getArtifactVersion());
            if (!file.exists()) {
                return;
            }
            newFile = new File(file.getAbsolutePath() + "\\" + item.name);
            databaseConfig.setJarUrl(newFile.getAbsolutePath());
        } else {
            newFile = new File(databaseConfig.getJarUrl());
        }
        if (!newFile.exists()) {
            return;
        }
        try {
            DriverManager.deregisterDriver(tmpDriverMap.get(newFile.getAbsolutePath()));
        } catch (SQLException ignored) {

        }
    }

    private Map<String, List<IntellijColumnInfo>> coverColumnIndex(@NotNull List<ColumnIndexInfo> columnIndexInfos) {
        Map<String, List<IntellijColumnInfo>> map = new HashMap<>();
        Map<String, List<ColumnIndexInfo>> stringListMap = columnIndexInfos.stream().collect(Collectors.groupingBy(ColumnIndexInfo::getTableName));
        for (Map.Entry<String, List<ColumnIndexInfo>> entry : stringListMap.entrySet()) {
            List<IntellijColumnInfo> list = new ArrayList<>();
            List<ColumnIndexInfo> columnIndexInfos1 = entry.getValue();
            if (columnIndexInfos1 != null) {
                columnIndexInfos1.forEach(columnIndexInfo -> {
                    IntellijColumnInfo intellijColumnInfo = new IntellijColumnInfo();
                    intellijColumnInfo.setKeySeq((short) 1);
                    intellijColumnInfo.setName(columnIndexInfo.getColumnName());
                    list.add(intellijColumnInfo);
                });
            }
            map.put(entry.getKey(), list);
        }
        return map;
    }

    private Map<String, List<IntellijColumnInfo>> coverColumn(@NotNull List<ColumnInfo> columnInfos, DatabaseConfig databaseConfig) {
        Map<String, List<IntellijColumnInfo>> map = new HashMap<>();
        Map<String, List<ColumnInfo>> listMap = columnInfos.stream().collect(Collectors.groupingBy(ColumnInfo::getTableName));
        for (Map.Entry<String, List<ColumnInfo>> entry : listMap.entrySet()) {
            List<IntellijColumnInfo> columnInfoList = new ArrayList<>();
            List<ColumnInfo> columnInfos1 = entry.getValue();
            if (columnInfos1 != null && !columnInfos1.isEmpty()) {
                columnInfos1.forEach(columnInfo -> {
                    IntellijColumnInfo intellijColumnInfo = new IntellijColumnInfo();
                    intellijColumnInfo.setGeneratedColumn(StringUtils.isNotBlank(columnInfo.getExtra()) && columnInfo.getExtra().equals("auto_increment"));
                    intellijColumnInfo.setColumnDefaultValue(columnInfo.getColumnDefault());
                    intellijColumnInfo.setNullable(columnInfo.isNullable());
                    intellijColumnInfo.setRemarks(columnInfo.getColumnComment());
                    intellijColumnInfo.setName(columnInfo.getColumnName());

                    String columnType = columnInfo.getColumnType();
                    String regex1 = "\\(([^}]*)\\)";
                    Matcher matcher = Pattern.compile(regex1).matcher(columnType);
                    String v = null;
                    while (matcher.find()) {
                        v = matcher.group();
                    }
                    if (v != null) {
                        v = v.replace("(", "").replace(")", "");
                    }
                    if (v != null && v.contains(",")) {
                        String[] str = v.split(",");
                        if (isNumeric(str[0])) {
                            intellijColumnInfo.setSize(Integer.parseInt(str[0]));
                        }
                        if (str.length > 1) {
                            if (isNumeric(str[1])) {
                                intellijColumnInfo.setDecimalDigits(Integer.parseInt(str[1]));
                            }
                        }
                    } else if (v != null && !v.contains(",")) {
                        if (isNumeric(v)) {
                            intellijColumnInfo.setSize(Integer.parseInt(v));
                        }
                    }
                    intellijColumnInfo.setDataType(JdbcUtils.convertTypeNameToJdbcType(columnInfo.getDataType(), intellijColumnInfo.getSize(), databaseConfig.getDataBaseType().name()));
                    intellijColumnInfo.setTypeName(JdbcUtils.convertJdbcTypeInformation(intellijColumnInfo.getDataType(),intellijColumnInfo.getSize(),false).getJdbcTypeName());
                    columnInfoList.add(intellijColumnInfo);
                });

            }
            map.put(entry.getKey(), columnInfoList);
        }
        return map;
    }

    public List<IntellijTableInfo> coverIntellijTableInfo(DatabaseConfig databaseConfig, @NotNull List<TableInfo> tableInfos, @NotNull List<ColumnInfo> columnInfos, @NotNull List<ColumnIndexInfo> columnIndexInfos) {
        List<IntellijTableInfo> intellijTableInfos = new ArrayList<>();
        Map<String, List<IntellijColumnInfo>> listMap = coverColumn(columnInfos, databaseConfig);
        Map<String, List<IntellijColumnInfo>> indexMap = coverColumnIndex(columnIndexInfos);
        tableInfos.forEach(tableInfo -> {
            IntellijTableInfo intellijTableInfo = new IntellijTableInfo();
            intellijTableInfo.setTableRemark(tableInfo.getComment());
            intellijTableInfo.setTableType(tableInfo.getTableType());
            intellijTableInfo.setDatabaseType(databaseConfig.getDataBaseType().toString());
            intellijTableInfo.setTableName(tableInfo.getTableName());
            if (listMap.containsKey(tableInfo.getTableName())) {
                intellijTableInfo.setColumnInfos(listMap.get(tableInfo.getTableName()));
            }
            if (indexMap.containsKey(tableInfo.getTableName())) {
                intellijTableInfo.setPrimaryKeyColumns(indexMap.get(tableInfo.getTableName()));
            }
            intellijTableInfos.add(intellijTableInfo);
        });
        return intellijTableInfos;
    }

    public List<IntellijTableInfo> coverIntellijTableInfo(DatabaseConfig databaseConfig, @NotNull List<TableInfo> tableInfos, @NotNull Map<String, List<IntellijColumnInfo>> listMap, @NotNull List<ColumnIndexInfo> columnIndexInfos) {
        List<IntellijTableInfo> intellijTableInfos = new ArrayList<>();
        Map<String, List<IntellijColumnInfo>> indexMap = coverColumnIndex(columnIndexInfos);
        tableInfos.forEach(tableInfo -> {
            IntellijTableInfo intellijTableInfo = new IntellijTableInfo();
            intellijTableInfo.setTableRemark(tableInfo.getComment());
            intellijTableInfo.setTableType(tableInfo.getTableType());
            intellijTableInfo.setDatabaseType(databaseConfig.getDataBaseType().toString());
            intellijTableInfo.setTableName(tableInfo.getTableName());
            if (listMap.containsKey(tableInfo.getTableName())) {
                intellijTableInfo.setColumnInfos(listMap.get(tableInfo.getTableName()));
            }
            if (indexMap.containsKey(tableInfo.getTableName())) {
                intellijTableInfo.setPrimaryKeyColumns(indexMap.get(tableInfo.getTableName()));
            }
            intellijTableInfos.add(intellijTableInfo);
        });
        return intellijTableInfos;
    }
}
