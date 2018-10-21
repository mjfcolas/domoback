package com.manu.domoback.features;

import com.manu.domoback.arduinoreader.ArduinoInfos;
import com.manu.domoback.arduinoreader.IArduinoReader;
import com.manu.domoback.arduinoreader.INFOS;
import com.manu.domoback.chauffage.IChauffageInfo;
import com.manu.domoback.database.IJdbc;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.sql.SQLException;
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
        this.chauffage = new Chauffage(this.arduinoReader, this.jdbc);
    }

    /**
     * Infos non renseignées
     */
    @Test
    public void testFormatInfos1() {
        final Map<String, String> result = this.chauffage.getInfos();
        assertEquals(2, result.size());
        assertEquals("N/A", result.get(INFOS.MODECHAUFF.name()));
        assertEquals("N/A", result.get(INFOS.TEMPCHAUFF.name()));
    }

    /**
     * Chauffage éteint doit être allumé
     */
    @Test
    public void testRun1() throws SQLException {
        Mockito.when(this.jdbc.getCommandeChauffage()).thenReturn(true);
        Mockito.when(this.jdbc.getCurrentTemp()).thenReturn(20);
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
        Mockito.when(this.jdbc.getCurrentTemp()).thenReturn(20);
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
        Mockito.when(this.jdbc.getCurrentTemp()).thenReturn(20);
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
        Mockito.when(this.jdbc.getCurrentTemp()).thenReturn(20);
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
        Mockito.when(this.jdbc.getCurrentTemp()).thenReturn(20);
        Mockito.when(this.arduinoReader.isReady()).thenReturn(true);
        final ArduinoInfos arduinoResult = new ArduinoInfos();
        arduinoResult.setChauffageState(false);
        arduinoResult.setTemperature(new Float(18));
        Mockito.when(this.arduinoReader.getInfos()).thenReturn(arduinoResult);
        this.chauffage.run();
        final IChauffageInfo result = this.chauffage.getChauffageInfo();
        assertEquals(new Boolean(false), result.getChauffageState());
    }

    @Test
    public void testChangeTemperature() throws SQLException {
        Mockito.when(this.jdbc.getCommandeChauffage()).thenReturn(true);
        Mockito.when(this.jdbc.getCurrentTemp()).thenReturn(20);
        Mockito.when(this.arduinoReader.isReady()).thenReturn(true);
        this.chauffage.changeTemperature(true);
        this.chauffage.changeTemperature(false);
    }
}
