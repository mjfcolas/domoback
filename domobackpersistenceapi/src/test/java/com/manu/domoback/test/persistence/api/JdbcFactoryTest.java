package com.manu.domoback.test.persistence.api;

import com.manu.domoback.persistence.api.factory.JdbcFactory;
import junit.framework.TestCase;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcFactoryTest extends TestCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcFactoryTest.class.getName());

    @Test
    public void testGetJdbc() {
        LOGGER.info(JdbcFactory.getInstance().getClass().getName());
        TestCase.assertFalse(JdbcFactory.getInstance() == null);
    }

}
