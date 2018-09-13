package com.manu.domoback.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class Jdbc implements IJdbc {

    private static final Logger LOGGER = LoggerFactory.getLogger(Jdbc.class.getName());

    public Boolean getCommandeChauffage() throws SQLException {
        LOGGER.trace("Jdbc.getCommandeChauffage");
        try (Connection connection = DataSource.getInstance().getConnection();
             Statement statement = connection.createStatement()) {

            LOGGER.trace("Jdbc.getCommandeChauffage - Connection got");

            Boolean currentMode = getCommandeChauffageInternal(statement);
            if (currentMode == null) {
                throw new SQLException("Aucune valeur de commande déja présente");
            }

            return currentMode;
        }
    }

    public Boolean switchCommandeChauffage() throws SQLException {
        String sql = "INSERT INTO com_chauff (onoff) VALUES (?)";
        try (Connection connection = DataSource.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             Statement statement = connection.createStatement()) {

            Boolean currentMode = getCommandeChauffageInternal(statement);
                preparedStatement.setBoolean(1, !currentMode);
                preparedStatement.executeUpdate();
            return !currentMode;
        }
    }

    public void saveMeteoInfos(Float temperature, Float pressionRel, Float pressionAbs, Float hygro, Integer type) {
        try (Connection connection = DataSource.getInstance().getConnection();
             Statement statement = connection.createStatement()) {
            if (temperature != null) {
                saveTemperature(temperature, statement, type);
            }
            if (pressionAbs != null && pressionRel != null) {
                savePression(pressionRel, pressionAbs, statement);
            }
            if (hygro != null) {
                saveHygro(hygro, statement);
            }
        } catch (Exception ex) {
            LOGGER.error("An error occured", ex);
        }

    }

    public void saveTeleinfos(Integer iInst, Integer hcAmount, Integer hpAmount) {
        try (Connection connection = DataSource.getInstance().getConnection();
             Statement statement = connection.createStatement()) {
            saveIntensity(iInst, statement);
            saveEdfIndex(hcAmount, 1, statement);
            saveEdfIndex(hpAmount, 2, statement);
        } catch (Exception ex) {
            LOGGER.error("An error occured", ex);
        }

    }

    public void setCurrentTemp(int temp) {
        String sql = "INSERT INTO temp_chauff (temp) VALUES (?)";
        try (Connection connection = DataSource.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, temp);
                preparedStatement.executeUpdate();
        } catch (Exception ex) {
            LOGGER.error("An error occured", ex);
        }

    }

    public Integer getCurrentTemp() throws SQLException {
        LOGGER.trace("Jdbc.getCurrentTemp");
        try (Connection connection = DataSource.getInstance().getConnection();
             Statement statement = connection.createStatement()) {
            Integer result = null;

            String sql = "SELECT temp FROM temp_chauff ORDER BY ID DESC LIMIT 1";

            try (ResultSet rs = statement.executeQuery(sql)) {
                while (rs.next()) {
                    result = rs.getInt("temp");
                }
            }
            if (result == null) {
                throw new SQLException("Aucune valeur de commande déja présente");
            }

            return result;
        }
    }

    private static void saveIntensity(Integer intensity, Statement statement) throws SQLException {
        String sql = "INSERT INTO intensity (value)VALUES (" + intensity.toString() + ")";
        statement.executeUpdate(sql);
    }

    private void saveEdfIndex(Integer index, Integer type, Statement statement) throws SQLException {
        String sql = "INSERT INTO edfindex (value, type)VALUES (" + index.toString() + ", " + type.toString() + ")";
        statement.executeUpdate(sql);
    }

    private void saveTemperature(Float temperature, Statement statement, Integer type) throws SQLException {
        String sql = "INSERT INTO temperature (value, type)VALUES ("
                + temperature.toString() + ", "
                + type.toString() + ")";
        statement.executeUpdate(sql);
    }

    private void savePression(Float pressionRel, Float pressionAbs, Statement statement) throws SQLException {
        String sql = "INSERT INTO pression (valueabs, valuerel)VALUES ("
                + pressionAbs.toString() + ", "
                + pressionRel.toString() + ")";
        statement.executeUpdate(sql);
    }

    private void saveHygro(Float value, Statement statement) throws SQLException {
        String sql = "INSERT INTO hygro (value) VALUES (" + value.toString() + ")";
        statement.executeUpdate(sql);
    }

    private Boolean getCommandeChauffageInternal(Statement statement) throws SQLException {
        String sql = "SELECT onoff FROM com_chauff ORDER BY ID DESC LIMIT 1";
        Boolean currentMode = null;
        try (ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                currentMode = rs.getBoolean("onoff");
            }
        }
        return currentMode;
    }

}