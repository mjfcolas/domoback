package com.manu.domoback.serial;

import com.manu.domoback.serial.exceptions.PortNotFoundException;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;

import java.util.Enumeration;

public class CommPortManager implements ICommPortWrapper {

    /**
     * The port we're normally going to use.
     */
    private static final String[] PORT_NAMES = {
            "/dev/ttyS80",
            "/dev/ttyS81",
            "/dev/ttyS82",
            "/dev/ttyS83",
            "/dev/ttyACM0",
            "/dev/ttyACM1",
            "/dev/ttyACM2",
            "/dev/ttyUSB0",
            "/dev/ttyUSB1"
    };

    @Override
    public SerialPort openPort(final String className, final Integer timeOut) throws PortNotFoundException, PortInUseException {

        final Enumeration<?> portEnum = CommPortIdentifier.getPortIdentifiers();
        final CommPortIdentifier portId;
        //First, Find an instance of serial port as set in PORT_NAMES.
        while (portEnum.hasMoreElements()) {
            final CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
            for (final String portName : PORT_NAMES) {
                if (currPortId.getName().equals(portName)) {
                    portId = currPortId;
                    return (SerialPort) portId.open(className, timeOut);
                }
            }
        }
        throw new PortNotFoundException();
    }

}
