package com.manu.domoback.database;

import com.manu.domoback.common.Bundles;
import com.manu.domoback.common.CustLogger;
import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.beans.PropertyVetoException;
import java.sql.*;

public class Jdbc implements IJdbc {

    // JDBC driver name and database URL
    static final String JDBC_DRIVER = Bundles.prop().getProperty("jdbc.driver");
    static final String DB_URL = Bundles.prop().getProperty("jdbc.url");

    //  mysql database credentials
    static final String USER_NAME = Bundles.prop().getProperty("jdbc.username");
    static final String PASSWORD = Bundles.prop().getProperty("jdbc.password");

    static final ComboPooledDataSource cpds = new ComboPooledDataSource();

    static {
        try {
            cpds.setDriverClass(JDBC_DRIVER); //loads the jdbc driver
            cpds.setJdbcUrl(DB_URL);
            cpds.setUser(USER_NAME);
            cpds.setPassword(PASSWORD);
        } catch (PropertyVetoException e) {
            CustLogger.errprintln(e.toString());
        }
    }

    private Connection getConnection() throws SQLException {
        return cpds.getConnection();
    }

    public static void closeConnection(Connection connection) throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    public Boolean getCommandeChauffage() throws SQLException {
        CustLogger.traprintln("Jdbc.getCommandeChauffage");
        Connection connection = null;
        try {
            connection = getConnection();
            Boolean currentMode = null;

            try (Statement statement = connection.createStatement()) {
                currentMode = getCommandeChauffageInternal(statement);
                if (currentMode == null) {
                    throw new SQLException("Aucune valeur de commande déja présente");
                }
            }

            return currentMode;
        } finally {
            closeConnection(connection);
        }
    }

    public Boolean switchCommandeChauffage() throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
            Boolean currentMode = null;
            String sql = "INSERT INTO com_chauff (onoff) VALUES (?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
                 Statement statement = connection.createStatement()
            ) {
                currentMode = getCommandeChauffageInternal(statement);
                preparedStatement.setBoolean(1, !currentMode);
                preparedStatement.executeUpdate();
            }
            return !currentMode;
        } finally {
            closeConnection(connection);
        }
    }

    public void saveMeteoInfos(Float temperature, Float pressionRel, Float pressionAbs, Float hygro, Integer type) throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
            try (Statement statement = connection.createStatement()) {
                if (temperature != null) {
                    saveTemperature(temperature, statement, type);
                }
                if (pressionAbs != null && pressionRel != null) {
                    savePression(pressionRel, pressionAbs, statement);
                }
                if (hygro != null) {
                    saveHygro(hygro, statement);
                }
            }
        } catch (Exception ex) {
            CustLogger.logException(ex);
        } finally {
            closeConnection(connection);
        }

    }

    public void saveTeleinfos(Integer iInst, Integer hcAmount, Integer hpAmount) throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
            try (Statement statement = connection.createStatement()) {
                saveIntensity(iInst, statement);
                saveEdfIndex(hcAmount, 1, statement);
                saveEdfIndex(hpAmount, 2, statement);
            }
        } catch (Exception ex) {
            CustLogger.logException(ex);
        } finally {
            closeConnection(connection);
        }

    }

    public void setCurrentTemp(int temp) throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
            String sql = "INSERT INTO temp_chauff (temp) VALUES (?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, temp);
                preparedStatement.executeUpdate();
            }
        } catch (Exception ex) {
            CustLogger.logException(ex);
        } finally {
            closeConnection(connection);
        }

    }

    public Integer getCurrentTemp() throws SQLException {
        CustLogger.traprintln("Jdbc.getCurrentTemp");
        Connection connection = null;
        try {
            connection = getConnection();
            Integer result = null;

            try (Statement statement = connection.createStatement()) {
                String sql = null;
                sql = "SELECT temp FROM temp_chauff ORDER BY ID DESC LIMIT 1";

                try (ResultSet rs = statement.executeQuery(sql)) {
                    while (rs.next()) {
                        result = rs.getInt("temp");
                    }
                }
                if (result == null) {
                    throw new SQLException("Aucune valeur de commande déja présente");
                }
            }

            return result;
        } finally {
            closeConnection(connection);
        }
    }

    private static void saveIntensity(Integer intensity, Statement statement) throws SQLException {
        String sql = null;
        sql = "INSERT INTO intensity (value)VALUES (" + intensity.toString() + ")";

        statement.executeUpdate(sql);
    }

    private void saveEdfIndex(Integer index, Integer type, Statement statement) throws SQLException {
        String sql = null;
        sql = "INSERT INTO edfindex (value, type)VALUES (" + index.toString() + ", " + type.toString() + ")";

        statement.executeUpdate(sql);
    }

    private void saveTemperature(Float temperature, Statement statement, Integer type) throws SQLException {
        String sql = null;
        sql = "INSERT INTO temperature (value, type)VALUES ("
                + temperature.toString() + ", "
                + type.toString() + ")";

        statement.executeUpdate(sql);
    }

    private void savePression(Float pressionRel, Float pressionAbs, Statement statement) throws SQLException {
        String sql = null;
        sql = "INSERT INTO pression (valueabs, valuerel)VALUES ("
                + pressionAbs.toString() + ", "
                + pressionRel.toString() + ")";

        statement.executeUpdate(sql);
    }

    private void saveHygro(Float value, Statement statement) throws SQLException {
        String sql = null;
        sql = "INSERT INTO hygro (value) VALUES (" + value.toString() + ")";

        statement.executeUpdate(sql);
    }

    private Boolean getCommandeChauffageInternal(Statement statement) throws SQLException {
        String sql = null;
        sql = "SELECT onoff FROM com_chauff ORDER BY ID DESC LIMIT 1";

        Boolean currentMode = null;
        try (ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                currentMode = rs.getBoolean("onoff");
            }
        }
        return currentMode;
    }

}