package com.mybatis.dialog;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBList;
import com.intellij.ui.tabs.TabInfo;
import com.intellij.ui.tabs.TabsListener;
import com.intellij.ui.tabs.impl.JBTabsImpl;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.mybatis.messages.MybatisSmartCodeHelpBundle;
import com.mybatis.model.CacheModel.Cache.MultiProjectState;
import com.mybatis.model.GeneratorConfig;
import com.mybatis.model.MyListModel;
import com.mybatis.state.MultipleMybatisStateComponent;
import com.mybatis.state.MybatisStateComponent;
import com.mybatis.state.ProjectState;
import com.mybatis.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MultipleMybatisSupportPanel implements Disposable {
    private final List<GeneratorConfig> generatorConfigList;
    private final Project project;
    private final MultiProjectState multiProjectState;
    private final ProjectState simpleProjectState;
    private final JBTabsImpl tabs;
    private final MyListModel<GeneratorConfig> myListModel;
    private final JList<GeneratorConfig> myList;
    private JPanel root;
    private JPanel leftJPanel;
    private JPanel rightJPanel;
    private JSplitPane jSplitPane;
    private GeneratorConfig selectGeneratorConfig;
    private GeneratorConfigPanel generatorConfigPanel;
    private StepTwoPanel stepTwoPanel;
    private ConfigPanel configPanel;
    private TabInfo generatorConfigPanelTabInfo;
    private TabInfo stepTwoPanelTabInfo;
    private TabInfo configPanelTabInfo;
    private ToolbarDecorator decorator;

    public MultipleMybatisSupportPanel(Project project, @NotNull List<GeneratorConfig> generatorConfigList) {
        $$$setupUI$$$();
        this.myList = new JBList<>();
        this.generatorConfigList = generatorConfigList;
        this.project = project;
        this.selectGeneratorConfig = generatorConfigList.get(0);
        this.tabs = new JBTabsImpl(project);
        this.myListModel = new MyListModel<>(generatorConfigList);
        initToolBar();
        initTableList();
        multiProjectState = MultipleMybatisStateComponent.getInstance(project).getState();
        simpleProjectState = MybatisStateComponent.getInstance(project).getState();
        initTabs();
    }

    private void initToolBar() {
        decorator = ToolbarDecorator.createDecorator(myList)
                .disableUpAction()
                .disableDownAction()
                .disableAddAction()
                .setRemoveAction(anActionButton -> {
                    List<GeneratorConfig> selectedValuesList = myList.getSelectedValuesList();
                    if (!selectedValuesList.isEmpty()) {
                        if (generatorConfigList.size() == selectedValuesList.size()) {
                            Messages.showInfoMessage(project, MybatisSmartCodeHelpBundle.message("multiple.delete.table.error"), MybatisSmartCodeHelpBundle.message("multiple.delete.table.title"));
                            return;
                        }
                        doValidate();
                        if (Messages.showOkCancelDialog(project, MybatisSmartCodeHelpBundle.message("multiple.delete.table.confirm.title", confirmMsg(selectedValuesList)), MybatisSmartCodeHelpBundle.message("multiple.delete.table.title"), Messages.getOkButton(), Messages.getCancelButton(), Messages.getQuestionIcon()) == Messages.OK) {
                            generatorConfigList.removeAll(selectedValuesList);
                        }
                        myList.setSelectedIndex(0);
                        if (generatorConfigList.size() == 1) {
                            anActionButton.setEnabled(false);
                        }
                        myList.updateUI();
                    }
                });
        if (generatorConfigList == null || generatorConfigList.isEmpty() || generatorConfigList.size() == 1) {
            decorator.disableRemoveAction();
        }
        leftJPanel.add(decorator.createPanel(), new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    }

    private String confirmMsg(List<GeneratorConfig> generatorConfigList) {
        if (generatorConfigList == null || generatorConfigList.isEmpty()) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("tables:[");
        for (GeneratorConfig generatorConfig : generatorConfigList) {
            stringBuilder.append(generatorConfig.getTableName());
            stringBuilder.append(",");
        }
        stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    private void initTableList() {
        myList.setModel(myListModel);
        myList.setSelectedIndex(myListModel.getIndex(selectGeneratorConfig));
        myList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                changeTab();
            }
        });
    }

    private void initTabs() {
        generatorConfigPanelTabInfo = initGeneratorConfigPanel();
        stepTwoPanelTabInfo = initStepTwoPanel();
        configPanelTabInfo = initConfigPanel();
        tabs.addTab(generatorConfigPanelTabInfo);
        tabs.addTab(stepTwoPanelTabInfo);
        tabs.addTab(configPanelTabInfo);
        rightJPanel.add(tabs, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        tabs.addListener(new TabsListener() {
            @Override
            public void selectionChanged(TabInfo oldSelection, TabInfo newSelection) {
                if (oldSelection.equals(generatorConfigPanelTabInfo)) {
                    generatorConfigPanel.initProjectState(getProjectState(selectGeneratorConfig.getTableName()));
                    generatorConfigPanel.setConfig(getProjectState(selectGeneratorConfig.getTableName()), selectGeneratorConfig);
                } else if (oldSelection.equals(stepTwoPanelTabInfo)) {
                    stepTwoPanel.initProjectState(getProjectState(selectGeneratorConfig.getTableName()));
                    stepTwoPanel.setConfig(getProjectState(selectGeneratorConfig.getTableName()), selectGeneratorConfig);
                } else if (oldSelection.equals(configPanelTabInfo)) {
                    configPanel.initProjectState(getProjectState(selectGeneratorConfig.getTableName()));
                    configPanel.setConfig(getProjectState(selectGeneratorConfig.getTableName()), selectGeneratorConfig);
                }
                if (newSelection.equals(generatorConfigPanelTabInfo)) {
                    generatorConfigPanel.setConfig(getProjectState(selectGeneratorConfig.getTableName()), selectGeneratorConfig);
                } else if (newSelection.equals(stepTwoPanelTabInfo)) {
                    stepTwoPanel.setConfig(getProjectState(selectGeneratorConfig.getTableName()), selectGeneratorConfig);
                } else if (newSelection.equals(configPanelTabInfo)) {
                    configPanel.setConfig(getProjectState(selectGeneratorConfig.getTableName()), selectGeneratorConfig);
                }
            }
        });
    }

    private TabInfo initGeneratorConfigPanel() {
        generatorConfigPanel = new GeneratorConfigPanel(project, getProjectState(selectGeneratorConfig.getTableName()), selectGeneratorConfig);
        TabInfo tabInfo = new TabInfo(generatorConfigPanel.$$$getRootComponent$$$());
        tabInfo.setText(MybatisSmartCodeHelpBundle.message("base.info.panel.title"));
        return tabInfo;
    }

    private TabInfo initStepTwoPanel() {
        stepTwoPanel = new StepTwoPanel(project, getProjectState(selectGeneratorConfig.getTableName()), selectGeneratorConfig);
        TabInfo tabInfo = new TabInfo(stepTwoPanel.$$$getRootComponent$$$());
        tabInfo.setText(MybatisSmartCodeHelpBundle.message("step.two.panel.title"));
        return tabInfo;
    }

    private TabInfo initConfigPanel() {
        configPanel = new ConfigPanel(project, getProjectState(selectGeneratorConfig.getTableName()), selectGeneratorConfig);
        TabInfo tabInfo = new TabInfo(configPanel.$$$getRootComponent$$$());
        tabInfo.setText(MybatisSmartCodeHelpBundle.message("config.panel.title"));
        return tabInfo;
    }


    public ProjectState getProjectState(String name) {

        if (StringUtils.isBlank(name)) {
            return initProjectState(name);
        }
        if (multiProjectState.getProjectStates().isEmpty()) {
            return initProjectState(name);
        }
        if (multiProjectState.getProjectStates().containsKey(name)) {
            return multiProjectState.getProjectStates().get(name);
        }
        return initProjectState(name);
    }

    public List<GeneratorConfig> getGeneratorConfigList() {
        return generatorConfigList;
    }

    public MultiProjectState getMultiProjectState() {
        return multiProjectState;
    }

    @NotNull
    private ProjectState initProjectState(String name) {
        ProjectState projectState = getState();
        copy(projectState);
        projectState.setTableName(name);
        multiProjectState.getProjectStates().put(name, projectState);
        return projectState;
    }

    @NotNull
    private ProjectState getState() {
        ProjectState projectState = new ProjectState();
        projectState.setJavaModules(new ArrayList<>());
        projectState.setPrimaryKeys(new ArrayList<>());
        if (projectState.getJavaModules() == null || projectState.getJavaModules().isEmpty()) {
            projectState.setJavaModules(new ArrayList<>());
        }
        if (projectState.getPrimaryKeys() == null || projectState.getPrimaryKeys().isEmpty()) {
            projectState.setPrimaryKeys(new ArrayList<>());
        }
        return projectState;
    }


    private void copy(@NotNull ProjectState projectState) {
        if (simpleProjectState == null) {
            return;
        }
        projectState.setRmPrefix(simpleProjectState.getRmPrefix());
        projectState.setModelName(simpleProjectState.getModelName());
        projectState.setTableName(simpleProjectState.getTableName());
        projectState.setSchema(simpleProjectState.getSchema());
        projectState.setJavaModelPackage(simpleProjectState.getJavaModelPackage());
        projectState.setJavaModelSourcePackage(simpleProjectState.getJavaModelSourcePackage());

        projectState.setJavaMapperPackage(simpleProjectState.getJavaMapperPackage());
        projectState.setJavaMapperSourcePackage(simpleProjectState.getJavaMapperSourcePackage());

        projectState.setJavaXmlPackage(simpleProjectState.getJavaXmlPackage());
        projectState.setJavaXmlSourcePackage(simpleProjectState.getJavaXmlSourcePackage());

        //service impl
        projectState.setGeneratorService(simpleProjectState.isGeneratorService());
        projectState.setJavaServicePackage(simpleProjectState.getJavaServicePackage());
        projectState.setJavaServiceSourcePackage(simpleProjectState.getJavaServiceSourcePackage());

        //service interface
        projectState.setGeneratorServiceInterface(simpleProjectState.isGeneratorServiceInterface());
        projectState.setJavaServiceInterfacePackage(simpleProjectState.getJavaServiceInterfacePackage());
        projectState.setJavaServiceInterfaceSourcePackage(simpleProjectState.getJavaServiceInterfaceSourcePackage());
        projectState.setServiceMode(simpleProjectState.getServiceMode());

        projectState.setUseActualColumnNames(simpleProjectState.isUseActualColumnNames());
        projectState.setTablePropertiesMap(simpleProjectState.getTablePropertiesMap());
        projectState.setUserDbCheckBox(simpleProjectState.isUserDbCheckBox());
        projectState.setUseActualColumnNamesCheckBox(simpleProjectState.isUseActualColumnNamesCheckBox());


        //config
        //lombok
        projectState.setLombokData(simpleProjectState.isLombokData());
        projectState.setLombokGetterSetter(simpleProjectState.isLombokGetterSetter());
        projectState.setLombokBuilder(simpleProjectState.isLombokBuilder());
        projectState.setLombokNoArgsConstructor(simpleProjectState.isLombokNoArgsConstructor());
        projectState.setLombokAllArgsConstructor(simpleProjectState.isLombokAllArgsConstructor());
        //config
        projectState.setToString(simpleProjectState.isToString());
        projectState.setEqualHashCode(simpleProjectState.isEqualHashCode());
        projectState.setNoJdbcType(simpleProjectState.isNoJdbcType());
        projectState.setAnnotationMapper(simpleProjectState.isAnnotationMapper());
        projectState.setModelStringTrim(simpleProjectState.isModelStringTrim());
        projectState.setComment(simpleProjectState.isComment());
        projectState.setSerializable(simpleProjectState.isSerializable());
        projectState.setGeneratorExample(simpleProjectState.isGeneratorExample());
        projectState.setSwagger(simpleProjectState.isSwagger());
        projectState.setCheckBlob(simpleProjectState.isCheckBlob());
        projectState.setAddSchema(simpleProjectState.isAddSchema());
        projectState.setInterfaceComment(simpleProjectState.isInterfaceComment());
    }

    public boolean doValidate() {
        if (generatorConfigList == null || generatorConfigList.isEmpty()) {
            return false;
        }
        //初始化下当前的配置
        generatorConfigPanel.initProjectState(getProjectState(selectGeneratorConfig.getTableName()));
        stepTwoPanel.initProjectState(getProjectState(selectGeneratorConfig.getTableName()));
        configPanel.initProjectState(getProjectState(selectGeneratorConfig.getTableName()));
        boolean flag = true;
        for (GeneratorConfig generatorConfig : generatorConfigList) {
            generatorConfigPanel.setConfig(getProjectState(generatorConfig.getTableName()), generatorConfig);
            stepTwoPanel.setConfig(getProjectState(generatorConfig.getTableName()), generatorConfig);
            configPanel.setConfig(getProjectState(generatorConfig.getTableName()), generatorConfig);
            if (!generatorConfigPanel.doValidate(getProjectState(generatorConfig.getTableName()))) {
                String html = "<html><u style='color:red;'>" + generatorConfigPanel.getGeneratorConfig().getDisplayName() + "</u></html>";
                generatorConfigPanel.getGeneratorConfig().setDisplayName(html);
                generatorConfigPanel.getGeneratorConfig().setVerify(false);
                myList.setToolTipText(MybatisSmartCodeHelpBundle.message("multiple.my.list.base.info.error.tip"));
            } else {
                generatorConfigPanel.getGeneratorConfig().setVerify(true);
                generatorConfigPanel.getGeneratorConfig().setDisplayName(generatorConfigPanel.getGeneratorConfig().getTableName());
            }

            if (!configPanel.doValidate(getProjectState(generatorConfig.getTableName()))) {
                String html = "<html><u style='color:red;'>" + configPanel.getGeneratorConfig().getDisplayName() + "</u></html>";
                generatorConfigPanel.getGeneratorConfig().setDisplayName(html);
                generatorConfigPanel.getGeneratorConfig().setVerify(false);
                myList.setToolTipText(MybatisSmartCodeHelpBundle.message("multiple.my.list.config.error.tip"));
            } else {
                if (configPanel.getGeneratorConfig().isVerify()) {
                    configPanel.getGeneratorConfig().setVerify(true);
                    configPanel.getGeneratorConfig().setDisplayName(configPanel.getGeneratorConfig().getTableName());
                }
            }
            if (!generatorConfig.isVerify()) {
                flag = false;
            }
            generatorConfigPanel.initProjectState(getProjectState(generatorConfig.getTableName()));
            stepTwoPanel.initProjectState(getProjectState(selectGeneratorConfig.getTableName()));
            configPanel.initProjectState(getProjectState(selectGeneratorConfig.getTableName()));
        }
        return flag;
    }


    private void changeTab() {
        if (!generatorConfigPanel.doValidate()) {
            String html = "<html><u style='color:red;'>" + generatorConfigPanel.getGeneratorConfig().getDisplayName() + "</u></html>";
            generatorConfigPanel.getGeneratorConfig().setDisplayName(html);
            generatorConfigPanel.getGeneratorConfig().setVerify(false);
            myList.setToolTipText(MybatisSmartCodeHelpBundle.message("multiple.my.list.base.info.error.tip"));
        } else {
            generatorConfigPanel.getGeneratorConfig().setVerify(true);
            generatorConfigPanel.getGeneratorConfig().setDisplayName(generatorConfigPanel.getGeneratorConfig().getTableName());
        }
        if (!configPanel.doValidate()) {
            String html = "<html><u style='color:red;'>" + configPanel.getGeneratorConfig().getDisplayName() + "</u></html>";
            configPanel.getGeneratorConfig().setDisplayName(html);
            configPanel.getGeneratorConfig().setVerify(false);
            myList.setToolTipText(MybatisSmartCodeHelpBundle.message("multiple.my.list.config.error.tip"));
        } else {
            if (configPanel.getGeneratorConfig().isVerify()) {
                configPanel.getGeneratorConfig().setVerify(true);
                configPanel.getGeneratorConfig().setDisplayName(configPanel.getGeneratorConfig().getTableName());
            }
        }

        generatorConfigPanel.initProjectState(getProjectState(selectGeneratorConfig.getTableName()));
        stepTwoPanel.initProjectState(getProjectState(selectGeneratorConfig.getTableName()));
        configPanel.initProjectState(getProjectState(selectGeneratorConfig.getTableName()));
        selectGeneratorConfig = myList.getSelectedValue();
        generatorConfigPanel.setConfig(getProjectState(selectGeneratorConfig.getTableName()), selectGeneratorConfig);
        stepTwoPanel.setConfig(getProjectState(selectGeneratorConfig.getTableName()), selectGeneratorConfig);
        configPanel.setConfig(getProjectState(selectGeneratorConfig.getTableName()), selectGeneratorConfig);

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
        root.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        jSplitPane = new JSplitPane();
        jSplitPane.setDividerLocation(154);
        jSplitPane.setDividerSize(2);
        root.add(jSplitPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        leftJPanel = new JPanel();
        leftJPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        leftJPanel.setMinimumSize(new Dimension(150, 28));
        jSplitPane.setLeftComponent(leftJPanel);
        leftJPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        rightJPanel = new JPanel();
        rightJPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        rightJPanel.setMinimumSize(new Dimension(720, 640));
        rightJPanel.setPreferredSize(new Dimension(710, 640));
        jSplitPane.setRightComponent(rightJPanel);
        rightJPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
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
