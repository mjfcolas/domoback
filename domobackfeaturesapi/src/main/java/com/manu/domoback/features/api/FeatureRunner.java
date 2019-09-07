package com.manu.domoback.features.api;

import com.manu.domoback.features.api.features.IFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimerTask;

public class FeatureRunner extends TimerTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(FeatureRunner.class.getName());

    private IFeature feature;

    public FeatureRunner(IFeature feature) {
        this.feature = feature;
    }

    public void run() {
        if (feature.getName() != null && !feature.getName().isEmpty()) {
            LOGGER.info("RUN FEATURE {}", feature.getName());
        }
        this.feature.run();
    }
}
