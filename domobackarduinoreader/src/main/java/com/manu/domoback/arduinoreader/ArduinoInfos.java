package com.manu.domoback.arduinoreader;

import com.manu.domoback.database.factory.JdbcFactory;
import com.manu.domoback.persistence.api.PersistenceApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.LocalDateTime;

public class ArduinoInfos implements IExternalInfos {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArduinoInfos.class.getName());
    private final PersistenceApi jdbc = JdbcFactory.getInstance();

    private String key;
    private Float temperature;
    private Float temperature2;
    private Float temperature3;
    private Float pressionAbsolue;
    private Float pressionRelative;
    private Float hygrometrie;
    private Boolean chauffageState;

    @Override
    public Float getTemperature() {
        return this.temperature;
    }

    public void setTemperature(final Float temperature) {
        this.temperature = temperature;
    }

    @Override
    public Float getTemperature2() {
        return this.temperature2;
    }

    public void setTemperature2(final Float temperature) {
        this.temperature2 = temperature;
    }

    @Override
    public Float getTemperature3() {
        return this.temperature3;
    }

    public void setTemperature3(final Float temperature) {
        this.temperature3 = temperature;
    }

    @Override
    public Float getPressionAbsolue() {
        return this.pressionAbsolue;
    }

    public void setPressionAbsolue(final Float pressionAbsolue) {
        this.pressionAbsolue = pressionAbsolue;
    }

    @Override
    public Float getPressionRelative() {
        return this.pressionRelative;
    }

    public void setPressionRelative(final Float pressionRelative) {
        this.pressionRelative = pressionRelative;
    }

    @Override
    public Float getHygrometrie() {
        return this.hygrometrie;
    }

    public void setHygrometrie(final Float hygrometrie) {
        this.hygrometrie = hygrometrie;
    }

    @Override
    public String getKey() {
        return this.key;
    }

    void setKey(final String key) {
        this.key = key;
    }

    @Override
    public Boolean getChauffageState() {
        final Boolean result = this.chauffageState;
        this.chauffageState = null;
        return result;
    }

    public void setChauffageState(final Boolean chauffageState) {
        this.chauffageState = chauffageState;
    }

    void addSerialEvent(final String errorType, final boolean isError) {
        final LocalDateTime date = LocalDateTime.now();
        try {
            this.jdbc.saveSerialEvent(date, errorType, isError);
        } catch (final SQLException e) {
            LOGGER.error("ArduinoInfos.errorType", e);
        }
    }
}
