package com.manu.domoback.test.teleinfo;

import com.manu.domoback.conf.CONFKEYS;
import com.manu.domoback.conf.DomobackConf;
import com.manu.domoback.teleinfo.JavaSoundRecorder;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class JavaSoundRecorderTest extends TestCase {

    @Test
    public void testRecord() {
        final JavaSoundRecorder recorder = new JavaSoundRecorder("linkyTest.wav", 96000, 16, 1, Integer.parseInt(DomobackConf.get(CONFKEYS.TELEINFO_TRAMETIME)));
        recorder.record();
    }
}
