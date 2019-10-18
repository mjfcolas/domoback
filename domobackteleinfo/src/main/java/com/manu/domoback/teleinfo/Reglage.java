package com.manu.domoback.teleinfo;

public class Reglage {

    private double gain;
    private int offset;
    private boolean inverse;
    private boolean redresser;

    public Reglage(double gain, int offset, boolean redresser, boolean inverse) {
        this.gain = gain;
        this.offset = offset;
        this.redresser = redresser;
        this.inverse = inverse;
    }

    public double getGain() {
        return gain;
    }

    public int getOffset() {
        return offset;
    }

    public boolean isInverse() {
        return inverse;
    }

    public boolean isRedresser() {
        return redresser;
    }
}
