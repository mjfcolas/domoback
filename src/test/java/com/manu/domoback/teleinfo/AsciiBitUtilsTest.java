package com.manu.domoback.teleinfo;

import junit.framework.TestCase;
import org.junit.Test;

public class AsciiBitUtilsTest extends TestCase {

    @Test
    public void testGetInversedBitsForChar() {

        boolean[] bits = AsciiBitUtils.getInversedBitsForChar('A');
        boolean[] resultA = {true, false, false, false, false, false, true};
        assertEquals(7, bits.length);
        for (int i = 0; i < bits.length; i++) {
            assertEquals(resultA[i], bits[i]);
        }

        bits = AsciiBitUtils.getInversedBitsForChar('C');
        boolean[] resultC = {true, true, false, false, false, false, true};
        assertEquals(7, bits.length);
        for (int i = 0; i < bits.length; i++) {
            assertEquals(resultC[i], bits[i]);
        }
    }
}
