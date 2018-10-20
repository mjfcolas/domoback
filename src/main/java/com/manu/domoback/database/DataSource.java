package com.manu.domoback.database;

import com.manu.domoback.common.Bundles;
import com.manu.domoback.exceptions.BusinessException;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

class DataSource {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataSource.class.getName());

    // JDBC driver name and database URL
    private static final String JDBC_DRIVER = Bundles.prop().getProperty("jdbc.driver");
    private static final String DB_URL = Bundles.prop().getProperty("jdbc.url");

    // database credentials
    private static final String USER_NAME = Bundles.prop().getProperty("jdbc.username");
    private static final String PASSWORD = Bundles.prop().getProperty("jdbc.password");

    private static DataSource datasource;
    private ComboPooledDataSource cpds;

    private DataSource() {
        this(JDBC_DRIVER, DB_URL, USER_NAME, PASSWORD);
    }

    private DataSource(final String driver, final String url, final String userName, final String password) {
        try {
            this.cpds = new ComboPooledDataSource();
            this.cpds.setDriverClass(driver); //loads the jdbc driver
            this.cpds.setJdbcUrl(url);
            this.cpds.setUser(userName);
            this.cpds.setPassword(password);
            //Max connection age in order to avoid connection closing from outside causes (firewall, nat...)
            this.cpds.setMaxConnectionAge(120);
        } catch (final PropertyVetoException e) {
            LOGGER.error("An error occured", e);
            System.exit(1);
        }

    }

    static void initialize(final String driver, final String url, final String userName, final String password) throws BusinessException {
        if (datasource != null) {
            throw new BusinessException("Datasource already initialize");
        } else {
            datasource = new DataSource(driver, url, userName, password);
        }
    }

    static boolean isIinitialized() {
        return datasource != null;
    }

    static DataSource getInstance() {
        if (datasource == null) {
            datasource = new DataSource();
            return datasource;
        } else {
            return datasource;
        }
    }

    Connection getConnection() throws SQLException {
        return this.cpds.getConnection();
    }
}
