package com.manu.domoback.test.database;

import com.manu.domoback.database.factory.JdbcFactory;
import junit.framework.TestCase;
import org.junit.Test;

public class JdbcFactoryTest extends TestCase {
    @Test
    public void testGetJdbc() {
        assertFalse(JdbcFactory.getInstance() == null);
    }

}
