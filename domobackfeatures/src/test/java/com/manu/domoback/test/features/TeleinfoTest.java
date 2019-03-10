package com.manu.domoback.test.features;

import com.manu.domoback.conf.CONFKEYS;
import com.manu.domoback.conf.DomobackConf;
import com.manu.domoback.features.Teleinfo;
import com.manu.domoback.persistence.api.PersistenceApi;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPortEvent;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ DomobackConf.class })
public class TeleinfoTest extends TestCase {

    @Mock
    private final PersistenceApi jdbc = null;

    @Before
    public void before() {
    }

    @Test
    public void testRun() {
        Teleinfo teleinfo = new Teleinfo(this.jdbc);
        teleinfo.run();
    }

    @Test
    public void testSave() {

        PowerMockito.mockStatic(DomobackConf.class);
        Mockito.when(DomobackConf.get(CONFKEYS.TELEINFO_FILETOUSE)).thenReturn("/home/emmanuel/Documents/Traitement du signal/back/linky.wav");
        Mockito.when(DomobackConf.get(CONFKEYS.TELEINFO_PROCESSRECORD)).thenReturn("0");
        Mockito.when(DomobackConf.get(CONFKEYS.TELEINFO_TRAMETIME)).thenReturn("1");
        Teleinfo teleinfo = new Teleinfo(this.jdbc);
        teleinfo.run();
        assertTrue(teleinfo.save());
    }
}
