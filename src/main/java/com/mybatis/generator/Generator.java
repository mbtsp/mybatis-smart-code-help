package com.mybatis.generator;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.mybatis.enums.DataBaseType;
import com.mybatis.generator.plugin.*;
import com.mybatis.model.CacheModel.TableProperties;
import com.mybatis.model.GeneratorConfig;
import com.mybatis.state.ProjectState;
import com.mybatis.utils.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.MyBatisIntelliGenerator;
import org.mybatis.generator.api.ShellCallback;
import org.mybatis.generator.config.*;
import org.mybatis.generator.plugins.*;
import tk.mybatis.mapper.generator.MapperPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Generator {
    private static final Logger logger = Logger.getInstance(Generator.class);
    private final Context context;
    private Configuration configuration;
    private Project project;
    private ProjectState projectState;
    private GeneratorConfig generatorConfig;
    private JavaModelGeneratorConfiguration javaModelGeneratorConfiguration;
    private SqlMapGeneratorConfiguration sqlMapGeneratorConfiguration;
    private JavaClientGeneratorConfiguration javaClientGeneratorConfiguration;
    private TableConfiguration tableConfiguration;
    private boolean intellij;
    private boolean refresh = true;
    private List<String> warnings;

    public Generator() {
        context = new Context(ModelType.FLAT);
        init();
    }

    public Generator(@NotNull Project project, @NotNull ProjectState projectState, @NotNull GeneratorConfig generatorConfig) {
        this.project = project;
        this.projectState = projectState;
        this.generatorConfig = generatorConfig;
        context = new Context(ModelType.FLAT);
        init();
        configuration.addContext(context);
    }

    private void init() {
        this.intellij = true;
        configuration = new Configuration();
        context.setId("Generator-" + UUID.randomUUID());
        context.setTargetRuntime("MyBatis3");
        context.addProperty("javaFileEncoding", "UTF-8");
        context.setIntellij(intellij);
        context.setCheckBlob(projectState.isCheckBlob());
        initDelimiter();
        initJdbcConnectionConfiguration();
        initJavaModelGeneratorConfiguration();
        initSqlMapGeneratorConfiguration();
        initJavaClientGeneratorConfiguration();
        initTableConfiguration();
        initParam();
    }

    public void buildConfig() throws Exception {
        if (this.project == null) {
            logger.error("build config is error,project is null");
            return;
        }
        FileDocumentManager.getInstance().saveAllDocuments();
        ShellCallback shellCallback = new IntellijShellCallback(true, true, this.project);
        warnings = new ArrayList<>();
        MyBatisIntelliGenerator myBatisIntelliGenerator = new MyBatisIntelliGenerator(configuration, shellCallback, warnings);
        myBatisIntelliGenerator.generate(null, generatorConfig.getIntellijTableInfo());
//        List<GeneratedJavaFile> generatedJavaFileList = myBatisIntelliGenerator.getGeneratedJavaFiles();
        VirtualFileManager.getInstance().refreshWithoutFileWatcher(true);
//        if (generatedJavaFileList != null) {
//            ApplicationManager.getApplication().invokeLater(new Runnable() {
//                @Override
//                public void run() {
//                    if (refresh) {
//                        VirtualFileManager.getInstance().asyncRefresh(() -> {
//                            List<PsiFile> psiFiles = new ArrayList<>();
//                            for (GeneratedJavaFile generatedJavaFile : generatedJavaFileList) {
//                                PsiClass psiClass = JavaPsiFacade.getInstance(project).findClass(generatedJavaFile.getCompilationUnit().getType().getFullyQualifiedName(), GlobalSearchScope.projectScope(project));
//                                if (psiClass != null) {
//                                    psiFiles.add(psiClass.getContainingFile());
//                                }
//                            }
//                            if (!psiFiles.isEmpty()) {
//                                for (PsiFile psiFile : psiFiles) {
//                                    Document document = FileDocumentManager.getInstance().getCachedDocument(psiFile.getVirtualFile());
//                                    if(document==null){
//                                        continue;
//                                    }
//                                    WriteCommandAction.runWriteCommandAction(project, () -> {
//                                        JavaCodeStyleManager.getInstance(project).shortenClassReferences(psiFile);
//                                        PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(document);
//                                        PsiDocumentManager.getInstance(project).performForCommittedDocument(document,()->{
//                                            WriteCommandAction.runWriteCommandAction(project,()->{
//                                                CodeStyleManager.getInstance(project).reformat(psiFile);
//                                                VirtualFileManager.getInstance().refreshWithoutFileWatcher(true);
//                                            });
//                                        });
//
////                                        Document document1 = FileDocumentManager.getInstance().getCachedDocument(psiFile.getVirtualFile());
////                                        if (document1 != null) {
////                                            PsiDocumentManager.getInstance(project).performForCommittedDocument(document1, () -> {
////                                                WriteCommandAction.runWriteCommandAction(project,()->{
////                                                    try {
////                                                        CodeStyleManager.getInstance(project).reformat(psiFile);
////                                                        Document document = FileDocumentManager.getInstance().getCachedDocument(psiFile.getVirtualFile());
////                                                        if (document != null) {
////                                                            PsiDocumentManager.getInstance(project).performForCommittedDocument(document, () -> {
////                                                                DocumentUtil.writeInRunUndoTransparentAction(() -> {
////                                                                    try {
////                                                                        JavaCodeStyleManager.getInstance(project).optimizeImports(psiFile);
////                                                                        Document document3 = FileDocumentManager.getInstance().getCachedDocument(psiFile.getVirtualFile());
////                                                                        if(document3!=null){
////                                                                            PsiDocumentManager.getInstance(project).commitDocument(document3);
////                                                                        }
////                                                                    } catch (Exception e) {
////                                                                        logger.warn("optimize import is error", e);
////                                                                    }
////                                                                });
////                                                            });
////                                                        }
////                                                    } catch (Exception e) {
////                                                        logger.error("reformat is error", e);
////                                                    }
////                                                });
////
////                                            });
////                                        }
//
//                                    });
//
//                                }
//                            }
//                            VirtualFileManager.getInstance().refreshWithoutFileWatcher(true);
//                        });
//                    }
//
//
//                }
//            });
//
//        }
//

    }



    public void setSkipIntellij() {
        intellij = false;
    }

    private void initDelimiter() {
        if (generatorConfig.getDbms().isMysql()) {
            context.addProperty(PropertyRegistry.CONTEXT_BEGINNING_DELIMITER, "`");
            context.addProperty(PropertyRegistry.CONTEXT_ENDING_DELIMITER, "`");
        } else if (generatorConfig.getDbms().isMicrosoft()) {
            context.addProperty(PropertyRegistry.CONTEXT_BEGINNING_DELIMITER, "[");
            context.addProperty(PropertyRegistry.CONTEXT_ENDING_DELIMITER, "]");
        }
//        if (generatorConfig.getDataBaseType().equals(DataBaseType.MySql)) {
//            context.addProperty(PropertyRegistry.CONTEXT_BEGINNING_DELIMITER, "`");
//            context.addProperty(PropertyRegistry.CONTEXT_ENDING_DELIMITER, "`");
//        } else if (generatorConfig.getDataBaseType().equals(DataBaseType.SqlServer)) {
//            context.addProperty(PropertyRegistry.CONTEXT_BEGINNING_DELIMITER, "[");
//            context.addProperty(PropertyRegistry.CONTEXT_ENDING_DELIMITER, "]");
//        }
        context.addProperty(PropertyRegistry.CONTEXT_AUTO_DELIMIT_KEYWORDS, "true");
    }

    private void initJdbcConnectionConfiguration() {
        JDBCConnectionConfiguration jDBCConnectionConfiguration = new JDBCConnectionConfiguration();
        jDBCConnectionConfiguration.setDriverClass(generatorConfig.getDataBaseType().getDriverClass());
        jDBCConnectionConfiguration.setConnectionURL(generatorConfig.getUrl());
        jDBCConnectionConfiguration.setUserId(generatorConfig.getUserName());
        jDBCConnectionConfiguration.setPassword(generatorConfig.getPassword());
        context.setJdbcConnectionConfiguration(jDBCConnectionConfiguration);

    }

    private void initJavaModelGeneratorConfiguration() {
        this.javaModelGeneratorConfiguration = new JavaModelGeneratorConfiguration();
        javaModelGeneratorConfiguration.setTargetPackage(projectState.getJavaModelPackage());
        javaModelGeneratorConfiguration.setTargetProject(projectState.getJavaModelSourcePackage());
        if (projectState.isModelStringTrim()) {
            javaModelGeneratorConfiguration.addProperty("trimStrings", "true");
        } else {
            javaModelGeneratorConfiguration.addProperty("trimStrings", "false");
        }
        context.setJavaModelGeneratorConfiguration(javaModelGeneratorConfiguration);
    }

    private void initSqlMapGeneratorConfiguration() {
        sqlMapGeneratorConfiguration = new SqlMapGeneratorConfiguration();
        sqlMapGeneratorConfiguration.setTargetPackage(projectState.getJavaXmlPackage());
        sqlMapGeneratorConfiguration.setTargetProject(projectState.getJavaXmlSourcePackage());
        context.setSqlMapGeneratorConfiguration(sqlMapGeneratorConfiguration);
    }

    private void initJavaClientGeneratorConfiguration() {
        javaClientGeneratorConfiguration = new JavaClientGeneratorConfiguration();
        javaClientGeneratorConfiguration.setConfigurationType("XMLMAPPER");
        javaClientGeneratorConfiguration.setTargetPackage(projectState.getJavaMapperPackage());
        javaClientGeneratorConfiguration.setTargetProject(projectState.getJavaMapperSourcePackage());
        context.setJavaClientGeneratorConfiguration(javaClientGeneratorConfiguration);
    }

    private void initTableConfiguration() {
        tableConfiguration = new TableConfiguration(this.context);
        tableConfiguration.addProperty("useActualColumnNames", String.valueOf(projectState.isUseActualColumnNames()));
        if (!projectState.isGeneratorExample()) {
            tableConfiguration.setCountByExampleStatementEnabled(false);
            tableConfiguration.setSelectByExampleStatementEnabled(false);
            tableConfiguration.setUpdateByExampleStatementEnabled(false);
            tableConfiguration.setDeleteByExampleStatementEnabled(false);
        }

        tableConfiguration.setDomainObjectName(projectState.getModelName());
        tableConfiguration.setTableName(projectState.getTableName());
        if (generatorConfig.getDataBaseType().equals(DataBaseType.MySql)
                || generatorConfig.getDataBaseType().equals(DataBaseType.PostgreSQL) || generatorConfig.getDataBaseType().equals(DataBaseType.MySQL_5)
        ) {
            tableConfiguration.setSchema(null);
        } else {
            tableConfiguration.setCatalog(generatorConfig.getSchema());
        }
        Map<String, TableProperties> tablePropertiesMap = projectState.getTablePropertiesMap();
        if (tablePropertiesMap != null) {
            TableProperties tableProperties = tablePropertiesMap.get(generatorConfig.getTableName());
            if (tableProperties != null && !tableProperties.getColumnOverrides().isEmpty()) {
                tableProperties.getColumnOverrides().forEach(columnOverride -> {
                    ColumnOverride columnOverride1 = new ColumnOverride(columnOverride.getColumnName());
                    columnOverride1.setJdbcType(columnOverride.getJdbcType());
                    columnOverride1.setJavaProperty(columnOverride.getJavaProperty());
                    columnOverride1.setColumnNameDelimited(columnOverride.isColumnNameDelimited());
                    columnOverride1.setJavaType(columnOverride.getJavaType());
                    columnOverride1.setTypeHandler(columnOverride.getTypeHandler());
                    tableConfiguration.addColumnOverride(columnOverride1);
                });
            }
            if (tableProperties != null && !tableProperties.getIgnoredColumns().isEmpty()) {
                tableProperties.getIgnoredColumns().forEach(ignoredColumn -> {
                    IgnoredColumn ignoredColumn1 = new IgnoredColumn(ignoredColumn.getColumnName());
                    ignoredColumn1.setColumnNameDelimited(ignoredColumn.isColumnNameDelimited());
                    tableConfiguration.addIgnoredColumn(ignoredColumn1);
                });
            }
        }
        String configuredSqlStatement = "";
        if (generatorConfig.getDataBaseType().equals(DataBaseType.MySql) || generatorConfig.getDataBaseType().equals(DataBaseType.MySQL_5)) {
            configuredSqlStatement = "JDBC";
        } else if (generatorConfig.getDataBaseType().equals(DataBaseType.Oracle)) {
            configuredSqlStatement = "select SEQ_{1}.nextval from dual";
        }
        if (StringUtils.isNotBlank(configuredSqlStatement) && StringUtils.isNotBlank(projectState.getKey())) {
            GeneratedKey generatedKey = new GeneratedKey(projectState.getKey(), configuredSqlStatement, false, "pre");
            tableConfiguration.setGeneratedKey(generatedKey);
        }
        context.addTableConfiguration(tableConfiguration);
    }
    private void setComment(boolean generateComments,boolean isInterfaceComment) {
        CommentGeneratorConfiguration commentGeneratorConfiguration = new CommentGeneratorConfiguration();
        commentGeneratorConfiguration.setConfigurationType(MybatisCommentGenerator.class.getCanonicalName());
        if(isInterfaceComment){
            commentGeneratorConfiguration.addProperty("interfaceComment","true");
        }else{
            commentGeneratorConfiguration.addProperty("interfaceComment","false");
        }
        if (!generateComments) {
            commentGeneratorConfiguration.addProperty("suppressAllComments", "true");
        } else {
            commentGeneratorConfiguration.addProperty("suppressDate", "true");
            commentGeneratorConfiguration.addProperty("addRemarkComments", "true");
        }
        this.context.setCommentGeneratorConfiguration(commentGeneratorConfiguration);
    }
    private void initParam() {
        //是否生成注释
        this.setComment(projectState.isComment(),projectState.isInterfaceComment());
        PluginConfiguration pluginConfiguration;
        if (!projectState.isComment()) {
            pluginConfiguration = new PluginConfiguration();
            pluginConfiguration.setConfigurationType(UnmergeableXmlMappersPlugin.class.getCanonicalName());
            context.addPluginConfiguration(pluginConfiguration);
        }
        //是否生成lombok
        if (projectState.isLombokData() || projectState.isLombokGetterSetter() || projectState.isLombokBuilder()
                || projectState.isLombokAllArgsConstructor() || projectState.isLombokNoArgsConstructor()) {
            pluginConfiguration = new PluginConfiguration();
            pluginConfiguration.setConfigurationType(LombokPlugin.class.getCanonicalName());
            pluginConfiguration.addProperty("lombokData", String.valueOf(projectState.isLombokData()));
            pluginConfiguration.addProperty("lombokGetterSetter", String.valueOf(projectState.isLombokGetterSetter()));
            pluginConfiguration.addProperty("lombokBuilder", String.valueOf(projectState.isLombokBuilder()));
            pluginConfiguration.addProperty("lombokNoArgsConstructor", String.valueOf(projectState.isLombokNoArgsConstructor()));
            pluginConfiguration.addProperty("lombokAllArgsConstructor", String.valueOf(projectState.isLombokAllArgsConstructor()));
            context.addPluginConfiguration(pluginConfiguration);
        }
        //swagger
        if (projectState.isSwagger()) {
            pluginConfiguration = new PluginConfiguration();
            pluginConfiguration.setConfigurationType(GeneratorSwagger2Doc.class.getCanonicalName());
            context.addPluginConfiguration(pluginConfiguration);
        }
        if (projectState.isToString()) {
            pluginConfiguration = new PluginConfiguration();
            pluginConfiguration.setConfigurationType(ToStringPlugin.class.getCanonicalName());
            context.addPluginConfiguration(pluginConfiguration);
        }
        if (projectState.isEqualHashCode()) {
            pluginConfiguration = new PluginConfiguration();
            pluginConfiguration.setConfigurationType(EqualsHashCodePlugin.class.getCanonicalName());
            context.addPluginConfiguration(pluginConfiguration);
        }
        if (projectState.isAnnotationMapper()) {
            pluginConfiguration = new PluginConfiguration();
            pluginConfiguration.setConfigurationType(MapperAnnotationPlugin.class.getCanonicalName());
            context.addPluginConfiguration(pluginConfiguration);
        }
        if (projectState.isSerializable()) {
            pluginConfiguration = new PluginConfiguration();
            pluginConfiguration.setConfigurationType(SerializablePlugin.class.getCanonicalName());
            context.addPluginConfiguration(pluginConfiguration);
        }
        if (projectState.isGeneratorServiceInterface()) {
            pluginConfiguration = new PluginConfiguration();
            pluginConfiguration.setConfigurationType(GenerateServiceInterfacePlugin.class.getCanonicalName());
            pluginConfiguration.addProperty("javaServiceInterfacePackage", projectState.getJavaServiceInterfacePackage());
            pluginConfiguration.addProperty("javaServiceInterfaceSourcePackage", projectState.getJavaServiceInterfaceSourcePackage());
            pluginConfiguration.addProperty("serviceInterfaceMode", projectState.getServiceMode() + "");
            context.addPluginConfiguration(pluginConfiguration);
        }
        if (projectState.isGeneratorService()) {
            pluginConfiguration = new PluginConfiguration();
            pluginConfiguration.setConfigurationType(GenerateServicePlugin.class.getCanonicalName());
            pluginConfiguration.addProperty("javaServiceInterfacePackage", projectState.getJavaServiceInterfacePackage());
            pluginConfiguration.addProperty("javaServicePackage", projectState.getJavaServicePackage());
            pluginConfiguration.addProperty("javaServiceSourcePackage", projectState.getJavaServiceSourcePackage());
            pluginConfiguration.addProperty("javaMapperPackage", projectState.getJavaMapperPackage());
            pluginConfiguration.addProperty("serviceMode", projectState.getServiceMode() + "");
            context.addPluginConfiguration(pluginConfiguration);
        }
        //tk.mappers
        if (projectState.isUserTkMapper()) {
            pluginConfiguration = new PluginConfiguration();
            pluginConfiguration.setConfigurationType(MapperPlugin.class.getCanonicalName());
            pluginConfiguration.addProperty("mappers", projectState.getTkMappers());
            pluginConfiguration.addProperty("caseSensitive", projectState.isCaseSensitive() + "");
            pluginConfiguration.addProperty("forceAnnotation", projectState.isForceAnnotation() + "");
            context.addPluginConfiguration(pluginConfiguration);
        }
    }



    public void setProjectState(ProjectState projectState) {
        this.projectState = projectState;
    }

    public GeneratorConfig getGeneratorConfig() {
        return generatorConfig;
    }

    public void setGeneratorConfig(GeneratorConfig generatorConfig) {
        this.generatorConfig = generatorConfig;
    }

    public JavaModelGeneratorConfiguration getJavaModelGeneratorConfiguration() {
        return javaModelGeneratorConfiguration;
    }

    public void setJavaModelGeneratorConfiguration(JavaModelGeneratorConfiguration javaModelGeneratorConfiguration) {
        this.javaModelGeneratorConfiguration = javaModelGeneratorConfiguration;
    }

    public SqlMapGeneratorConfiguration getSqlMapGeneratorConfiguration() {
        return sqlMapGeneratorConfiguration;
    }

    public void setSqlMapGeneratorConfiguration(SqlMapGeneratorConfiguration sqlMapGeneratorConfiguration) {
        this.sqlMapGeneratorConfiguration = sqlMapGeneratorConfiguration;
    }

    public JavaClientGeneratorConfiguration getJavaClientGeneratorConfiguration() {
        return javaClientGeneratorConfiguration;
    }

    public void setJavaClientGeneratorConfiguration(JavaClientGeneratorConfiguration javaClientGeneratorConfiguration) {
        this.javaClientGeneratorConfiguration = javaClientGeneratorConfiguration;
    }

    public TableConfiguration getTableConfiguration() {
        return tableConfiguration;
    }

    public void setTableConfiguration(TableConfiguration tableConfiguration) {
        this.tableConfiguration = tableConfiguration;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public boolean isRefresh() {
        return refresh;
    }

    public void setRefresh(boolean refresh) {
        this.refresh = refresh;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }
}
