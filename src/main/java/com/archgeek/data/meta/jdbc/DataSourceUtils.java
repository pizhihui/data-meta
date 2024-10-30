package com.archgeek.data.meta.jdbc;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.ha.DataSourceCreator;

import javax.sql.DataSource;

/**
 * @author pizhihui
 * @date 2024-10-28 14:36
 */
public class DataSourceUtils {

    private static final String POOL_TEST_QUERY = "SELECT 1";


    public static DataSource createDataSource(JdbcConfig jdbcConfig) {

        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName(jdbcConfig.getJdbcDriverName());
        druidDataSource.setUrl(jdbcConfig.getJdbcDriverUrl());

        String jdbcDriverUser = jdbcConfig.getJdbcDriverUser();
        if (null != jdbcDriverUser && !jdbcDriverUser.isEmpty()) {
            druidDataSource.setUsername(jdbcDriverUser);
        }
        String jdbcDriverPasword = jdbcConfig.getJdbcDriverPasword();
        if (null != jdbcDriverPasword && !jdbcDriverPasword.isEmpty()) {
            druidDataSource.setPassword(jdbcDriverPasword);
        }
        druidDataSource.setMinIdle(2);
        druidDataSource.setMaxActive(10);
        druidDataSource.setTestOnBorrow(true);
        druidDataSource.setValidationQuery(POOL_TEST_QUERY);
        return druidDataSource;

    }



}
