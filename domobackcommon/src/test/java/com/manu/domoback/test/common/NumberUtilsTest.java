package com.manu.domoback.test.common;

import com.manu.domoback.common.NumberUtils;
import junit.framework.TestCase;
import org.junit.Test;

public class NumberUtilsTest extends TestCase {

    @Test
    public void testTryParseDouble() {
        assertFalse(NumberUtils.tryParseDouble("TOTO"));
        assertTrue(NumberUtils.tryParseDouble("20.5"));
    }
}
