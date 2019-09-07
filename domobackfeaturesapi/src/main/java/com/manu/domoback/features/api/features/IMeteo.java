package com.manu.domoback.features.api.features;

import com.manu.domoback.arduinoreader.ExternalDataController;

public interface IMeteo extends IFeature {

    /**
     * Initialize feature
     * @param externalDataController
     * @param identifier
     */
    void init(ExternalDataController externalDataController, String identifier);
}
