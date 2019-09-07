package com.manu.domoback.features.api.factory;

import com.manu.domoback.features.api.features.IChauffage;

public interface IChauffageFactory extends IFeatureFactory{
    /**
     * Instanciate a feature
     * @return
     */
    IChauffage instantiate();
}
