package com.manu.domoback.test.features;

import com.manu.domoback.conf.CONFKEYS;
import com.manu.domoback.conf.DomobackConf;
import com.manu.domoback.features.Teleinfo;
import com.manu.domoback.persistence.api.PersistenceApi;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.sql.SQLException;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ DomobackConf.class })
public class TeleinfoTest extends TestCase {

    @InjectMocks
    private Teleinfo teleinfo = new Teleinfo();
    @Mock
    private final PersistenceApi jdbc = null;

    @Before
    public void before() {
    }

    @Test
    public void testRun() {
        PowerMockito.mockStatic(DomobackConf.class);
        Mockito.when(DomobackConf.get(CONFKEYS.TELEINFO_PROCESSRECORD)).thenReturn("0");
        this.teleinfo.run();
    }

    @Test
    @Ignore
    public void testSave() throws SQLException {

        PowerMockito.mockStatic(DomobackConf.class);
        Mockito.when(DomobackConf.get(CONFKEYS.TELEINFO_FILETOUSE)).thenReturn("linky.wav");
        Mockito.when(DomobackConf.get(CONFKEYS.TELEINFO_PROCESSRECORD)).thenReturn("0");
        Mockito.when(DomobackConf.get(CONFKEYS.TELEINFO_TRAMETIME)).thenReturn("1");
        Mockito.doNothing().when(this.jdbc).saveTeleinfos(Mockito.any(),Mockito.any(), Mockito.any());
        this.teleinfo.run();
        assertTrue(this.teleinfo.save());
    }
}
