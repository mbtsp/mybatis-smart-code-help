package com.mybatis.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.ui.table.JBTable;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.mybatis.messages.MybatisSmartCodeHelpBundle;
import com.mybatis.model.CacheModel.Cache.TableColumnOverride;
import com.mybatis.model.CacheModel.Cache.TableIgnoredColumn;
import com.mybatis.model.CacheModel.TableProperties;
import com.mybatis.model.ColumnInfo;
import com.mybatis.model.GeneratorConfig;
import com.mybatis.model.MyAutoCompletionWithBrowserTableCellEditor;
import com.mybatis.model.MyTableModel;
import com.mybatis.state.ProjectState;
import com.mybatis.utils.JdbcUtils;
import com.mybatis.utils.StringUtils;
import org.mybatis.generator.api.IntellijColumnInfo;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class StepTwoPanel {
    private final ProjectState projectState;
    private final Project project;
    private final List<ColumnInfo> infoList;
    private GeneratorConfig generatorConfig;
    private JPanel root;
    private JCheckBox useActualColumnNamesCheckBox;
    private JCheckBox userDbCheckBox;
    private JBTable columnTable;
    private JScrollPane jScrollPanel;

    public StepTwoPanel(Project project, ProjectState projectState, GeneratorConfig generatorConfig) {
        this.project = project;
        this.projectState = projectState;
        setGeneratorConfig(generatorConfig);
        List<IntellijColumnInfo> intellijColumnInfos = generatorConfig.getIntellijTableInfo().getColumnInfos();
        List<IntellijColumnInfo> priMaryKeyIntellijColumnInfos = generatorConfig.getIntellijTableInfo().getPrimaryKeyColumns();
        $$$setupUI$$$();
        MyTableModel tableModel = new MyTableModel();
        infoList = new ArrayList<>();
        for (int i = 0; i < intellijColumnInfos.size(); i++) {
            IntellijColumnInfo intellijColumnInfo = intellijColumnInfos.get(i);
            ColumnInfo columnInfo = new ColumnInfo();
            columnInfo.setId(i);
            columnInfo.setUpdate(false);
            columnInfo.setIgnore(false);
            columnInfo.setKey(checkPriMaryKey(priMaryKeyIntellijColumnInfos, intellijColumnInfo));
            columnInfo.setJavaType(JdbcUtils.convertJavaType(intellijColumnInfo.getDataType(), intellijColumnInfo.getSize(), false).getFullyQualifiedName());
            columnInfo.setName(intellijColumnInfo.getName());
            columnInfo.setJavaName(StringUtils.lowerCaseFirstChar(StringUtils.getUpperCamelFromAny(columnInfo.getName())));
            columnInfo.setDataType(1);
            columnInfo.setTypeName(intellijColumnInfo.getTypeName().toUpperCase(Locale.ROOT));
            infoList.add(columnInfo);
        }
        buildInfoList(infoList);
        tableModel.setColumnInfos(infoList);
        columnTable.setModel(tableModel);
        DefaultTableCellRenderer defaultTableCellRenderer = new DefaultTableCellRenderer();
        defaultTableCellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        columnTable.setDefaultRenderer(Object.class, defaultTableCellRenderer);
        columnTable.setFillsViewportHeight(true);
        //jdbc 类型
        TableColumn tableColumn = columnTable.getColumnModel().getColumn(2);
        tableColumn.setCellEditor(new MyAutoCompletionWithBrowserTableCellEditor(project, JdbcUtils.getJdbcTypes()));
        //java 类型
        columnTable.getColumnModel().getColumn(4).setCellEditor(new MyAutoCompletionWithBrowserTableCellEditor(project, JdbcUtils.getJavaTypes()));
        useActualColumnNamesCheckBox.setSelected(!projectState.isUserDbCheckBox());
        userDbCheckBox.setSelected(projectState.isUserDbCheckBox());
        useActualColumnNamesCheckBox.addActionListener(e -> {
            userDbCheckBox.setSelected(!useActualColumnNamesCheckBox.isSelected());
            buildList(infoList);
            columnTable.updateUI();
        });
        userDbCheckBox.addActionListener(e -> {
            useActualColumnNamesCheckBox.setSelected(!userDbCheckBox.isSelected());
            buildList(infoList);
            columnTable.updateUI();
        });
        initLanguage();
    }

    public void initLanguage() {
        useActualColumnNamesCheckBox.setText(MybatisSmartCodeHelpBundle.message("use.actual.column.text"));
        userDbCheckBox.setText(MybatisSmartCodeHelpBundle.message("use.db.text"));
        columnTable.setToolTipText(MybatisSmartCodeHelpBundle.message("column.table.tip"));
    }

    private void setGeneratorConfig(GeneratorConfig generatorConfig) {
        this.generatorConfig = generatorConfig;
    }

    public void setConfig(ProjectState projectState, GeneratorConfig generatorConfig) {
        setGeneratorConfig(generatorConfig);
        List<IntellijColumnInfo> intellijColumnInfos = generatorConfig.getIntellijTableInfo().getColumnInfos();
        List<IntellijColumnInfo> priMaryKeyIntellijColumnInfos = generatorConfig.getIntellijTableInfo().getPrimaryKeyColumns();
        MyTableModel tableModel = new MyTableModel();
        infoList.clear();
        for (int i = 0; i < intellijColumnInfos.size(); i++) {
            IntellijColumnInfo intellijColumnInfo = intellijColumnInfos.get(i);
            ColumnInfo columnInfo = new ColumnInfo();
            columnInfo.setId(i);
            columnInfo.setUpdate(false);
            columnInfo.setIgnore(false);
            columnInfo.setKey(checkPriMaryKey(priMaryKeyIntellijColumnInfos, intellijColumnInfo));
            columnInfo.setJavaType(JdbcUtils.convertJavaType(intellijColumnInfo.getDataType(), intellijColumnInfo.getSize(), false).getFullyQualifiedName());
            columnInfo.setName(intellijColumnInfo.getName());
            columnInfo.setJavaName(StringUtils.lowerCaseFirstChar(StringUtils.getUpperCamelFromAny(columnInfo.getName())));
            columnInfo.setDataType(1);
            columnInfo.setTypeName(intellijColumnInfo.getTypeName().toUpperCase(Locale.ROOT));
            infoList.add(columnInfo);
        }
        buildInfoList(infoList);
        tableModel.setColumnInfos(infoList);
        columnTable.setModel(tableModel);

        useActualColumnNamesCheckBox.setSelected(!projectState.isUserDbCheckBox());
        userDbCheckBox.setSelected(projectState.isUserDbCheckBox());

    }


    /**
     * 加载之前的配置
     *
     * @param infoList 表字段配置信息
     */
    private void buildInfoList(List<ColumnInfo> infoList) {
        Map<String, TableProperties> map = projectState.getTablePropertiesMap();
        TableProperties tableProperties = map.get(generatorConfig.getTableName());
        if (tableProperties == null) {
            return;
        }
        if (infoList == null || infoList.isEmpty()) {
            return;
        }
        List<TableColumnOverride> columnOverrides = tableProperties.getColumnOverrides();
        if (columnOverrides != null && !columnOverrides.isEmpty()) {
            infoList.forEach(columnInfo -> columnOverrides.forEach(columnOverride -> {
                if (columnOverride.getColumnName().equals(columnInfo.getName())) {
                    columnInfo.setTypeName(columnOverride.getJdbcType());
                    columnInfo.setJavaType(columnOverride.getJavaType());
                    columnInfo.setJavaName(columnOverride.getJavaProperty());
                    columnInfo.setUpdate(true);
                }
            }));
        }
        List<TableIgnoredColumn> ignoredColumns = tableProperties.getIgnoredColumns();
        if (ignoredColumns != null && !ignoredColumns.isEmpty()) {
            infoList.forEach(columnInfo -> ignoredColumns.forEach(ignoredColumn -> {
                if (ignoredColumn.getColumnName().equals(columnInfo.getName())) {
                    columnInfo.setIgnore(true);
                }
            }));
        }
    }


    public void initProjectState(ProjectState projectState) {
        projectState.setUseActualColumnNames(userDbCheckBox.isSelected());
        if (this.infoList != null && !this.infoList.isEmpty()) {
            Map<String, TableProperties> map = projectState.getTablePropertiesMap();
            TableProperties tableProperties = new TableProperties();
            List<TableColumnOverride> columnOverrides = new ArrayList<>();
            List<TableIgnoredColumn> ignoredColumns = new ArrayList<>();
            infoList.forEach(columnInfo -> {
                if (columnInfo.isKey()) {
                    projectState.setKey(columnInfo.getName());
                }
                if ((columnInfo.isIgnore())) {
                    TableIgnoredColumn ignoredColumn = new TableIgnoredColumn();
                    ignoredColumn.setColumnName(columnInfo.getName());
                    ignoredColumns.add(ignoredColumn);
                } else {
                    /**
                     * 第一个字母大写,进行全覆盖
                     */
                    if (columnInfo.isUpdate() || StringUtils.checkFirstCharUpperCase(columnInfo.getName())) {
                        TableColumnOverride columnOverride = new TableColumnOverride();
                        columnOverride.setColumnName(columnInfo.getName());
                        columnOverride.setJdbcType(columnInfo.getTypeName());
                        columnOverride.setJavaType(columnInfo.getJavaType());
                        columnOverride.setJavaProperty(columnInfo.getJavaName());
                        columnOverrides.add(columnOverride);
                    }
                }
            });
            tableProperties.setColumnOverrides(columnOverrides);
            tableProperties.setIgnoredColumns(ignoredColumns);
            tableProperties.setTableName(generatorConfig.getTableName());
            map.put(generatorConfig.getTableName(), tableProperties);
            projectState.setTablePropertiesMap(map);
        }
        projectState.setUserDbCheckBox(userDbCheckBox.isSelected());
        projectState.setUseActualColumnNamesCheckBox(useActualColumnNamesCheckBox.isSelected());
    }


    private boolean checkPriMaryKey(List<IntellijColumnInfo> priMaryKeyIntellijColumnInfos, String name) {
        if (priMaryKeyIntellijColumnInfos == null || priMaryKeyIntellijColumnInfos.isEmpty() || StringUtils.isBlank(name)) {
            return false;
        }
        for (IntellijColumnInfo columnInfo : priMaryKeyIntellijColumnInfos) {
            if (columnInfo.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkPriMaryKey(List<IntellijColumnInfo> priMaryKeyIntellijColumnInfos, IntellijColumnInfo intellijColumnInfo) {
        if (priMaryKeyIntellijColumnInfos == null || priMaryKeyIntellijColumnInfos.isEmpty() || intellijColumnInfo == null) {
            return false;
        }
        for (IntellijColumnInfo columnInfo : priMaryKeyIntellijColumnInfos) {
            if (columnInfo.getName().equals(intellijColumnInfo.getName())) {
                return true;
            }
        }
        return false;
    }


    private void buildList(List<ColumnInfo> infoList) {
        if (infoList == null || infoList.isEmpty()) {
            return;
        }
        if (useActualColumnNamesCheckBox.isSelected()) {
            infoList.forEach(columnInfo -> {
                columnInfo.setJavaName(StringUtils.lowerCaseFirstChar(StringUtils.getUpperCamelFromAny(columnInfo.getName())));
            });
        }
        if (userDbCheckBox.isSelected()) {
            infoList.forEach(columnInfo -> columnInfo.setJavaName(columnInfo.getName()));
        }
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        root = new JPanel();
        root.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        root.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 5, new Insets(0, 0, 0, 0), -1, -1));
        root.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        useActualColumnNamesCheckBox = new JCheckBox();
        useActualColumnNamesCheckBox.setSelected(true);
        useActualColumnNamesCheckBox.setText("驼峰命名");
        panel1.add(useActualColumnNamesCheckBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, new Dimension(10, -1), new Dimension(10, -1), new Dimension(10, -1), 0, false));
        userDbCheckBox = new JCheckBox();
        userDbCheckBox.setText("数据库默认命名");
        panel1.add(userDbCheckBox, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel1.add(spacer2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel1.add(spacer3, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        jScrollPanel = new JScrollPane();
        root.add(jScrollPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        columnTable = new JBTable();
        columnTable.setToolTipText("类字段编辑器");
        jScrollPanel.setViewportView(columnTable);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return root;
    }

}
