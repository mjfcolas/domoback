package com.manu.domoback.features;

import com.manu.domoback.features.api.features.IFeature;
import com.manu.domoback.features.api.factory.IFeatureFactory;

public class TeleinfoFactory implements IFeatureFactory {
    @Override
    public String identify() {
        return "TeleinfoFactory";
    }

    @Override
    public IFeature instantiate() {
        return new Teleinfo();
    }
}
