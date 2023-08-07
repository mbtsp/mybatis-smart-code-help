package com.mybatis.dialog;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.ui.tabs.TabInfo;
import com.intellij.ui.tabs.impl.JBTabsImpl;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.mybatis.messages.MybatisSmartCodeHelpBundle;
import com.mybatis.model.GeneratorConfig;
import com.mybatis.state.ProjectState;
import com.mybatis.utils.StringUtils;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class ConfigPanel implements Disposable {
    private final ProjectState projectState;
    private JCheckBox toStringCheckBox;
    private JCheckBox equalHashCodeCheckBox;
    private JCheckBox notBuilderJdbcTypeCheckBox;
    private JCheckBox mapperCheckBox;
    private JCheckBox stringTrimCheckBox;
    private JCheckBox commentCheckBox;
    private JCheckBox serializableCheckBox;
    private JCheckBox exampleCheckBox;
    private JCheckBox swaggerCheckBox;
    private JCheckBox blonCheckBox;
    private JCheckBox addSchemaCheckBox;
    private JCheckBox interfaceCommentCheckBox;
    private JPanel root;
    private JCheckBox dataCheckBox;
    private JCheckBox getterSetterCheckBox;
    private JCheckBox builderCheckBox;
    private JCheckBox noArgsConstructorCheckBox;
    private JCheckBox allArgsConstructorCheckBox;
    private JPanel tabPanel;
    private Project project;
    private TkConfigDialog tkConfigDialog;
    private GeneratorConfig generatorConfig;

    public ConfigPanel(Project project, ProjectState projectState, GeneratorConfig generatorConfig) {
        $$$setupUI$$$();
        this.projectState = projectState;
        this.project = project;
        setGeneratorConfig(generatorConfig);
        this.dataCheckBox.addActionListener(e -> {
            if (this.dataCheckBox.isSelected()) {
                this.getterSetterCheckBox.setSelected(false);
                this.toStringCheckBox.setSelected(false);
                this.equalHashCodeCheckBox.setSelected(false);
            }
        });
        this.getterSetterCheckBox.addActionListener(e -> {
            if (this.getterSetterCheckBox.isSelected()) {
                this.dataCheckBox.setSelected(false);
            }
        });
        this.toStringCheckBox.addActionListener(e -> {
            if (toStringCheckBox.isSelected()) {
                this.dataCheckBox.setSelected(false);
            }
        });
        this.equalHashCodeCheckBox.addActionListener(e -> {
            if (this.equalHashCodeCheckBox.isSelected()) {
                this.dataCheckBox.setSelected(false);
            }
        });
        initProjectStateValue(projectState);
        addSchemaCheckBox.setVisible(false);
        addSchemaCheckBox.setEnabled(false);
        interfaceCommentCheckBox.setEnabled(true);
        interfaceCommentCheckBox.setVisible(true);
        notBuilderJdbcTypeCheckBox.setEnabled(false);
        notBuilderJdbcTypeCheckBox.setVisible(false);
        initTablePanel();
        initLanguage();
    }

    public void initLanguage() {
        commentCheckBox.setText(MybatisSmartCodeHelpBundle.message("comment.check.text"));
        blonCheckBox.setText(MybatisSmartCodeHelpBundle.message("blob.check.text"));
        addSchemaCheckBox.setText(MybatisSmartCodeHelpBundle.message("add.schema.check.text"));
        exampleCheckBox.setText(MybatisSmartCodeHelpBundle.message("example.check.text"));
        stringTrimCheckBox.setToolTipText(MybatisSmartCodeHelpBundle.message("string.trim.check.tip"));
        stringTrimCheckBox.setText(MybatisSmartCodeHelpBundle.message("string.trim.check.text"));
        interfaceCommentCheckBox.setText(MybatisSmartCodeHelpBundle.message("interface.comment.text"));
    }

    private void initProjectStateValue(ProjectState projectState) {
        //lombok
        dataCheckBox.setSelected(projectState.isLombokData());
        getterSetterCheckBox.setSelected(projectState.isLombokGetterSetter());
        builderCheckBox.setSelected(projectState.isLombokBuilder());
        noArgsConstructorCheckBox.setSelected(projectState.isLombokNoArgsConstructor());
        allArgsConstructorCheckBox.setSelected(projectState.isLombokAllArgsConstructor());
        //config
        toStringCheckBox.setSelected(projectState.isToString());
        equalHashCodeCheckBox.setSelected(projectState.isEqualHashCode());
        notBuilderJdbcTypeCheckBox.setSelected(projectState.isNoJdbcType());
        mapperCheckBox.setSelected(projectState.isAnnotationMapper());
        stringTrimCheckBox.setSelected(projectState.isModelStringTrim());
        stringTrimCheckBox.setToolTipText("此选项要生成set get 方法才有效果,像@Data自动编译生成就不会有任何效果,请注意!");
        commentCheckBox.setSelected(projectState.isComment());
        serializableCheckBox.setSelected(projectState.isSerializable());
        exampleCheckBox.setSelected(projectState.isGeneratorExample());
        swaggerCheckBox.setSelected(projectState.isSwagger());
        blonCheckBox.setSelected(projectState.isCheckBlob());
        addSchemaCheckBox.setSelected(projectState.isAddSchema());
        interfaceCommentCheckBox.setSelected(projectState.isInterfaceComment());
    }

    public void setConfig(ProjectState projectState, GeneratorConfig generatorConfig) {
        setGeneratorConfig(generatorConfig);
        initProjectStateValue(projectState);
        tkConfigDialog.setConfig(projectState);
    }

    public GeneratorConfig getGeneratorConfig() {
        return generatorConfig;
    }

    private void setGeneratorConfig(GeneratorConfig generatorConfig) {
        this.generatorConfig = generatorConfig;
    }

    public void initTablePanel() {
        JBTabsImpl tabs = new JBTabsImpl(project);
        tkConfigDialog = new TkConfigDialog(project, projectState);
        TabInfo tkTableInfo = new TabInfo(tkConfigDialog.$$$getRootComponent$$$());
        tkTableInfo.setText("Tk Mapper Config");
        tabs.addTab(tkTableInfo);
        MybatisPlusDialog mybatisPlusDialog = new MybatisPlusDialog();
        TabInfo mybatisPlusTabInfo = new TabInfo(mybatisPlusDialog.$$$getRootComponent$$$());
        mybatisPlusTabInfo.setText("Mybatis Plus Config");
//        tabs.addTab(mybatisPlusTabInfo);
        tabs.setTabDraggingEnabled(true);
        GridConstraints gridConstraints = new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false);
        tabPanel.add(tabs, gridConstraints);
    }

    public boolean doValidate() {
        if (!tkConfigDialog.doValidate()) {
            return false;
        }
        return true;
    }

    public boolean doValidate(ProjectState projectState) {
        if (projectState.isUserTkMapper() && StringUtils.isBlank(projectState.getTkMappers())) {
            return false;
        }
        return true;
    }


    public void initProjectState(ProjectState projectState) {
        //lombok
        projectState.setLombokData(dataCheckBox.isSelected());
        projectState.setLombokGetterSetter(getterSetterCheckBox.isSelected());
        projectState.setLombokBuilder(builderCheckBox.isSelected());
        projectState.setLombokNoArgsConstructor(noArgsConstructorCheckBox.isSelected());
        projectState.setLombokAllArgsConstructor(allArgsConstructorCheckBox.isSelected());
        //config
        projectState.setToString(toStringCheckBox.isSelected());
        projectState.setEqualHashCode(equalHashCodeCheckBox.isSelected());
        projectState.setNoJdbcType(notBuilderJdbcTypeCheckBox.isSelected());
        projectState.setAnnotationMapper(mapperCheckBox.isSelected());
        projectState.setModelStringTrim(stringTrimCheckBox.isSelected());
        projectState.setComment(commentCheckBox.isSelected());
        projectState.setSerializable(serializableCheckBox.isSelected());
        projectState.setGeneratorExample(exampleCheckBox.isSelected());
        projectState.setSwagger(swaggerCheckBox.isSelected());
        projectState.setCheckBlob(blonCheckBox.isSelected());
        projectState.setAddSchema(addSchemaCheckBox.isSelected());
        projectState.setInterfaceComment(interfaceCommentCheckBox.isSelected());
        tkConfigDialog.initProjectState(projectState);
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
        root.setLayout(new GridLayoutManager(11, 3, new Insets(0, 0, 0, 0), -1, -1));
        root.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final Spacer spacer1 = new Spacer();
        root.add(spacer1, new GridConstraints(10, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        root.add(panel1, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("lombok");
        panel1.add(label1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JSeparator separator1 = new JSeparator();
        panel1.add(separator1, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JSeparator separator2 = new JSeparator();
        panel1.add(separator2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        root.add(panel2, new GridConstraints(3, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Mybatis");
        panel2.add(label2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JSeparator separator3 = new JSeparator();
        panel2.add(separator3, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JSeparator separator4 = new JSeparator();
        panel2.add(separator4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        dataCheckBox = new JCheckBox();
        dataCheckBox.setText("@Data");
        root.add(dataCheckBox, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(187, 24), null, 0, false));
        getterSetterCheckBox = new JCheckBox();
        getterSetterCheckBox.setText("@GetterSetter");
        root.add(getterSetterCheckBox, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        builderCheckBox = new JCheckBox();
        builderCheckBox.setText("@Builder");
        root.add(builderCheckBox, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        noArgsConstructorCheckBox = new JCheckBox();
        noArgsConstructorCheckBox.setText("@NoArgsConstructor");
        root.add(noArgsConstructorCheckBox, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(187, 24), null, 0, false));
        allArgsConstructorCheckBox = new JCheckBox();
        allArgsConstructorCheckBox.setText("@AllArgsConstructor");
        root.add(allArgsConstructorCheckBox, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        mapperCheckBox = new JCheckBox();
        mapperCheckBox.setText("@Mapper");
        root.add(mapperCheckBox, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(187, -1), null, 0, false));
        addSchemaCheckBox = new JCheckBox();
        addSchemaCheckBox.setText("表里面添加schema名");
        root.add(addSchemaCheckBox, new GridConstraints(4, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        blonCheckBox = new JCheckBox();
        blonCheckBox.setText("区分blon字段");
        root.add(blonCheckBox, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        commentCheckBox = new JCheckBox();
        commentCheckBox.setText("生成注释");
        root.add(commentCheckBox, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        exampleCheckBox = new JCheckBox();
        exampleCheckBox.setText("生成ByExample语句");
        root.add(exampleCheckBox, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        interfaceCommentCheckBox = new JCheckBox();
        interfaceCommentCheckBox.setText("接口方法生成注释");
        root.add(interfaceCommentCheckBox, new GridConstraints(5, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        root.add(panel3, new GridConstraints(6, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Other");
        panel3.add(label3, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JSeparator separator5 = new JSeparator();
        panel3.add(separator5, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JSeparator separator6 = new JSeparator();
        panel3.add(separator6, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        toStringCheckBox = new JCheckBox();
        toStringCheckBox.setText("toString");
        root.add(toStringCheckBox, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        swaggerCheckBox = new JCheckBox();
        swaggerCheckBox.setText("swagger");
        root.add(swaggerCheckBox, new GridConstraints(7, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        serializableCheckBox = new JCheckBox();
        serializableCheckBox.setText("@Serializable");
        root.add(serializableCheckBox, new GridConstraints(7, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        equalHashCodeCheckBox = new JCheckBox();
        equalHashCodeCheckBox.setText("equalHashCode");
        root.add(equalHashCodeCheckBox, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        stringTrimCheckBox = new JCheckBox();
        stringTrimCheckBox.setText("model类对string进行trim");
        root.add(stringTrimCheckBox, new GridConstraints(8, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        notBuilderJdbcTypeCheckBox = new JCheckBox();
        notBuilderJdbcTypeCheckBox.setText("不生成jdbcType");
        root.add(notBuilderJdbcTypeCheckBox, new GridConstraints(8, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tabPanel = new JPanel();
        tabPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        root.add(tabPanel, new GridConstraints(9, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return root;
    }

    @Override
    public void dispose() {
        root.removeAll();
    }
}
