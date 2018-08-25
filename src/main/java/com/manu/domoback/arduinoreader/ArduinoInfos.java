package com.manu.domoback.arduinoreader;

public class ArduinoInfos implements IExternalInfos {

    protected String key;
    protected Float temperature;
    protected Float temperature2;
    protected Float pressionAbsolue;
    protected Float pressionRelative;
    protected Float hygrometrie;
    protected Boolean chauffageState;

    public Float getTemperature() {
        return temperature;
    }

    public void setTemperature(Float temperature) {
        this.temperature = temperature;
    }

    public Float getTemperature2() {
        return temperature2;
    }

    public void setTemperature2(Float temperature) {
        this.temperature2 = temperature;
    }

    public Float getPressionAbsolue() {
        return pressionAbsolue;
    }

    public void setPressionAbsolue(Float pressionAbsolue) {
        this.pressionAbsolue = pressionAbsolue;
    }

    public Float getPressionRelative() {
        return pressionRelative;
    }

    public void setPressionRelative(Float pressionRelative) {
        this.pressionRelative = pressionRelative;
    }

    public Float getHygrometrie() {
        return hygrometrie;
    }

    public void setHygrometrie(Float hygrometrie) {
        this.hygrometrie = hygrometrie;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Boolean getChauffageState() {
        Boolean result = chauffageState;
        this.chauffageState = null;
        return result;
    }

    public void setChauffageState(Boolean chauffageState) {
        this.chauffageState = chauffageState;
    }
}
