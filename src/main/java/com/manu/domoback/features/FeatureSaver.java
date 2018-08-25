package com.manu.domoback.features;

import java.util.TimerTask;

public class FeatureSaver extends TimerTask {

    IFeature feature;

    public FeatureSaver(IFeature feature) {
        this.feature = feature;
    }

    public void run() {
        this.feature.save();
    }
}
