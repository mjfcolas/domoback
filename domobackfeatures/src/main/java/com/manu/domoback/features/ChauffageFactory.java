package com.manu.domoback.features;

import com.manu.domoback.features.api.factory.IFeatureFactory;
import com.manu.domoback.features.api.features.IChauffage;
import com.manu.domoback.features.api.factory.IChauffageFactory;

public class ChauffageFactory implements IChauffageFactory, IFeatureFactory {
    @Override
    public String identify() {
        return "ChauffageFactory";
    }

    @Override
    public IChauffage instantiate() {
        return new Chauffage();
    }
}
