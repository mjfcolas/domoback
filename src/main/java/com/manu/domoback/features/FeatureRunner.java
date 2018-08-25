package com.manu.domoback.features;

import com.manu.domoback.common.CustLogger;

import java.util.TimerTask;

public class FeatureRunner extends TimerTask {

    IFeature feature;

    public FeatureRunner(IFeature feature) {
        this.feature = feature;
    }

    public void run() {
        if (feature.getName() != null && !feature.getName().isEmpty()) {
            CustLogger.outprintln("RUN FEATURE " + feature.getName());
        }
        this.feature.run();
    }
}
