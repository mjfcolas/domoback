package com.manu.domoback.features;

import com.manu.domoback.arduinoreader.ArduinoInfos;
import com.manu.domoback.arduinoreader.IArduinoReader;
import com.manu.domoback.arduinoreader.INFOS;
import com.manu.domoback.database.IJdbc;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class MeteoTest extends TestCase {

    private Meteo meteo = null;
    @Mock
    private IArduinoReader arduinoReader = null;
    @Mock
    private IJdbc jdbc = null;
    private static ArduinoInfos arduinoResult = null;

    @BeforeClass
    public static void beforeClass() {
        arduinoResult = new ArduinoInfos();
        arduinoResult.setTemperature((float) 20);
        arduinoResult.setHygrometrie(null);
        arduinoResult.setTemperature2((float) 25);
    }

    @Before
    public void before() {
        meteo = new Meteo(arduinoReader, jdbc, "TST");
    }

    /**
     * Infos non renseignées
     */
    @Test
    public void testFormatInfos1() {
        Map<String, String> result = meteo.getInfos();
        assertEquals(5, result.size());
        assertEquals("N/A", result.get(INFOS.TEMP.name()));
    }

    /**
     * Donnée renseignée
     */
    @Test
    public void testRunMeteo() {
        Mockito.when(arduinoReader.getInfos()).thenReturn(arduinoResult);
        Mockito.when(arduinoReader.isReady()).thenReturn(true);
        meteo.run();
        Map<String, String> result = meteo.getInfos();
        assertEquals(5, result.size());
        assertEquals("N/A", result.get(INFOS.HYGROHUM.name()));
        assertEquals("20.0", result.get(INFOS.TEMP.name()));
    }
}
