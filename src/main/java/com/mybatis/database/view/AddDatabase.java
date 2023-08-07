package com.mybatis.database.view;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.ComponentValidator;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.fields.ExpandableTextField;
import com.intellij.ui.components.labels.LinkLabel;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.intellij.util.Consumer;
import com.mybatis.database.connect.ConnectManager;
import com.mybatis.database.connect.DatabaseArtifactList;
import com.mybatis.database.connect.Version;
import com.mybatis.database.model.DataConfigSource;
import com.mybatis.database.model.DatabaseConfig;
import com.mybatis.database.model.TableSource;
import com.mybatis.database.xml.DriverManger;
import com.mybatis.enums.DataBaseType;
import com.mybatis.messages.MybatisSmartCodeHelpBundle;
import com.mybatis.model.CacheModel.Cache.DataConfigSourceDto;
import com.mybatis.model.CacheModel.DatabaseSource;
import com.mybatis.model.CacheModel.Dbms;
import com.mybatis.state.MybatisDatabaseComponent;
import com.mybatis.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.regex.Pattern;

public class AddDatabase implements Disposable {
    //    private static final String MISS_TEXT = MybatisSmartCodeHelpBundle.message("database.miss.driver");
    private final Project project;
    private final DriverManger driverManger;
    private JPanel root;
    private EditorTextField databaseName;
    private JComboBox<Dbms> dbms;
    private EditorTextField host;
    private EditorTextField port;
    private EditorTextField username;
    private JBPasswordField password;
    private EditorTextField database;
    private EditorTextField url;
    private LinkLabel<String> testConnectionButton;
    private JLabel dbType;
    private ExpandableTextField comment;
    private LinkLabel<String> driverType;
    private JLabel error;
    private JLabel dataBaseLabel;
    private JLabel driverLabel;
    private ComboBox<String> driverComboBox;
    private EditorTextField sid;
    private JLabel sidLabel;
    private JPanel errorPanel;
    private TableSource tableSource;
    private DatabaseArtifactList databaseArtifactList;
    private DatabaseArtifactList.Artifact selectArtifact = null;
    private Version version = null;
    private Map<String, DataConfigSourceDto> stringDataConfigSourceDtoMap;
    private DataConfigSource dataConfigSource;
    private boolean isValid = true;

    public AddDatabase(Project project, DataConfigSource dataConfigSource) {
        this.project = project;
        this.driverManger = DriverManger.getInstance();
        this.version = Version.of("8.0.21");
        DatabaseSource databaseSource = MybatisDatabaseComponent.getInstance(project).getState();
        assert databaseSource != null;
        this.stringDataConfigSourceDtoMap = databaseSource.getSources();
        this.dataConfigSource = dataConfigSource;
        $$$setupUI$$$();
        initValidator();
        initComponent();
        initDriverCheck();
    }

    public static boolean isNumeric(String string) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(string).matches();
    }

    private void initComponent() {
        testConnectionButton.setIcon(null);
        driverLabel.setVisible(false);
        driverComboBox.setVisible(false);
        sid.setVisible(false);
        sidLabel.setVisible(false);
        driverType.setIcon(null);
        dbms.addItem(Dbms.MYSQL);
        dbms.addItem(Dbms.ORACLE);
        dbms.addActionListener(e -> {
            driverType.setText(((Dbms) Objects.requireNonNull(dbms.getSelectedItem())).getDisplayName());
            url.setText(getUrl());
            error.setText(null);
            if (getSelectDbms().isOracle()) {
                driverLabel.setVisible(true);
                driverComboBox.setVisible(true);
                sid.setVisible(true);
                sid.setText("XE");
                sidLabel.setVisible(true);
                version = Version.of("21.1.0.0");
                port.setText("1521");
                database.setVisible(false);
                dataBaseLabel.setVisible(false);
            } else if (getSelectDbms().isMysql()) {
                database.setVisible(true);
                dataBaseLabel.setVisible(true);
                driverLabel.setVisible(false);
                driverComboBox.setVisible(false);
                sid.setVisible(false);
                sidLabel.setVisible(false);
                version = Version.of("8.0.21");
            }
            initDriverCheck();
        });

        int i = 0;
        while (true) {
            String name = "localhost";
            if (i != 0) {
                name = name + i;
            }
            if (dataConfigSource == null && stringDataConfigSourceDtoMap.containsKey(name)) {
                i++;
            } else {
                databaseName.setText(name);
                break;
            }
        }
        if (dataConfigSource != null) {
            host.setText(dataConfigSource.getHost());
            DataBaseType dataBaseType = dataConfigSource.getDataBaseType();
            if (dataBaseType.equals(DataBaseType.MySql) || dataBaseType.equals(DataBaseType.MySQL_5)) {
                driverType.setText(Dbms.MYSQL.getDisplayName());
                dbms.setSelectedItem(Dbms.MYSQL);
                database.setVisible(true);
                dataBaseLabel.setVisible(true);
            } else if (dataBaseType.equals(DataBaseType.Oracle)) {
                driverType.setText(Dbms.ORACLE.getDisplayName());
                dbms.setSelectedItem(Dbms.ORACLE);
                driverLabel.setVisible(true);
                driverComboBox.setVisible(true);
                sid.setVisible(true);
                sid.setText(dataConfigSource.getSid());
                sidLabel.setVisible(true);
                database.setVisible(false);
                dataBaseLabel.setVisible(false);
                driverComboBox.setSelectedItem(dataConfigSource.getDriverCombox());
            }
            port.setText(dataConfigSource.getPort() + "");
            url.setText(dataConfigSource.getUrl());
            String id = "MySQL Connector/J 8";
            if (dataBaseType.equals(DataBaseType.MySQL_5)) {
                id = "MySQL Connector/J";
                version = Version.of("5.1.47");
            } else if (dataBaseType.equals(DataBaseType.Oracle)) {
                id = "Oracle";
                version = Version.of("21.1.0.0");
            }
            selectArtifact = driverManger.getArtifactList().getArtifact(id);
            comment.setText(dataConfigSource.getComment());
            databaseName.setText(dataConfigSource.getName());
            database.setText(dataConfigSource.getDatabase());
            username.setText(dataConfigSource.getUserName());
            password.setText(dataConfigSource.getPassword());
        } else {
            host.setText("localhost");
            driverType.setText(Dbms.MYSQL.getDisplayName());
            port.setText("3306");
            url.setText(getUrl());
        }

        username.addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(@NotNull DocumentEvent event) {
                setMsg(null);
            }
        });
        password.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(javax.swing.event.@NotNull DocumentEvent e) {
                setMsg(null);
            }
        });
        url.addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(@NotNull DocumentEvent event) {
                setMsg(null);
            }
        });
        port.addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(@NotNull DocumentEvent event) {
                if (isNumeric(port.getText())) {
                    url.setText(getUrl());
                    setMsg(null);
                }
            }
        });
        database.addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(@NotNull DocumentEvent event) {
                url.setText(getUrl());
                setMsg(null);
            }
        });
        host.addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(@NotNull DocumentEvent event) {
                url.setText(getUrl());
                int i = 0;
                while (true) {
                    String name = host.getText();
                    if (i != 0) {
                        name = name + i;
                    }
                    if (dataConfigSource == null && stringDataConfigSourceDtoMap.containsKey(name)) {
                        i++;
                    } else {
                        databaseName.setText(name);
                        break;
                    }
                }
                setMsg(null);
            }
        });
        driverComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                url.setText(getUrl());
            }
        });
        sid.addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(@NotNull DocumentEvent event) {
                url.setText(getUrl());
            }
        });
        driverType.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                List<String> list = null;
                setMsg(null);
                if (((Dbms) Objects.requireNonNull(dbms.getSelectedItem())).isMysql()) {
                    list = new ArrayList<>();
                    list.add("MySQL");
                    list.add("MySQL 5.1");
                } else if (((Dbms) Objects.requireNonNull(dbms.getSelectedItem())).isOracle()) {
                    list = new ArrayList<>();
                    list.add("Oracle 21");
                    list.add("Oracle 19");
                    list.add("Oracle 12");
                    list.add("Oracle 11");
                }
                if (list != null && !list.isEmpty()) {
                    JBPopupFactory.getInstance().createPopupChooserBuilder(list)
                            .setTitle(MybatisSmartCodeHelpBundle.message("database.choose.driver.version")).setItemChosenCallback(new Consumer<String>() {
                                @Override
                                public void consume(String s) {
                                    //检测driver 是否存在
                                    String id = "";
                                    if (((Dbms) Objects.requireNonNull(dbms.getSelectedItem())).isMysql() && s.equals("MySQL 5.1")) {
                                        id = "MySQL Connector/J";
                                        version = Version.of("5.1.47");
                                    } else if (((Dbms) Objects.requireNonNull(dbms.getSelectedItem())).isMysql()) {
                                        id = "MySQL Connector/J 8";
                                        version = Version.of("8.0.21");
                                    } else if (((Dbms) Objects.requireNonNull(dbms.getSelectedItem())).isOracle()) {
                                        id = "Oracle";
                                        if (s.equals("Oracle 21")) {
                                            version = Version.of("21.1.0.0");
                                        } else if (s.equals("Oracle 19")) {
                                            version = Version.of("19.8.0.0");
                                        } else if (s.equals("Oracle 12")) {
                                            version = Version.of("12.2.0.1");
                                        } else if (s.equals("Oracle 11")) {
                                            version = Version.of("11.2.0.4");
                                        } else {
                                            version = Version.of("21.1.0.0");
                                        }
                                    }
                                    selectArtifact = driverManger.getArtifactList().getArtifact(id);
                                    if (selectArtifact == null) {
                                        showMissText();
                                        return;
                                    }
                                    DatabaseArtifactList.ArtifactVersion artifactVersion = selectArtifact.get(version);
                                    if (artifactVersion == null) {
                                        showMissText();
                                        return;
                                    }
                                    if (!driverManger.getArtifactList().isValid(artifactVersion)) {
                                        showMissText();
                                    }

                                }
                            }).createPopup().showUnderneathOf(driverType);
                }

            }
        });
        testConnectionButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                testConnectionButton.setEnabled(false);
                if (!check()) {
                    testConnectionButton.setEnabled(true);
                    return;
                }
                if (!isValid) {
                    showMissText();
                    testConnectionButton.setEnabled(true);
                    return;
                }
                ProgressManager.getInstance().runProcessWithProgressSynchronously(() -> {
                    Dbms dbms = getSelectDbms();
                    if (!dbms.isMysql() && !dbms.isOracle()) {
                        error.setText(MybatisSmartCodeHelpBundle.message("database.dbms.is.not.support"));
//                        Messages.showInfoMessage(project, "For the time being, only Mysql or Oracle database is supported", "not Support");
                        testConnectionButton.setEnabled(true);
                        return;
                    }

                    ConnectManager connectManager = ConnectManager.getConnectManager(getDatabaseConfig().getDataBaseType());
                    try {
                        connectManager.checkConnect(getDatabaseConfig(), project);
                        error.setText(MybatisSmartCodeHelpBundle.message("test.connect.is.success"));
                    } catch (Exception exception) {
                        setMsg(exception.getMessage());
                    }
                    testConnectionButton.setEnabled(true);
                }, MybatisSmartCodeHelpBundle.message("test.connect.title"), true, project, testConnectionButton);
            }
        });
        error.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                error.setEnabled(false);
                if (!error.getText().equals(MybatisSmartCodeHelpBundle.message("database.miss.driver"))) {
                    error.setEnabled(true);
                    return;
                }
                ProgressManager.getInstance().runProcessWithProgressSynchronously(() -> {
                    initDriver();
                    Dbms dbms = getSelectDbms();
                    if (!dbms.isMysql() && !dbms.isOracle()) {
                        setMsg(MybatisSmartCodeHelpBundle.message("database.dbms.is.not.support"));
                        return;
                    }
                    if (selectArtifact == null) {
                        if (dbms.isOracle()) {
                            selectArtifact = driverManger.getArtifactList().getArtifact("Oracle");
                        } else if (dbms.isMysql()) {
                            selectArtifact = driverManger.getArtifactList().getArtifact("MySQL Connector/J 8");
                        }
                        if (selectArtifact == null) {
                            setMsg(MybatisSmartCodeHelpBundle.message("database.load.driver.is.fail"));
                            return;
                        }
                    }
                    DatabaseArtifactList.ArtifactVersion artifactVersion = selectArtifact.get(version);
                    if (artifactVersion == null) {
                        setMsg(MybatisSmartCodeHelpBundle.message("database.load.driver.is.fail"));
                        return;
                    }
                    try {
                        DriverManger.getInstance().downloadArtifact(artifactVersion, artifactVersion1 -> error.setText(null));
                        isValid = true;
                    } catch (IOException exception) {
                        setMsg(exception.getMessage());
                    }
                }, MybatisSmartCodeHelpBundle.message("download.maven.title"), true, project, error);

            }
        });

    }

    private void setMsg(String text) {
        error.setText(text);
        error.setToolTipText(text);
        error.setEnabled(true);
    }

    private void initDriverCheck() {
        Dbms dbms = getSelectDbms();
        if (dbms.isMysql()) {
            selectArtifact = driverManger.getArtifactList().getArtifact("MySQL Connector/J 8");
            if (selectArtifact == null) {
                showMissText();
                return;
            }
        } else if (dbms.isOracle()) {
            selectArtifact = driverManger.getArtifactList().getArtifact("Oracle");
            if (selectArtifact == null) {
                showMissText();
                return;
            }
        }
        DatabaseArtifactList.ArtifactVersion artifactVersion = selectArtifact.get(version);
        if (artifactVersion == null) {
            showMissText();
            return;
        }
        if (!driverManger.getArtifactList().isValid(artifactVersion)) {
            showMissText();
            return;
        }
        isValid = true;
    }

    private void showMissText() {
        error.setText(MybatisSmartCodeHelpBundle.message("database.miss.driver"));
        error.setToolTipText(MybatisSmartCodeHelpBundle.message("database.miss.driver"));
        isValid = false;
    }

    private void initDriver() {
        databaseArtifactList = this.driverManger.getArtifactList();
        if (databaseArtifactList.getArtifacts().isEmpty()) {
            this.driverManger.forceUpdate(project);
        }
    }

    public DatabaseConfig getDatabaseConfig() {
        DatabaseConfig databaseConfig = new DatabaseConfig();
        databaseConfig.setHost(host.getText());
        if (getSelectDbms().isMysql() && version.toString().contains("5.")) {
            databaseConfig.setDataBaseType(DataBaseType.MySQL_5);
        } else if (getSelectDbms().isMysql()) {
            databaseConfig.setDataBaseType(DataBaseType.MySql);
        } else if (getSelectDbms().isOracle()) {
            databaseConfig.setDataBaseType(DataBaseType.Oracle);
        }
        databaseConfig.setComment(comment.getText());
        databaseConfig.setPort(Integer.parseInt(port.getText()));
        databaseConfig.setUrl(url.getText());
        databaseConfig.setUsername(username.getText());
        databaseConfig.setPassword(String.valueOf(password.getPassword()));
        databaseConfig.setArtifactVersion(selectArtifact.get(version));
        databaseConfig.setSchema(database.getText());
        databaseConfig.setName(databaseName.getText());
        databaseConfig.setSid(sid.getText());
        return databaseConfig;
    }

    public void setError(String text) {
        error.setText(text);
        error.setToolTipText(text);
    }

    private String getUrl() {
        Object object = dbms.getSelectedItem();
        if (object instanceof Dbms) {
            if (((Dbms) dbms.getSelectedItem()).isMysql()) {
                return "jdbc:mysql://" + getHost() + ":" + getPort() + "/" + getDatabase() + "?useSSL=false&serverTimezone=GMT";
            } else if (((Dbms) dbms.getSelectedItem()).isOracle()) {
                String driverCombo = (String) driverComboBox.getSelectedItem();
                if (StringUtils.isBlank(driverCombo)) {
                    driverCombo = "Thin";
                }
                return "jdbc:oracle:" + driverCombo.toLowerCase() + ":@" + getHost() + ":" + getPort() + ":" + sid.getText();
            }
        }

        return null;
    }

    private String getDatabase() {
        return this.database.getText();
    }

    private String getPort() {
        return StringUtils.isBlank(this.port.getText()) ? "3306" : this.port.getText();
    }

    private Dbms getSelectDbms() {
        return (Dbms) dbms.getSelectedItem();
    }

    private String getHost() {
        return StringUtils.isBlank(this.host.getText()) ? "localhost" : this.host.getText();
    }

    public boolean check() {
        if (StringUtils.isBlank(username.getText())) {
            error.setText(MybatisSmartCodeHelpBundle.message("database.user.is.empty"));
            return false;
        }
        if (StringUtils.isBlank(String.valueOf(password.getPassword()))) {
            error.setText(MybatisSmartCodeHelpBundle.message("database.password.is.empty"));
            return false;
        }
        if (StringUtils.isBlank(host.getText())) {
            error.setText(MybatisSmartCodeHelpBundle.message("database.host.is.empty"));
            return false;
        }
        if (StringUtils.isBlank(port.getText())) {
            error.setText(MybatisSmartCodeHelpBundle.message("database.port.is.empty"));
            return false;
        }
        if (StringUtils.isBlank(url.getText())) {
            error.setText(MybatisSmartCodeHelpBundle.message("database.url.is.empty"));
            return false;
        }
        if (StringUtils.isBlank(databaseName.getText())) {
            error.setText(MybatisSmartCodeHelpBundle.message("database.name.is.empty"));
            return false;
        }
        setMsg(null);
        if (dataConfigSource == null && stringDataConfigSourceDtoMap.containsKey(databaseName.getText())) {
            error.setText(MybatisSmartCodeHelpBundle.message("database.name.is.duplicate"));
            return false;
        }
        if (getSelectDbms().isOracle() && StringUtils.isBlank(sid.getText())) {
            error.setText(MybatisSmartCodeHelpBundle.message("database.sid.is.empty"));
            return false;
        }

        if (!isValid) {
            showMissText();
            return false;
        }

        return true;
    }

    private void initValidator() {
        new ComponentValidator(this).withValidator(() -> StringUtils.isBlank(this.databaseName.getText()) ? new ValidationInfo(MybatisSmartCodeHelpBundle.message("database.name.is.empty"), this.databaseName) : null).
                andRegisterOnDocumentListener(this.databaseName).
                installOn(this.databaseName);
        new ComponentValidator(this).withValidator(() -> StringUtils.isNotBlank(this.databaseName.getText()) && dataConfigSource == null && stringDataConfigSourceDtoMap.containsKey(this.databaseName.getText()) ? new ValidationInfo(MybatisSmartCodeHelpBundle.message("database.name.is.duplicate"), this.databaseName) : null).
                andRegisterOnDocumentListener(this.databaseName).
                installOn(this.databaseName);
        new ComponentValidator(this).withValidator(() -> StringUtils.isBlank(this.host.getText()) ? new ValidationInfo(MybatisSmartCodeHelpBundle.message("database.host.is.empty"), this.host) : null).
                andRegisterOnDocumentListener(this.host).
                installOn(this.host);

        new ComponentValidator(this).withValidator(() -> StringUtils.isBlank(this.port.getText()) ? new ValidationInfo(MybatisSmartCodeHelpBundle.message("database.port.is.empty"), this.port) : null).
                andRegisterOnDocumentListener(this.port).
                installOn(this.port);
        new ComponentValidator(this).withValidator(() -> !isNumeric(this.port.getText()) ? new ValidationInfo(MybatisSmartCodeHelpBundle.message("database.port.must.integer"), this.port) : null).
                andRegisterOnDocumentListener(this.port).
                installOn(this.port);

        new ComponentValidator(this).withValidator(() -> StringUtils.isBlank(this.username.getText()) ? new ValidationInfo(MybatisSmartCodeHelpBundle.message("database.user.is.empty"), this.username) : null).
                andRegisterOnDocumentListener(this.username).
                installOn(this.username);

        new ComponentValidator(this).withValidator(() -> this.password.getPassword() == null ? new ValidationInfo(MybatisSmartCodeHelpBundle.message("database.password.is.empty"), this.password) : null).
                andRegisterOnDocumentListener(this.password).
                installOn(this.password);

        new ComponentValidator(this).withValidator(() -> StringUtils.isBlank(this.url.getText()) ? new ValidationInfo(MybatisSmartCodeHelpBundle.message("database.url.is.empty"), this.url) : null).
                andRegisterOnDocumentListener(this.url).
                installOn(this.url);
        new ComponentValidator(this).withValidator(() -> getSelectDbms().isOracle() && StringUtils.isBlank(this.sid.getText()) ? new ValidationInfo(MybatisSmartCodeHelpBundle.message("database.sid.is.empty"), this.sid) : null)
                .andRegisterOnDocumentListener(this.sid)
                .installOn(this.sid);
        new ComponentValidator(this).withValidator(() -> getSelectDbms().isOracle() && StringUtils.isBlank(this.database.getText()) ? new ValidationInfo(MybatisSmartCodeHelpBundle.message("database.schema.is.empty"), this.database) : null)
                .andRegisterOnDocumentListener(this.database)
                .installOn(this.database);
    }

    public TableSource getTableSource() {
        return tableSource;
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
        root.setLayout(new GridLayoutManager(5, 1, new Insets(0, 0, 0, 0), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        root.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("name:");
        panel1.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Comment：");
        panel1.add(label2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        databaseName = new EditorTextField();
        panel1.add(databaseName, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        comment = new ExpandableTextField();
        panel1.add(comment, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1));
        root.add(panel2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        dbType = new JLabel();
        dbType.setText("Database Type:");
        panel2.add(dbType, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        dbms = new JComboBox();
        panel2.add(dbms, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Driver");
        panel2.add(label3, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        driverType = new LinkLabel();
        panel2.add(driverType, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(5, 4, new Insets(0, 0, 0, 0), -1, -1));
        root.add(panel3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Host：");
        panel3.add(label4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        host = new EditorTextField();
        host.setText("");
        panel3.add(host, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Port：");
        panel3.add(label5, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        port = new EditorTextField();
        port.setText("");
        panel3.add(port, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("User：");
        panel3.add(label6, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        username = new EditorTextField();
        panel3.add(username, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("Password：");
        panel3.add(label7, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        password = new JBPasswordField();
        password.setText("");
        panel3.add(password, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        dataBaseLabel = new JLabel();
        dataBaseLabel.setText("Database：");
        panel3.add(dataBaseLabel, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        database = new EditorTextField();
        panel3.add(database, new GridConstraints(3, 1, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label8 = new JLabel();
        label8.setText("URL：");
        panel3.add(label8, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        url = new EditorTextField();
        panel3.add(url, new GridConstraints(4, 1, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        driverComboBox = new ComboBox();
        driverComboBox.setEnabled(true);
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("Thin");
        defaultComboBoxModel1.addElement("OCI");
        defaultComboBoxModel1.addElement("OCI8");
        driverComboBox.setModel(defaultComboBoxModel1);
        panel3.add(driverComboBox, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        driverLabel = new JLabel();
        driverLabel.setText("Driver：");
        panel3.add(driverLabel, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        sidLabel = new JLabel();
        sidLabel.setText("sid：");
        panel3.add(sidLabel, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        sid = new EditorTextField();
        panel3.add(sid, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        root.add(panel4, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        testConnectionButton = new LinkLabel();
        testConnectionButton.setText("Test Connection");
        panel4.add(testConnectionButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel4.add(spacer1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        errorPanel = new JPanel();
        errorPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        root.add(errorPanel, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, new Dimension(400, -1), 0, false));
        error = new JLabel();
        error.setText("");
        error.putClientProperty("html.disable", Boolean.FALSE);
        errorPanel.add(error, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
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
