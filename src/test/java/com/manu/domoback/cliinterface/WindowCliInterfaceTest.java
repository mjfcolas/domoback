package com.manu.domoback.cliinterface;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.manu.domoback.arduinoreader.ArduinoReader;
import com.manu.domoback.arduinoreader.IArduinoReader;
import com.manu.domoback.common.DependanceFactory;
import com.manu.domoback.features.*;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class WindowCliInterfaceTest extends TestCase {

    private WindowCliInterface gui = null;
    @Mock
    private final IArduinoReader arduinoReader = new ArduinoReader();
    @Mock
    private final IMeteo meteo = new Meteo(this.arduinoReader, DependanceFactory.getJdbc(), "METEO");
    @Mock
    private final IChauffage chauffage = new Chauffage(this.arduinoReader, DependanceFactory.getJdbc(), 1000);
    @Mock
    private final IFeature teleinfo = new Teleinfo(DependanceFactory.getJdbc());
    @Mock
    private final IFeatureWrapper featureWrapper = new FeatureWrapper(this.meteo, this.chauffage, this.teleinfo);

    @Before
    public void before() {
        this.gui = new WindowCliInterface(this.featureWrapper, this.chauffage);
    }

    @Test
    public void testInterfaceCreate() {
        assertTrue(true);
    }

    @Test
    public void testChangedOccured() {
        final Map<String, String> infos = new HashMap<>();
        infos.put("TEMP", "20");
        infos.put("IINST", "10");
        infos.put("ISOUSC", "20");
        Mockito.when(this.featureWrapper.getFeaturesInfos()).thenReturn(infos);
        this.gui.changedOccured();
    }

    @Test
    public void testInputs() {
        final Map<String, String> infos = new HashMap<>();
        infos.put("TEMP", "20");
        infos.put("IINST", "10");
        infos.put("ISOUSC", "20");
        Mockito.when(this.featureWrapper.getFeaturesInfos()).thenReturn(infos);
        this.gui.onInput(null, new KeyStroke(KeyType.F2), null);
        this.gui.onInput(null, new KeyStroke(KeyType.F1), null);
        this.gui.onInput(null, new KeyStroke(KeyType.F3), null);
        this.gui.onInput(null, new KeyStroke(KeyType.F8), null);
        this.gui.onInput(null, new KeyStroke(KeyType.ArrowUp), null);
        this.gui.onInput(null, new KeyStroke(KeyType.ArrowDown), null);
        for (int i = 0; i < 30; i++) {
            this.gui.onInput(null, new KeyStroke(KeyType.Tab), null);
        }
        this.gui.onInput(null, new KeyStroke(KeyType.ArrowUp), null);
        this.gui.onInput(null, new KeyStroke(KeyType.ArrowDown), null);
    }
}
