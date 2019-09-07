package com.manu.domoback.features;

import com.manu.domoback.features.api.features.IChauffage;
import com.manu.domoback.features.api.factory.IChauffageFactory;

public class ChauffageFactory extends AbstractFeatureFactory implements IChauffageFactory {
    @Override
    public String identify() {
        return "ChauffageFactory";
    }

    @Override
    public IChauffage instantiate() {
        return new Chauffage();
    }
}
