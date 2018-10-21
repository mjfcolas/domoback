package com.manu.domoback.serial;

import gnu.io.CommPortIdentifier;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Enumeration;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ CommPortIdentifier.class })
@SuppressStaticInitializationFor("gnu.io.CommPortIdentifier")
public class CommPortManagerTest extends TestCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommPortManagerTest.class.getName());

    @Mock
    private Enumeration<?> portEnum;
    @Mock
    private CommPortIdentifier cpi;
    @InjectMocks
    private CommPortManager commPortManager;

    @Test
    public void testOpenPort() {
        try {

            PowerMockito.mockStatic(CommPortIdentifier.class);
            Mockito.when(this.portEnum.hasMoreElements()).thenReturn(true);
            Mockito.when(this.cpi.getName()).thenReturn("/dev/ttyS81");
            Mockito.when((CommPortIdentifier) this.portEnum.nextElement()).thenReturn(this.cpi);
            Mockito.when(CommPortIdentifier.getPortIdentifiers()).thenReturn(this.portEnum);
            this.commPortManager.openPort("TEST", 2000);
        } catch (final Exception e) {
            LOGGER.error("Exception", e);
            fail();
        }
    }
}
