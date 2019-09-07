package com.manu.domoback.test.conf;

import com.manu.domoback.conf.CONFKEYS;
import com.manu.domoback.conf.DomobackConf;
import junit.framework.TestCase;
import org.junit.Test;

public class DomobackConfTest extends TestCase {

    @Test
    public void testTryParseDouble() {
        String test = DomobackConf.get(CONFKEYS.TELEINFO_TRAMETIME);
        assertNotNull(test);
    }
}
