package com.mybatis.database.view;

import com.intellij.credentialStore.OneTimeString;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.util.treeView.PresentableNodeDescriptor;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.UserDataHolderEx;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.SyntaxTraverser;
import com.intellij.psi.impl.FakePsiElement;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.tree.BaseTreeModel;
import com.intellij.util.concurrency.Invoker;
import com.intellij.util.concurrency.InvokerSupplier;
import com.intellij.util.containers.JBIterable;
import com.mybatis.database.model.ColumnSource;
import com.mybatis.database.model.DataConfigSource;
import com.mybatis.database.model.SchemaSource;
import com.mybatis.database.model.TableSource;
import com.mybatis.enums.DataBaseType;
import com.mybatis.model.CacheModel.Cache.ColumnSourceDto;
import com.mybatis.model.CacheModel.Cache.DataConfigSourceDto;
import com.mybatis.model.CacheModel.Cache.SchemaSourceDto;
import com.mybatis.model.CacheModel.Cache.TableSourceDto;
import com.mybatis.model.CacheModel.DatabaseSource;
import com.mybatis.model.CacheModel.Dbms;
import com.mybatis.state.MybatisDatabaseComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DatabaseStructure extends BaseTreeModel<Object> implements InvokerSupplier {
    @NotNull
    private final Invoker myInvoker = Invoker.forBackgroundThreadWithReadAction(this);
    @NotNull
    private final DbRootGroup myRootGroup;
    private final Project project;
    private final DatabaseSource databaseSource;

    public DatabaseStructure(Project project) {
        this.project = project;
        this.myRootGroup = getRootGroup(project);
        databaseSource = MybatisDatabaseComponent.getInstance(project).getState();
    }

    public void refreshAll() {
        treeStructureChanged(null, null, null);
    }

    @NotNull
    DbRootGroup getRootGroup(@NotNull Project project) {
        UserDataHolderEx userDataHolderEx = (UserDataHolderEx) project;
        DbRootGroup root = userDataHolderEx.getUserData(DatabaseTreeKeys.MYBATIS_ROOT_GROUP);
        if (root == null)
            root = userDataHolderEx.putUserDataIfAbsent(DatabaseTreeKeys.MYBATIS_ROOT_GROUP, new DbRootGroup(project, Dbms.MYSQL));
        return root;

    }

    @Override
    public List<?> getChildren(Object parent) {
        List<Object> children = new ArrayList<>();
        for (Object o : getChildElements(parent)) {
            if (children.size() % 10 == 0) ProgressManager.checkCanceled();
            children.add(o);
        }
        return children;
    }

    @NotNull
    private JBIterable<?> getChildElements(Object element) {
        if (element instanceof DbRootGroup) {
            if (databaseSource.getSources().isEmpty()) {
                return JBIterable.empty();
            }
            Map<String, DataConfigSourceDto> sourceDtoMap = databaseSource.getSources();
            List<DataConfigSource> configSources = new ArrayList<>();
            MyPasswordSafe myPasswordSafe = new MyPasswordSafe();
            for (Map.Entry<String, DataConfigSourceDto> entry : sourceDtoMap.entrySet()) {
                Dbms dbms = Dbms.UNKNOWN;
                if (entry.getValue().getDataBaseType() != null && entry.getValue().getDataBaseType().equals(DataBaseType.MySQL_5) || entry.getValue().getDataBaseType().equals(DataBaseType.MySql)) {
                    dbms = Dbms.MYSQL;
                } else if (entry.getValue().getDataBaseType() != null && entry.getValue().getDataBaseType().equals(DataBaseType.Oracle)) {
                    dbms = Dbms.ORACLE;
                }
                DataConfigSource dataConfigSource = new DataConfigSource(entry.getKey(), dbms, project);
                dataConfigSource.setDatabase(entry.getValue().getDatabase());
                dataConfigSource.setMyUniqueId(entry.getValue().getMyUniqueId());
                dataConfigSource.setHost(entry.getValue().getHost());
                dataConfigSource.setPort(entry.getValue().getPort());
                dataConfigSource.setDriverClass(entry.getValue().getDriverClass());
                dataConfigSource.setDataBaseType(entry.getValue().getDataBaseType());
                dataConfigSource.setJarUrl(entry.getValue().getJarUrl());
                dataConfigSource.setUserName(entry.getValue().getUserName());
                dataConfigSource.setDriverCombox(entry.getValue().getDriverCombos());
                OneTimeString oneTimeString = myPasswordSafe.getPassword(entry.getValue());
                dataConfigSource.setPassword(oneTimeString == null ? null : oneTimeString.toString());
                dataConfigSource.setSchemas(entry.getValue().getSchemas());
                dataConfigSource.setUrl(entry.getValue().getUrl());
                dataConfigSource.setComment(entry.getValue().getComment());
                dataConfigSource.setSid(entry.getValue().getSid());
                List<TableSource> tableSources = new ArrayList<>();
                Dbms finalDbms = dbms;
                SchemaSourceDto schemaSourceDto = entry.getValue().getSchemaSourceDto();
                if (schemaSourceDto == null) {
                    configSources.add(dataConfigSource);
                    continue;
                }
                SchemaSource schemaSource = new SchemaSource(schemaSourceDto.getName(), finalDbms, project);
                List<TableSourceDto> tableSourceDos = schemaSourceDto.getTableSources();
                if (tableSourceDos != null && !tableSourceDos.isEmpty()) {
                    tableSourceDos.forEach(tableSourceDto -> {
                        TableSource tableSource = new TableSource(tableSourceDto.getTableName(), finalDbms, project);
                        tableSource.setDataConfigSource(dataConfigSource);
                        tableSource.setSchema(tableSourceDto.getSchema());
                        tableSource.setDatabaseType(tableSourceDto.getDatabaseType());
                        tableSource.setTableType(tableSourceDto.getTableType());
                        tableSource.setTableRemark(tableSourceDto.getTableRemark());
                        List<ColumnSource> columnSources = new ArrayList<>();
                        List<ColumnSourceDto> columnSourceDos = tableSourceDto.getColumns();
                        if (columnSourceDos != null) {
                            columnSourceDos.forEach(columnSourceDto -> {
                                ColumnSource columnSource = getColumnSource(entry, columnSourceDto, finalDbms);
                                columnSources.add(columnSource);
                            });
                        }
                        tableSource.setColumns(columnSources);
                        List<ColumnSource> primaryKeyList = new ArrayList<>();
                        List<ColumnSourceDto> primaryKeyColumns = tableSourceDto.getPrimaryKeyColumns();
                        if (primaryKeyColumns != null) {
                            primaryKeyColumns.forEach(columnSourceDto -> {
                                ColumnSource columnSource = getColumnSource(entry, columnSourceDto, finalDbms);
                                primaryKeyList.add(columnSource);
                            });
                        }
                        tableSource.setPrimaryKeyColumns(primaryKeyList);
                        tableSources.add(tableSource);
                    });

                }
                schemaSource.setTableSources(tableSources);
                schemaSource.setDataConfigSource(dataConfigSource);
                dataConfigSource.setSchemaSources(schemaSource);
                configSources.add(dataConfigSource);
            }
            return JBIterable.from(configSources);
        }
        if (element instanceof SchemaSource) {
            return JBIterable.from(((SchemaSource) element).getTableSources());
        }
        if (element instanceof TableSource) {
            return JBIterable.from(((TableSource) element).getColumns());
        }
        if (element instanceof ColumnSource) {
            return JBIterable.empty();
        }
        if (element instanceof DataConfigSource) {
            DataConfigSource dataConfigSource = (DataConfigSource) element;
            return JBIterable.of(dataConfigSource.getSchemaSources());
        }

        return JBIterable.empty();
    }

    @NotNull
    private ColumnSource getColumnSource(Map.Entry<String, DataConfigSourceDto> entry, ColumnSourceDto columnSourceDto, Dbms dbms) {
        ColumnSource columnSource = new ColumnSource(columnSourceDto.getColumnName(), dbms, project);
        columnSource.setDataType(columnSourceDto.getDataType());
        columnSource.setGeneratedColumn(columnSourceDto.isGeneratedColumn());
        columnSource.setAutoIncrement(columnSourceDto.isAutoIncrement());
        columnSource.setSize(columnSourceDto.getSize());
        columnSource.setDecimalDigits(columnSourceDto.getDecimalDigits());
        columnSource.setRemarks(columnSourceDto.getRemarks());
        columnSource.setColumnDefaultValue(columnSourceDto.getColumnDefaultValue());
        columnSource.setNullable(columnSourceDto.getNullable());
        columnSource.setKeySeq(columnSourceDto.getKeySeq());
        columnSource.setTypeName(columnSourceDto.getTypeName());
        return columnSource;
    }


    @Override
    public @NotNull Invoker getInvoker() {
        return myInvoker;
    }

    @Override
    public Object getRoot() {
        return this.myRootGroup;
    }


    public static class DbRootGroup extends DbGroup {
        public final Project project;

        public DbRootGroup(@NotNull Project project, Dbms dbms) {
            super("Mybatis Tool", null, dbms);
            this.project = project;
        }


        @NotNull
        public Project getProject() {
            return this.project;
        }


        public PsiManager getManager() {
            return PsiManager.getInstance(getProject());
        }


        public String getQualifiedName() {
            return "";
        }


        public PsiFile getContainingFile() {
            return null;
        }


        public PsiElement getParent() {
            return null;
        }


        public boolean isValid() {
            return !getProject().isDisposed();
        }

    }

    public static class DbGroup
            extends Group {
        final DbGroup parent;
        private int mySize = 0;

        public DbGroup(String name, DbGroup parent, Dbms dbms) {
            super(name, dbms);
            this.parent = parent;
        }


        public int getSize() {
            return this.mySize;
        }


        void setSize(int size) {
            this.mySize = size;
        }


        void incSize() {
            this.mySize++;
        }


        protected Icon getBaseIcon() {
            return super.getBaseIcon();
        }


        public PsiElement getParent() {
            return this.parent;
        }


        @NotNull
        public Project getProject() {
            return getParent().getProject();
        }


        @NotNull
        public JBIterable<? extends PsiElement> iterateChildren() {
            DbRootGroup root = Objects.requireNonNull(SyntaxTraverser.psiApi().parents(this).filter(DbRootGroup.class).first());
            return JBIterable.empty();
        }


        public String getQualifiedName() {
            StringBuilder sb = new StringBuilder(this.name);
            for (DbGroup cur = this.parent; cur != null && !(cur instanceof DbRootGroup); cur = cur.parent) {
                sb.insert(0, cur.name + "/");
            }
            return sb.toString();
        }

        @NotNull
        public ItemPresentation getQualifiedPresentation() {
            PresentationData presentation = (PresentationData) getPresentation();
            presentation.getColoredText().set(0, new PresentableNodeDescriptor.ColoredFragment(
                    getQualifiedName(), SimpleTextAttributes.REGULAR_ATTRIBUTES));
            return presentation;
        }
    }

    public static abstract class Group extends FakePsiElement implements NavigationItem {
        public final String name;
        final PresentationData presentation = new PresentationData();
        private final Dbms dbms;

        public Group(@NotNull String name, Dbms dbms) {
            this.name = name;
            this.dbms = dbms;
        }

        @NotNull
        public abstract JBIterable<? extends PsiElement> iterateChildren();

        @NotNull
        public ItemPresentation getPresentation() {
            this.presentation.clear();
            this.presentation.setIcon(getIcon(false));
            this.presentation.addText(getName(), SimpleTextAttributes.REGULAR_ATTRIBUTES);
            int theSize = getSize();
            if (theSize > 0) {
                this.presentation.addText("  " + theSize, SimpleTextAttributes.GRAYED_SMALL_ATTRIBUTES);
            }
            return this.presentation;
        }


        @NotNull
        public String getName() {
            return this.name;
        }


        public abstract int getSize();


        public void navigate(boolean requestFocus) {
        }


        public boolean canNavigate() {
            return false;
        }

        @Override
        protected Icon getBaseIcon() {
            return dbms.getIcon();
        }

        @Override
        public @Nullable Icon getIcon(boolean open) {
            return getBaseIcon();
        }
    }
}
