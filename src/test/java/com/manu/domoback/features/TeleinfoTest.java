package com.manu.domoback.features;

import com.manu.domoback.database.IJdbc;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TeleinfoTest extends TestCase {

    private Teleinfo teleinfo;

    @Mock
    private final IJdbc jdbc = null;

    @Before
    public void before() {
        this.teleinfo = new Teleinfo(this.jdbc);
    }

    @Test
    public void testRun() {
        this.teleinfo.run();
    }

    @Test
    public void testSave() {
        this.teleinfo.run();
        assertTrue(this.teleinfo.save());
    }
}
