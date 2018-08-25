package com.manu.domoback.common;

import junit.framework.TestCase;
import org.junit.Test;

public class BundlesTest extends TestCase {

    @Test
    public void testProp() {
        assertFalse(Bundles.prop() == null);
    }

    @Test
    public void testMessages() {
        assertFalse(Bundles.messages() == null);
    }

}
