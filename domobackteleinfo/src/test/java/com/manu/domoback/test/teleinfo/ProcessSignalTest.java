package com.manu.domoback.test.teleinfo;

import com.manu.domoback.conf.CONFKEYS;
import com.manu.domoback.conf.DomobackConf;
import com.manu.domoback.teleinfo.ProcessSignal;
import com.manu.domoback.teleinfo.Trame;
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
        signalProcessor = new ProcessSignal(DomobackConf.get(CONFKEYS.TELEINFO_FILETOUSE),
                false,20, -17000, 1000, false);
    }

    @Test
    public void testGetTrames() throws UnsupportedAudioFileException, IOException {
        List<Trame> trames = signalProcessor.getTrames(Integer.parseInt(DomobackConf.get(CONFKEYS.TELEINFO_TRAMETIME)));
        assertFalse(trames.get(0).isInError());
        trames.get(0).parseInfos();
        trames.get(0).formatInfos();
        Map<String, String> trameInfos = trames.get(0).getFormatedInfos();
        assertEquals(11, trameInfos.size());
        assertEquals("021875330811", trameInfos.get("ADCO"));
    }
}
