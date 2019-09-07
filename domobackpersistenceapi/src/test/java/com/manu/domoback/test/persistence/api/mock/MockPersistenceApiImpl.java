package com.manu.domoback.test.persistence.api.mock;

import com.manu.domoback.persistence.api.PersistenceApi;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

public class MockPersistenceApiImpl implements PersistenceApi {
    @Override
    public String identify() {
        return null;
    }

    @Override
    public void saveMeteoInfos(final Float temperature, final Float pressionRel, final Float pressionAbs, final Float hygro, final Integer type) throws SQLException {

    }

    @Override
    public void saveTeleinfos(final Integer iInst, final Integer hcAmount, final Integer hpAmount) throws SQLException {

    }

    @Override
    public Boolean switchCommandeChauffage() throws SQLException {
        return null;
    }

    @Override
    public Boolean switchHourModeChauffage() throws SQLException {
        return null;
    }

    @Override
    public Boolean getCommandeChauffage() throws SQLException {
        return null;
    }

    @Override
    public Boolean getHourModeChauffage() throws SQLException {
        return null;
    }

    @Override
    public void setCurrentTemp(final int temp) throws SQLException {

    }

    @Override
    public void setTemp(final int temp, final Date startTime) {

    }

    @Override
    public Integer getCurrentTemp(final boolean hourMode) throws SQLException {
        return null;
    }

    @Override
    public Integer getTempForStartHour(final Date startHour) throws SQLException {
        return null;
    }

    @Override
    public Integer getTemp(final Date date) throws SQLException {
        return null;
    }

    @Override
    public Map<Date, Integer> getTempMap() throws SQLException {
        return null;
    }

    @Override
    public void saveSerialEvent(final LocalDateTime date, final String errorType, final boolean isError) throws SQLException {

    }
}
