package com.manu.domoback.features.api;

import com.manu.domoback.features.api.features.IFeature;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FeatureWrapper implements IFeatureWrapper {

    protected Set<IFeature> featureSet = new HashSet<>();

    public FeatureWrapper(IFeature... features) {
        for (IFeature feature : features) {
            featureSet.add(feature);
        }
    }

    public void addFeature(IFeature feature) {
        this.featureSet.add(feature);
    }

    @Override
    public Map<String, String> getFeaturesInfos() {
        Map<String, String> infos = new HashMap<>();

        for (IFeature feature : featureSet) {
            infos.putAll(feature.getInfos());
        }

        return infos;
    }

    @Override
    public Set<IFeature> getFeatures() {
        return featureSet;
    }

}
