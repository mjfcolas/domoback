package com.manu.domoback.test.features.api.factory;

import com.manu.domoback.features.api.factory.FeatureFactoryLoader;
import junit.framework.TestCase;
import org.junit.Test;

public class FeatureFactoryLoaderTest extends TestCase {
    @Test
    public void testGetFeature() {
        TestCase.assertFalse(FeatureFactoryLoader.getFeatureFactory("MOCK") == null);
    }

}
