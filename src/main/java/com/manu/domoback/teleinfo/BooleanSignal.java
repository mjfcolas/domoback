package com.manu.domoback.teleinfo;

import java.util.List;

public class BooleanSignal {

    protected List<Boolean> signal;
    protected int highAmp;
    protected int lowAmp;

    public BooleanSignal(List<Boolean> signal, int highAmp, int lowAmp) {
        this.signal = signal;
        this.highAmp = highAmp;
        this.lowAmp = lowAmp;
    }

    public List<Boolean> getSignal() {
        return signal;
    }

    public void setSignal(List<Boolean> signal) {
        this.signal = signal;
    }

    public int getHighAmp() {
        return highAmp;
    }

    public void setHighAmp(int highAmp) {
        this.highAmp = highAmp;
    }

    public int getLowAmp() {
        return lowAmp;
    }

    public void setLowAmp(int lowAmp) {
        this.lowAmp = lowAmp;
    }
}
