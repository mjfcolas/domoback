package com.manu.domoback.test.features.api.factory.mock;

import com.manu.domoback.features.api.factory.IFeatureFactory;
import com.manu.domoback.features.api.features.IFeature;

public class MockFeatureFactoryImpl implements IFeatureFactory {
    @Override
    public String identify() {
        return "MOCK";
    }

    @Override
    public IFeature instantiate() {
        return null;
    }

}
