package com.manu.domoback.chauffage;

public class ChauffageInfo implements IChauffageInfo {

    private Boolean hasChangedModeAttr = false;
    private Boolean chauffageState = false;
    private Boolean chauffageStateKnown = false;
    private Boolean chauffageModeAttr;
    private Integer chauffageTempAttr;

    public Boolean getChauffageMode() {
        return chauffageModeAttr;
    }

    public void setChauffageMode(Boolean chauffageMode) {
        if (chauffageModeAttr != chauffageMode) {
            hasChangedModeAttr = true;
        }
        chauffageModeAttr = chauffageMode;
    }

    public Integer getChauffageTemp() {
        return chauffageTempAttr;
    }

    public void setChauffageTemp(Integer chauffageTemp) {
        if (chauffageTempAttr == null || !chauffageTempAttr.equals(chauffageTemp)) {
            chauffageTempAttr = chauffageTemp;
        }
        chauffageTempAttr = chauffageTemp;
    }

    public Boolean hasChangedMode() {
        if (hasChangedModeAttr) {
            hasChangedModeAttr = false;
            return true;
        }
        return false;
    }

    public Boolean getChauffageState() {
        return chauffageState;
    }

    public void setChauffageState(Boolean chauffageState) {
        this.chauffageState = chauffageState;
    }

    public Boolean getChauffageStateKnown() {
        return chauffageStateKnown;
    }

    public void setChauffageStateKnown(Boolean chauffageStateKnown) {
        this.chauffageStateKnown = chauffageStateKnown;
    }
}
