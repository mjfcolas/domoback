package com.manu.domoback.database.datasource;

import com.manu.domoback.conf.CONFKEYS;
import com.manu.domoback.conf.DomobackConf;
import com.manu.domoback.exceptions.BusinessException;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

public class DataSource {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataSource.class.getName());

    // JDBC driver name and database URL
    private static final String JDBC_DRIVER = DomobackConf.get(CONFKEYS.JDBC_DRIVER);
    private static final String DB_URL = DomobackConf.get(CONFKEYS.JDBC_URL);

    // database credentials
    private static final String USER_NAME = DomobackConf.get(CONFKEYS.JDBC_USERNAME);
    private static final String PASSWORD = DomobackConf.get(CONFKEYS.JDBC_PASSWORD);

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

    public static void initialize(final String driver, final String url, final String userName, final String password) throws BusinessException {
        if (datasource != null) {
            throw new BusinessException("Datasource already initialize");
        } else {
            datasource = new DataSource(driver, url, userName, password);
        }
    }

    public static boolean isIinitialized() {
        return datasource != null;
    }

    public static DataSource getInstance() {
        if (datasource == null) {
            datasource = new DataSource();
            return datasource;
        } else {
            return datasource;
        }
    }

    public Connection getConnection() throws SQLException {
        return this.cpds.getConnection();
    }
}
