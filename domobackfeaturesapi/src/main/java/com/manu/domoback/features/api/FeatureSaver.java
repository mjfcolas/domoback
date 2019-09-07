package com.manu.domoback.features.api;

import com.manu.domoback.features.api.features.IFeature;

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
