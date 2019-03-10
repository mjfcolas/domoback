package com.manu.domoback.serial;

import com.manu.domoback.serial.exceptions.PortNotFoundException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;

public interface ICommPortWrapper {

    /**
     * Find a serial port and open it
     *
     * @param className
     * @param timeOut   time out waiting for port
     * @return the open serial port
     * @throws PortNotFoundException
     * @throws PortInUseException
     */
    SerialPort openPort(final String className, final Integer timeOut) throws PortNotFoundException, PortInUseException;
}
