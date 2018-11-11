package com.manu.domoback.teleinfo;

import com.manu.domoback.common.Bundles;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class JavaSoundRecorderTest extends TestCase {

    @Test
    public void testRecord() {
        final JavaSoundRecorder recorder = new JavaSoundRecorder("linkyTest.wav", 96000, 16, 1, Integer.parseInt(Bundles.prop().getProperty("teleinfo.trametime")));
        recorder.record();
    }
}
