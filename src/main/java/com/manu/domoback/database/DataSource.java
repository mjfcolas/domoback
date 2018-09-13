package com.manu.domoback.database;

import com.manu.domoback.common.Bundles;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

public class DataSource {
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
        try {
            cpds = new ComboPooledDataSource();
            cpds.setDriverClass(JDBC_DRIVER); //loads the jdbc driver
            cpds.setJdbcUrl(DB_URL);
            cpds.setUser(USER_NAME);
            cpds.setPassword(PASSWORD);
            //Max connection age in order to avoid connection closing from outside causes (firewall, nat...)
            cpds.setMaxConnectionAge(120);
        } catch (PropertyVetoException e) {
            LOGGER.error("An error occured", e);
            System.exit(1);
        }

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
