package com.mybatis.model.CacheModel;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.extensions.AbstractExtensionPointBean;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.ConcurrencyUtil;
import com.intellij.util.ReflectionUtil;
import com.intellij.util.xmlb.annotations.Attribute;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;

public class Dbms implements Comparable<Dbms> {
    private static final Logger LOG = Logger.getInstance(Dbms.class);
    private static final ConcurrentMap<String, Dbms> ourValues = new ConcurrentHashMap<>();
    private static final List<Pair<Pattern, Dbms>> ourDetectors = new CopyOnWriteArrayList<>();
    public static final Dbms UNKNOWN = create("UNKNOWN", AllIcons.Providers.Mysql);
    public static final Dbms ORACLE = create("Oracle", AllIcons.Providers.Oracle);
    public static final Dbms MEMSQL = create("MemSQL", AllIcons.Providers.Mysql);
    public static final Dbms MARIA = create("MariaDB", AllIcons.Providers.Mariadb);
    public static final Dbms MYSQL_AURORA = create("MYSQL_AURORA", "Amazon Aurora MySQL", AllIcons.Providers.Mysql);
    public static final Dbms MYSQL = create("MySQL", AllIcons.Providers.Mysql);
    //    public static final Dbms MYSQL_5 = create("MySQL 5.1", AllIcons.Providers.Mysql);
    public static final Dbms GITBASE = create("gitbase", AllIcons.Providers.Greenplum);
    public static final Dbms POSTGRES = create("POSTGRES", "PostgreSQL", AllIcons.Providers.Postgresql);
    public static final Dbms REDSHIFT = create("REDSHIFT", "Amazon Redshift", AllIcons.Providers.Redshift);
    public static final Dbms GREENPLUM = create("GREENPLUM", "Greenplum", AllIcons.Providers.Greenplum);
    public static final Dbms SYBASE = create("SYBASE", "Sybase ASE", AllIcons.Providers.Sybase, defaultPattern("sybase|adaptive server") + "|ase.*");
    public static final Dbms MSSQL = create("MSSQL", "Microsoft SQL Server", AllIcons.Providers.SqlServer, defaultPattern("microsoft|sqlserver|jtds"));
    public static final Dbms AZURE = create("AZURE", "Azure SQL Database", AllIcons.Providers.Azure);
    public static final Dbms DB2_LUW = create("DB2_LUW", "IBM Db2 LUW", AllIcons.Providers.DB2);
    public static final Dbms DB2_IS = create("DB2_IS", "IBM Db2 iSeries", AllIcons.Providers.DB2);
    public static final Dbms DB2_ZOS = create("DB2_ZOS", "IBM Db2 z/OS", AllIcons.Providers.DB2);
    public static final Dbms DB2 = create("DB2", "IBM Db2", AllIcons.Providers.DB2, "(?i).*(?:\\bDB2|[:.]as400[:.]).*");
    public static final Dbms SQLITE = create("SQLite", AllIcons.Providers.Sqlite);
    public static final Dbms HSQL = create("HSQLDB", "HSQLDB", AllIcons.Providers.Hsqldb, defaultPattern("hsql"));
    public static final Dbms H2 = create("H2", AllIcons.Providers.H2);
    public static final Dbms DERBY = create("DERBY", "Apache Derby", AllIcons.Providers.ApacheDerby);
    public static final Dbms EXASOL = create("EXASOL", "Exasol", AllIcons.Providers.Exasol, defaultPattern("exasol|exa"));
    public static final Dbms CLICKHOUSE = create("ClickHouse", AllIcons.Providers.ClickHouse);
    public static final Dbms CASSANDRA = create("CASSANDRA", "Apache Cassandra", AllIcons.Providers.Cassandra);
    public static final Dbms VERTICA = create("Vertica", AllIcons.Providers.Vertica);
    public static final Dbms HIVE = create("HIVE", "Apache Hive", AllIcons.Providers.Hive);
    public static final Dbms SPARK = create("SPARK", "Apache Spark", AllIcons.Providers.Spark);
    public static final Dbms SNOWFLAKE = create("Snowflake", AllIcons.Providers.Snowflake);
    public static final Dbms MONGO = create("MONGO", "MongoDB", AllIcons.Providers.MongoDB);
    public static final Dbms COCKROACH = create("COCKROACH", "CockroachDB", AllIcons.Providers.CockroachDB);
    public static final Dbms BIGQUERY = create("BIGQUERY", "BigQuery", AllIcons.Providers.BigQuery);
    public static final Dbms COUCHBASE = create("COUCHBASE", "Couchbase", AllIcons.Providers.Couchbase);
    public static final Dbms HANA = create("HANA", "HANA", AllIcons.Providers.HANA, defaultPattern("sap|hana|hdb"));
    public static final Dbms FIREBIRD = create("Firebird", AllIcons.Providers.Firebird);
    public static final Dbms PRESTO = create("Presto", AllIcons.Providers.Presto);
    public static final Dbms INFORMIX = create("INFORMIX", "Informix", AllIcons.Providers.Informix, defaultPattern("informix") + "|ids.*");
    public static final Dbms TERADATA = create("Teradata", AllIcons.Providers.Teradata);
    private final String myName;
    private final String myDisplayName;
    private final Icon myIcon;

    private Dbms(@NotNull String name, @NotNull String displayName, @NotNull Icon icon) {
        this.myName = name;
        this.myDisplayName = displayName;
        this.myIcon = icon;
    }

    @NotNull
    public static Dbms create(@NotNull String displayName, @NotNull Icon icon) {
        return create(displayName, displayName, icon);
    }

    @NotNull
    public static Dbms create(@NotNull String name, @NotNull String displayName, @NotNull Icon icon) {
        return create(name, displayName, icon, null);
    }

    @NotNull
    public static Dbms create(@NotNull String name, @NotNull String displayName, @NotNull Icon icon, @RegExp @Nullable String detectPattern) {
        name = StringUtil.toUpperCase(name);
        Dbms existing = ourValues.get(name);
        if (existing != null) {
            return existing;
        }
        Dbms proposed = new Dbms(name, displayName, icon);
        Dbms result = ConcurrencyUtil.cacheOrGet(ourValues, name, proposed);
        if (proposed == result) {
            String pattern = (detectPattern == null) ? defaultPattern(name) : detectPattern;
            ourDetectors.add(Pair.create(Pattern.compile(pattern), result));
        }
        return result;
    }

    private static String defaultPattern(@NotNull String name) {
        return String.format("(?i).*\\b(?:%s).*", name.replace("_", ".*"));
    }

    @NotNull
    public static Dbms fromString(@Nullable String text) {
        if (text == null) {
            return UNKNOWN;
        }
        for (Pair<Pattern, Dbms> detector : ourDetectors) {
            if (((Pattern) detector.first).matcher(text).matches()) {
                return (Dbms) detector.second;
            }
        }


        String pattern = "(?i).*\\b(?:%s).*";
        for (Dbms dbms : allValues()) {
            if (text.matches(String.format(pattern, dbms.getName()))) {
                return dbms;
            }

        }
        return UNKNOWN;
    }

    @Nullable
    public static Dbms byName(@NotNull String name) {
        return LazyData.allDbms().get(name);
    }

    @NotNull
    public static Collection<Dbms> allValues() {
        return LazyData.allDbms().values();
    }

    @NotNull
    public String getName() {
        return this.myName;
    }

    @NotNull
    public String getDisplayName() {
        return this.myDisplayName;
    }

    @NotNull
    public Icon getIcon() {
        return this.myIcon;
    }

    public String toString() {
        return getName();
    }

    public boolean isOracle() {
        return (this == ORACLE);
    }

    public boolean isMysql() {
        return (this == MYSQL || this == MYSQL_AURORA || this == MARIA || this == MEMSQL || this == GITBASE);
    }

    public boolean isPostgres() {
        return (this == POSTGRES || this == REDSHIFT || this == GREENPLUM || this == COCKROACH);
    }

    public boolean isBigquery() {
        return (this == BIGQUERY);
    }

    public boolean isRedshift() {
        return (this == REDSHIFT);
    }

    public boolean isGreenplum() {
        return (this == GREENPLUM);
    }

    public boolean isVertica() {
        return (this == VERTICA);
    }

    public boolean isMicrosoft() {
        return (this == MSSQL || this == AZURE);
    }

    public boolean isSybase() {
        return (this == SYBASE);
    }

    public boolean isDb2() {
        return (this == DB2_LUW || this == DB2_IS || this == DB2_ZOS || this == DB2);
    }

    public boolean isHsqldb() {
        return (this == HSQL);
    }

    public boolean isH2() {
        return (this == H2);
    }

    public boolean isDerby() {
        return (this == DERBY);
    }

    public boolean isSqlite() {
        return (this == SQLITE);
    }

    public boolean isTransactSql() {
        return (isMicrosoft() || isSybase());
    }

    public boolean isExasol() {
        return (this == EXASOL);
    }

    public boolean isClickHouse() {
        return (this == CLICKHOUSE);
    }

    public boolean isCassandra() {
        return (this == CASSANDRA);
    }

    public boolean isHive() {
        return (this == HIVE);
    }

    public boolean isSpark() {
        return (this == SPARK);
    }

//    @NotNull
//    public static Dbms forConnection(@Nullable RawConnectionConfig o) {
//        if (o == null) {
//            return UNKNOWN;
//        }
//        Dbms result = fromString(o.getUrl());
//        if (result != UNKNOWN) {
//            return result;
//        }
//        return fromString(o.getDriverClass());
//    }

    public boolean isSnowflake() {
        return (this == SNOWFLAKE);
    }

    public boolean isMongo() {
        return (this == MONGO);
    }

    public boolean isCouchbase() {
        return (this == COUCHBASE);
    }

    public int compareTo(@NotNull Dbms o) {
        return Comparing.compare(getName(), o.getName());
    }

    public static class DbmsBean extends AbstractExtensionPointBean {
        @Attribute("instance")
        public String instance;
    }

    private static class LazyData {
        private static final ExtensionPointName<DbmsBean> CONFIG_EP = ExtensionPointName.create("com.mybatis.database.dbms");

        static {
            for (Dbms.DbmsBean bean : CONFIG_EP.getExtensionsIfPointIsRegistered()) {
                int dotIdx = bean.instance.lastIndexOf('.');
                if (dotIdx < 0) {
                    LOG.warn("Class field reference should contain `.` in: " + bean.instance + "[" + bean.getPluginId() + "]");
                    continue;
                }
                try {
                    Class<?> holder = Class.forName(bean.instance.substring(0, dotIdx), true, bean.getLoaderForClass());
                    Dbms dbms = ReflectionUtil.getStaticFieldValue(holder, Dbms.class, bean.instance.substring(dotIdx + 1));
                    if (dbms == null) {
                        LOG.warn("Static field not found: " + bean.instance + "[" + bean.getPluginId() + "]");
                        continue;
                    }
                    assert ourValues.get(dbms.getName()) == dbms;
                } catch (Exception e) {
                    LOG.warn("Unable to find dbms: " + bean.instance + "[" + bean.getPluginId() + "]", e);
                }
            }
        }

        private static Map<String, Dbms> allDbms() {
            return ourValues;
        }
    }
}

