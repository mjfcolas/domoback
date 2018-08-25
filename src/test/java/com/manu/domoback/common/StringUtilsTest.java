package com.manu.domoback.common;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class StringUtilsTest extends TestCase {

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public StringUtilsTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(StringUtilsTest.class);
    }

    public void testRepeat() {
        String result = StringUtils.repeat('X', 5);
        assertEquals(result, "XXXXX");
    }

}

