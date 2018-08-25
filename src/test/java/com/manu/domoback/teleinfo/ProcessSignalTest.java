package com.manu.domoback.teleinfo;

import com.manu.domoback.common.Bundles;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class ProcessSignalTest extends TestCase {
    private ProcessSignal signalProcessor = null;

    @Before
    public void before() {
        signalProcessor = new ProcessSignal(Bundles.prop().getProperty("teleinfo.filetouse"),
                false);
    }

    @Test
    public void testGetTrames() throws UnsupportedAudioFileException, IOException {
        List<Trame> trames = signalProcessor.getTrames(Integer.parseInt(Bundles.prop().getProperty("teleinfo.trametime")));
        assertFalse(trames.get(0).isInError());
        trames.get(0).parseInfos();
        trames.get(0).formatInfos();
        Map<String, String> trameInfos = trames.get(0).getFormatedInfos();
        assertEquals(11, trameInfos.size());
        assertEquals("021875330811", trameInfos.get("ADCO"));
    }
}
