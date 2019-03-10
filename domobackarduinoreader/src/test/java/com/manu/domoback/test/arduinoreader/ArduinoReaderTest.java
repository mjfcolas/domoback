package com.manu.domoback.test.arduinoreader;

import com.manu.domoback.arduinoreader.ArduinoReader;
import com.manu.domoback.serial.ICommPortWrapper;
import com.manu.domoback.serial.exceptions.PortNotFoundException;
import gnu.io.PortInUseException;
import gnu.io.SerialPortEvent;
import junit.framework.TestCase;
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
    @Mock
    private SerialPortEvent spe;

    @Test
    public void testInitializeErrorPortInUse() {
        try {
            Mockito.when(this.port.openPort(any(String.class), any(Integer.class))).thenThrow(new PortInUseException());
            Mockito.when(this.spe.getEventType()).thenReturn(SerialPortEvent.DATA_AVAILABLE);
            this.arduinoReader.initialize();
        } catch (final Exception e) {
            fail();
        }
    }

    @Test
    public void testInitializeErrorPortNotFound() {
        try {
            Mockito.when(this.port.openPort(any(String.class), any(Integer.class))).thenThrow(new PortNotFoundException());
            Mockito.when(this.spe.getEventType()).thenReturn(SerialPortEvent.DATA_AVAILABLE);
            this.arduinoReader.initialize();
        } catch (final Exception e) {
            fail();
        }
    }

    @Test
    public void testInitializeErrorGeneric() {
        try {
            final SerialPortMock spm = new SerialPortMock();
            spm.setSerialPortParamsError(true);
            Mockito.when(this.port.openPort(any(String.class), any(Integer.class))).thenReturn(spm);
            Mockito.when(this.spe.getEventType()).thenReturn(SerialPortEvent.DATA_AVAILABLE);
            this.arduinoReader.initialize();
        } catch (final Exception e) {
            fail();
        }
    }

    @Test
    public void testSerialEvent() {
        try {

            Mockito.when(this.port.openPort(any(String.class), any(Integer.class))).thenReturn(new SerialPortMock(false));
            Mockito.when(this.spe.getEventType()).thenReturn(SerialPortEvent.DATA_AVAILABLE);
            this.arduinoReader.initialize();
            this.arduinoReader.serialEvent(this.spe);
            this.arduinoReader.getInfos();
        } catch (final Exception e) {
            fail();
        }
    }

    @Test
    public void testSerialEventError() {
        try {
            Mockito.when(this.port.openPort(any(String.class), any(Integer.class))).thenReturn(new SerialPortMock(true));
            Mockito.when(this.spe.getEventType()).thenReturn(SerialPortEvent.DATA_AVAILABLE);
            this.arduinoReader.initialize();
            this.arduinoReader.serialEvent(this.spe);
        } catch (final PortInUseException e) {
            fail();
        } catch (final PortNotFoundException e) {
            fail();
        }
    }

    @Test
    public void testWrite() {
        try {
            Mockito.when(this.port.openPort(any(String.class), any(Integer.class))).thenReturn(new SerialPortMock(false));
            this.arduinoReader.initialize();
            this.arduinoReader.writeData("TEST WRITE");
        } catch (final Exception e) {
            fail();
        }
    }

    @Test
    public void testWriteError() {
        try {
            Mockito.when(this.port.openPort(any(String.class), any(Integer.class))).thenReturn(new SerialPortMock(true));
            this.arduinoReader.initialize();
            this.arduinoReader.writeData("TEST WRITE");
        } catch (final Exception e) {
            fail();
        }
    }

}
