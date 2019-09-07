package com.manu.domoback.test.database;

import com.manu.domoback.database.datasource.DataSource;
import com.manu.domoback.database.impl.Jdbc;
import com.manu.domoback.persistence.api.factory.JdbcFactory;
import com.manu.domoback.exceptions.BusinessException;
import com.manu.domoback.persistence.api.PersistenceApi;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Scanner;

@RunWith(MockitoJUnitRunner.class)
public class JdbcTest extends TestCase {
    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcTest.class.getName());
    private final PersistenceApi jdbc = new Jdbc();

    @Before
    public void before() {
        try {
            if (!DataSource.isIinitialized()) {
                DataSource.initialize("org.h2.Driver", "jdbc:h2:mem:myDb;DB_CLOSE_DELAY=-1", "", "");
            }
        } catch (final BusinessException e) {
            fail();
        }

        try (Connection connection = DataSource.getInstance().getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("DROP ALL OBJECTS");
            final String script = new Scanner(new File("src/test/resources/h2.sql"), "UTF-8").useDelimiter("\\A").next();
            statement.execute(script);
        } catch (final Exception e) {
            LOGGER.error("Exception during datasource initialization", e);
            fail();
        }

    }

    private void initializeData(final String scriptName) {
        try (Connection connection = DataSource.getInstance().getConnection();
             Statement statement = connection.createStatement()) {
            final String script = new Scanner(new File("src/test/resources/" + scriptName), "UTF-8").useDelimiter("\\A").next();
            statement.execute(script);
        } catch (final Exception e) {
            LOGGER.error("Exception during datasource initialization", e);
            fail();
        }
    }

    @Test
    public void testGetCommandeChauffage() {
        try {
            this.initializeData("commandeChauffage.sql");
            assertTrue(this.jdbc.getCommandeChauffage());
        } catch (final Exception e) {
            fail();
        }
    }

    @Test
    public void testGetCommandeChauffageNoValues() {
        boolean error = true;
        try {
            this.jdbc.getCommandeChauffage();
        } catch (final SQLException e) {
            error = false;
        }
        assertFalse(error);
    }

    @Test
    public void testSwitchCommandeChauffage() {
        final boolean error = true;
        try {
            this.initializeData("commandeChauffage.sql");
            this.jdbc.switchCommandeChauffage();
            assertFalse(this.jdbc.getCommandeChauffage());
        } catch (final SQLException e) {
            fail();
        }
    }

    @Test
    public void testSaveMeteoInfos() {
        try {
            this.jdbc.saveMeteoInfos(new Float(15), new Float(1000), new Float(1010), new Float(20), 1);
        } catch (final SQLException e) {
            fail();
        }
    }

    @Test
    public void testSaveTeleinfos() {
        try {
            this.jdbc.saveTeleinfos(10, 100000, 50000);
        } catch (final SQLException e) {
            fail();
        }
    }

    @Test
    public void testSetCurrentTemp() {
        try {
            this.initializeData("tempChauff.sql");
            this.jdbc.setCurrentTemp(22);
            this.jdbc.setCurrentTemp(24);
            assertEquals((Integer) 24, this.jdbc.getCurrentTemp(false));
        } catch (final SQLException e) {
            fail();
        }
    }

    @Test
    public void testGetCurrentTempNoValues() {
        boolean error = true;
        try {
            this.jdbc.getCurrentTemp(false);
        } catch (final SQLException e) {
            error = false;
        }
        assertFalse(error);
    }

    @Test
    public void testGetTempNoValues() {
        boolean error = true;
        try {
            this.jdbc.getTemp(new Date());
        } catch (final SQLException e) {
            error = false;
        }
        assertFalse(error);
    }

    @Test
    public void testGetTemp() throws ParseException, SQLException {
        this.initializeData("tempChauff.sql");
        final SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
        final Date date = sdf.parse("221015");
        assertEquals((Integer) 34, this.jdbc.getTemp(date));
    }

    @Test
    public void testGetTempBeforeMidnight() throws ParseException, SQLException {
        this.initializeData("tempChauff.sql");
        final SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
        final Date date = sdf.parse("231015");
        assertEquals((Integer) 35, this.jdbc.getTemp(date));
    }

    @Test
    public void testGetTempForStartHour() throws ParseException, SQLException {
        this.initializeData("tempChauff.sql");
        final SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
        final Date date = sdf.parse("230000");
        assertEquals((Integer) 35, this.jdbc.getTempForStartHour(date));
    }

    @Test
    public void testGetTempMap() throws SQLException {
        this.initializeData("tempChauff.sql");
        this.jdbc.getTempMap();
    }

    @Test
    public void testGetHourModeChauffage() throws SQLException {
        this.initializeData("modeChauff.sql");
        assertEquals((Boolean) true, this.jdbc.getHourModeChauffage());
    }

    @Test
    public void testGetHourModeChauffageNoValues() {
        boolean error = true;
        try {
            this.jdbc.getHourModeChauffage();
        } catch (final SQLException e) {
            error = false;
        }
        assertFalse(error);
    }

    @Test
    public void testSetTemp() {
        this.jdbc.setTemp(25, new Date());
    }

    @Test
    public void testSwitchHourModeChauffage() throws SQLException {
        this.initializeData("modeChauff.sql");
        this.jdbc.switchHourModeChauffage();
        assertEquals((Boolean) false, this.jdbc.getHourModeChauffage());
    }

    @Test
    public void testSaveError() {
        try {
            this.jdbc.saveSerialEvent(LocalDateTime.now(), "T", true);
        } catch (final SQLException e) {
            fail();
        }
    }
}
