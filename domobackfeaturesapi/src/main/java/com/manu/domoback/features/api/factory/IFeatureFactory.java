package com.manu.domoback.features.api.factory;

import com.manu.domoback.features.api.features.IFeature;

public interface IFeatureFactory {
    /**
     * Return the identifier of the implementation
     *
     * @return
     */
    String identify();

    /**
     * Instanciate a feature
     * @return
     */
    IFeature instantiate();
}
