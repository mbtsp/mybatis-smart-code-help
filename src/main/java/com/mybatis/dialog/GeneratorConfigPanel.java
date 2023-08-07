package com.mybatis.dialog;

import com.intellij.icons.AllIcons;
import com.intellij.ide.HelpTooltip;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.ui.ComponentValidator;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.TextFieldWithAutoCompletion;
import com.intellij.ui.components.JBTextField;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.mybatis.enums.ServiceMode;
import com.mybatis.messages.MybatisSmartCodeHelpBundle;
import com.mybatis.model.GeneratorConfig;
import com.mybatis.state.ProjectState;
import com.mybatis.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;
import java.util.*;

public class GeneratorConfigPanel implements Disposable {
    private final Project project;
    private final TextFieldWithAutoCompletion<String> rmPrefixJText;
    private final MyPackageNameReferenceEditorCombo javaModelCombo;
    private final MyTextFieldWithAutoCompletionWithBrowserButton javaSourceModelButton;
    private final MyPackageNameReferenceEditorCombo javaMapperCombo;
    private final MyTextFieldWithAutoCompletionWithBrowserButton javaMapperSourceButton;
    private final MyPackageNameReferenceEditorCombo javaMapperXmlCombo;
    private final MyTextFieldWithAutoCompletionWithBrowserButton javaMapperXmlSourceButton;
    private final MyPackageNameReferenceEditorCombo javaServiceCombo;
    private final MyTextFieldWithAutoCompletionWithBrowserButton javaServiceSourceButton;
    private final MyPackageNameReferenceEditorCombo javaServiceInterfaceCombo;
    private final MyTextFieldWithAutoCompletionWithBrowserButton javaServiceInterfaceSourceButton;
    private final MyStringsCompletionProvider prefixCompletionProvider;
    private final Map<String, Module> moduleMap;
    private final JCheckBoxItem easyMode;
    private final JCheckBoxItem fullMode;
    private final JCheckBoxItem pureMode;
    private ProjectState projectState;
    private JPanel javaModelPanel;
    private JPanel javaModelSourcePanel;
    private JPanel javaMapperPanel;
    private JPanel javaMapperSourcePanel;
    private JPanel javaMapperXmlPanel;
    private JPanel javaXmlResourcesPanel;
    private JPanel root;
    private JCheckBox generatorService;
    private JBTextField modelName;
    private JPanel servicePanel;
    private JPanel javaServicePackagePanel;
    private JPanel javaServiceSrcFolderPanel;
    private JPanel javaServiceInterfacePackagePanel;
    private JPanel javaServiceInterfaceSrcFolderPanel;
    private JPanel rmPrefixPanel;
    private JComboBox<String> javaModules;
    private JComboBox<JCheckBoxItem> serviceMode;
    private JLabel javaModelLabel;
    private JLabel javaModelSourceLabel;
    private JLabel javaMapperLabel;
    private JLabel javaMapperSourceLabel;
    private JLabel javaMapperXmlLabel;
    private JLabel javaMapperXmlSourceLabel;
    private JLabel serviceModelLabel;
    private JLabel javaServiceInterfaceLabel;
    private JLabel javaServiceInterfaceSourceLabel;
    private JLabel javaServiceLabel;
    private JLabel javaServiceSourceLabel;
    private JLabel modelNameLabel;
    private JLabel rmFixLabel;
    private Module myModule;
    private GeneratorConfig generatorConfig;

    public GeneratorConfigPanel(Project project, ProjectState projectState, GeneratorConfig generatorConfig) {
        this.generatorConfig = generatorConfig;
        this.project = project;
        this.projectState = projectState;
        $$$setupUI$$$();
        moduleMap = new HashMap<>();
        Module[] modules = ModuleManager.getInstance(project).getModules();
        String javaBasePath = null;
        String resourceBasePath = null;
        if (modules.length > 0) {
            for (Module module : modules) {
                moduleMap.put(module.getName(), module);
                javaModules.addItem(module.getName());
            }
            myModule = modules[0];
            Project project1 = myModule.getModuleScope().getProject();
            if (project1 != null) {
                javaBasePath = project1.getBasePath() + "/src/main/java";
                resourceBasePath = project1.getBasePath() + "/src/main/resources";
            }

        }
        List<String> items = new ArrayList<>();
        buildPrefixList(items);
        prefixCompletionProvider = new MyStringsCompletionProvider(items);
        rmPrefixJText = new TextFieldWithAutoCompletion<>(project, prefixCompletionProvider, true, projectState.getRmPrefix());
        rmPrefixJText.setToolTipText(MybatisSmartCodeHelpBundle.message("rm.prefix.tip.text"));
        FileChooserDescriptor fileChooserDescriptor = getFileChooserDescriptor();
        this.javaModelCombo = new MyPackageNameReferenceEditorCombo(projectState.getJavaModelPackage(), project, myModule, "mybatisJavaModelRecent", "Choose Java Model Package for Generate Mybatis Model");
        this.javaSourceModelButton = new MyTextFieldWithAutoCompletionWithBrowserButton(project);
        this.javaSourceModelButton.addBrowseFolderListener("Choose Src Folder for Java Model", "Choose some", this.project, fileChooserDescriptor, new TextFieldWithAutoCompletionBrowserTextAccessor());
        this.javaSourceModelButton.setText(javaBasePath);

        this.javaMapperCombo = new MyPackageNameReferenceEditorCombo(projectState.getJavaMapperPackage(), project, myModule, "mybatisJavaMapperRecent", "Choose Java Mapper Package for Generate Mybatis Model");
        this.javaMapperSourceButton = new MyTextFieldWithAutoCompletionWithBrowserButton(project);
        this.javaMapperSourceButton.addBrowseFolderListener("Choose Src Folder for Mapper", "Choose some", this.project, fileChooserDescriptor, new TextFieldWithAutoCompletionBrowserTextAccessor());
        this.javaMapperSourceButton.setText(javaBasePath);
        this.javaMapperXmlCombo = new MyPackageNameReferenceEditorCombo(projectState.getJavaXmlPackage(), project, myModule, "mybatisJavaXmlRecent", "Choose Java Xml Package For Generate Mybatis Model", true);
        this.javaMapperXmlSourceButton = new MyTextFieldWithAutoCompletionWithBrowserButton(project);
        this.javaMapperXmlSourceButton.addBrowseFolderListener("Choose Src Folder for Xml", "Choose some", this.project, fileChooserDescriptor, new TextFieldWithAutoCompletionBrowserTextAccessor());
        this.javaMapperXmlSourceButton.setText(resourceBasePath);
        this.javaServiceCombo = new MyPackageNameReferenceEditorCombo(projectState.getJavaServicePackage(), project, myModule, "mybatisJavaServiceRecent", "Choose Java Service Model Package for Generate Mybatis Model");
        this.javaServiceSourceButton = new MyTextFieldWithAutoCompletionWithBrowserButton(project);
        this.javaServiceSourceButton.addBrowseFolderListener("Choose Src Folder for Service", "Choose some", this.project, fileChooserDescriptor, new TextFieldWithAutoCompletionBrowserTextAccessor());
        this.javaServiceSourceButton.setText(javaBasePath);
        this.javaServiceInterfaceCombo = new MyPackageNameReferenceEditorCombo(projectState.getJavaServiceInterfacePackage(), project, myModule, "mybatisJavaServiceInterfaceRecent", "Choose Java Service Interface Model Package for Generate Mybatis Model");
        this.javaServiceInterfaceSourceButton = new MyTextFieldWithAutoCompletionWithBrowserButton(project);
        this.javaServiceInterfaceSourceButton.addBrowseFolderListener("Choose Src Folder for Service Interface", "Choose some", this.project, fileChooserDescriptor, new TextFieldWithAutoCompletionBrowserTextAccessor());
        this.javaServiceInterfaceSourceButton.setText(javaBasePath);
        this.rmPrefixJText.addDocumentListener(new DocumentListener() {
            /**
             * Called after the text of the document has been changed.
             *
             * @param event the event containing the information about the change.
             */
            @Override
            public void documentChanged(@NotNull DocumentEvent event) {
                String rmPrefix = rmPrefixJText.getText();
                String value = rmPrefix(generatorConfig.getTableName(), rmPrefix);
                modelName.setText(value);
            }
        });
        javaModules.addActionListener(e -> {
            String name = (String) javaModules.getSelectedItem();
            myModule = moduleMap.get(name);
            this.javaMapperXmlCombo.setMyModule(myModule);
            this.javaModelCombo.setMyModule(myModule);
            this.javaMapperCombo.setMyModule(myModule);
            this.javaServiceCombo.setMyModule(myModule);
            this.javaServiceInterfaceCombo.setMyModule(myModule);
        });
        serviceMode.setToolTipText("1." + ServiceMode.EASY.getDesc() + "\n 2." + ServiceMode.FULL.getDesc() + "\n 3." + ServiceMode.PURE.getDesc());
        easyMode = new JCheckBoxItem();
        easyMode.setKey(ServiceMode.EASY.getKey());
        easyMode.setValue(ServiceMode.EASY.getValue());
        serviceMode.addItem(easyMode);

        fullMode = new JCheckBoxItem();
        fullMode.setKey(ServiceMode.FULL.getKey());
        fullMode.setValue(ServiceMode.FULL.getValue());
        serviceMode.addItem(fullMode);

        pureMode = new JCheckBoxItem();
        pureMode.setKey(ServiceMode.PURE.getKey());
        pureMode.setValue(ServiceMode.PURE.getValue());
        serviceMode.addItem(pureMode);
        serviceMode.setSelectedItem(easyMode);
        this.generatorService.setSelected(false);
        this.javaServiceCombo.setEnabled(false);
        this.javaServiceSourceButton.setEnabled(false);
        this.javaServiceInterfaceCombo.setEnabled(false);
        this.javaServiceInterfaceSourceButton.setEnabled(false);
        initHelp();
        initBuildComponents();
        initValidator();
        initLanguage();

    }

    private void initHelp() {
        this.javaModelLabel.setIcon(AllIcons.General.ContextHelp);
        new HelpTooltip().setDescription(MybatisSmartCodeHelpBundle.message("java.model.help.label.tip.text")).installOn(this.javaModelLabel);
        this.javaModelSourceLabel.setIcon(AllIcons.General.ContextHelp);
        new HelpTooltip().setDescription(MybatisSmartCodeHelpBundle.message("java.model.source.label.tip.text")).installOn(this.javaModelSourceLabel);
        this.javaMapperLabel.setIcon(AllIcons.General.ContextHelp);
        new HelpTooltip().setDescription(MybatisSmartCodeHelpBundle.message("java.mapper.label.tip.text")).installOn(this.javaMapperLabel);
        this.javaMapperSourceLabel.setIcon(AllIcons.General.ContextHelp);
        new HelpTooltip().setDescription(MybatisSmartCodeHelpBundle.message("java.mapper.label.source.tip.text")).installOn(this.javaMapperSourceLabel);
        this.javaMapperXmlLabel.setIcon(AllIcons.General.ContextHelp);
        new HelpTooltip().setDescription(MybatisSmartCodeHelpBundle.message("java.mapper.xml.label.tip.text")).installOn(this.javaMapperXmlLabel);
        this.javaMapperXmlSourceLabel.setIcon(AllIcons.General.ContextHelp);
        new HelpTooltip().setDescription(MybatisSmartCodeHelpBundle.message("java.mapper.source.label.tip.text")).installOn(this.javaMapperXmlSourceLabel);
        this.javaServiceLabel.setIcon(AllIcons.General.ContextHelp);
        new HelpTooltip().setDescription(MybatisSmartCodeHelpBundle.message("java.service.label.tip.text")).installOn(this.javaServiceLabel);
        this.javaServiceSourceLabel.setIcon(AllIcons.General.ContextHelp);
        new HelpTooltip().setDescription(MybatisSmartCodeHelpBundle.message("java.service.source.label.tip.text")).installOn(this.javaServiceSourceLabel);
        this.javaServiceInterfaceLabel.setIcon(AllIcons.General.ContextHelp);
        new HelpTooltip().setDescription(MybatisSmartCodeHelpBundle.message("java.service.interface.label.tip.text")).installOn(this.javaServiceInterfaceLabel);
        this.javaServiceInterfaceSourceLabel.setIcon(AllIcons.General.ContextHelp);
        new HelpTooltip().setDescription(MybatisSmartCodeHelpBundle.message("java.service.interface.source.label.tip.text")).installOn(this.javaServiceInterfaceSourceLabel);
    }

    private void initBuildComponents() {
        this.modelName.setText(rmPrefix(generatorConfig.getTableName(), projectState.getRmPrefix()));
        GridConstraints gridConstraints = new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false);
        this.rmPrefixPanel.add(this.rmPrefixJText, gridConstraints);
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

        //init label
//        GridConstraints labelGridConstraints = new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false);
//        javaModelHelpPanel.add(this.javaModelHelpLabel, labelGridConstraints);


        initProjectStateValue();
    }


    public void initLanguage() {
        modelNameLabel.setText(MybatisSmartCodeHelpBundle.message("model.label.text"));
        rmFixLabel.setText(MybatisSmartCodeHelpBundle.message("rm.prefix.text"));
        javaModelLabel.setText(MybatisSmartCodeHelpBundle.message("java.model.label.text"));
        javaModelSourceLabel.setText(MybatisSmartCodeHelpBundle.message("java.model.source.label.text"));
        javaMapperLabel.setText(MybatisSmartCodeHelpBundle.message("java.mapper.label.text"));
        javaMapperSourceLabel.setText(MybatisSmartCodeHelpBundle.message("java.mapper.source.label.text"));
        javaMapperXmlLabel.setText(MybatisSmartCodeHelpBundle.message("java.mapper.xml.label.text"));
        javaMapperXmlSourceLabel.setText(MybatisSmartCodeHelpBundle.message("java.mapper.xml.source.label.text"));
        generatorService.setText(MybatisSmartCodeHelpBundle.message("generator.service.text"));
        serviceModelLabel.setText(MybatisSmartCodeHelpBundle.message("service.model.label.text"));
        javaServiceInterfaceLabel.setText(MybatisSmartCodeHelpBundle.message("java.service.interface.label.text"));
        javaServiceInterfaceSourceLabel.setText(MybatisSmartCodeHelpBundle.message("java.service.interface.source.label.text"));
        javaServiceLabel.setText(MybatisSmartCodeHelpBundle.message("java.service.label.text"));
        javaServiceSourceLabel.setText(MybatisSmartCodeHelpBundle.message("java.service.source.label.text"));
    }


    public void setConfig(ProjectState projectState, GeneratorConfig generatorConfig) {
        this.generatorConfig = generatorConfig;
        this.projectState = projectState;
        List<String> items = new ArrayList<>();
        buildPrefixList(items);
        prefixCompletionProvider.setItems(items);
        rmPrefixJText.setText(projectState.getRmPrefix());
        if (StringUtils.isNotBlank(projectState.getJavaModelPackage())) {
            this.javaModelCombo.setText(projectState.getJavaModelPackage());
        }
        if (StringUtils.isNotBlank(projectState.getJavaMapperPackage())) {
            this.javaMapperCombo.setText(projectState.getJavaMapperPackage());
        }
        if (StringUtils.isNotBlank(projectState.getJavaXmlPackage())) {
            this.javaMapperXmlCombo.setText(projectState.getJavaXmlPackage());
        }
        if (StringUtils.isNotBlank(projectState.getJavaServicePackage())) {
            this.javaServiceCombo.setText(projectState.getJavaServicePackage());
        }
        if (StringUtils.isNotBlank(projectState.getJavaServiceInterfacePackage())) {
            this.javaServiceInterfaceCombo.setText(projectState.getJavaServiceInterfacePackage());
        }
        this.modelName.setText(rmPrefix(generatorConfig.getTableName(), projectState.getRmPrefix()));
        initProjectStateValue();
    }


    private void initValidator() {
        new ComponentValidator(this).withValidator(() -> StringUtils.isBlank(this.modelName.getText()) ? new ValidationInfo("Model Name cannot be empty", this.modelName) : null).
                andStartOnFocusLost().
                andRegisterOnDocumentListener(this.modelName).
                installOn(this.modelName);

        new ComponentValidator(this).withValidator(() -> StringUtils.isBlank(this.javaModelCombo.getText()) ? new ValidationInfo("Choose java model package for generate mybatis model,eg:com.example.model", this.javaModelCombo) : null)
                .withOutlineProvider(ComponentValidator.CWBB_PROVIDER).installOn(this.javaModelCombo);
        this.javaModelCombo.getChildComponent().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(@NotNull DocumentEvent event) {
                ComponentValidator.getInstance(javaModelCombo).ifPresent(ComponentValidator::revalidate);
            }
        });
        new ComponentValidator(this).withValidator(() -> StringUtils.isBlank(this.javaSourceModelButton.getText()) ? new ValidationInfo("Please choose src folder for java model,eg:D:\\workspace\\demo\\src\\main\\java", this.javaSourceModelButton) : null)
                .withOutlineProvider(ComponentValidator.CWBB_PROVIDER).installOn(this.javaSourceModelButton);
        this.javaSourceModelButton.getChildComponent().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(@NotNull DocumentEvent event) {
                ComponentValidator.getInstance(javaSourceModelButton).ifPresent(ComponentValidator::revalidate);
            }
        });

        new ComponentValidator(this).withValidator(() -> StringUtils.isBlank(this.javaMapperCombo.getText()) ? new ValidationInfo("Choose java mapper package for generate mybatis model,eg:com.example.mapper", this.javaMapperCombo) : null)
                .withOutlineProvider(ComponentValidator.CWBB_PROVIDER).installOn(this.javaMapperCombo);
        this.javaMapperCombo.getChildComponent().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(@NotNull DocumentEvent event) {
                ComponentValidator.getInstance(javaMapperCombo).ifPresent(ComponentValidator::revalidate);
            }
        });
        new ComponentValidator(this).withValidator(() -> StringUtils.isBlank(this.javaMapperSourceButton.getText()) ? new ValidationInfo("Please Choose src folder for xml,eg:D:\\workspace\\demo\\\\src\\main\\java", this.javaMapperSourceButton) : null)
                .withOutlineProvider(ComponentValidator.CWBB_PROVIDER).installOn(this.javaMapperSourceButton);
        this.javaMapperSourceButton.getChildComponent().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(@NotNull DocumentEvent event) {
                ComponentValidator.getInstance(javaMapperSourceButton).ifPresent(ComponentValidator::revalidate);
            }
        });
        //mapper xml
        new ComponentValidator(this).withValidator(() -> StringUtils.isBlank(this.javaMapperXmlCombo.getText()) ? new ValidationInfo("Choose java xml package For generate mybatis model,eg:mapperxml", this.javaMapperXmlCombo) : null)
                .withOutlineProvider(ComponentValidator.CWBB_PROVIDER).installOn(this.javaMapperXmlCombo);
        this.javaMapperXmlCombo.getChildComponent().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(@NotNull DocumentEvent event) {
                ComponentValidator.getInstance(javaMapperXmlCombo).ifPresent(ComponentValidator::revalidate);
            }
        });
        new ComponentValidator(this).withValidator(() -> StringUtils.isBlank(this.javaMapperXmlSourceButton.getText()) ? new ValidationInfo("Please choose src folder for xml,eg:D:\\workspace\\demo\\src\\main\\resources", this.javaMapperXmlSourceButton) : null)
                .withOutlineProvider(ComponentValidator.CWBB_PROVIDER).installOn(this.javaMapperXmlSourceButton);
        this.javaMapperXmlSourceButton.getChildComponent().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(@NotNull DocumentEvent event) {
                ComponentValidator.getInstance(javaMapperXmlSourceButton).ifPresent(ComponentValidator::revalidate);
            }
        });
        //service
        new ComponentValidator(this).withValidator(() -> StringUtils.isBlank(this.javaServiceCombo.getText()) && this.generatorService.isSelected() ? new ValidationInfo("Choose java service model package for generate mybatis model,eg:com.example.service.impl", this.javaServiceCombo) : null)
                .withOutlineProvider(ComponentValidator.CWBB_PROVIDER).installOn(this.javaServiceCombo);
        this.javaServiceCombo.getChildComponent().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(@NotNull DocumentEvent event) {
                ComponentValidator.getInstance(javaServiceCombo).ifPresent(ComponentValidator::revalidate);
            }
        });
        new ComponentValidator(this).withValidator(() -> StringUtils.isBlank(this.javaServiceSourceButton.getText()) && this.generatorService.isSelected() ? new ValidationInfo("Please choose src folder for service,eg:D:\\workspace\\demo\\src\\main\\java", this.javaServiceSourceButton) : null)
                .withOutlineProvider(ComponentValidator.CWBB_PROVIDER).installOn(this.javaServiceSourceButton);
        this.javaServiceSourceButton.getChildComponent().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(@NotNull DocumentEvent event) {
                ComponentValidator.getInstance(javaServiceSourceButton).ifPresent(ComponentValidator::revalidate);
            }
        });
        //service interface
        new ComponentValidator(this).withValidator(() -> StringUtils.isBlank(this.javaServiceInterfaceCombo.getText()) && this.generatorService.isSelected() ? new ValidationInfo("Choose java service interface model package for generate mybatis model,eg:com.example.service", this.javaServiceInterfaceCombo) : null)
                .withOutlineProvider(ComponentValidator.CWBB_PROVIDER).installOn(this.javaServiceInterfaceCombo);
        this.javaServiceInterfaceCombo.getChildComponent().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(@NotNull DocumentEvent event) {
                ComponentValidator.getInstance(javaServiceInterfaceCombo).ifPresent(ComponentValidator::revalidate);
            }
        });
        new ComponentValidator(this).withValidator(() -> StringUtils.isBlank(this.javaServiceInterfaceSourceButton.getText()) && this.generatorService.isSelected() ? new ValidationInfo("Please choose src folder for service interface,eg:D:\\workspace\\demo\\src\\main\\java", this.javaServiceInterfaceSourceButton) : null)
                .withOutlineProvider(ComponentValidator.CWBB_PROVIDER).installOn(this.javaServiceInterfaceSourceButton);
        this.javaServiceInterfaceSourceButton.getChildComponent().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(@NotNull DocumentEvent event) {
                ComponentValidator.getInstance(javaServiceInterfaceSourceButton).ifPresent(ComponentValidator::revalidate);
            }
        });
    }

    public boolean doValidate() {
        boolean flag = true;
        if (StringUtils.isBlank(this.modelName.getText())) {
            ComponentValidator.getInstance(this.modelName).ifPresent(ComponentValidator::revalidate);
            flag = false;
        }
        if (StringUtils.isBlank(this.javaModelCombo.getText())) {
            ComponentValidator.getInstance(this.javaModelCombo).ifPresent(ComponentValidator::revalidate);
            flag = false;
        }
        if (StringUtils.isBlank(this.javaSourceModelButton.getText())) {
            ComponentValidator.getInstance(this.javaSourceModelButton).ifPresent(ComponentValidator::revalidate);
            flag = false;
        }
        if (StringUtils.isBlank(this.javaMapperCombo.getText())) {
            ComponentValidator.getInstance(this.javaMapperCombo).ifPresent(ComponentValidator::revalidate);
            flag = false;
        }
        if (StringUtils.isBlank(this.javaMapperSourceButton.getText())) {
            ComponentValidator.getInstance(this.javaMapperSourceButton).ifPresent(ComponentValidator::revalidate);
            flag = false;
        }
        if (StringUtils.isBlank(this.javaMapperXmlCombo.getText())) {
            ComponentValidator.getInstance(this.javaMapperXmlCombo).ifPresent(ComponentValidator::revalidate);
            flag = false;
        }
        if (StringUtils.isBlank(this.javaMapperXmlSourceButton.getText())) {
            ComponentValidator.getInstance(this.javaMapperXmlSourceButton).ifPresent(ComponentValidator::revalidate);
            flag = false;
        }
        if (this.generatorService.isSelected()) {
            if (StringUtils.isBlank(this.javaServiceCombo.getText())) {
                ComponentValidator.getInstance(this.javaServiceCombo).ifPresent(ComponentValidator::revalidate);
                flag = false;
            }
            if (StringUtils.isBlank(this.javaServiceSourceButton.getText())) {
                ComponentValidator.getInstance(this.javaServiceSourceButton).ifPresent(ComponentValidator::revalidate);
                flag = false;
            }
            if (StringUtils.isBlank(this.javaServiceInterfaceCombo.getText())) {
                ComponentValidator.getInstance(this.javaServiceInterfaceCombo).ifPresent(ComponentValidator::revalidate);
                flag = false;
            }
            if (StringUtils.isBlank(this.javaServiceInterfaceSourceButton.getText())) {
                ComponentValidator.getInstance(this.javaServiceInterfaceSourceButton).ifPresent(ComponentValidator::revalidate);
                flag = false;
            }
        }
        return flag;
    }


    public boolean doValidate(ProjectState projectState) {
        boolean flag = true;
        if (StringUtils.isBlank(projectState.getModelName())) {
            ComponentValidator.getInstance(this.modelName).ifPresent(ComponentValidator::revalidate);
            flag = false;
        }
        if (StringUtils.isBlank(projectState.getJavaModelPackage())) {
            ComponentValidator.getInstance(this.javaModelCombo).ifPresent(ComponentValidator::revalidate);
            flag = false;
        }
        if (StringUtils.isBlank(projectState.getJavaModelSourcePackage())) {
            ComponentValidator.getInstance(this.javaSourceModelButton).ifPresent(ComponentValidator::revalidate);
            flag = false;
        }
        if (StringUtils.isBlank(projectState.getJavaMapperPackage())) {
            ComponentValidator.getInstance(this.javaMapperCombo).ifPresent(ComponentValidator::revalidate);
            flag = false;
        }
        if (StringUtils.isBlank(projectState.getJavaMapperSourcePackage())) {
            ComponentValidator.getInstance(this.javaMapperSourceButton).ifPresent(ComponentValidator::revalidate);
            flag = false;
        }
        if (StringUtils.isBlank(projectState.getJavaXmlPackage())) {
            ComponentValidator.getInstance(this.javaMapperXmlCombo).ifPresent(ComponentValidator::revalidate);
            flag = false;
        }
        if (StringUtils.isBlank(projectState.getJavaXmlSourcePackage())) {
            ComponentValidator.getInstance(this.javaMapperXmlSourceButton).ifPresent(ComponentValidator::revalidate);
            flag = false;
        }
        if (projectState.isGeneratorService()) {
            if (StringUtils.isBlank(projectState.getJavaServicePackage())) {
                ComponentValidator.getInstance(this.javaServiceCombo).ifPresent(ComponentValidator::revalidate);
                flag = false;
            }
            if (StringUtils.isBlank(projectState.getJavaServiceSourcePackage())) {
                ComponentValidator.getInstance(this.javaServiceSourceButton).ifPresent(ComponentValidator::revalidate);
                flag = false;
            }
            if (StringUtils.isBlank(projectState.getJavaServiceInterfacePackage())) {
                ComponentValidator.getInstance(this.javaServiceInterfaceCombo).ifPresent(ComponentValidator::revalidate);
                flag = false;
            }
            if (StringUtils.isBlank(projectState.getJavaServiceInterfaceSourcePackage())) {
                ComponentValidator.getInstance(this.javaServiceInterfaceSourceButton).ifPresent(ComponentValidator::revalidate);
                flag = false;
            }
        }
        return flag;
    }


    private void buildPrefixList(List<String> prefixes) {
        String name = generatorConfig.getTableName();
        prefixes.add(",");
        buildList(prefixes, name);
        if (StringUtils.isNotBlank(projectState.getRmPrefix())) {
            String[] fixes = projectState.getRmPrefix().split(",");
            prefixes.addAll(Arrays.asList(fixes));
            prefixes.add(projectState.getRmPrefix());
        }

    }

    public GeneratorConfig getGeneratorConfig() {
        return generatorConfig;
    }

    private void buildList(List<String> prefixes, String text) {
        String[] pres = new String[]{"-", "_"};
        for (String pre : pres) {
            String value = text.substring(0, text.indexOf(pre) + pre.length());
            if (StringUtils.isNotBlank(value)) {
                prefixes.add(value);
            }
        }
    }

    private String rmPrefix(String text, String prefix) {
        if (StringUtils.isBlank(text)) {
            return text;
        }
        if (StringUtils.isBlank(prefix)) {
            return StringUtils.getUpperCamelFromAny(text);
        }
        String[] rmPrefixes = prefix.split(",");
        for (String str : rmPrefixes) {
            String value = generatorConfig.getTableName();
            if (value.startsWith(str)) {
                return StringUtils.getUpperCamelFromAny(value.substring(str.length()));
            }
        }
        return StringUtils.getUpperCamelFromAny(text);
    }


    private void initProjectStateValue() {
        if (StringUtils.isNotBlank(projectState.getJavaModelPackage())) {
            this.javaModelCombo.setText(projectState.getJavaModelPackage());
        }
        if (StringUtils.isNotBlank(projectState.getJavaModelSourcePackage())) {
            this.javaSourceModelButton.setText(projectState.getJavaModelSourcePackage());
        }

        if (StringUtils.isNotBlank(projectState.getJavaMapperPackage())) {
            this.javaMapperCombo.setText(projectState.getJavaMapperPackage());
        }

        if (StringUtils.isNotBlank(projectState.getJavaMapperSourcePackage())) {
            this.javaMapperSourceButton.setText(projectState.getJavaMapperSourcePackage());
        }

        if (StringUtils.isNotBlank(projectState.getJavaXmlPackage())) {
            this.javaMapperXmlCombo.setText(projectState.getJavaXmlPackage());
        }
        if (StringUtils.isNotBlank(projectState.getJavaXmlSourcePackage())) {
            this.javaMapperXmlSourceButton.setText(projectState.getJavaXmlSourcePackage());
        }
        if (StringUtils.isNotBlank(projectState.getJavaServicePackage())) {
            this.javaServiceCombo.setText(projectState.getJavaServicePackage());
        }
        if (StringUtils.isNotBlank(projectState.getJavaServiceSourcePackage())) {
            this.javaServiceSourceButton.setText(projectState.getJavaServiceSourcePackage());
        }
        if (StringUtils.isNotBlank(projectState.getJavaServiceInterfacePackage())) {
            this.javaServiceInterfaceCombo.setText(projectState.getJavaServiceInterfacePackage());
        }
        if (StringUtils.isNotBlank(projectState.getJavaServiceInterfaceSourcePackage())) {
            this.javaServiceInterfaceSourceButton.setText(projectState.getJavaServiceInterfaceSourcePackage());
        }
        this.generatorService.setSelected(projectState.isGeneratorService());
        this.javaServiceCombo.setEnabled(projectState.isGeneratorService());
        if (projectState.getServiceMode() != null) {
            if (projectState.getServiceMode().equals(easyMode.getKey())) {
                serviceMode.setSelectedItem(easyMode);
            } else if (projectState.getServiceMode().equals(fullMode.getKey())) {
                serviceMode.setSelectedItem(fullMode);
            } else if (projectState.getServiceMode().equals(pureMode.getKey())) {
                serviceMode.setSelectedItem(pureMode);
            }
        }
        this.javaServiceSourceButton.setEnabled(projectState.isGeneratorService());
        this.javaServiceInterfaceCombo.setEnabled(projectState.isGeneratorService());
        this.javaServiceInterfaceSourceButton.setEnabled(projectState.isGeneratorService());
        this.generatorService.addActionListener(e -> {
            this.javaServiceCombo.setEnabled(this.generatorService.isSelected());
            this.javaServiceSourceButton.setEnabled(this.generatorService.isSelected());
            this.javaServiceInterfaceCombo.setEnabled(this.generatorService.isSelected());
            this.javaServiceInterfaceSourceButton.setEnabled(this.generatorService.isSelected());
            this.serviceMode.setEnabled(this.generatorService.isSelected());
        });
        this.rmPrefixJText.setText(projectState.getRmPrefix());
    }


    public void initProjectState(ProjectState projectState) {
        projectState.setRmPrefix(rmPrefixJText.getText());
        projectState.setModelName(this.modelName.getText());
        projectState.setTableName(generatorConfig.getTableName());
        projectState.setSchema(generatorConfig.getSchema());
        projectState.setJavaModelPackage(this.javaModelCombo.getText());
        projectState.setJavaModelSourcePackage(this.javaSourceModelButton.getText());

        projectState.setJavaMapperPackage(this.javaMapperCombo.getText());
        projectState.setJavaMapperSourcePackage(this.javaMapperSourceButton.getText());

        projectState.setJavaXmlPackage(this.javaMapperXmlCombo.getText());
        projectState.setJavaXmlSourcePackage(this.javaMapperXmlSourceButton.getText());

        //service impl
        projectState.setGeneratorService(this.generatorService.isSelected());
        projectState.setJavaServicePackage(this.javaServiceCombo.getText());
        projectState.setJavaServiceSourcePackage(this.javaServiceSourceButton.getText());

        //service interface
        projectState.setGeneratorServiceInterface(this.generatorService.isSelected());
        projectState.setJavaServiceInterfacePackage(this.javaServiceInterfaceCombo.getText());
        projectState.setJavaServiceInterfaceSourcePackage(this.javaServiceInterfaceSourceButton.getText());
        Object o = this.serviceMode.getSelectedItem();
        if (o != null) {
            projectState.setServiceMode(((JCheckBoxItem) this.serviceMode.getSelectedItem()).getKey());
        }

    }

    private FileChooserDescriptor getFileChooserDescriptor() {
        FileChooserDescriptor fileChooserDescriptor = FileChooserDescriptorFactory.createSingleFileDescriptor();
        fileChooserDescriptor.setShowFileSystemRoots(true);
        fileChooserDescriptor.setTitle(MybatisSmartCodeHelpBundle.message("choose.a.file"));
        fileChooserDescriptor.setHideIgnored(false);
        VirtualFile virtualFile = ProjectUtil.guessProjectDir(project);
        if (virtualFile != null) {
            fileChooserDescriptor.setRoots(virtualFile);
        }
        return fileChooserDescriptor;
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
        root.setLayout(new GridLayoutManager(8, 1, new Insets(0, 0, 0, 0), -1, -1));
        root.setPreferredSize(new Dimension(900, 500));
        root.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1));
        root.add(panel1, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        generatorService = new JCheckBox();
        generatorService.setText("是否生成service接口和实现类");
        panel1.add(generatorService, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JSeparator separator1 = new JSeparator();
        panel1.add(separator1, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        serviceMode = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        serviceMode.setModel(defaultComboBoxModel1);
        serviceMode.setToolTipText("");
        panel1.add(serviceMode, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        serviceModelLabel = new JLabel();
        serviceModelLabel.setText("Service生成模式：");
        panel1.add(serviceModelLabel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        root.add(panel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        modelNameLabel = new JLabel();
        modelNameLabel.setText("Model 名称：");
        panel2.add(modelNameLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        rmFixLabel = new JLabel();
        rmFixLabel.setText("去除前缀");
        panel2.add(rmFixLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        rmPrefixPanel = new JPanel();
        rmPrefixPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(rmPrefixPanel, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        modelName = new JBTextField();
        panel2.add(modelName, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        servicePanel = new JPanel();
        servicePanel.setLayout(new GridLayoutManager(4, 2, new Insets(0, 18, 0, 0), -1, -1));
        root.add(servicePanel, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        javaServiceLabel = new JLabel();
        javaServiceLabel.setText("service实现类包路径：");
        servicePanel.add(javaServiceLabel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        javaServicePackagePanel = new JPanel();
        javaServicePackagePanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        servicePanel.add(javaServicePackagePanel, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        javaServiceSourceLabel = new JLabel();
        javaServiceSourceLabel.setText("service实现类存放根目录：");
        servicePanel.add(javaServiceSourceLabel, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        javaServiceSrcFolderPanel = new JPanel();
        javaServiceSrcFolderPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        javaServiceSrcFolderPanel.setOpaque(false);
        servicePanel.add(javaServiceSrcFolderPanel, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        javaServiceInterfaceLabel = new JLabel();
        javaServiceInterfaceLabel.setText("service接口包路径：");
        servicePanel.add(javaServiceInterfaceLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        javaServiceInterfacePackagePanel = new JPanel();
        javaServiceInterfacePackagePanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        servicePanel.add(javaServiceInterfacePackagePanel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(629, 24), null, 0, false));
        javaServiceInterfaceSourceLabel = new JLabel();
        javaServiceInterfaceSourceLabel.setText("service接口存放根目录：");
        servicePanel.add(javaServiceInterfaceSourceLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        javaServiceInterfaceSrcFolderPanel = new JPanel();
        javaServiceInterfaceSrcFolderPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        javaServiceInterfaceSrcFolderPanel.setOpaque(false);
        servicePanel.add(javaServiceInterfaceSrcFolderPanel, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(629, 24), null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(6, 2, new Insets(0, 40, 0, 0), -1, -1));
        root.add(panel3, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        javaModelPanel = new JPanel();
        javaModelPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        javaModelPanel.setToolTipText("java model");
        panel3.add(javaModelPanel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        javaModelSourcePanel = new JPanel();
        javaModelSourcePanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(javaModelSourcePanel, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        javaMapperPanel = new JPanel();
        javaMapperPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(javaMapperPanel, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        javaMapperSourcePanel = new JPanel();
        javaMapperSourcePanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(javaMapperSourcePanel, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        javaMapperXmlPanel = new JPanel();
        javaMapperXmlPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(javaMapperXmlPanel, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        javaXmlResourcesPanel = new JPanel();
        javaXmlResourcesPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(javaXmlResourcesPanel, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        javaModelSourceLabel = new JLabel();
        javaModelSourceLabel.setText("实体存放根目录：");
        panel3.add(javaModelSourceLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        javaMapperLabel = new JLabel();
        javaMapperLabel.setText("Mapper包路径：");
        panel3.add(javaMapperLabel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        javaMapperSourceLabel = new JLabel();
        javaMapperSourceLabel.setText("Mapper存放根目录：");
        panel3.add(javaMapperSourceLabel, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        javaMapperXmlLabel = new JLabel();
        javaMapperXmlLabel.setText("Mapper xml 包路径：");
        panel3.add(javaMapperXmlLabel, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        javaMapperXmlSourceLabel = new JLabel();
        javaMapperXmlSourceLabel.setText("Mapper xml根目录：");
        panel3.add(javaMapperXmlSourceLabel, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        javaModelLabel = new JLabel();
        javaModelLabel.setText("Model包路径：");
        panel3.add(javaModelLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        root.add(spacer1, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JSeparator separator2 = new JSeparator();
        root.add(separator2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JSeparator separator3 = new JSeparator();
        root.add(separator3, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        root.add(panel4, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("module：");
        panel4.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        javaModules = new JComboBox();
        panel4.add(javaModules, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
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
