package com.mybatis.enums;

public enum DataBaseType {
    MySQL_5("com.mysql.jdbc.Driver", "jdbc:mysql://%s:%s/%s?useUnicode=true&useSSL=false&characterEncoding=%s", "mysql-connector-java-5.1.38.jar"),
    Oracle("oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@%s:%s:%s", "oracleJDBC.jar"),
    MySql("com.mysql.cj.jdbc.Driver", "jdbc:mysql://%s:%s/%s?useUnicode=true&useSSL=false&characterEncoding=%s&useLegacyDatetimeCode=false&serverTimezone=UTC&allowPublicKeyRetrieval=true", "mysqlJDBC.jar"),
    SqlServer("com.microsoft.sqlserver.jdbc.SQLServerDriver", "jdbc:sqlserver://%s:%s;databaseName=%s", "sqlserverJDBC.jar"),
    PostgreSQL("org.postgresql.Driver", "jdbc:postgresql://%s", "postgresqlJDBC.jar"),
    Sqlite("org.postgresql.Driver", "jdbc:sqlite://%s", "sqliteJDBC.jar"),
    UNKNOWN("", "", "");
    private final String driverClass;
    private final String connectionUrlPattern;
    private final String connectorJarFile;

    DataBaseType(String driverClass, String connectionUrlPattern, String connectorJarFile) {
        this.driverClass = driverClass;
        this.connectionUrlPattern = connectionUrlPattern;
        this.connectorJarFile = connectorJarFile;
    }

    public String getDriverClass() {
        return driverClass;
    }

    public String getConnectionUrlPattern() {
        return connectionUrlPattern;
    }

    public String getConnectorJarFile() {
        return connectorJarFile;
    }
}
