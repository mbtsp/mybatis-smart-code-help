package com.mybatis.utils;

import com.intellij.openapi.project.Project;
import com.mybatis.datasource.DbConfig;
import com.mybatis.dialog.MyWizardDialogUI;
import com.mybatis.model.GeneratorConfig;

public class GenConfigDialog {
    public static void showConfigDialog(Project project, DbConfig dbConfig) {

        GeneratorConfig generatorConfig = convertConfig(dbConfig);
        MyWizardDialogUI myWizardDialogUI = new MyWizardDialogUI(project, generatorConfig);
        myWizardDialogUI.showAndGet();
    }

    public static GeneratorConfig convertConfig(DbConfig dbConfig) {
        GeneratorConfig generatorConfig = new GeneratorConfig();
        generatorConfig.setPassword(dbConfig.getUserName());
        generatorConfig.setUserName(dbConfig.getUserName());
        generatorConfig.setSchema(dbConfig.getSchema());
        generatorConfig.setTableName(dbConfig.getTableName());
        generatorConfig.setIntellijTableInfo(dbConfig.getTableInfos());
        generatorConfig.setDataBaseType(dbConfig.getDataBaseType());
        generatorConfig.setUrl(dbConfig.getUrl());
        generatorConfig.setDisplayName(generatorConfig.getTableName());
        generatorConfig.setVerify(true);
        generatorConfig.setDbms(dbConfig.getDbms());
        return generatorConfig;
    }

}
