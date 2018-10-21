package com.manu.domoback.arduinoreader;

import com.manu.domoback.mocks.InputStreamMock;
import com.manu.domoback.mocks.OutputStreamMock;
import gnu.io.SerialPort;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.TooManyListenersException;

public class SerialPortMock extends SerialPort {

    private final boolean streamsError;
    private boolean serialPortParamsError = false;

    SerialPortMock() {
        this.streamsError = false;
    }

    SerialPortMock(final boolean streamsError) {
        this.streamsError = streamsError;
    }

    @Override
    public void setSerialPortParams(final int i, final int i1, final int i2, final int i3) throws UnsupportedCommOperationException {
        if (this.serialPortParamsError) {
            throw new UnsupportedCommOperationException();
        }
    }

    @Override
    public int getBaudRate() {
        return 0;
    }

    @Override
    public int getDataBits() {
        return 0;
    }

    @Override
    public int getStopBits() {
        return 0;
    }

    @Override
    public int getParity() {
        return 0;
    }

    @Override
    public void setFlowControlMode(final int i) throws UnsupportedCommOperationException {

    }

    @Override
    public int getFlowControlMode() {
        return 0;
    }

    @Override
    public boolean isDTR() {
        return false;
    }

    @Override
    public void setDTR(final boolean b) {

    }

    @Override
    public void setRTS(final boolean b) {

    }

    @Override
    public boolean isCTS() {
        return false;
    }

    @Override
    public boolean isDSR() {
        return false;
    }

    @Override
    public boolean isCD() {
        return false;
    }

    @Override
    public boolean isRI() {
        return false;
    }

    @Override
    public boolean isRTS() {
        return false;
    }

    @Override
    public void sendBreak(final int i) {

    }

    @Override
    public void addEventListener(final SerialPortEventListener serialPortEventListener) throws TooManyListenersException {

    }

    @Override
    public void removeEventListener() {

    }

    @Override
    public void notifyOnDataAvailable(final boolean b) {

    }

    @Override
    public void notifyOnOutputEmpty(final boolean b) {

    }

    @Override
    public void notifyOnCTS(final boolean b) {

    }

    @Override
    public void notifyOnDSR(final boolean b) {

    }

    @Override
    public void notifyOnRingIndicator(final boolean b) {

    }

    @Override
    public void notifyOnCarrierDetect(final boolean b) {

    }

    @Override
    public void notifyOnOverrunError(final boolean b) {

    }

    @Override
    public void notifyOnParityError(final boolean b) {

    }

    @Override
    public void notifyOnFramingError(final boolean b) {

    }

    @Override
    public void notifyOnBreakInterrupt(final boolean b) {

    }

    @Override
    public byte getParityErrorChar() throws UnsupportedCommOperationException {
        return 0;
    }

    @Override
    public boolean setParityErrorChar(final byte b) throws UnsupportedCommOperationException {
        return false;
    }

    @Override
    public byte getEndOfInputChar() throws UnsupportedCommOperationException {
        return 0;
    }

    @Override
    public boolean setEndOfInputChar(final byte b) throws UnsupportedCommOperationException {
        return false;
    }

    @Override
    public boolean setUARTType(final String s, final boolean b) throws UnsupportedCommOperationException {
        return false;
    }

    @Override
    public String getUARTType() throws UnsupportedCommOperationException {
        return null;
    }

    @Override
    public boolean setBaudBase(final int i) throws UnsupportedCommOperationException, IOException {
        return false;
    }

    @Override
    public int getBaudBase() throws UnsupportedCommOperationException, IOException {
        return 0;
    }

    @Override
    public boolean setDivisor(final int i) throws UnsupportedCommOperationException, IOException {
        return false;
    }

    @Override
    public int getDivisor() throws UnsupportedCommOperationException, IOException {
        return 0;
    }

    @Override
    public boolean setLowLatency() throws UnsupportedCommOperationException {
        return false;
    }

    @Override
    public boolean getLowLatency() throws UnsupportedCommOperationException {
        return false;
    }

    @Override
    public boolean setCallOutHangup(final boolean b) throws UnsupportedCommOperationException {
        return false;
    }

    @Override
    public boolean getCallOutHangup() throws UnsupportedCommOperationException {
        return false;
    }

    @Override
    public void enableReceiveFraming(final int i) throws UnsupportedCommOperationException {

    }

    @Override
    public void disableReceiveFraming() {

    }

    @Override
    public boolean isReceiveFramingEnabled() {
        return false;
    }

    @Override
    public int getReceiveFramingByte() {
        return 0;
    }

    @Override
    public void disableReceiveTimeout() {

    }

    @Override
    public void enableReceiveTimeout(final int i) throws UnsupportedCommOperationException {

    }

    @Override
    public boolean isReceiveTimeoutEnabled() {
        return false;
    }

    @Override
    public int getReceiveTimeout() {
        return 0;
    }

    @Override
    public void enableReceiveThreshold(final int i) throws UnsupportedCommOperationException {

    }

    @Override
    public void disableReceiveThreshold() {

    }

    @Override
    public int getReceiveThreshold() {
        return 0;
    }

    @Override
    public boolean isReceiveThresholdEnabled() {
        return false;
    }

    @Override
    public void setInputBufferSize(final int i) {

    }

    @Override
    public int getInputBufferSize() {
        return 0;
    }

    @Override
    public void setOutputBufferSize(final int i) {

    }

    @Override
    public int getOutputBufferSize() {
        return 0;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (this.streamsError) {
            return new InputStreamMock(true);
        }
        final StringBuilder sb = new StringBuilder();
        final String systemPropertyLineSeparator = "line.separator";
        sb.append("T 15");
        sb.append(System.getProperty(systemPropertyLineSeparator));
        sb.append("T2 16");
        sb.append(System.getProperty(systemPropertyLineSeparator));
        sb.append("T3 17");
        sb.append(System.getProperty(systemPropertyLineSeparator));
        sb.append("AP 1000");
        sb.append(System.getProperty(systemPropertyLineSeparator));
        sb.append("RP 1010");
        sb.append(System.getProperty(systemPropertyLineSeparator));
        sb.append("HH 25");
        sb.append(System.getProperty(systemPropertyLineSeparator));
        sb.append("MK 1");
        sb.append(System.getProperty(systemPropertyLineSeparator));
        sb.append("D 0");
        sb.append(System.getProperty(systemPropertyLineSeparator));
        sb.append("NOK 0");
        sb.append(System.getProperty(systemPropertyLineSeparator));
        return new ByteArrayInputStream(sb.toString().getBytes());
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return new OutputStreamMock(this.streamsError);
    }

    void setSerialPortParamsError(final boolean serialPortParamsError) {
        this.serialPortParamsError = serialPortParamsError;
    }
}
