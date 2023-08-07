package com.mybatis.database.xml;

import com.intellij.facet.frameworks.LibrariesDownloadConnectionService;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressIndicatorProvider;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.Consumer;
import com.intellij.util.io.HttpRequests;
import com.intellij.util.messages.Topic;
import com.mybatis.database.connect.DatabaseArtifactList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.EventListener;
import java.util.concurrent.ExecutionException;

public class DriverManger {
    public static final Topic<ArtifactListener> TOPIC = new Topic<>("ARTIFACTS_TOPIC", ArtifactListener.class);
    private static final Logger LOG = Logger.getInstance(DriverManger.class);
    private static final DriverManger INSTANCE = new DriverManger();
    private static final String ARTIFACTS_XML = "jdbc-drivers.xml";
    private final Object myFileLock = new Object();
    private final Object myLock = new Object();
    private boolean myLoaded;
    private Long myLastUpdate = null;
    private volatile DatabaseArtifactList myArtifactList = DatabaseArtifactList.EMPTY;

    private DriverManger() {
        this.myLoaded = true;
        Application app = ApplicationManager.getApplication();
        if (app.isUnitTestMode()) {
            loadArtifacts(false);
            return;
        }
        File file = getLocalArtifactListPath();
        app.executeOnPooledThread(() -> loadArtifacts(file.exists()));
    }

    public static DriverManger getInstance() {
        return INSTANCE;
    }

    @NotNull
    private static File getLocalArtifactListPath() {
        return new File(getDownloadPath(), ARTIFACTS_XML);
    }

    private static void listLoaded() {
        ApplicationManager.getApplication().invokeLater(() -> fireChanged(null));
    }

    @NotNull
    public static String getDownloadPath() {
        return System.getProperty("java.io.tmpdir") + ".idea\\jdbc-drivers";
    }

    private static void fireChanged(@Nullable DatabaseArtifactList.ArtifactVersion version) {
        ApplicationManager.getApplication().getMessageBus().syncPublisher(TOPIC).artifactChanged(version);
    }

    @Nullable
    public static String createVersionsUrl() {
        String serviceUrl = LibrariesDownloadConnectionService.getInstance().getServiceUrl();
        return StringUtil.isNotEmpty(serviceUrl) ? (serviceUrl + "/jdbc-drivers/jdbc-drivers.xml") : null;
    }

    public static DatabaseArtifactList getArtifacts() {
        return getInstance().getArtifactList();
    }

    public void forceUpdate(Project project) {
        updateLists(project, false);
    }

    @NotNull
    public DatabaseArtifactList getArtifactList() {
        checkForUpdates();
        return this.myArtifactList;
    }

    public void checkForUpdates() {
        boolean notLoaded, outdated;
        Application app = ApplicationManager.getApplication();
        if (app == null || app.isUnitTestMode()) {
            return;
        }
        synchronized (this.myLock) {

            if (outdated = isOutdated()) {
                setLastUpdate(System.currentTimeMillis());
            }
            notLoaded = (!outdated && !this.myLoaded);
            this.myLoaded = true;
        }
        if (outdated) {
            updateLists(null, true);
        } else if (notLoaded) {
            initLocal();
            listLoaded();
        }
    }

    private void updateLists(Project project, boolean background) {
        setLastUpdate(System.currentTimeMillis());
        (new Task.ConditionalModal(project, "Update mybatis database  drivers list", false, () -> background) {
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setText("Loading database drivers list...");
                loadRemoteArtifactList();
                initLocal();
                listLoaded();
            }
        }).queue();
    }

    private void initLocal() {
        try {
            ApplicationManager.getApplication().executeOnPooledThread(this::loadLocalArtifactsList)
                    .get();
        } catch (InterruptedException ignored) {

        } catch (ExecutionException e) {
            LOG.error(e);
        }
    }

    private void loadRemoteArtifactList() {
        loadArtifactsList();
        loadLocalArtifactsList();
    }

    private boolean isOutdated() {
        if (this.myLastUpdate == null) {
            File path = getLocalArtifactListPath();
            if (path.exists()) {
                this.myLastUpdate = path.lastModified();
            } else {

                return true;
            }
        }
        return (System.currentTimeMillis() - this.myLastUpdate > 86400000L);
    }

    private void loadArtifacts(boolean localOnly) {
        if (!localOnly) loadArtifactsList();
        loadLocalArtifactsList();
    }

    private void loadLocalArtifactsList() {
        File listFile = getLocalArtifactListPath();
        DatabaseArtifactList list = new DatabaseArtifactList(false);
        Exception wentWrong = null;
        boolean remoteLoadFailed = false;
        synchronized (this.myFileLock) {
            if (listFile.exists()) {
                try {
                    list.loadList(listFile.toURI().toURL(), false);
                } catch (Exception e) {
                    wentWrong = e;
                }
                remoteLoadFailed = list.getArtifacts().isEmpty();
            }

        }
        if (remoteLoadFailed) {
            LOG.warn("Unable to load " + listFile.getName() + ". Will delete", wentWrong);
            FileUtil.delete(listFile);
            synchronized (this.myLock) {
                this.myLoaded = false;
                setLastUpdate(System.currentTimeMillis() - 86400000L + 300000L);
            }
        }
        this.myArtifactList = list;
        ApplicationManager.getApplication().invokeLater(() -> fireChanged(null));
    }

    private void setLastUpdate(Long lastUpdate) {
        synchronized (this.myLock) {
            this.myLastUpdate = lastUpdate;
        }
    }

    private void loadArtifactsList() {
        File file = getLocalArtifactListPath();
        boolean tests = ApplicationManager.getApplication().isUnitTestMode();
        if (!tests) {
            loadProvidedArtifactList(file);
        }
    }

    private void loadProvidedArtifactList(File file) {
        String remoteUrl = createVersionsUrl();
        if (remoteUrl != null) {
            try {
                byte[] text = HttpRequests.request(remoteUrl).readBytes(ProgressIndicatorProvider.getGlobalProgressIndicator());
                synchronized (this.myFileLock) {
                    FileUtil.writeToFile(file, text);
                }

            } catch (IOException e) {
                LOG.warn(e);
            }
        }
    }

    public void downloadArtifact(@NotNull DatabaseArtifactList.ArtifactVersion version, @Nullable Consumer<? super DatabaseArtifactList.ArtifactVersion> onFinish) throws IOException {
        this.myArtifactList.downloadArtifact(version);
        ApplicationManager.getApplication().invokeLater(() -> {
            if (onFinish != null) onFinish.consume(version);
            fireChanged(version);
        });
    }

    public static interface ArtifactListener extends EventListener {
        void artifactChanged(@Nullable DatabaseArtifactList.ArtifactVersion param1ArtifactVersion);
    }
}
