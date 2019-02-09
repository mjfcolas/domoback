package com.manu.domoback.database;

import com.manu.domoback.common.Bundles;
import com.manu.domoback.common.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Jdbc implements IJdbc {

    private static final Logger LOGGER = LoggerFactory.getLogger(Jdbc.class.getName());
    private static final String NO_COMMAND_PRESENT = "Aucune valeur de commande présente";

    @Override
    public Boolean getCommandeChauffage() throws SQLException {
        LOGGER.trace("Jdbc.getCommandeChauffage");
        try (Connection connection = DataSource.getInstance().getConnection();
             Statement statement = connection.createStatement()) {

            LOGGER.trace("Jdbc.getCommandeChauffage - Connection got");

            final Boolean currentMode = this.getCommandeChauffageInternal(statement);
            if (currentMode == null) {
                throw new SQLException(NO_COMMAND_PRESENT);
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
    public Boolean getHourModeChauffage() throws SQLException {
        LOGGER.trace("Jdbc.getHourModeChauffage");
        try (Connection connection = DataSource.getInstance().getConnection();
             Statement statement = connection.createStatement()) {

            LOGGER.trace("Jdbc.getHourModeChauffage - Connection got");

            final Boolean currentMode = this.getHourModeChauffageInternal(statement);
            if (currentMode == null) {
                throw new SQLException(NO_COMMAND_PRESENT);
            }

            return currentMode;
        }
    }

    @Override
    public Boolean switchHourModeChauffage() throws SQLException {
        final String sql = "INSERT INTO mode_chauff (hourmode) VALUES (?)";
        try (Connection connection = DataSource.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             Statement statement = connection.createStatement()) {

            final Boolean currentMode = this.getHourModeChauffageInternal(statement);
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
        final String sql = "UPDATE temp_chauff SET temp=? WHERE start_hour IS NULL";
        try (Connection connection = DataSource.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, temp);
            preparedStatement.executeUpdate();
        } catch (final Exception ex) {
            LOGGER.error(Bundles.messages().getProperty(Constants.KEY_GENERIC_ERROR), ex);
        }

    }

    @Override
    public void setTemp(final int temp, final Date startTime) {
        final String sql = "UPDATE temp_chauff SET temp=? WHERE start_hour=? ";
        try (Connection connection = DataSource.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, temp);
            preparedStatement.setTime(2, new Time(startTime.getTime()));
            preparedStatement.executeUpdate();
        } catch (final Exception ex) {
            LOGGER.error(Bundles.messages().getProperty(Constants.KEY_GENERIC_ERROR), ex);
        }

    }

    @Override
    public void saveSerialEvent(final LocalDateTime date, final String errorType, final boolean isError) throws SQLException {
        final String sql = "INSERT INTO serial_event (date, error_type, success) VALUES (?, ?, ?)";
        try (Connection connection = DataSource.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setTimestamp(1, Timestamp.valueOf(date));
            preparedStatement.setString(2, errorType);
            preparedStatement.setBoolean(3, !isError);
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public Integer getCurrentTemp(final boolean hourMode) throws SQLException {
        LOGGER.trace("Jdbc.getCurrentTemp");

        if (hourMode) {
            return this.getTemp(new Date());
        } else {
            try (Connection connection = DataSource.getInstance().getConnection();
                 Statement statement = connection.createStatement()) {
                Integer result = null;

                final String sql = "SELECT temp FROM temp_chauff WHERE start_hour IS NULL";

                try (ResultSet rs = statement.executeQuery(sql)) {
                    while (rs.next()) {
                        result = rs.getInt("temp");
                    }
                }
                if (result == null) {
                    throw new SQLException(NO_COMMAND_PRESENT);
                }

                return result;
            }
        }
    }

    @Override
    public Integer getTemp(final Date date) throws SQLException {
        LOGGER.trace("Jdbc.getTemp");
        final Time time = new Time(date.getTime());
        final SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
        boolean beforeElevenPm = false;
        final Time midnight;
        try {
            final Date dateCompare = sdf.parse("230000");
            final Date curHour = sdf.parse(sdf.format(date));
            midnight = new Time(sdf.parse("000000").getTime());
            LOGGER.trace("CurHour {} - dateCompare {}", curHour, dateCompare);
            if (curHour.before(dateCompare)) {
                LOGGER.trace("SecondParameter true");
                beforeElevenPm = true;
            }
        } catch (final ParseException e) {
            throw new SQLException("Parse de la date de comparaison échoué");
        }
        final String sql;
        if (beforeElevenPm) {
            sql = "SELECT temp FROM temp_chauff WHERE start_hour < ? AND end_hour >= ?;";
        } else {
            sql = "SELECT temp FROM temp_chauff WHERE start_hour < ? AND end_hour = ?;";
        }
        boolean gotOneResult = false;
        try (Connection connection = DataSource.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            Integer result = null;

            statement.setTime(1, time);
            statement.setTime(2, beforeElevenPm ? time : midnight);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    if (gotOneResult) {
                        throw new SQLException("Plusieurs résultats de température");
                    }
                    result = rs.getInt("temp");
                    gotOneResult = true;
                }
            }
            if (result == null) {
                throw new SQLException(NO_COMMAND_PRESENT);
            }

            return result;
        }
    }

    @Override
    public Integer getTempForStartHour(final Date startHour) throws SQLException {
        LOGGER.trace("Jdbc.getTempForStartHour");
        final String sql = "SELECT temp FROM temp_chauff WHERE start_hour = ? ";
        try (Connection connection = DataSource.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            Integer result = null;

            final Time time = new Time(startHour.getTime());
            statement.setTime(1, time);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    result = rs.getInt("temp");
                }
            }
            if (result == null) {
                throw new SQLException(NO_COMMAND_PRESENT);
            }

            return result;
        }
    }

    @Override
    public Map<Date, Integer> getTempMap() throws SQLException {
        LOGGER.trace("Jdbc.getTempForStartHour");
        final String sql = "SELECT start_hour, temp FROM temp_chauff WHERE start_hour IS NOT NULL ";
        try (Connection connection = DataSource.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            final Map<Date, Integer> result = new HashMap<>();

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    result.put(rs.getTime("start_hour"), rs.getInt("temp"));
                }
            }
            if (result.isEmpty()) {
                throw new SQLException(NO_COMMAND_PRESENT);
            }

            return result;
        }
    }

    private static void saveIntensity(final Integer intensity, final Statement statement) throws SQLException {
        final String sql = "INSERT INTO intensity (value)VALUES (" + intensity.toString() + ")";
        statement.executeUpdate(sql);
    }

    private void saveEdfIndex(final Integer index, final Integer type, final Statement statement) throws
            SQLException {
        final String sql = "INSERT INTO edfindex (value, type)VALUES (" + index.toString() + ", " + type.toString() + ")";
        statement.executeUpdate(sql);
    }

    private void saveTemperature(final Float temperature, final Statement statement, final Integer type) throws
            SQLException {
        final String sql = "INSERT INTO temperature (value, type)VALUES ("
                + temperature.toString() + ", "
                + type.toString() + ")";
        statement.executeUpdate(sql);
    }

    private void savePression(final Float pressionRel, final Float pressionAbs, final Statement statement) throws
            SQLException {
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

    private Boolean getHourModeChauffageInternal(final Statement statement) throws SQLException {
        final String sql = "SELECT hourmode FROM mode_chauff ORDER BY ID DESC LIMIT 1";
        Boolean currentMode = null;
        try (ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                currentMode = rs.getBoolean("hourmode");
            }
        }
        return currentMode;
    }

}