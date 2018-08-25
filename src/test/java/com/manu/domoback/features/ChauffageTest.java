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
    private IArduinoReader arduinoReader = null;
    @Mock
    private IJdbc jdbc = null;

    @BeforeClass
    public static void beforeClass() {

    }

    @Before
    public void before() {
        chauffage = new Chauffage(arduinoReader, jdbc);
    }

    /**
     * Infos non renseignées
     */
    @Test
    public void testFormatInfos1() {
        Map<String, String> result = chauffage.getInfos();
        assertEquals(2, result.size());
        assertEquals("N/A", result.get(INFOS.MODECHAUFF.name()));
        assertEquals("N/A", result.get(INFOS.TEMPCHAUFF.name()));
    }

    /**
     * Chauffage éteint doit être allumé
     */
    @Test
    public void testRun1() throws SQLException {
        Mockito.when(jdbc.getCommandeChauffage()).thenReturn(true);
        Mockito.when(jdbc.getCurrentTemp()).thenReturn(20);
        Mockito.when(arduinoReader.isReady()).thenReturn(true);
        ArduinoInfos arduinoResult = new ArduinoInfos();
        arduinoResult.setChauffageState(false);
        arduinoResult.setTemperature(new Float(18));
        Mockito.when(arduinoReader.getInfos()).thenReturn(arduinoResult);
        chauffage.run();
        IChauffageInfo result = chauffage.getChauffageInfo();
        assertEquals(new Boolean(true), result.getChauffageState());
    }

    /**
     * Chauffage éteint doit resté éteint
     */
    @Test
    public void testRun2() throws SQLException {
        Mockito.when(jdbc.getCommandeChauffage()).thenReturn(true);
        Mockito.when(jdbc.getCurrentTemp()).thenReturn(20);
        Mockito.when(arduinoReader.isReady()).thenReturn(true);
        ArduinoInfos arduinoResult = new ArduinoInfos();
        arduinoResult.setChauffageState(false);
        arduinoResult.setTemperature(new Float(21));
        Mockito.when(arduinoReader.getInfos()).thenReturn(arduinoResult);
        chauffage.run();
        IChauffageInfo result = chauffage.getChauffageInfo();
        assertEquals(new Boolean(false), result.getChauffageState());
    }

    /**
     * Chauffage allumé doit resté allumé
     */
    @Test
    public void testRun3() throws SQLException {
        Mockito.when(jdbc.getCommandeChauffage()).thenReturn(true);
        Mockito.when(jdbc.getCurrentTemp()).thenReturn(20);
        Mockito.when(arduinoReader.isReady()).thenReturn(true);
        ArduinoInfos arduinoResult = new ArduinoInfos();
        arduinoResult.setChauffageState(true);
        arduinoResult.setTemperature(new Float(19));
        Mockito.when(arduinoReader.getInfos()).thenReturn(arduinoResult);
        chauffage.run();
        IChauffageInfo result = chauffage.getChauffageInfo();
        assertEquals(new Boolean(true), result.getChauffageState());
    }

    /**
     * Chauffage allumé doit être éteint
     */
    @Test
    public void testRun4() throws SQLException {
        Mockito.when(jdbc.getCommandeChauffage()).thenReturn(true);
        Mockito.when(jdbc.getCurrentTemp()).thenReturn(20);
        Mockito.when(arduinoReader.isReady()).thenReturn(true);
        ArduinoInfos arduinoResult = new ArduinoInfos();
        arduinoResult.setChauffageState(true);
        arduinoResult.setTemperature(new Float(21));
        Mockito.when(arduinoReader.getInfos()).thenReturn(arduinoResult);
        chauffage.run();
        IChauffageInfo result = chauffage.getChauffageInfo();
        assertEquals(new Boolean(false), result.getChauffageState());
    }
}
