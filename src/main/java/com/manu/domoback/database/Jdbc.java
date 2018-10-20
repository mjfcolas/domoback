package com.manu.domoback.database;

import com.manu.domoback.common.Bundles;
import com.manu.domoback.common.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class Jdbc implements IJdbc {

    private static final Logger LOGGER = LoggerFactory.getLogger(Jdbc.class.getName());

    @Override
    public Boolean getCommandeChauffage() throws SQLException {
        LOGGER.trace("Jdbc.getCommandeChauffage");
        try (Connection connection = DataSource.getInstance().getConnection();
             Statement statement = connection.createStatement()) {

            LOGGER.trace("Jdbc.getCommandeChauffage - Connection got");

            final Boolean currentMode = this.getCommandeChauffageInternal(statement);
            if (currentMode == null) {
                throw new SQLException("Aucune valeur de commande déja présente");
            }

            return currentMode;
        }
    }

    @Override
    public Boolean switchCommandeChauffage() throws SQLException {
        final String sql = "INSERT INTO com_chauff (onoff) VALUES (?)";
        try (Connection connection = DataSource.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             Statement statement = connection.createStatement()) {

            final Boolean currentMode = this.getCommandeChauffageInternal(statement);
            preparedStatement.setBoolean(1, !currentMode);
            preparedStatement.executeUpdate();
            return !currentMode;
        }
    }

    @Override
    public void saveMeteoInfos(final Float temperature, final Float pressionRel, final Float pressionAbs, final Float hygro, final Integer type) {
        try (Connection connection = DataSource.getInstance().getConnection();
             Statement statement = connection.createStatement()) {
            if (temperature != null) {
                this.saveTemperature(temperature, statement, type);
            }
            if (pressionAbs != null && pressionRel != null) {
                this.savePression(pressionRel, pressionAbs, statement);
            }
            if (hygro != null) {
                this.saveHygro(hygro, statement);
            }
        } catch (final Exception ex) {
            LOGGER.error(Bundles.messages().getProperty(Constants.KEY_GENERIC_ERROR), ex);
        }

    }

    @Override
    public void saveTeleinfos(final Integer iInst, final Integer hcAmount, final Integer hpAmount) {
        try (Connection connection = DataSource.getInstance().getConnection();
             Statement statement = connection.createStatement()) {
            saveIntensity(iInst, statement);
            this.saveEdfIndex(hcAmount, 1, statement);
            this.saveEdfIndex(hpAmount, 2, statement);
        } catch (final Exception ex) {
            LOGGER.error(Bundles.messages().getProperty(Constants.KEY_GENERIC_ERROR), ex);
        }

    }

    @Override
    public void setCurrentTemp(final int temp) {
        final String sql = "INSERT INTO temp_chauff (temp) VALUES (?)";
        try (Connection connection = DataSource.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, temp);
            preparedStatement.executeUpdate();
        } catch (final Exception ex) {
            LOGGER.error(Bundles.messages().getProperty(Constants.KEY_GENERIC_ERROR), ex);
        }

    }

    @Override
    public Integer getCurrentTemp() throws SQLException {
        LOGGER.trace("Jdbc.getCurrentTemp");
        try (Connection connection = DataSource.getInstance().getConnection();
             Statement statement = connection.createStatement()) {
            Integer result = null;

            final String sql = "SELECT temp FROM temp_chauff ORDER BY ID DESC LIMIT 1";

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

    private static void saveIntensity(final Integer intensity, final Statement statement) throws SQLException {
        final String sql = "INSERT INTO intensity (value)VALUES (" + intensity.toString() + ")";
        statement.executeUpdate(sql);
    }

    private void saveEdfIndex(final Integer index, final Integer type, final Statement statement) throws SQLException {
        final String sql = "INSERT INTO edfindex (value, type)VALUES (" + index.toString() + ", " + type.toString() + ")";
        statement.executeUpdate(sql);
    }

    private void saveTemperature(final Float temperature, final Statement statement, final Integer type) throws SQLException {
        final String sql = "INSERT INTO temperature (value, type)VALUES ("
                + temperature.toString() + ", "
                + type.toString() + ")";
        statement.executeUpdate(sql);
    }

    private void savePression(final Float pressionRel, final Float pressionAbs, final Statement statement) throws SQLException {
        final String sql = "INSERT INTO pression (valueabs, valuerel)VALUES ("
                + pressionAbs.toString() + ", "
                + pressionRel.toString() + ")";
        statement.executeUpdate(sql);
    }

    private void saveHygro(final Float value, final Statement statement) throws SQLException {
        final String sql = "INSERT INTO hygro (value) VALUES (" + value.toString() + ")";
        statement.executeUpdate(sql);
    }

    private Boolean getCommandeChauffageInternal(final Statement statement) throws SQLException {
        final String sql = "SELECT onoff FROM com_chauff ORDER BY ID DESC LIMIT 1";
        Boolean currentMode = null;
        try (ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                currentMode = rs.getBoolean("onoff");
            }
        }
        return currentMode;
    }

}