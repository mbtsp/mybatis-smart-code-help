package com.mybatis.setting;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.AncestorListenerAdapter;
import com.intellij.ui.components.labels.LinkLabel;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.mybatis.dialog.SupportDialog;
import com.mybatis.dialog.SupportForm;
import com.mybatis.messages.MybatisSmartCodeHelpBundle;
import com.mybatis.model.CacheModel.MybatisSettingConfig;
import com.mybatis.state.MybatisSettingsState;
import com.mybatis.utils.ApplicationUtils;
import com.mybatis.utils.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MybatisSettingPanel {
    private final MybatisSettingConfig mybatisSettingConfig;
    private JPanel root;
    private JCheckBox enableMybatisDatabaseCheckBox;
    private JButton restart;
    private JComboBox<MybatisSupportLanguage> languageComboBox;
    private JLabel languageLabel;
    private JCheckBox mapperMethodReferenceJumpCheckBox;
    private JCheckBox showFileBirdIconCheckBox;
    private LinkLabel<String> support;

    public MybatisSettingPanel() {
        $$$setupUI$$$();
        this.mybatisSettingConfig = MybatisSettingsState.getInstance().getState();
        assert this.mybatisSettingConfig != null;
        restart.setEnabled(false);
        enableMybatisDatabaseCheckBox.addActionListener(e -> {
            restart.setEnabled(enableMybatisDatabaseCheckBox.isSelected() != this.mybatisSettingConfig.isEnableCustomDatabaseTools());
        });
        restart.addActionListener(e -> {
            this.mybatisSettingConfig.setEnableCustomDatabaseTools(enableMybatisDatabaseCheckBox.isSelected());
            if (ApplicationUtils.showRestartDialog(MybatisSmartCodeHelpBundle.message("restart"), action -> MybatisSmartCodeHelpBundle.message("apply.change.and.restart")) == Messages.YES) {
                ApplicationManager.getApplication().exit(true, false, true);
            }
        });
        support.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new SupportDialog(null).show();
            }
        });
        initLanguage();
        initSettingStateValue();
    }

    private void initSettingStateValue() {
        if (StringUtils.isNotBlank(this.mybatisSettingConfig.getLanguage())) {
            String language = this.mybatisSettingConfig.getLanguage();
            MybatisSupportLanguage mybatisSupportLanguage = MybatisSupportLanguage.getLanguage(language);
            languageComboBox.setSelectedItem(mybatisSupportLanguage);
        }
        enableMybatisDatabaseCheckBox.setSelected(this.mybatisSettingConfig.isEnableCustomDatabaseTools());
        mapperMethodReferenceJumpCheckBox.setSelected(this.mybatisSettingConfig.isEnableMapperJumpXml());
        showFileBirdIconCheckBox.setSelected(this.mybatisSettingConfig.isShowFileBirdIcon());
    }

    public boolean isModified() {
        if (mybatisSettingConfig.isEnableCustomDatabaseTools() != enableMybatisDatabaseCheckBox.isSelected()) {
            return true;
        }
        MybatisSupportLanguage mybatisSupportLanguage = (MybatisSupportLanguage) languageComboBox.getSelectedItem();
        if (mybatisSupportLanguage == null) {
            return true;
        }
        if (!mybatisSettingConfig.getLanguage().equals(mybatisSupportLanguage.getLocale().getLanguage())) {
            return true;
        }
        if (mybatisSettingConfig.isEnableMapperJumpXml() != mapperMethodReferenceJumpCheckBox.isSelected()) {
            return true;
        }
        if (mybatisSettingConfig.isShowFileBirdIcon() != showFileBirdIconCheckBox.isSelected()) {
            return true;
        }
        return false;
    }

    public void apply() {
        boolean flag = false;
        if (mybatisSettingConfig.isEnableCustomDatabaseTools() != enableMybatisDatabaseCheckBox.isSelected()) {
            mybatisSettingConfig.setEnableCustomDatabaseTools(enableMybatisDatabaseCheckBox.isSelected());
            flag = true;
        }
        MybatisSupportLanguage mybatisSupportLanguage = (MybatisSupportLanguage) languageComboBox.getSelectedItem();
        if (mybatisSupportLanguage != null) {
            mybatisSettingConfig.setLanguage(mybatisSupportLanguage.getCode());
        }
        mybatisSettingConfig.setShowFileBirdIcon(showFileBirdIconCheckBox.isSelected());
        mybatisSettingConfig.setEnableMapperJumpXml(mapperMethodReferenceJumpCheckBox.isSelected());
        if (flag && ApplicationUtils.showRestartDialog(MybatisSmartCodeHelpBundle.message("restart"), action -> MybatisSmartCodeHelpBundle.message("apply.change.and.restart")) == Messages.YES) {
            ApplicationManager.getApplication().exit(true, false, true);
        }
    }

    public void reset() {
        initSettingStateValue();
    }

    private void initLanguage() {
        MybatisSupportLanguage[] languages = MybatisSupportLanguage.values();
        for (MybatisSupportLanguage language : languages) {
            languageComboBox.addItem(language);
        }
        enableMybatisDatabaseCheckBox.setText(MybatisSmartCodeHelpBundle.message("setting.enable.database.config"));
        enableMybatisDatabaseCheckBox.setToolTipText(MybatisSmartCodeHelpBundle.message("setting.enable.database.config.tip"));
        restart.setToolTipText(MybatisSmartCodeHelpBundle.message("setting.restart.tip"));
        restart.setText(MybatisSmartCodeHelpBundle.message("setting.restart.text"));
        languageLabel.setText(MybatisSmartCodeHelpBundle.message("setting.choose.language.text"));
        mapperMethodReferenceJumpCheckBox.setText(MybatisSmartCodeHelpBundle.message("mapper.method.reference.jump.xml"));
        showFileBirdIconCheckBox.setText(MybatisSmartCodeHelpBundle.message("show.file.bird.icon"));
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
        root.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 6, new Insets(0, 0, 0, 0), -1, -1));
        root.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        enableMybatisDatabaseCheckBox = new JCheckBox();
        enableMybatisDatabaseCheckBox.setText("Enable built-in database configuration");
        enableMybatisDatabaseCheckBox.setToolTipText("After checking, the database configuration that comes with the plug-in will be enabled");
        panel1.add(enableMybatisDatabaseCheckBox, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        restart = new JButton();
        restart.setText("Restart IDE");
        restart.setToolTipText("Restart idea to make the configuration take effect");
        panel1.add(restart, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        languageComboBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        languageComboBox.setModel(defaultComboBoxModel1);
        panel1.add(languageComboBox, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        languageLabel = new JLabel();
        languageLabel.setText("");
        panel1.add(languageLabel, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        support = new LinkLabel();
        support.setText("支持/捐赠");
        panel1.add(support, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        root.add(spacer2, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        root.add(panel2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        mapperMethodReferenceJumpCheckBox = new JCheckBox();
        mapperMethodReferenceJumpCheckBox.setText("Mapper method reference jump Xml");
        panel2.add(mapperMethodReferenceJumpCheckBox, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel2.add(spacer3, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        showFileBirdIconCheckBox = new JCheckBox();
        showFileBirdIconCheckBox.setText("Show file bird icon");
        panel2.add(showFileBirdIconCheckBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return root;
    }

}
