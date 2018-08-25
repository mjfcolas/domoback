package com.manu.domoback.teleinfo.signalmodifiers;

public class CarreParam {

    int triggerVersHaut = -100;
    int triggerVersBas = 100;
    boolean inverseBits;
    int frequence;
    double triggerTemps = 0.3;
    int sampleRate;

    public int getTriggerVersHaut() {
        return triggerVersHaut;
    }

    public void setTriggerVersHaut(int triggerVersHaut) {
        this.triggerVersHaut = triggerVersHaut;
    }

    public int getTriggerVersBas() {
        return triggerVersBas;
    }

    public void setTriggerVersBas(int triggerVersBas) {
        this.triggerVersBas = triggerVersBas;
    }

    public boolean isInverseBits() {
        return inverseBits;
    }

    public void setInverseBits(boolean inverseBits) {
        this.inverseBits = inverseBits;
    }

    public int getFrequence() {
        return frequence;
    }

    public void setFrequence(int frequence) {
        this.frequence = frequence;
    }

    public double getTriggerTemps() {
        return triggerTemps;
    }

    public void setTriggerTemps(double triggerTemps) {
        this.triggerTemps = triggerTemps;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
    }
}
