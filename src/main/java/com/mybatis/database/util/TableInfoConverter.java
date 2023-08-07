package com.mybatis.database.util;

import com.mybatis.database.model.ColumnSource;
import com.mybatis.database.model.DatabaseConfig;
import com.mybatis.database.view.MyPasswordSafe;
import com.mybatis.model.CacheModel.Cache.ColumnSourceDto;
import com.mybatis.model.CacheModel.Cache.DataConfigSourceDto;
import com.mybatis.model.CacheModel.Cache.SchemaSourceDto;
import com.mybatis.model.CacheModel.Cache.TableSourceDto;
import org.jetbrains.annotations.NotNull;
import org.mybatis.generator.api.IntellijColumnInfo;
import org.mybatis.generator.api.IntellijTableInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TableInfoConverter {

    private static final MyPasswordSafe myPasswordSafe = new MyPasswordSafe();

    public static void coverColumns(List<IntellijColumnInfo> columnInfos, List<ColumnSource> columnSources) {
        if (columnInfos == null || columnSources == null) {
            return;
        }
        columnSources.forEach(columnSource -> {
            IntellijColumnInfo intellijColumnInfo = new IntellijColumnInfo();
            intellijColumnInfo.setName(columnSource.getName());
            intellijColumnInfo.setDataType(columnSource.getDataType());
            intellijColumnInfo.setGeneratedColumn(columnSource.isGeneratedColumn());
            intellijColumnInfo.setAutoIncrement(columnSource.isAutoIncrement());
            intellijColumnInfo.setSize(columnSource.getSize());
            intellijColumnInfo.setDecimalDigits(columnSource.getDecimalDigits());
            intellijColumnInfo.setRemarks(columnSource.getRemarks());
            intellijColumnInfo.setColumnDefaultValue(columnSource.getColumnDefaultValue());
            intellijColumnInfo.setNullable(columnSource.getNullable());
            intellijColumnInfo.setKeySeq(columnSource.getKeySeq());
            intellijColumnInfo.setTypeName(columnSource.getTypeName());
            columnInfos.add(intellijColumnInfo);
        });
    }

    public static void loadState(@NotNull List<IntellijTableInfo> tableInfos, DatabaseConfig databaseConfig, Map<String, DataConfigSourceDto> dataConfigSources) {
        if (tableInfos.isEmpty()) {
            return;
        }
        if (databaseConfig == null) {
            return;
        }

        DataConfigSourceDto dataConfigSource = getDataConfigSource(databaseConfig.getName(), dataConfigSources);
        dataConfigSource.setDatabase(databaseConfig.getSchema());
        myPasswordSafe.setPassword(dataConfigSource, databaseConfig.getPassword());
        dataConfigSource.setDatabaseName(databaseConfig.getName());
        dataConfigSource.setHost(databaseConfig.getHost());
        dataConfigSource.setPort(databaseConfig.getPort());
        dataConfigSource.setUserName(databaseConfig.getUsername());
        dataConfigSource.setUrl(databaseConfig.getUrl());
        dataConfigSource.setComment(databaseConfig.getComment());
        dataConfigSource.setDataBaseType(databaseConfig.getDataBaseType());
        dataConfigSource.setJarUrl(databaseConfig.getJarUrl());
        dataConfigSource.setDriverCombos(databaseConfig.getDriverCombos());
        dataConfigSource.setSid(databaseConfig.getSid());
        List<TableSourceDto> tableSources = new ArrayList<>();
        tableInfos.forEach(intellijTableInfo -> {
            TableSourceDto tableSource = new TableSourceDto();
            tableSource.setTableName(intellijTableInfo.getTableName());
            tableSource.setSchema(databaseConfig.getSchema());
            tableSource.setTableType(intellijTableInfo.getTableType());
            tableSource.setDatabaseType(intellijTableInfo.getDatabaseType());
            tableSource.setTableRemark(intellijTableInfo.getTableRemark());
            //字段信息
            List<ColumnSourceDto> columnSources = tableSource.getColumns();
            List<IntellijColumnInfo> intellijColumnInfos = intellijTableInfo.getColumnInfos();
            if (intellijColumnInfos != null) {
                intellijColumnInfos.forEach(intellijColumnInfo -> {
                    ColumnSourceDto columnSource = getColumnSource(intellijColumnInfo);
                    columnSources.add(columnSource);
                });
            }
            tableSource.setColumns(columnSources);
            List<ColumnSourceDto> primaryKColumns = new ArrayList<>();
            List<IntellijColumnInfo> primaryColumns = intellijTableInfo.getPrimaryKeyColumns();
            if (primaryColumns != null) {
                primaryColumns.forEach(intellijColumnInfo -> {
                    ColumnSourceDto columnSource = getColumnSource(intellijColumnInfo);
                    primaryKColumns.add(columnSource);
                });
            }

            tableSource.setPrimaryKeyColumns(primaryKColumns);
            tableSources.add(tableSource);
        });
        SchemaSourceDto schemaSourceDto = new SchemaSourceDto();
        schemaSourceDto.setName(databaseConfig.getSchema());
        schemaSourceDto.setTableSources(tableSources);
        dataConfigSource.setSchemaSourceDto(schemaSourceDto);
        dataConfigSources.put(dataConfigSource.getDatabaseName(), dataConfigSource);
    }

    private static DataConfigSourceDto getDataConfigSource(String tableName, Map<String, DataConfigSourceDto> dataConfigSources) {
        if (dataConfigSources.isEmpty()) {
            DataConfigSourceDto dataConfigSourceDto = new DataConfigSourceDto();
            dataConfigSourceDto.setMyUniqueId(UUID.randomUUID().toString());
            return dataConfigSourceDto;
        }
        if (dataConfigSources.containsKey(tableName)) {
            return dataConfigSources.get(tableName);
        }
        DataConfigSourceDto dataConfigSourceDto = new DataConfigSourceDto();
        dataConfigSourceDto.setMyUniqueId(UUID.randomUUID().toString());
        return dataConfigSourceDto;
    }


    @NotNull
    private static ColumnSourceDto getColumnSource(IntellijColumnInfo intellijColumnInfo) {
        ColumnSourceDto columnSource = new ColumnSourceDto();
        columnSource.setColumnName(intellijColumnInfo.getName());
        columnSource.setDataType(intellijColumnInfo.getDataType());
        columnSource.setGeneratedColumn(intellijColumnInfo.isGeneratedColumn());
        columnSource.setAutoIncrement(intellijColumnInfo.isAutoIncrement());
        columnSource.setSize(intellijColumnInfo.getSize());
        columnSource.setDecimalDigits(intellijColumnInfo.getDecimalDigits());
        columnSource.setRemarks(intellijColumnInfo.getRemarks());
        columnSource.setColumnDefaultValue(intellijColumnInfo.getColumnDefaultValue());
        columnSource.setNullable(intellijColumnInfo.getNullable());
        columnSource.setKeySeq(intellijColumnInfo.getKeySeq());
        columnSource.setTypeName(intellijColumnInfo.getTypeName());
        return columnSource;
    }
}
