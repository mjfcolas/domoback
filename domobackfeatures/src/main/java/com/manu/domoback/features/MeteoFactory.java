package com.manu.domoback.features;

import com.manu.domoback.features.api.features.IMeteo;
import com.manu.domoback.features.api.factory.IMeteoFactory;

public class MeteoFactory extends AbstractFeatureFactory implements IMeteoFactory {
    @Override
    public String identify() {
        return "MeteoFactory";
    }

    @Override
    public IMeteo instantiate() {
        return new Meteo();
    }
}
