package com.manu.domoback.features;

import com.manu.domoback.arduinoreader.ArduinoInfos;
import com.manu.domoback.arduinoreader.IArduinoReader;
import com.manu.domoback.chauffage.IChauffageInfo;
import com.manu.domoback.database.IJdbc;
import com.manu.domoback.enums.INFOS;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class ChauffageTest extends TestCase {

    private Chauffage chauffage = null;
    @Mock
    private final IArduinoReader arduinoReader = null;
    @Mock
    private final IJdbc jdbc = null;

    @BeforeClass
    public static void beforeClass() {

    }

    @Before
    public void before() {
        this.chauffage = new Chauffage(this.arduinoReader, this.jdbc, 1);
    }

    /**
     * Infos non renseignées
     */
    @Test
    public void testFormatInfos1() {
        final Map<String, String> result = this.chauffage.getInfos();
        assertEquals(3, result.size());
        assertEquals("N/A", result.get(INFOS.MODECHAUFF.name()));
        assertEquals("N/A", result.get(INFOS.TEMPCHAUFF.name()));
        assertEquals("N/A", result.get(INFOS.TEMPHOURMODE.name()));
    }

    /**
     * Formatage infos
     */
    @Test
    public void testFormatInfos2() throws ParseException, SQLException {
        Mockito.when(this.jdbc.getCurrentTemp(true)).thenReturn(20);
        Mockito.when(this.jdbc.getHourModeChauffage()).thenReturn(true);
        Mockito.when(this.jdbc.getCommandeChauffage()).thenReturn(true);
        final SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
        final Date date = sdf.parse("230000");
        final Map<Date, Integer> tempHourMap = new HashMap<>();
        tempHourMap.put(date, 25);
        Mockito.when(this.jdbc.getTempMap()).thenReturn(tempHourMap);
        this.chauffage.run();
        this.chauffage.getInfos();
        Mockito.when(this.jdbc.getHourModeChauffage()).thenReturn(false);
        Mockito.when(this.jdbc.getCommandeChauffage()).thenReturn(false);
        this.chauffage.run();
        this.chauffage.getInfos();
    }

    /**
     * Chauffage éteint doit être allumé
     */
    @Test
    public void testRun1() throws SQLException {
        Mockito.when(this.jdbc.getCommandeChauffage()).thenReturn(true);
        Mockito.when(this.jdbc.getCurrentTemp(false)).thenReturn(20);
        Mockito.when(this.arduinoReader.isReady()).thenReturn(true);
        final ArduinoInfos arduinoResult = new ArduinoInfos();
        arduinoResult.setChauffageState(false);
        arduinoResult.setTemperature(new Float(18));
        Mockito.when(this.arduinoReader.getInfos()).thenReturn(arduinoResult);
        this.chauffage.run();
        final IChauffageInfo result = this.chauffage.getChauffageInfo();
        assertEquals(new Boolean(true), result.getChauffageState());
    }

    /**
     * Chauffage éteint doit resté éteint
     */
    @Test
    public void testRun2() throws SQLException {
        Mockito.when(this.jdbc.getCommandeChauffage()).thenReturn(true);
        Mockito.when(this.jdbc.getCurrentTemp(false)).thenReturn(20);
        Mockito.when(this.arduinoReader.isReady()).thenReturn(true);
        final ArduinoInfos arduinoResult = new ArduinoInfos();
        arduinoResult.setChauffageState(false);
        arduinoResult.setTemperature(new Float(21));
        Mockito.when(this.arduinoReader.getInfos()).thenReturn(arduinoResult);
        this.chauffage.run();
        final IChauffageInfo result = this.chauffage.getChauffageInfo();
        assertEquals(new Boolean(false), result.getChauffageState());
    }

    /**
     * Chauffage allumé doit resté allumé
     */
    @Test
    public void testRun3() throws SQLException {
        Mockito.when(this.jdbc.getCommandeChauffage()).thenReturn(true);
        Mockito.when(this.jdbc.getCurrentTemp(false)).thenReturn(20);
        Mockito.when(this.arduinoReader.isReady()).thenReturn(true);
        final ArduinoInfos arduinoResult = new ArduinoInfos();
        arduinoResult.setChauffageState(true);
        arduinoResult.setTemperature(new Float(19));
        Mockito.when(this.arduinoReader.getInfos()).thenReturn(arduinoResult);
        this.chauffage.run();
        final IChauffageInfo result = this.chauffage.getChauffageInfo();
        assertEquals(new Boolean(true), result.getChauffageState());
    }

    /**
     * Chauffage allumé doit être éteint
     */
    @Test
    public void testRun4() throws SQLException {
        Mockito.when(this.jdbc.getCommandeChauffage()).thenReturn(true);
        Mockito.when(this.jdbc.getCurrentTemp(false)).thenReturn(20);
        Mockito.when(this.arduinoReader.isReady()).thenReturn(true);
        final ArduinoInfos arduinoResult = new ArduinoInfos();
        arduinoResult.setChauffageState(true);
        arduinoResult.setTemperature(new Float(21));
        Mockito.when(this.arduinoReader.getInfos()).thenReturn(arduinoResult);
        this.chauffage.run();
        final IChauffageInfo result = this.chauffage.getChauffageInfo();
        assertEquals(new Boolean(false), result.getChauffageState());
    }

    /**
     * Chauffage off ne doit pas s'allumer
     */
    @Test
    public void testRun5() throws SQLException {
        Mockito.when(this.jdbc.getCommandeChauffage()).thenReturn(false);
        Mockito.when(this.jdbc.getCurrentTemp(false)).thenReturn(20);
        Mockito.when(this.arduinoReader.isReady()).thenReturn(true);
        final ArduinoInfos arduinoResult = new ArduinoInfos();
        arduinoResult.setChauffageState(false);
        arduinoResult.setTemperature(new Float(18));
        Mockito.when(this.arduinoReader.getInfos()).thenReturn(arduinoResult);
        this.chauffage.run();
        final IChauffageInfo result = this.chauffage.getChauffageInfo();
        assertEquals(new Boolean(false), result.getChauffageState());
    }

    /**
     * Test synchronisation chauffage
     *
     * @throws SQLException
     */
    @Test
    public void testRun6() throws SQLException {
        Mockito.when(this.jdbc.getCommandeChauffage()).thenReturn(false);
        Mockito.when(this.jdbc.getCurrentTemp(false)).thenReturn(20);
        Mockito.when(this.arduinoReader.isReady()).thenReturn(true);
        final ArduinoInfos arduinoResult = new ArduinoInfos();
        arduinoResult.setChauffageState(false);
        arduinoResult.setTemperature(new Float(18));
        Mockito.when(this.arduinoReader.getInfos()).thenReturn(arduinoResult);
        for (int i = 0; i < 3; i++) {
            this.chauffage.run();
        }
    }

    @Test
    public void testChangeTemperature() throws SQLException {
        Mockito.when(this.jdbc.getCommandeChauffage()).thenReturn(true);
        Mockito.when(this.jdbc.getCurrentTemp(false)).thenReturn(20);
        Mockito.when(this.arduinoReader.isReady()).thenReturn(true);
        this.chauffage.changeTemperature(true);
        this.chauffage.changeTemperature(false);
    }

    @Test
    public void testChangeTemperatureHour() throws SQLException {
        Mockito.when(this.jdbc.getCommandeChauffage()).thenReturn(true);
        Mockito.when(this.jdbc.getCurrentTemp(false)).thenReturn(20);
        Mockito.when(this.arduinoReader.isReady()).thenReturn(true);
        this.chauffage.changeTemperatureHour(true, 22);
        this.chauffage.changeTemperatureHour(false, 22);
    }

    @Test
    public void testSave() {
        this.chauffage.save();
    }

    @Test
    public void testChangeMode() {
        this.chauffage.changeMode();
    }
}
