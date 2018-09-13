package com.manu.domoback.arduinoreader;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ArduinoReader implements SerialPortEventListener, IArduinoReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArduinoReader.class.getName());

    private ArduinoInfos infos = new ArduinoInfos();
    private boolean isReady = false;

    private SerialPort serialPort;
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
            "/dev/ttyACM2"
    };

    /**
     * A BufferedReader which will be fed by a InputStreamReader
     * converting the bytes into characters
     * making the displayed results codepage independent
     */
    private BufferedReader input;
    private OutputStream output;
    /**
     * Milliseconds to block while waiting for port open
     */
    private static final int TIME_OUT = 2000;
    /**
     * Default bits per second for COM port.
     */
    private static final int DATA_RATE = 9600;

    private static final int COMMAND_SIZE = 30;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Object sendLock = new Object();

    public void initialize() {

        CommPortIdentifier portId = null;
        Enumeration<?> portEnum = CommPortIdentifier.getPortIdentifiers();

        //First, Find an instance of serial port as set in PORT_NAMES.
        while (portEnum.hasMoreElements()) {
            CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
            for (String portName : PORT_NAMES) {
                if (currPortId.getName().equals(portName)) {
                    portId = currPortId;
                    break;
                }
            }
        }
        if (portId == null) {
            LOGGER.error("Could not find COM port.");
            return;
        }

        try {
            // open serial port, and use class name for the appName.
            serialPort = (SerialPort) portId.open(this.getClass().getName(),
                    TIME_OUT);

            // set port parameters
            serialPort.setSerialPortParams(DATA_RATE,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);

            // open the streams
            input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
            output = serialPort.getOutputStream();

            // add event listeners
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
            this.isReady = true;
        } catch (Exception e) {
            LOGGER.error("An error occured", e);
        }
    }

    /**
     * Handle an event on the serial port. Read the data and print it.
     */
    public synchronized void serialEvent(SerialPortEvent oEvent) {
        if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            try {
                String inputLine;
                while ((inputLine = input.readLine()) != null) {

                    LOGGER.info("FRA {}", inputLine);
                    String[] info = inputLine.split(" ");

                    if (info.length > 1) {
                        this.fillInfos(info);
                    }
                }
            } catch (Exception e) {
                LOGGER.error("An error occured", e);
            }
        }
    }

    private void fillInfos(String[] info) {
        if (InfoKeys.T.name().equals(info[0])) {
            infos.setTemperature(Float.parseFloat(info[1]));
        } else if (InfoKeys.T2.name().equals(info[0])) {
            infos.setTemperature2(Float.parseFloat(info[1]));
        } else if (InfoKeys.AP.name().equals(info[0])) {
            infos.setPressionAbsolue(Float.parseFloat(info[1]));
        } else if (InfoKeys.RP.name().equals(info[0])) {
            infos.setPressionRelative(Float.parseFloat(info[1]));
        } else if (InfoKeys.HH.name().equals(info[0])) {
            infos.setHygrometrie(Float.parseFloat(info[1]));
        } else if (InfoKeys.MK.name().equals(info[0])) {
            infos.setKey(info[1]);
        } else if (InfoKeys.D.name().equals(info[0])) {
            infos.setChauffageState("1".equals(info[1]));
        }
    }

    public void writeData(String toSend) {
        LOGGER.debug("writeData IN");
        this.executor.submit(() -> this.sendMessage(toSend));
        LOGGER.debug("writeData OUT");
    }

    private void sendMessage(String toSend) {
        LOGGER.debug("sendMessage IN");
        long initTime = System.currentTimeMillis();
        if (toSend != null) {
            try {
                LOGGER.info("TOA {}", toSend);
                byte[] bytes = toSend.getBytes(Charset.forName("UTF-8"));
                byte[] preparedMessage = new byte[COMMAND_SIZE];
                for (int i = 0; i < bytes.length; i++) {
                    preparedMessage[i] = bytes[i];
                }
                for (int i = bytes.length; i < COMMAND_SIZE; i++) {
                    preparedMessage[i] = 0;
                }
                output.write(preparedMessage);
                output.flush();
                synchronized (sendLock) {
                    while (System.currentTimeMillis() - initTime <= 1000) {
                        sendLock.wait(1000);
                    }
                }
            } catch (IOException | InterruptedException e) {
                LOGGER.error("An error occured", e);
            }
        }
        LOGGER.debug("sendMessage OUT");
    }

    /**
     * This should be called when you stop using the port.
     * This will prevent port locking on platforms like Linux.
     */
    public synchronized void close() {
        if (serialPort != null) {
            serialPort.removeEventListener();
            serialPort.close();
        }
    }

    public boolean isReady() {
        return isReady;
    }

    public ArduinoInfos getInfos() {
        return infos;
    }
}
