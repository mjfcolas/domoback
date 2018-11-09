package com.manu.domoback.features;

import com.manu.domoback.arduinoreader.ArduinoInfos;
import com.manu.domoback.arduinoreader.IArduinoReader;
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

import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class MeteoTest extends TestCase {

    private Meteo meteo = null;
    @Mock
    private final IArduinoReader arduinoReader = null;
    @Mock
    private final IJdbc jdbc = null;
    private static ArduinoInfos arduinoResult = null;

    @BeforeClass
    public static void beforeClass() {
        arduinoResult = new ArduinoInfos();
        arduinoResult.setTemperature((float) 20);
        arduinoResult.setHygrometrie(null);
        arduinoResult.setTemperature2((float) 25);
        arduinoResult.setTemperature3((float) 23);
        arduinoResult.setPressionAbsolue((float) 1000);
        arduinoResult.setPressionRelative((float) 1015);
    }

    @Before
    public void before() {
        this.meteo = new Meteo(this.arduinoReader, this.jdbc, "TST");
    }

    /**
     * Infos non renseignées
     */
    @Test
    public void testFormatInfos1() {
        final Map<String, String> result = this.meteo.getInfos();
        assertEquals(5, result.size());
        assertEquals("N/A", result.get(INFOS.TEMP.name()));
    }

    /**
     * Donnée renseignée
     */
    @Test
    public void testRunMeteo() {
        Mockito.when(this.arduinoReader.getInfos()).thenReturn(arduinoResult);
        Mockito.when(this.arduinoReader.isReady()).thenReturn(true);
        this.meteo.run();
        final Map<String, String> result = this.meteo.getInfos();
        assertEquals(5, result.size());
        assertEquals("N/A", result.get(INFOS.HYGROHUM.name()));
        assertEquals("20.0", result.get(INFOS.TEMP.name()));
    }

    @Test
    public void testSave() {
        Mockito.when(this.arduinoReader.getInfos()).thenReturn(arduinoResult);
        Mockito.when(this.arduinoReader.isReady()).thenReturn(true);
        this.runAndSave("METEO");
        this.runAndSave("METEO2");
        this.runAndSave("METEO3");
    }

    private void runAndSave(final String key) {
        this.meteo = new Meteo(this.arduinoReader, this.jdbc, key);
        this.meteo.run();
        this.meteo.save();
    }
}
