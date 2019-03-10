package com.manu.domoback.arduinoreader;

import com.manu.domoback.conf.CONFKEYS;
import com.manu.domoback.conf.DomobackConf;
import com.manu.domoback.arduinoreader.enums.InfoKeys;
import com.manu.domoback.serial.CommPortManager;
import com.manu.domoback.serial.ICommPortWrapper;
import com.manu.domoback.serial.exceptions.PortNotFoundException;
import gnu.io.PortInUseException;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ArduinoReader implements SerialPortEventListener, IArduinoReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArduinoReader.class.getName());

    private static final String ERROR_MESSAGE = "NO_ANSWER";
    private static final String RECEPTION_PREFIX = "RECE";

    private final ArduinoInfos infos = new ArduinoInfos();
    private boolean isReady = false;

    private SerialPort serialPort;

    private ICommPortWrapper commPortWrapper = new CommPortManager();

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
    private static final int DATA_RATE = Integer.parseInt(DomobackConf.get(CONFKEYS.SERIAL_DATA_RATE));

    private static final int COMMAND_SIZE = 30;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Object sendLock = new Object();

    @Override
    public void initialize() {

        try {
            // open serial port, and use class name for the appName.
            this.serialPort = this.commPortWrapper.openPort(this.getClass().getName(),
                    TIME_OUT);

            // set port parameters
            this.serialPort.setSerialPortParams(DATA_RATE,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);

            // open the streams
            this.input = new BufferedReader(new InputStreamReader(this.serialPort.getInputStream()));
            this.output = this.serialPort.getOutputStream();

            // add event listeners
            this.serialPort.addEventListener(this);
            this.serialPort.notifyOnDataAvailable(true);
            this.isReady = true;
        } catch (final PortNotFoundException e) {
            LOGGER.error("Port not found", e);
        } catch (final PortInUseException e) {
            LOGGER.error("Port in use", e);
        } catch (final Exception e) {
            LOGGER.error(DomobackConf.get(CONFKEYS.LOG_ERROR_GENERIC), e);
        }
    }

    /**
     * Handle an event on the serial port. Read the data and print it.
     */
    @Override
    public synchronized void serialEvent(final SerialPortEvent oEvent) {
        if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            try {
                String inputLine;
                while ((inputLine = this.input.readLine()) != null) {
                    LOGGER.info("FRA {}", inputLine);
                    final String[] info = inputLine.split(" ");
                    if (this.logSerialEvent(info)) {
                        continue;
                    }
                    if (info.length > 2 && RECEPTION_PREFIX.equals(info[0])) {
                        this.fillInfos(info);
                    }
                }
            } catch (final Exception e) {
                LOGGER.error(DomobackConf.get(CONFKEYS.LOG_ERROR_GENERIC), e);
            }
        }
    }

    private boolean logSerialEvent(final String[] info) {
        boolean error = false;
        if (info.length > 1 && RECEPTION_PREFIX.equals(info[0])) {
            final String actionType = info[1];
            if (info.length > 2 && ERROR_MESSAGE.equals(info[2])) {
                error = true;
            }
            if (ArduinoKeys.allValues().contains(actionType)) {
                this.infos.addSerialEvent(actionType, error);
            }
        }
        return error;
    }

    private void fillInfos(final String[] info) {
        if (InfoKeys.T.name().equals(info[1])) {
            this.infos.setTemperature(Float.parseFloat(info[2]));
        } else if (InfoKeys.T2.name().equals(info[1])) {
            this.infos.setTemperature2(Float.parseFloat(info[2]));
        } else if (InfoKeys.T3.name().equals(info[1])) {
            this.infos.setTemperature3(Float.parseFloat(info[2]));
        } else if (InfoKeys.AP.name().equals(info[1])) {
            this.infos.setPressionAbsolue(Float.parseFloat(info[2]));
        } else if (InfoKeys.RP.name().equals(info[1])) {
            this.infos.setPressionRelative(Float.parseFloat(info[2]));
        } else if (InfoKeys.HH.name().equals(info[1])) {
            this.infos.setHygrometrie(Float.parseFloat(info[2]));
        } else if (InfoKeys.MK.name().equals(info[1])) {
            this.infos.setKey(info[2]);
        } else if (InfoKeys.D.name().equals(info[1])) {
            this.infos.setChauffageState("1".equals(info[2]));
        }
    }

    @Override
    public void writeData(final String toSend) {
        LOGGER.debug("writeData IN");
        this.executor.submit(() -> this.sendMessage(toSend));
        LOGGER.debug("writeData OUT");
    }

    private void sendMessage(final String toSend) {
        LOGGER.debug("sendMessage IN");
        final long initTime = System.currentTimeMillis();
        if (toSend != null) {
            try {
                LOGGER.info("TOA {}", toSend);
                final byte[] bytes = toSend.getBytes(Charset.forName("UTF-8"));
                final byte[] preparedMessage = new byte[COMMAND_SIZE];
                for (int i = 0; i < bytes.length; i++) {
                    preparedMessage[i] = bytes[i];
                }
                for (int i = bytes.length; i < COMMAND_SIZE; i++) {
                    preparedMessage[i] = 0;
                }
                this.output.write(preparedMessage);
                this.output.flush();
                synchronized (this.sendLock) {
                    while (System.currentTimeMillis() - initTime <= 1000) {
                        this.sendLock.wait(1000);
                    }
                }
            } catch (IOException | InterruptedException e) {
                Thread.currentThread().interrupt();
                LOGGER.error(DomobackConf.get(CONFKEYS.LOG_ERROR_GENERIC), e);
            }
        }
        LOGGER.debug("sendMessage OUT");
    }

    /**
     * This should be called when you stop using the port.
     * This will prevent port locking on platforms like Linux.
     */
    public synchronized void close() {
        if (this.serialPort != null) {
            this.serialPort.removeEventListener();
            this.serialPort.close();
        }
    }

    @Override
    public boolean isReady() {
        return this.isReady;
    }

    @Override
    public ArduinoInfos getInfos() {
        return this.infos;
    }

    public void setCommPortWrapper(final ICommPortWrapper commPortWrapper) {
        this.commPortWrapper = commPortWrapper;
    }

    public void setSerialPort(final SerialPort serialPort) {
        this.serialPort = serialPort;
    }
}
