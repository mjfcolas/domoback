package com.manu.domoback.features.api.factory;

import com.manu.domoback.features.api.features.IMeteo;

public interface IMeteoFactory extends IFeatureFactory{
    /**
     * Instanciate a feature
     * @return
     */
    IMeteo instantiate();
}
