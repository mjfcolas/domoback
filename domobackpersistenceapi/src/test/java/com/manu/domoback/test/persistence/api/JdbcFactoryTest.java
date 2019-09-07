package com.manu.domoback.test.persistence.api;

import com.manu.domoback.persistence.api.factory.JdbcFactory;
import junit.framework.TestCase;
import org.junit.Test;

public class JdbcFactoryTest extends TestCase {
    @Test
    public void testGetJdbc() {
        TestCase.assertFalse(JdbcFactory.getInstance() == null);
    }

}
