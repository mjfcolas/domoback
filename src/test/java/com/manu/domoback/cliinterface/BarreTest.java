package com.manu.domoback.cliinterface;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BarreTest extends TestCase {

    @Test
    public void testBuildBarre() {
        new Barre(3, 8, 9, 20);
    }

}
