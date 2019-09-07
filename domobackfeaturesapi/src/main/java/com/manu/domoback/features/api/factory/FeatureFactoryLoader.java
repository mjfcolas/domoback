package com.manu.domoback.features.api.factory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

public class FeatureFactoryLoader {
    private static Map<String, IFeatureFactory> features = new HashMap<>();

    private FeatureFactoryLoader() {
        throw new IllegalStateException("Utility class");
    }

    public static <T extends IFeatureFactory> T getFeatureFactory(String feature) {
        if (features.isEmpty()) {
            ServiceLoader<IFeatureFactory> loader = ServiceLoader.load(IFeatureFactory.class);
            Iterator<IFeatureFactory> persistenceApiIterator = loader.iterator();
            while (persistenceApiIterator.hasNext()) {
                IFeatureFactory current = persistenceApiIterator.next();
                features.put(current.identify(), current);
            }
        }
        return (T) features.get(feature);
    }
}
