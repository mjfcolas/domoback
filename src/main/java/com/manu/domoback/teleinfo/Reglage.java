package com.manu.domoback.teleinfo;

public class Reglage {

    protected double gain;
    protected int offset;
    protected int limite;
    protected boolean inverse;

    public Reglage(double gain, int offset, int limite, boolean inverse) {
        this.gain = gain;
        this.offset = offset;
        this.limite = limite;
        this.inverse = inverse;
    }


}
