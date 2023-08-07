package com.mybatis.database.connect;

import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressIndicatorProvider;
import com.intellij.openapi.util.Bitness;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.ArrayUtil;
import com.intellij.util.PathUtil;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.JBIterable;
import com.intellij.util.download.DownloadableFileDescription;
import com.intellij.util.download.DownloadableFileService;
import com.intellij.util.download.DownloadableFileSetDescription;
import com.intellij.util.download.FileDownloader;
import com.intellij.util.download.impl.DownloadableFileSetDescriptionImpl;
import com.intellij.util.io.DigestUtil;
import com.intellij.util.io.ZipUtil;
import com.mybatis.database.xml.DriverManger;
import com.mybatis.model.CacheModel.Dbms;
import com.mybatis.xml.MXParser;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.xml.XppReader;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.eclipse.aether.repository.RemoteRepository;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.aether.ArtifactRepositoryManager;
import org.jetbrains.idea.maven.aether.ProgressConsumer;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

public class DatabaseArtifactList {
    public static final String STABLE_CHANNEL = "stable";
    public static final DatabaseArtifactList EMPTY = new DatabaseArtifactList(false);
    private static final Logger LOG = Logger.getInstance(DatabaseArtifactList.class);
    private static final Version IDE_VERSION = Version.of(ApplicationInfo.getInstance().getBuild().getComponents());
    private static final File NATIVE_LIBS_ROOT = new File(PathManager.getSystemPath(), "jdbc-native-deps");
    private final Map<String, Artifact> myArtifacts = new TreeMap<>();
    private final Map<String, Channel> myChannels = new TreeMap<>();
    private final List<RemoteRepository> myRepositories = new ArrayList<>(ArtifactRepositoryManager.createDefaultRemoteRepositories());
    private final Channel myStableChannel = new Channel("stable", "Latest stable", false);

    public DatabaseArtifactList(boolean checkDuplicates, @NotNull URL... urls) {
        this.myChannels.put(this.myStableChannel.id, this.myStableChannel);
        for (URL url : urls) loadList(url, checkDuplicates);

    }

    public static boolean isValidFileList(@NotNull ArtifactVersion desc) {
        return isAllDownloaded(desc);
    }

    private static boolean isAllDownloaded(@NotNull ArtifactVersion artifact) {
        File dir = getArtifactDir(artifact);
        for (Item item : artifact.items) {
            if (item.os.applicable &&
                    item.type != Item.Type.LICENSE && !exists(dir, artifact, item)) return false;
        }
        return true;
    }

    private static boolean exists(File dir, ArtifactVersion ver, Item item) {
        if (item.type != Item.Type.MAVEN) return (new File(dir, item.name)).exists();
        Pair<List<File>, Boolean> state = item.getClassPathState(ver, dir);
        if (!state.second) return false;
        for (File file : state.first) {
            if (!file.exists()) return false;
        }
        return true;
    }

    private static void refillCache(@NotNull ArtifactVersion download, File dir, Item item) {
        item.classpath = null;
        item.getClassPathState(download, dir);
    }

    public static void unpackZippedItems(@NotNull ArtifactVersion version) {
        File dir = getArtifactDir(version);
        for (Item item : version.items) {
            if (item.os.applicable && (
                    item.type == Item.Type.NATIVE || item.type == Item.Type.PACK)) {
                unpackZipIfNeeded(dir, version, item);
            }
        }
    }

    @Nullable
    private static File unpackZipIfNeeded(File dir, @NotNull ArtifactVersion version, Item item) {
        File archive = new File(dir, item.name);
        return unpackZipIfNeeded(archive, version);
    }

    public static List<File> getNativeLibraries(@NotNull ArtifactVersion version, @NotNull Bitness bitness) {
        File dir = getArtifactDir(version);
        List<File> paths = new ArrayList<>();
        for (Item item : version.items) {
            if (!item.os.applicable || item.type != Item.Type.NATIVE) {
                continue;
            }
            File archive = new File(dir, item.name);
            File archPath = getNativeLibrary(archive, version, bitness);
            if (archPath != null) paths.add(archPath);
        }
        return paths;
    }

    public static File getNativeLibrary(@NotNull File archive, @Nullable ArtifactVersion version, @NotNull Bitness bitness) {
        File path = unpackZipIfNeeded(archive, version);
        if (path == null) return null;
        return chooseArchPath(path, bitness);
    }

    private static File chooseArchPath(File path, Bitness bitness) {
        File archFolder = new File(path, bitness.toString());
        if (!archFolder.exists()) return path;
        File sso = new File(archFolder, "SSO");
        if (sso.exists()) return sso;
        return archFolder;
    }

    private static void extractLibrary(File archive, File out, String name) {
        if (isExtracted(out))
            return;
        ProgressIndicator indicator = ProgressIndicatorProvider.getGlobalProgressIndicator();
        if (indicator != null) {
            indicator.setText2("Progress.details.extracting");
        }
        try {

            ZipUtil.extract(archive.toPath(), out.toPath(), null, true);
        } catch (IOException e) {
            LOG.warn("Failed to extract " + name, e);
            FileUtil.delete(out);
        }
    }

    private static File unpackZipIfNeeded(@NotNull File archive, @Nullable ArtifactVersion version) {
        if (!archive.exists()) return null;
        File out = getUnpackPath(archive, version);
        extractLibrary(archive, out, archive.getName());
        return out;
    }

    private static boolean isExtracted(File out) {
        return (out.exists() && !ArrayUtil.isEmpty((Object[]) out.list()));
    }

    @NotNull
    private static File getUnpackPath(File archive, @Nullable ArtifactVersion version) {
        String name = FileUtil.getNameWithoutExtension(archive);
        if (version != null) {
            return new File(new File(new File(NATIVE_LIBS_ROOT, version.artifact.id), version.version.toString()), name);
        }
        MessageDigest digest = DigestUtil.sha256();
        DigestUtil.updateContentHash(digest, archive.toPath());
        String fileHash = StringUtil.toHexString(digest.digest());
        return new File(new File(NATIVE_LIBS_ROOT, "user-provided"), name + "-" + name);
    }

    private static void readConstraint(HierarchicalStreamReader reader, @NotNull Artifact artifact) {
        Version from = Version.of(reader.getAttribute("from"));
        Version to = Version.of(reader.getAttribute("to"));
        String dbmsName = reader.getAttribute("dbms");
        Version dbForm = Version.of(reader.getAttribute("db-from"));
        Version dbTo = Version.of(reader.getAttribute("db-to"));
        Version ideForm = Version.of(reader.getAttribute("ide-from"));
        Version ideTo = Version.of(reader.getAttribute("ide-to"));
        Dbms dbms = (dbmsName == null) ? null : Dbms.byName(dbmsName);
        if (dbms == null && dbmsName != null)
            return;
        artifact.constraints.add(new Constraint(from, to, dbms, dbForm, dbTo, ideForm, ideTo));
    }

    @NotNull
    public static File getArtifactDir(@NotNull ArtifactVersion version) {
        return getArtifactDir(version.artifact, version.version);
    }

    //
//    @NotNull
    static File getArtifactDir(@NotNull Artifact artifact, @NotNull Version version) {
        File expected = getArtifactDirImpl(artifact.id, version);
        if (expected.exists()) {
            return expected;
        }
        File old = getArtifactDirImpl(artifact.name, version);
        if (old.exists()) {
            return old;
        }
        if (artifact.id.equals("MySQL ConnectorJ")) {
            old = getArtifactDirImpl("MySQL Connector/J 8", version);
            if (old.exists()) {
                return old;
            }

        }
        return expected;
    }

    @NotNull
    public static File getArtifactDir(@NotNull String name, @NotNull Version version) {
        File asIs = getArtifactDirImpl(name, version);
        if (asIs.exists()) {
            return asIs;
        }
        DatabaseArtifactList artifacts = DriverManger.getArtifacts();
        Artifact artifact = artifacts.getArtifact(name);
        return (artifact == null) ? asIs : getArtifactDir(artifact, version);
    }

    @NotNull
    private static File getArtifactDirImpl(@NotNull String artifact, @NotNull Version version) {
        return new File(new File(getDownloadPath(), artifact), version.toString());
    }

    @NotNull
    public static String getDownloadPath() {
        return System.getProperty("java.io.tmpdir") + ".idea\\jdbc-drivers";
    }

    @NotNull
    private static DownloadableFileDescription asDownloadable(@NotNull Item item) {
        return DownloadableFileService.getInstance().createFileDescription(item.url, item.name);
    }

    @NotNull
    public static DownloadableFileSetDescription asDownloadable(@NotNull ArtifactVersion version) {
        return new DownloadableFileSetDescriptionImpl(version.artifact.name, version.version
                .toString(),
                ContainerUtil.mapNotNull(version.items, item -> (item.os.applicable && item.type != Item.Type.MAVEN) ? asDownloadable(item) : null));
    }

    @NotNull
    public static List<File> getClasspathElements(@NotNull ArtifactVersion version) {
        File dir = getArtifactDir(version);
        List<File> elements = new ArrayList<>();
        for (Item item : version.items) {
            List<File> cp = item.getClassPath(version, dir);
            if (cp != null) elements.addAll(cp);
        }
        return elements;
    }

    @Nullable
    public Artifact getArtifact(@Nullable String id) {
        return (id == null) ? null : this.myArtifacts.get(id);
    }

    @Nullable
    public ArtifactVersion resolve(@NotNull String artifactId) {
        Artifact artifact = getArtifact(artifactId);
        if (artifact == null) return null;
        return artifact.get();
    }

    @Nullable
    public ArtifactVersion resolve(@NotNull String artifactId, @Nullable Version version) {
        Artifact artifact = getArtifact(artifactId);
        if (artifact == null) return null;
        return (version == null) ? null : artifact.get(version);
    }

    @Nullable
    public ArtifactVersion resolve(@NotNull String artifactId, @Nullable Version version, @Nullable String channelId, @Nullable Condition<? super ArtifactVersion> filter) {
        if (channelId != null) {
            Channel channel = getChannel(channelId);
            Artifact artifact = getArtifact(artifactId);
            return (artifact == null || channel == null) ? null : channel.getLatest(artifact, filter);
        }
        return resolve(artifactId, version);
    }

    @Nullable
    public Channel getChannel(@Nullable String id) {
        return (id == null) ? null : this.myChannels.get(id);
    }

    @NotNull
    public Set<Artifact> getArtifacts() {
        return new LinkedHashSet<>(this.myArtifacts.values());
    }

    @NotNull
    public List<Channel> getChannels() {
        return new ArrayList<>(this.myChannels.values());
    }

    public boolean isValid(@NotNull ArtifactVersion version) {
        return isValidFileList(version);
    }

    public boolean isValid(@NotNull String name, @Nullable Version version) {
        Artifact artifact = getArtifact(name);
        ArtifactVersion desc = (artifact == null || version == null) ? null : artifact.get(version);
        if (desc != null) return isValid(desc);
        return (version != null && getArtifactDir(name, version).exists());
    }

    public boolean isFreshFiles(@NotNull ArtifactVersion version) {
        return isAllDownloaded(version);
    }

    public void downloadArtifact(@NotNull ArtifactVersion download) throws IOException {
        File dir = getArtifactDir(download);
        try {
            DownloadableFileSetDescription description = asDownloadable(download);
            if (!description.getFiles().isEmpty()) {
                FileDownloader downloader = DownloadableFileService.getInstance().createDownloader(description);
                downloader.download(dir);
            }

            try {
                downloadMavenItems(download, dir);
            } catch (Exception e) {
                throw new IOException("Download from maven failed", e);
            }
            unpackZippedItems(download);
        } finally {

            for (Item item : download.items) {
                refillCache(download, dir, item);
            }
        }
    }

    private void downloadMavenItems(@NotNull ArtifactVersion download, File dir) throws Exception {
        for (Item item : download.items) {
            if (item.type == Item.Type.MAVEN) {
                resolveMavenItem(dir, item, true);
            }
        }
    }

    public void loadList(@NotNull URL url, boolean checkDuplicates) {
        try {
            InputStream stream = url.openStream();
            try {
                String file = PathUtil.getFileName(url.getPath());
                if (!file.startsWith("test-") && !file.startsWith("builtin-")) file = null;
                loadList(new XppReader(new InputStreamReader(stream, StandardCharsets.UTF_8), new MXParser()), checkDuplicates, file);
                stream.close();
            } catch (Throwable throwable) {
                if (stream != null)
                    try {
                        stream.close();
                    } catch (Throwable throwable1) {
                        throwable.addSuppressed(throwable1);
                    }
                throw throwable;
            }
        } catch (IOException e) {
            if (ApplicationManager.getApplication().isUnitTestMode()) {
                LOG.error(e);
            } else {

                LOG.warn(e);
            }
        }

    }

    void loadList(@NotNull HierarchicalStreamReader reader, boolean checkDuplicates, @Nullable String source) throws IOException {
        if (!"artifacts".equals(reader.getNodeName())) {
            throw new IOException("Unexpected root: " + reader.getNodeName());
        }
        while (reader.hasMoreChildren()) {
            Artifact artifact;
            String id;
            reader.moveDown();
            switch (reader.getNodeName()) {
                case "repository":
                    readRepository(reader);
                    break;
                case "artifact":
                    readArtifact(reader, checkDuplicates, source);
                    break;
                case "channel":
                    readChannel(reader, null, null);
                    break;
                case "constraint":
                    id = reader.getAttribute("artifact-id");
                    if (id == null) throw new IOException("No artifact id for constraint");
                    artifact = getArtifact(id);
                    if (artifact == null) throw new IOException("Unable to find artifact " + id);
                    readConstraint(reader, artifact);
                    break;
            }
            reader.moveUp();
        }
    }

    private void readChannel(HierarchicalStreamReader reader, @Nullable String artifactId, @Nullable Version version) {
        String id = reader.getAttribute("id");
        Channel channel = this.myChannels.get(id);
        if (channel == null) {
            String name = reader.getAttribute("name");
            String implicit = reader.getAttribute("implicit");
            channel = new Channel(id, name, "true".equals(implicit));
            this.myChannels.put(channel.id, channel);
        }
        if (artifactId == null) {
            while (reader.hasMoreChildren()) {
                reader.moveDown();
                if ("artifact".equals(reader.getNodeName())) {
                    artifactId = reader.getAttribute("id");
                    version = Version.parse(reader.getAttribute("version"));
                    if (artifactId != null && version != null) {
                        channel.addHead(artifactId, version);
                    }
                }
                reader.moveUp();
            }
        } else {

            String versionStr = reader.getAttribute("version");
            if (versionStr != null) version = Version.parse(versionStr);
            if (version != null) {
                channel.addHead(artifactId, version);
            }
        }
    }

    private void readRepository(HierarchicalStreamReader reader) throws IOException {
        String id = reader.getAttribute("id");
        if (id == null) throw new IOException("No id for repository");
        String url = reader.getAttribute("url");
        if (url == null) throw new IOException("No url for repository");
        RemoteRepository repo = ArtifactRepositoryManager.createRemoteRepository(id, url);
        this.myRepositories.add(repo);
    }

    private void readArtifact(HierarchicalStreamReader reader, boolean checkDuplicates, @Nullable String source) throws IOException {
        String id = reader.getAttribute("id");
        String name = reader.getAttribute("name");
        if (id == null) id = StringUtil.trimEnd(name.replaceAll("[^a-zA-Z0-9. _-]", ""), " 8");
        if ("Redshift 2".equals(id)) {


            id = "Redshift";
            name = "Redshift";
        }
        Artifact artifact = this.myArtifacts.get(id);
        if (artifact == null) {
            artifact = new Artifact(id, name);
            checkIndexConsistency(artifact);
            this.myArtifacts.put(artifact.id, artifact);
        }
        this.myArtifacts.put(name, artifact);
        artifact.names.add(name);
        ArtifactVersion prevVersion = null;
        if (reader.getAttribute("version") == null) {
            while (reader.hasMoreChildren()) {
                reader.moveDown();
                switch (reader.getNodeName()) {
                    case "version":
                        prevVersion = readArtifactVersion(reader, artifact, checkDuplicates, source);
                        break;
                    case "constraint":
                        readConstraint(reader, artifact);
                        break;
                    case "channel":
                        readChannel(reader, artifact.id, (prevVersion == null) ? null : prevVersion.version);
                        break;
                }
                reader.moveUp();
            }
        } else {

            readArtifactVersion(reader, artifact, checkDuplicates, source);
        }
    }

    private void checkIndexConsistency(Artifact artifact) throws IOException {
        Artifact byId = this.myArtifacts.get(artifact.id);
        Artifact byName = this.myArtifacts.get(artifact.name);
        if (byId != byName) {
            throw new IOException("id/name inconsistency: " + (

                    (byId == null) ? null : (byId.id + ":" + byId.id)) + "/" + (
                    (byName == null) ? null : (byName.id + ":" + byName.id)));
        }
    }

    private ArtifactVersion readArtifactVersion(HierarchicalStreamReader reader, @NotNull Artifact artifact, boolean checkDuplicates, @Nullable String source) {
        Version version = Version.of(reader.getAttribute("version"));
        List<Item> items = new ArrayList<>();
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            switch (reader.getNodeName()) {
                case "item":
                    items.add(readItem(reader));
                    break;
                case "channel":
                    readChannel(reader, artifact.id, version);
                    break;
            }
            reader.moveUp();
        }
        ArtifactVersion av = new ArtifactVersion(artifact, version, items, source);
        if (!artifact.versions.add(av) && checkDuplicates) {
            LOG.error("Duplicate artifact version " + av);
        }
        return av;
    }

    private Item readItem(HierarchicalStreamReader reader) {
        String url = reader.getAttribute("url");
        String name = reader.getAttribute("name");
        if (name == null) name = PathUtil.getFileName(url);

        String osName = reader.getAttribute("os");
        Item.OS os = (osName == null) ? null : ContainerUtil.find(Item.OS.values(), o -> o.name().equalsIgnoreCase(osName));
        if (os == null) os = name.contains("win") ? Item.OS.WIN : Item.OS.ANY;

        String typeName = reader.getAttribute("type");
        Item.Type type = ContainerUtil.find(Item.Type.values(), o -> o.name().equalsIgnoreCase(typeName));
        if (type == null) {
            if (name.endsWith(".txt")) {
                type = Item.Type.LICENSE;
            } else if ((name.startsWith("sqlserver") || name.startsWith("jtds")) && name.endsWith("win-auth.jar")) {
                type = Item.Type.NATIVE;
            } else {

                type = Item.Type.JAR;
            }
        }
        return new Item(this, name, url, os, type);
    }

    @NotNull
    private Collection<File> resolveMavenItem(File dir, Item item, boolean download) throws Exception {
        final ProgressIndicator indicator = ProgressIndicatorProvider.getGlobalProgressIndicator();
        if (indicator != null) {
            indicator.setText("{0, choice, 0#Downloading |1#Resolving }{1}");
//            indicator.setText(new DatabaseBundle().getMessage("", new Object[]{download ? 0 : 1, item.url}));

        }


        ArtifactRepositoryManager manager = new ArtifactRepositoryManager(dir, getRepositories(), (indicator != null) ? new ProgressConsumer() {
            public void consume(String message) {
                indicator.setText2(message);
            }

            public boolean isCanceled() {
                return indicator.isCanceled();
            }
        } : ProgressConsumer.DEAF, !download);
        List<String> coords = StringUtil.split(item.url, ":");
        if (coords.size() != 3) {
            throw new IOException("Illegal maven coordinates " + item.url + " should be group:artifact:version");
        }

        return manager.resolveDependency(coords.get(0), coords.get(1), coords.get(2), true, Collections.emptyList());
    }

    @NotNull
    private List<RemoteRepository> getRepositories() {
        return this.myRepositories;
    }

    public enum OS {
        ANY(true), LINUX(SystemInfo.isLinux), MAC(SystemInfo.isMac), WIN(SystemInfo.isWindows);

        public final boolean applicable;

        OS(boolean applicable) {
            this.applicable = applicable;
        }
    }

    public enum Type {JAR, PACK, NATIVE, LICENSE, MAVEN}


    public interface VersionRef {
        @Nls
        String getVersionDisplayName();
    }

    public static final class Item {
        public final String name;
        public final String url;
        public final OS os;
        public final Type type;
        private final DatabaseArtifactList list;
        public volatile Pair<List<File>, Boolean> classpath;

        public Item(@NotNull DatabaseArtifactList list, @NotNull String name, @NotNull String url, @NotNull OS os, @NotNull Type type) {
            this.list = list;
            this.name = name;
            this.url = url;
            this.os = os;
            this.type = type;
        }

        public String toString() {
            return this.name;
        }

        @Nullable
        public List<File> getClassPath(DatabaseArtifactList.ArtifactVersion ver, File dir) {
            return (getClassPathState(ver, dir)).first;
        }

        @NotNull
        private Pair<List<File>, Boolean> getClassPathState(DatabaseArtifactList.ArtifactVersion ver, File dir) {
            Pair<List<File>, Boolean> cp = this.classpath;
            if (cp != null) {
                return cp;
            }
            Pair<List<File>, Boolean> newClasspath = resolveClassPath(ver, dir);
            this.classpath = newClasspath;
            return newClasspath;
        }

        @NotNull
        private Pair<List<File>, Boolean> resolveClassPath(DatabaseArtifactList.ArtifactVersion ver, File dir) {
            if (this.type == Type.JAR) {
                return Pair.create(Collections.singletonList(new File(dir, this.name)), Boolean.TRUE);
            }
            if (this.type == Type.PACK) {
                File root = DatabaseArtifactList.unpackZipIfNeeded(dir, ver, this);
                List<File> files = JBIterable.of((root == null) ? null : root.listFiles()).filter(f -> StringUtil.endsWithIgnoreCase(f.getName(), ".jar")).toList();
                return Pair.create(files, Boolean.TRUE);
            }
            if (this.type == Type.MAVEN) {
                try {
                    return Pair.create(new ArrayList<>(this.list.resolveMavenItem(dir, this, false)), Boolean.TRUE);
                } catch (ArtifactResolutionException ignore) {
                    return Pair.create(Collections.emptyList(), Boolean.FALSE);
                } catch (Exception e) {
                    DatabaseArtifactList.LOG.warn(e);
                    return Pair.create(Collections.emptyList(), Boolean.FALSE);
                }
            }
            return Pair.create(Collections.emptyList(), Boolean.TRUE);
        }


        public enum OS {
            ANY(true), LINUX(SystemInfo.isLinux), MAC(SystemInfo.isMac), WIN(SystemInfo.isWindows);
            public final boolean applicable;

            OS(boolean applicable) {
                this.applicable = applicable;
            }
        }

        public enum Type {
            JAR, PACK, NATIVE, LICENSE, MAVEN
        }
    }

    public static final class Artifact {
        @NonNls
        public final String id;

        public final String name;
        public final SortedSet<DatabaseArtifactList.ArtifactVersion> versions;
        public final List<DatabaseArtifactList.Constraint> constraints;
        final Set<String> names = new HashSet<>();

        public Artifact(@NotNull String id, @NotNull String name) {
            this.id = id;
            this.name = name;
            this.versions = new TreeSet<>();
            this.constraints = new ArrayList<>();
            this.names.add(id);
            this.names.add(name);
        }

        @Nullable
        public DatabaseArtifactList.ArtifactVersion get() {
            return this.versions.first();
        }


        @Nullable
        public DatabaseArtifactList.ArtifactVersion get(@NotNull Version version) {
            return ContainerUtil.find(this.versions, v -> v.version.equals(version));
        }


        public String toString() {
            return this.name;
        }


        public boolean isId(String id) {
            return this.names.contains(id);
        }
    }

    public static final class ArtifactVersion
            implements Comparable<ArtifactVersion>, VersionRef {
        public final DatabaseArtifactList.Artifact artifact;

        public final Version version;

        public final List<DatabaseArtifactList.Item> items;

        public final String source;

        public ArtifactVersion(@NotNull DatabaseArtifactList.Artifact artifact, @NotNull Version version, @NotNull List<DatabaseArtifactList.Item> items, @Nullable String source) {
            this.artifact = artifact;
            this.version = version;
            this.items = items;
            this.source = source;
        }


        public int compareTo(@NotNull ArtifactVersion o) {
            return o.version.compareTo(this.version);
        }


        public boolean matches(@Nullable Dbms dbms, @Nullable Version version) {
            return matches(dbms, version, version);
        }


        public boolean matches(@Nullable Dbms dbms, @Nullable Version dbFrom, @Nullable Version dbTo) {
            DatabaseArtifactList.Constraint constraint = ContainerUtil.find(this.artifact.constraints, c -> c.matches(null, null, null, null));
            if (constraint == null) return true;
            constraint = ContainerUtil.find(this.artifact.constraints, c -> c.matches(this.version, null, null, null));
            if (constraint == null) return true;
            constraint = ContainerUtil.find(this.artifact.constraints, c -> c.matches(this.version, dbms, null, null));
            if (constraint == null) return true;
            constraint = ContainerUtil.find(this.artifact.constraints, c -> c.matches(this.version, dbms, dbFrom, dbTo));
            return (constraint != null);
        }


        public String getVersionDisplayName() {
            String verS = this.version.toString();
            return (this.source == null) ? verS : (verS + " (" + verS + ")");
        }


        public String toString() {
            return this.artifact.name + ":" + this.artifact.name;
        }
    }

    public static final class Channel
            implements VersionRef {
        public final String id;

        public final String name;
        public final boolean implicit;
        public final Map<String, Version> heads;

        public Channel(@NotNull String id, @NotNull String name, boolean implicit) {
            this.id = id;
            this.name = name;
            this.implicit = implicit;
            this.heads = new LinkedHashMap<>();
        }

        private void addHead(@NotNull String artifactId, @NotNull Version version) {
            Version prev = this.heads.get(artifactId);
            if (prev == null || prev.less(version)) {
                this.heads.put(artifactId, version);
            }
        }

        public boolean contains(@NotNull DatabaseArtifactList.ArtifactVersion version) {
            Version head = getHead(version.artifact.id);
            return head.isOrGreater(version.version);
        }


        public boolean isEnabledFor(@NotNull DatabaseArtifactList.Artifact artifact) {
            return (!this.implicit || this.heads.containsKey(artifact.id));
        }


        private Version getHead(String artifactId) {
            return this.heads.getOrDefault(artifactId, this.implicit ? null : Version.INFINITY);
        }


        @Nullable
        public DatabaseArtifactList.ArtifactVersion getLatest(@NotNull DatabaseArtifactList.Artifact artifact, @Nullable Condition<? super DatabaseArtifactList.ArtifactVersion> filter) {
            Version head = getHead(artifact.id);
            return (head == null) ? null : ContainerUtil.find(artifact.versions, v -> (head.isOrGreater(v.version) && (filter == null || filter.value(v))));
        }


        public String getVersionDisplayName() {
            return this.name;
        }


        public String toString() {
            return "Channel: " + this.name;
        }
    }

    public static final class Constraint {
        @Nullable
        public final Version from;
        @Nullable
        public final Version to;
        @Nullable
        public final Dbms dbms;
        @Nullable
        public final Version dbFrom;
        @Nullable
        public final Version dbTo;
        @Nullable
        public final Version ideFrom;
        @Nullable
        public final Version ideTo;

        public Constraint(@Nullable Version from, @Nullable Version to, @Nullable Dbms dbms, @Nullable Version dbFrom, @Nullable Version dbTo, @Nullable Version ideFrom, @Nullable Version ideTo) {
            this.from = from;
            this.to = to;
            this.dbms = dbms;
            this.dbFrom = dbFrom;
            this.dbTo = dbTo;
            this.ideFrom = ideFrom;
            this.ideTo = ideTo;
        }


        public boolean matches(@Nullable Version ver, @Nullable Dbms dbms, @Nullable Version dbVer) {
            return matches(ver, dbms, dbVer, dbVer);
        }


        public boolean matches(@Nullable Version ver, @Nullable Dbms dbms, @Nullable Version dbFrom, @Nullable Version dbTo) {
            if (this.ideFrom != null && DatabaseArtifactList.IDE_VERSION.less(this.ideFrom)) return false;
            if (this.ideTo != null && DatabaseArtifactList.IDE_VERSION.isOrGreater(this.ideTo)) return false;

            if (ver != null && this.from != null && ver.less(this.from)) return false;
            if (ver != null && this.to != null && ver.isOrGreater(this.to)) return false;
            if (dbms != null && this.dbms != null && this.dbms != dbms) return false;
            if (dbFrom != null && this.dbFrom != null && dbFrom.less(this.dbFrom)) return false;
            return dbTo == null || this.dbTo == null || !dbTo.isOrGreater(this.dbTo);
        }
    }
}
