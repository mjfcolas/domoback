package com.manu.domoback.chauffage;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChauffageInfo implements IChauffageInfo {

    private Boolean hasChangedModeAttr = false;
    private Boolean chauffageState = false;
    private Boolean chauffageStateKnown = false;
    private Boolean chauffageModeAttr;
    private Boolean chauffaHourgeModeAttr;
    private Integer chauffageTemp;

    private Map<Date, Integer> tempByHoursMap = new HashMap<>();

    @Override
    public Boolean getChauffageMode() {
        return this.chauffageModeAttr;
    }

    @Override
    public Boolean getChauffageHourMode() {
        return this.chauffaHourgeModeAttr;
    }

    @Override
    public void setChauffageMode(final Boolean chauffageMode) {
        if (this.chauffageModeAttr != chauffageMode) {
            this.hasChangedModeAttr = true;
        }
        this.chauffageModeAttr = chauffageMode;
    }

    @Override
    public void setChauffageHourMode(final Boolean chauffageHourMode) {
        this.chauffaHourgeModeAttr = chauffageHourMode;
    }

    @Override
    public Integer getChauffageTemp() {
        return this.chauffageTemp;
    }

    @Override
    public void setChauffageTemp(final Integer chauffageTemp) {
        this.chauffageTemp = chauffageTemp;
    }

    @Override
    public Boolean hasChangedMode() {
        if (this.hasChangedModeAttr) {
            this.hasChangedModeAttr = false;
            return true;
        }
        return false;
    }

    @Override
    public Boolean getChauffageState() {
        return this.chauffageState;
    }

    @Override
    public void setChauffageState(final Boolean chauffageState) {
        this.chauffageState = chauffageState;
    }

    @Override
    public Boolean getChauffageStateKnown() {
        return this.chauffageStateKnown;
    }

    @Override
    public void setChauffageStateKnown(final Boolean chauffageStateKnown) {
        this.chauffageStateKnown = chauffageStateKnown;
    }

    @Override
    public Map<Date, Integer> getTempByHoursMap() {
        return this.tempByHoursMap;
    }

    @Override
    public void setTempByHoursMap(final Map<Date, Integer> tempByHoursMap) {
        this.tempByHoursMap = tempByHoursMap;
    }
}
