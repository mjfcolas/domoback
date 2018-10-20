package com.manu.domoback.arduinoreader;

import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;

@RunWith(MockitoJUnitRunner.class)
public class ArduinoReaderTest extends TestCase {

    @InjectMocks
    private ArduinoReader arduinoReader;
    @Mock
    private ICommPortWrapper port;
    private final SerialPort serialPort = new SerialPortMock();
    @Mock
    private SerialPortEvent spe;

    @Before
    public void before() {
        try {
            Mockito.when(this.port.openPort(any(String.class), any(Integer.class))).thenReturn(this.serialPort);
            this.arduinoReader.initialize();
        } catch (final Exception e) {
            fail();
        }
    }

    @Test
    public void testSerialEvent() {
        try {
            Mockito.when(this.spe.getEventType()).thenReturn(SerialPortEvent.DATA_AVAILABLE);
            this.arduinoReader.serialEvent(this.spe);
            this.arduinoReader.getInfos();
        } catch (final Exception e) {
            fail();
        }
    }

    @Test
    public void testWrite() {
        try {
            this.arduinoReader.writeData("TEST WRITE");
        } catch (final Exception e) {
            fail();
        }
    }

}
