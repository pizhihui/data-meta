package com.archgeek.data.meta.jdbc;

import java.util.Map;
import java.util.Properties;

/**
 * @author pizhihui
 * @date 2024-10-28 14:37
 */
public class JdbcConfig {


    public static final String JDBC_DRIVER_NAME = "jdbc-driver";
    public static final String JDBC_URL = "jdbc-url";
    public static final String JDBC_USER = "jdbc-user";
    public static final String JDBC_PASWORD = "jdbc-password";
    public static final String JDBC_DATABASE = "jdbc-database";


    private final Properties properties = new Properties();

    public JdbcConfig() {

    }

    public JdbcConfig(Map<String, String> maps) {
        properties.putAll(maps);
    }

    public String getJdbcDriverName() {
        return properties.getProperty(JDBC_DRIVER_NAME);
    }

    public String getJdbcDriverUrl() {
        return properties.getProperty(JDBC_URL);
    }

    public String getJdbcDriverUser() {
        return properties.getProperty(JDBC_USER);
    }

    public String getJdbcDriverPasword() {
        return properties.getProperty(JDBC_PASWORD);
    }

}
