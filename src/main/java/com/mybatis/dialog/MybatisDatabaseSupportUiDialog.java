package com.mybatis.dialog;

import com.intellij.ide.util.TreeClassChooser;
import com.intellij.ide.util.TreeClassChooserFactory;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiPackage;
import com.intellij.refactoring.ui.PackageNameReferenceEditorCombo;
import com.intellij.ui.components.labels.LinkLabel;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.mybatis.datasource.DbConfig;
import com.mybatis.state.ProjectState;
import com.mybatis.utils.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.mybatis.generator.api.IntellijColumnInfo;
import org.mybatis.generator.api.IntellijTableInfo;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MybatisDatabaseSupportUiDialog {
    private final DbConfig dbConfig;
    private final Project project;
    private final ProjectState projectState;
    private final MyTextFieldWithAutoCompletionWithBrowserButton generatedKeyButton;
    private final PackageNameReferenceEditorCombo javaModelCombo;
    private final MyTextFieldWithAutoCompletionWithBrowserButton javaSourceModelButton;
    private final PackageNameReferenceEditorCombo javaMapperCombo;
    private final MyTextFieldWithAutoCompletionWithBrowserButton javaMapperSourceButton;
    private final MyPackageNameReferenceEditorCombo javaMapperXmlCombo;
    private final MyTextFieldWithAutoCompletionWithBrowserButton javaMapperXmlSourceButton;
    private final PackageNameReferenceEditorCombo javaServiceCombo;
    private final MyTextFieldWithAutoCompletionWithBrowserButton javaServiceSourceButton;
    private final PackageNameReferenceEditorCombo javaServiceInterfaceCombo;
    private final MyTextFieldWithAutoCompletionWithBrowserButton javaServiceInterfaceSourceButton;
    //父类配置
    private final MyTextFieldWithAutoCompletionWithBrowserButton superClassModelButton;
    private final MyTextFieldWithAutoCompletionWithBrowserButton superMapperClassModelButton;
    public JPanel mainPanel;
    private JTextField modelName;
    private JButton customColumnButton;
    private JTextField oracleKeyText;
    private JTextField oracleColumnText;
    private JLabel javaModelNameLabel;
    private JLabel generatedKeyLabel;
    private JLabel oracleGeneredKeyLabel;
    private JLabel oracleColumn;
    private JPanel topPanel;
    private JPanel centerPanel;
    private JComboBox<String> javaModules;
    private JPanel javaModelPanel;
    private JPanel javaModelSourcePanel;
    private JPanel javaMapperPanel;
    private JPanel javaMapperSourcePanel;
    private JPanel javaMapperXmlPanel;
    private JPanel javaXmlResourcesPanel;
    private JCheckBox dataCheckBox;
    private JCheckBox getterSetterCheckBox;
    private JCheckBox builderCheckBox;
    private JCheckBox noArgsConstructorCheckBox;
    private JCheckBox allArgsConstructorCheckBox;
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
    private JTabbedPane tabbedPane1;
    private JCheckBox generateServiceCheckBox;
    private JCheckBox generateServiceInterfaceCheckBox;
    private JCheckBox batchUpdateCheckBox;
    private JCheckBox batchUpdateSelectiveCheckBox;
    private JCheckBox batchInsertCheckBox;
    private JCheckBox insertOnDuplicateMysqlCheckBox;
    private JCheckBox tkMapperCheckBox;
    private JCheckBox insertCheckBox;
    private JCheckBox insertSelectiveCheckBox;
    private JCheckBox selectPrimaryKeyCheckBox;
    private JCheckBox updateByPrimaryKeyCheckBox;
    private JCheckBox updateByPrimaryKeySelectiveCheckBox;
    private JCheckBox deleteByPrimaryKeyCheckBox;
    private JCheckBox mybatisPlus3CheckBox;
    private JCheckBox mybatisPlus2CheckBox;
    private JCheckBox modelStaticCheckBox;
    private JComboBox<String> idTypeComboBox;
    private JPanel javaServicePackagePanel;
    private JPanel javaServiceSrcFolderPanel;
    private JPanel javaServiceInterfacePackagePanel;
    private JPanel javaServiceInterfaceSrcFolderPanel;
    private JPanel superClassModelPanel;
    private JPanel superClassMapperPanel;
    private JPanel javaServiceTabPanel;
    private JPanel javaServiceInterfaceTabPanel;
    private JPanel tkMapperSupperPanel;
    private JPanel moreSqlGenerate;
    private JPanel generatedKeyPanel;
    private LinkLabel<String> removePrefix;
    private MyAddEditDeleteListPanel methodNameGeneratorSql;
    private JScrollPane root;


    public MybatisDatabaseSupportUiDialog(Project project, @NotNull DbConfig dbConfig, @NotNull ProjectState projectState) {
        this.projectState = projectState;
        this.project = project;
        this.dbConfig = dbConfig;
        this.generatedKeyButton = new MyTextFieldWithAutoCompletionWithBrowserButton(project);
        $$$setupUI$$$();

        FileChooserDescriptor fileChooserDescriptor = getFileChooserDescriptor();
        this.javaModelCombo = new PackageNameReferenceEditorCombo(projectState.getJavaModelPackage(), project, "mybatisJavaModelRecent", "choose java model package for generate mybatis model");
        this.javaSourceModelButton = new MyTextFieldWithAutoCompletionWithBrowserButton(project);

        this.javaSourceModelButton.addBrowseFolderListener("Choose Src Folder for Java Model", "Choose some", this.project, fileChooserDescriptor, new TextFieldWithAutoCompletionBrowserTextAccessor());

        this.javaMapperCombo = new PackageNameReferenceEditorCombo(projectState.getJavaMapperPackage(), project, "mybatisJavaMapperRecent", "choose java mapper package for generate mybatis model");
        this.javaMapperSourceButton = new MyTextFieldWithAutoCompletionWithBrowserButton(project);

        this.javaMapperSourceButton.addBrowseFolderListener("Choose Src Folder for Mapper", "Choose some", this.project, fileChooserDescriptor, new TextFieldWithAutoCompletionBrowserTextAccessor());

        this.javaMapperXmlCombo = new MyPackageNameReferenceEditorCombo(projectState.getJavaXmlPackage(), project, null, "mybatisJavaXmlRecent", "choose java xml package for generate mybatis model");
        this.javaMapperXmlSourceButton = new MyTextFieldWithAutoCompletionWithBrowserButton(project);

        this.javaMapperXmlSourceButton.addBrowseFolderListener("Choose Src Folder for Xml", "Choose some", this.project, fileChooserDescriptor, new TextFieldWithAutoCompletionBrowserTextAccessor());

        this.javaServiceCombo = new PackageNameReferenceEditorCombo(projectState.getJavaServicePackage(), project, "mybatisJavaServiceRecent", "choose java service model package for generate mybatis model");
        this.javaServiceSourceButton = new MyTextFieldWithAutoCompletionWithBrowserButton(project);

        this.javaServiceSourceButton.addBrowseFolderListener("Choose Src Folder for Service", "Choose some", this.project, fileChooserDescriptor, new TextFieldWithAutoCompletionBrowserTextAccessor());

        this.javaServiceInterfaceCombo = new PackageNameReferenceEditorCombo(projectState.getJavaServiceInterfacePackage(), project, "mybatisJavaServiceInterfaceRecent", "choose java service interface model package for generate mybatis model");
        this.javaServiceInterfaceSourceButton = new MyTextFieldWithAutoCompletionWithBrowserButton(project);

        this.javaServiceInterfaceSourceButton.addBrowseFolderListener("Choose Src Folder for Service Interface", "Choose some", this.project, fileChooserDescriptor, new TextFieldWithAutoCompletionBrowserTextAccessor());

        this.superClassModelButton = new MyTextFieldWithAutoCompletionWithBrowserButton(project);
//        this.superClassModelButton.getChildComponent().addDocumentListener(new BulkAwareDocumentListener.Simple() {
//            @Override
//            public void documentChanged(@NotNull DocumentEvent event) {
//                String text = event.getDocument().getText();
//                superClassModelButton.setAutoCompletionItems(getItems(text));
//            }
//        });
        this.superClassModelButton.addActionListener(e -> {
            TreeClassChooserFactory treeClassChooserFactory = TreeClassChooserFactory.getInstance(project);
            TreeClassChooser treeClassChooser = treeClassChooserFactory.createAllProjectScopeChooser("Choose SuperClass");
            treeClassChooser.showDialog();
            PsiClass psiClass = treeClassChooser.getSelected();
            if (psiClass != null) {
                this.superClassModelButton.setText(psiClass.getQualifiedName());
            }
        });
        this.superMapperClassModelButton = new MyTextFieldWithAutoCompletionWithBrowserButton(project);
        this.superMapperClassModelButton.getChildComponent().addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(@NotNull DocumentEvent event) {
                String text = event.getDocument().getText();
                superMapperClassModelButton.setAutoCompletionItems(getItems(text));
            }
        });
        this.superMapperClassModelButton.addActionListener(e -> {
            TreeClassChooserFactory treeClassChooserFactory = TreeClassChooserFactory.getInstance(project);
            TreeClassChooser treeClassChooser = treeClassChooserFactory.createAllProjectScopeChooser("Choose Mapper SuperClass");
            treeClassChooser.showDialog();
            PsiClass psiClass = treeClassChooser.getSelected();
            if (psiClass != null) {
                this.superMapperClassModelButton.setText(psiClass.getQualifiedName());
            }
        });
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
        initBuildComponents();
    }

    private void initBuildComponents() {
        IntellijTableInfo intellijTableInfo = dbConfig.getTableInfos();
        if (intellijTableInfo != null && intellijTableInfo.getPrimaryKeyColumns() != null && !intellijTableInfo.getPrimaryKeyColumns().isEmpty()) {
            intellijTableInfo.getPrimaryKeyColumns().forEach(columnInfo -> {
                if (!projectState.getPrimaryKeys().contains(columnInfo.getName())) {
                    projectState.getPrimaryKeys().add(columnInfo.getName());
                }
            });
            this.generatedKeyButton.setAutoCompletionItems(projectState.getPrimaryKeys());
            this.generatedKeyButton.setText(intellijTableInfo.getPrimaryKeyColumns().get(0).getName());
        }
        this.modelName.setText(StringUtils.getUpperCamelFromAny(dbConfig.getTableName()));
        if (projectState.getJavaModules() != null && !projectState.getJavaModules().isEmpty()) {
            projectState.getJavaModules().forEach(str -> javaModules.addItem(str));
        }

        GridConstraints gridConstraints = new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false);
        this.generatedKeyPanel.add(this.generatedKeyButton, gridConstraints);

        this.javaModelPanel.add(this.javaModelCombo, gridConstraints);
        this.javaModelSourcePanel.add(this.javaSourceModelButton, gridConstraints);

        this.javaMapperPanel.add(this.javaMapperCombo, gridConstraints);
        this.javaMapperSourcePanel.add(this.javaMapperSourceButton, gridConstraints);

        this.javaMapperXmlPanel.add(this.javaMapperXmlCombo, gridConstraints);
        this.javaXmlResourcesPanel.add(this.javaMapperXmlSourceButton, gridConstraints);

        this.javaServicePackagePanel.add(this.javaServiceCombo, gridConstraints);
        this.javaServiceSrcFolderPanel.add(this.javaServiceSourceButton, gridConstraints);

        this.javaServiceInterfacePackagePanel.add(this.javaServiceInterfaceCombo, gridConstraints);
        this.javaServiceInterfaceSrcFolderPanel.add(this.javaServiceInterfaceSourceButton, gridConstraints);

        this.javaSourceModelButton.setText(projectState.getJavaModelSourcePackage());
        this.javaMapperSourceButton.setText(projectState.getJavaMapperSourcePackage());
        this.javaMapperXmlSourceButton.setText(projectState.getJavaXmlSourcePackage());
        this.javaServiceSourceButton.setText(projectState.getJavaServiceSourcePackage());
        this.javaServiceInterfaceSourceButton.setText(projectState.getJavaServiceInterfaceSourcePackage());

        this.generateServiceCheckBox.setSelected(projectState.isGeneratorService());
        this.javaServiceCombo.setEnabled(projectState.isGeneratorService());
        this.javaServiceSourceButton.setEnabled(projectState.isGeneratorService());
        this.generateServiceCheckBox.addActionListener(e -> {
            this.javaServiceCombo.setEnabled(this.generateServiceCheckBox.isSelected());
            this.javaServiceSourceButton.setEnabled(this.generateServiceCheckBox.isSelected());
        });
        this.generateServiceInterfaceCheckBox.setSelected(projectState.isGeneratorServiceInterface());
        this.javaServiceInterfaceCombo.setEnabled(projectState.isGeneratorServiceInterface());
        this.javaServiceInterfaceSourceButton.setEnabled(projectState.isGeneratorServiceInterface());
        this.generateServiceInterfaceCheckBox.addActionListener(e -> {
            this.javaServiceInterfaceCombo.setEnabled(this.generateServiceInterfaceCheckBox.isSelected());
            this.javaServiceInterfaceSourceButton.setEnabled(this.generateServiceInterfaceCheckBox.isSelected());
        });
        this.javaServiceTabPanel.setEnabled(false);
        this.javaServiceInterfaceTabPanel.setEnabled(false);

        //父类配置
        this.superClassModelPanel.add(this.superClassModelButton, gridConstraints);
        this.superClassMapperPanel.add(this.superMapperClassModelButton, gridConstraints);
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
        commentCheckBox.setSelected(projectState.isComment());
        serializableCheckBox.setSelected(projectState.isSerializable());
        exampleCheckBox.setSelected(projectState.isGeneratorExample());
        swaggerCheckBox.setSelected(projectState.isSwagger());
        blonCheckBox.setSelected(projectState.isCheckBlob());
        addSchemaCheckBox.setSelected(projectState.isAddSchema());
        interfaceCommentCheckBox.setSelected(projectState.isInterfaceComment());


    }

    private void createUIComponents() {
        methodNameGeneratorSql = new MyAddEditDeleteListPanel("Method name for generate sql", this.project, new ArrayList<>(), getColumns());
    }

    public List<String> getColumns() {
        List<String> list = new ArrayList<>();
        if (dbConfig == null || dbConfig.getTableInfos() == null || dbConfig.getTableInfos().getColumnInfos() == null || dbConfig.getTableInfos().getColumnInfos().isEmpty()) {
            return list;
        }
        List<IntellijColumnInfo> columnInfos = dbConfig.getTableInfos().getColumnInfos();
        for (IntellijColumnInfo columnInfo : columnInfos) {
            list.add(StringUtils.upperCaseFirstChar(columnInfo.getName()));
        }
        return list;
    }

    private FileChooserDescriptor getFileChooserDescriptor() {
        FileChooserDescriptor fileChooserDescriptor = FileChooserDescriptorFactory.createSingleFileDescriptor();
        fileChooserDescriptor.setShowFileSystemRoots(true);
        fileChooserDescriptor.setTitle("Choose a Folder...");
        fileChooserDescriptor.setHideIgnored(false);
        VirtualFile virtualFile = ProjectUtil.guessProjectDir(project);
        if (virtualFile != null) {
            fileChooserDescriptor.setRoots(virtualFile);
        }
        return fileChooserDescriptor;
    }

    public void initProjectState(ProjectState projectState) {
        projectState.setModelName(this.modelName.getText());
        projectState.setTableName(dbConfig.getTableName());
        projectState.setSchema(dbConfig.getSchema());
        projectState.setJavaModelPackage(this.javaModelCombo.getText());
        projectState.setJavaModelSourcePackage(this.javaSourceModelButton.getText());

        projectState.setJavaMapperPackage(this.javaMapperCombo.getText());
        projectState.setJavaMapperSourcePackage(this.javaMapperSourceButton.getText());

        projectState.setJavaXmlPackage(this.javaMapperXmlCombo.getText());
        projectState.setJavaXmlSourcePackage(this.javaMapperXmlSourceButton.getText());

        //service impl
        projectState.setGeneratorService(this.generateServiceCheckBox.isSelected());
        projectState.setJavaServicePackage(this.javaServiceCombo.getText());
        projectState.setJavaServiceSourcePackage(this.javaServiceSourceButton.getText());

        //service interface
        projectState.setGeneratorServiceInterface(this.generateServiceInterfaceCheckBox.isSelected());
        projectState.setJavaServiceInterfacePackage(this.javaServiceInterfaceCombo.getText());
        projectState.setJavaServiceInterfaceSourcePackage(this.javaServiceInterfaceSourceButton.getText());

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

    }

    /**
     * @noinspection ALL
     */
    public JComponent getRootPanel() {
        return mainPanel;
    }

    private List<String> getItems(String text) {
        List<String> items = new ArrayList<>();
        if (StringUtils.isBlank(text)) {
            return items;
        }
        if (text.endsWith(".")) {
            PsiPackage superPsiPackage = JavaPsiFacade.getInstance(project).findPackage(text.substring(0, text.length() - 1));
            if (superPsiPackage != null) {

                PsiPackage[] psiPackages = superPsiPackage.getSubPackages();
                if (Arrays.stream(psiPackages).count() > 0) {
                    items.addAll(Arrays.stream(psiPackages).map(psiPackage -> text + psiPackage.getName()).collect(Collectors.toList()));
                }
                //获取下面所有的class
                Arrays.stream(psiPackages).forEach(psiPackage -> {
                    PsiClass[] psiClasses = psiPackage.getClasses();
                    items.addAll(Arrays.stream(psiClasses).map(PsiClass::getQualifiedName).collect(Collectors.toList()));
                });
                items.addAll(Arrays.stream(superPsiPackage.getClasses()).map(PsiClass::getQualifiedName).collect(Collectors.toList()));

            }

        }
        return items;
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        root = new JScrollPane();
        root.setMaximumSize(new Dimension(32767, 710));
        root.setPreferredSize(new Dimension(684, 710));
        root.setRequestFocusEnabled(true);
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(7, 1, new Insets(2, 0, 0, 0), -1, -1));
        mainPanel.setFocusable(false);
        mainPanel.setPreferredSize(new Dimension(644, 850));
        root.setViewportView(mainPanel);
        mainPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-6357127)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        topPanel = new JPanel();
        topPanel.setLayout(new GridLayoutManager(1, 12, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(topPanel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        oracleGeneredKeyLabel = new JLabel();
        oracleGeneredKeyLabel.setText("oracle序列（Oracle一般用于生成主键的序列）");
        topPanel.add(oracleGeneredKeyLabel, new GridConstraints(0, 0, 1, 8, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        oracleKeyText = new JTextField();
        topPanel.add(oracleKeyText, new GridConstraints(0, 8, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        oracleColumn = new JLabel();
        oracleColumn.setText("列名");
        topPanel.add(oracleColumn, new GridConstraints(0, 9, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        oracleColumnText = new JTextField();
        topPanel.add(oracleColumnText, new GridConstraints(0, 10, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayoutManager(5, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(centerPanel, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        centerPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-2100999)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        centerPanel.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("module：");
        panel1.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        javaModules = new JComboBox();
        panel1.add(javaModules, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        centerPanel.add(panel2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("默认生成文件");
        panel2.add(label2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JSeparator separator1 = new JSeparator();
        panel2.add(separator1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(6, 3, new Insets(0, 20, 0, 0), -1, -1));
        centerPanel.add(panel3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("java model 包名：");
        panel3.add(label3, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("java model source 目录：");
        panel3.add(label4, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        javaModelPanel = new JPanel();
        javaModelPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        javaModelPanel.setToolTipText("java model");
        panel3.add(javaModelPanel, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        javaModelSourcePanel = new JPanel();
        javaModelSourcePanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(javaModelSourcePanel, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("java mapper 包名：");
        panel3.add(label5, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        javaMapperPanel = new JPanel();
        javaMapperPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(javaMapperPanel, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("java mapper source 目录：");
        panel3.add(label6, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        javaMapperSourcePanel = new JPanel();
        javaMapperSourcePanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(javaMapperSourcePanel, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("mapper xml 包名：");
        panel3.add(label7, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        javaMapperXmlPanel = new JPanel();
        javaMapperXmlPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(javaMapperXmlPanel, new GridConstraints(4, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label8 = new JLabel();
        label8.setText("xml resources 目录：");
        panel3.add(label8, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        javaXmlResourcesPanel = new JPanel();
        javaXmlResourcesPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(javaXmlResourcesPanel, new GridConstraints(5, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        centerPanel.add(panel4, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label9 = new JLabel();
        label9.setText("lombok");
        panel4.add(label9, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JSeparator separator2 = new JSeparator();
        panel4.add(separator2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(2, 3, new Insets(0, 20, 0, 0), -1, -1));
        centerPanel.add(panel5, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        dataCheckBox = new JCheckBox();
        dataCheckBox.setText("@Data");
        panel5.add(dataCheckBox, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        getterSetterCheckBox = new JCheckBox();
        getterSetterCheckBox.setText("@GetterSetter");
        panel5.add(getterSetterCheckBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        builderCheckBox = new JCheckBox();
        builderCheckBox.setText("Builder");
        panel5.add(builderCheckBox, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        noArgsConstructorCheckBox = new JCheckBox();
        noArgsConstructorCheckBox.setText("@NoArgsConstructor");
        panel5.add(noArgsConstructorCheckBox, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        allArgsConstructorCheckBox = new JCheckBox();
        allArgsConstructorCheckBox.setText("@AllArgsConstructor");
        panel5.add(allArgsConstructorCheckBox, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(panel6, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        javaModelNameLabel = new JLabel();
        javaModelNameLabel.setText("java model 名称:");
        panel6.add(javaModelNameLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(96, 17), new Dimension(96, -1), 0, false));
        modelName = new JTextField();
        modelName.setOpaque(false);
        panel6.add(modelName, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        customColumnButton = new JButton();
        customColumnButton.setBorderPainted(false);
        customColumnButton.setContentAreaFilled(false);
        customColumnButton.setOpaque(true);
        customColumnButton.setText("定制列");
        panel6.add(customColumnButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        removePrefix = new LinkLabel();
        removePrefix.setText("移除表名的前缀");
        panel6.add(removePrefix, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(panel7, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        generatedKeyLabel = new JLabel();
        generatedKeyLabel.setText("userGeneratedKey(主键是否为自动生成,可为空):");
        panel7.add(generatedKeyLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        generatedKeyPanel = new JPanel();
        generatedKeyPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel7.add(generatedKeyPanel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(panel8, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label10 = new JLabel();
        label10.setText("配置");
        panel8.add(label10, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JSeparator separator3 = new JSeparator();
        panel8.add(separator3, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridLayoutManager(4, 3, new Insets(0, 20, 0, 0), -1, -1));
        mainPanel.add(panel9, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        toStringCheckBox = new JCheckBox();
        toStringCheckBox.setText("toString");
        panel9.add(toStringCheckBox, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        equalHashCodeCheckBox = new JCheckBox();
        equalHashCodeCheckBox.setText("equalHashCode");
        panel9.add(equalHashCodeCheckBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        notBuilderJdbcTypeCheckBox = new JCheckBox();
        notBuilderJdbcTypeCheckBox.setText("不生成jdbcType");
        panel9.add(notBuilderJdbcTypeCheckBox, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        mapperCheckBox = new JCheckBox();
        mapperCheckBox.setText("@Mapper注解");
        panel9.add(mapperCheckBox, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        stringTrimCheckBox = new JCheckBox();
        stringTrimCheckBox.setText("model类对string进行trim");
        panel9.add(stringTrimCheckBox, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        commentCheckBox = new JCheckBox();
        commentCheckBox.setText("生成注释");
        panel9.add(commentCheckBox, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        serializableCheckBox = new JCheckBox();
        serializableCheckBox.setText("@Serializable");
        panel9.add(serializableCheckBox, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        exampleCheckBox = new JCheckBox();
        exampleCheckBox.setText("生成selectByExample,updateByExample等查询(不推荐使用)");
        panel9.add(exampleCheckBox, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        swaggerCheckBox = new JCheckBox();
        swaggerCheckBox.setText("swagger注解");
        panel9.add(swaggerCheckBox, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        blonCheckBox = new JCheckBox();
        blonCheckBox.setText("区分blon字段");
        panel9.add(blonCheckBox, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        addSchemaCheckBox = new JCheckBox();
        addSchemaCheckBox.setText("表里面添加schema名");
        panel9.add(addSchemaCheckBox, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        interfaceCommentCheckBox = new JCheckBox();
        interfaceCommentCheckBox.setText("接口方法生成注释");
        panel9.add(interfaceCommentCheckBox, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(panel10, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        tabbedPane1 = new JTabbedPane();
        tabbedPane1.setTabLayoutPolicy(0);
        tabbedPane1.setTabPlacement(1);
        panel10.add(tabbedPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        final JPanel panel11 = new JPanel();
        panel11.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("service", panel11);
        final JPanel panel12 = new JPanel();
        panel12.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel11.add(panel12, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label11 = new JLabel();
        label11.setText("生成service类");
        panel12.add(label11, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JSeparator separator4 = new JSeparator();
        panel12.add(separator4, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel11.add(spacer1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        javaServiceTabPanel = new JPanel();
        javaServiceTabPanel.setLayout(new GridLayoutManager(3, 2, new Insets(0, 20, 0, 0), -1, -1));
        panel11.add(javaServiceTabPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        generateServiceCheckBox = new JCheckBox();
        generateServiceCheckBox.setText("generateService");
        javaServiceTabPanel.add(generateServiceCheckBox, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label12 = new JLabel();
        label12.setText("java service package");
        javaServiceTabPanel.add(label12, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label13 = new JLabel();
        label13.setText("java service src folder");
        javaServiceTabPanel.add(label13, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        javaServicePackagePanel = new JPanel();
        javaServicePackagePanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        javaServiceTabPanel.add(javaServicePackagePanel, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        javaServiceSrcFolderPanel = new JPanel();
        javaServiceSrcFolderPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        javaServiceSrcFolderPanel.setOpaque(false);
        javaServiceTabPanel.add(javaServiceSrcFolderPanel, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JSeparator separator5 = new JSeparator();
        javaServiceTabPanel.add(separator5, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel13 = new JPanel();
        panel13.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("service interface", panel13);
        final JPanel panel14 = new JPanel();
        panel14.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel13.add(panel14, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label14 = new JLabel();
        label14.setText("生成service接口");
        panel14.add(label14, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JSeparator separator6 = new JSeparator();
        panel14.add(separator6, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel13.add(spacer2, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        javaServiceInterfaceTabPanel = new JPanel();
        javaServiceInterfaceTabPanel.setLayout(new GridLayoutManager(3, 2, new Insets(0, 20, 0, 0), -1, -1));
        panel13.add(javaServiceInterfaceTabPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        generateServiceInterfaceCheckBox = new JCheckBox();
        generateServiceInterfaceCheckBox.setText("generateServiceInterface");
        javaServiceInterfaceTabPanel.add(generateServiceInterfaceCheckBox, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label15 = new JLabel();
        label15.setText("service interface package");
        javaServiceInterfaceTabPanel.add(label15, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label16 = new JLabel();
        label16.setText("service interface src folder");
        javaServiceInterfaceTabPanel.add(label16, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        javaServiceInterfacePackagePanel = new JPanel();
        javaServiceInterfacePackagePanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        javaServiceInterfaceTabPanel.add(javaServiceInterfacePackagePanel, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        javaServiceInterfaceSrcFolderPanel = new JPanel();
        javaServiceInterfaceSrcFolderPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        javaServiceInterfaceSrcFolderPanel.setOpaque(false);
        javaServiceInterfaceTabPanel.add(javaServiceInterfaceSrcFolderPanel, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JSeparator separator7 = new JSeparator();
        javaServiceInterfaceTabPanel.add(separator7, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel15 = new JPanel();
        panel15.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("父类配置", panel15);
        final JPanel panel16 = new JPanel();
        panel16.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel15.add(panel16, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label17 = new JLabel();
        label17.setText("superClass");
        panel16.add(label17, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JSeparator separator8 = new JSeparator();
        panel16.add(separator8, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel17 = new JPanel();
        panel17.setLayout(new GridLayoutManager(2, 2, new Insets(0, 20, 0, 0), -1, -1));
        panel15.add(panel17, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label18 = new JLabel();
        label18.setText("model的父类");
        panel17.add(label18, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label19 = new JLabel();
        label19.setText("mapper的父类");
        panel17.add(label19, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        superClassModelPanel = new JPanel();
        superClassModelPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel17.add(superClassModelPanel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        superClassMapperPanel = new JPanel();
        superClassMapperPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel17.add(superClassMapperPanel, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel15.add(spacer3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        moreSqlGenerate = new JPanel();
        moreSqlGenerate.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("更多sql生成", moreSqlGenerate);
        final JPanel panel18 = new JPanel();
        panel18.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        moreSqlGenerate.add(panel18, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label20 = new JLabel();
        label20.setText("更多sql生成");
        panel18.add(label20, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JSeparator separator9 = new JSeparator();
        panel18.add(separator9, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        moreSqlGenerate.add(spacer4, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel19 = new JPanel();
        panel19.setLayout(new GridLayoutManager(2, 2, new Insets(0, 20, 0, 0), -1, -1));
        moreSqlGenerate.add(panel19, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        batchUpdateCheckBox = new JCheckBox();
        batchUpdateCheckBox.setText("batchUpdate批量更新(mysqlAndOracle)");
        panel19.add(batchUpdateCheckBox, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        batchUpdateSelectiveCheckBox = new JCheckBox();
        batchUpdateSelectiveCheckBox.setText("batchUpdateSelective批量可选更新(mysqlAndOracle)");
        panel19.add(batchUpdateSelectiveCheckBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        batchInsertCheckBox = new JCheckBox();
        batchInsertCheckBox.setText("batchInsert批量插入(mysql and sqlserver)");
        panel19.add(batchInsertCheckBox, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        insertOnDuplicateMysqlCheckBox = new JCheckBox();
        insertOnDuplicateMysqlCheckBox.setText("insertOnDuplicate(mysql)");
        panel19.add(insertOnDuplicateMysqlCheckBox, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel20 = new JPanel();
        panel20.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("tkmapper", panel20);
        final JPanel panel21 = new JPanel();
        panel21.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel20.add(panel21, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label21 = new JLabel();
        label21.setText("tkMapper配置");
        panel21.add(label21, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JSeparator separator10 = new JSeparator();
        panel21.add(separator10, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer5 = new Spacer();
        panel20.add(spacer5, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel22 = new JPanel();
        panel22.setLayout(new GridLayoutManager(2, 2, new Insets(0, 20, 0, 0), -1, -1));
        panel20.add(panel22, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        tkMapperCheckBox = new JCheckBox();
        tkMapperCheckBox.setText("使用tk.mapper");
        panel22.add(tkMapperCheckBox, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer6 = new Spacer();
        panel22.add(spacer6, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JLabel label22 = new JLabel();
        label22.setText("tk.Mapper的父类");
        panel22.add(label22, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tkMapperSupperPanel = new JPanel();
        tkMapperSupperPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel22.add(tkMapperSupperPanel, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel23 = new JPanel();
        panel23.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("定制默认方法", panel23);
        final JPanel panel24 = new JPanel();
        panel24.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel23.add(panel24, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label23 = new JLabel();
        label23.setText("默认生成sql语句");
        panel24.add(label23, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JSeparator separator11 = new JSeparator();
        panel24.add(separator11, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer7 = new Spacer();
        panel23.add(spacer7, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel25 = new JPanel();
        panel25.setLayout(new GridLayoutManager(2, 3, new Insets(0, 20, 0, 0), -1, -1));
        panel23.add(panel25, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        insertCheckBox = new JCheckBox();
        insertCheckBox.setText("insert");
        panel25.add(insertCheckBox, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        insertSelectiveCheckBox = new JCheckBox();
        insertSelectiveCheckBox.setText("insertSelective");
        panel25.add(insertSelectiveCheckBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        selectPrimaryKeyCheckBox = new JCheckBox();
        selectPrimaryKeyCheckBox.setText("selectPrimaryKey");
        panel25.add(selectPrimaryKeyCheckBox, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        updateByPrimaryKeyCheckBox = new JCheckBox();
        updateByPrimaryKeyCheckBox.setText("updateByPrimaryKey");
        panel25.add(updateByPrimaryKeyCheckBox, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        updateByPrimaryKeySelectiveCheckBox = new JCheckBox();
        updateByPrimaryKeySelectiveCheckBox.setText("updateByPrimaryKeySelective");
        panel25.add(updateByPrimaryKeySelectiveCheckBox, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        deleteByPrimaryKeyCheckBox = new JCheckBox();
        deleteByPrimaryKeyCheckBox.setText("deleteByPrimaryKey");
        panel25.add(deleteByPrimaryKeyCheckBox, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel26 = new JPanel();
        panel26.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("mybatisPlus配置", panel26);
        final JPanel panel27 = new JPanel();
        panel27.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel26.add(panel27, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        mybatisPlus3CheckBox = new JCheckBox();
        mybatisPlus3CheckBox.setText("mybatisPlus3");
        panel27.add(mybatisPlus3CheckBox, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        mybatisPlus2CheckBox = new JCheckBox();
        mybatisPlus2CheckBox.setText("mybatisPlus2");
        panel27.add(mybatisPlus2CheckBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer8 = new Spacer();
        panel26.add(spacer8, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel28 = new JPanel();
        panel28.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel26.add(panel28, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        modelStaticCheckBox = new JCheckBox();
        modelStaticCheckBox.setText("model类生成static列名字段");
        panel28.add(modelStaticCheckBox, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer9 = new Spacer();
        panel28.add(spacer9, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel29 = new JPanel();
        panel29.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel26.add(panel29, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label24 = new JLabel();
        label24.setText("idType");
        panel29.add(label24, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        idTypeComboBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        idTypeComboBox.setModel(defaultComboBoxModel1);
        panel29.add(idTypeComboBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel30 = new JPanel();
        panel30.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("方法名生成完整sql", panel30);
        final Spacer spacer10 = new Spacer();
        panel30.add(spacer10, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        panel30.add(methodNameGeneratorSql, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return root;
    }

}
