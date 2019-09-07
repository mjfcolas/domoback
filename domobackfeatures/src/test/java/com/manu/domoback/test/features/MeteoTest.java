package com.manu.domoback.test.features;

import com.manu.domoback.arduinoreader.ArduinoInfos;
import com.manu.domoback.arduinoreader.ExternalDataController;
import com.manu.domoback.features.Meteo;
import com.manu.domoback.features.api.enums.INFOS;
import com.manu.domoback.persistence.api.PersistenceApi;
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
    private final ExternalDataController arduinoReader = null;
    @Mock
    private final PersistenceApi jdbc = null;
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
        this.meteo = new Meteo();
        this.meteo.init(this.arduinoReader, "TST");
    }

    /**
     * Infos non renseignées
     */
    @Test
    public void testFormatInfos1() {
        final Map<String, String> result = this.meteo.getInfos();
        assertEquals(0, result.size());
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
        assertEquals(4, result.size());
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
        this.meteo = new Meteo();
        this.meteo.init(this.arduinoReader, key);
        this.meteo.run();
        this.meteo.save();
    }
}
