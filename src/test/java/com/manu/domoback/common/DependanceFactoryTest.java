package com.manu.domoback.common;

import junit.framework.TestCase;
import org.junit.Test;

public class DependanceFactoryTest extends TestCase {
    @Test
    public void testGetJdbc() {
        assertFalse(DependanceFactory.getJdbc() == null);
    }

}
