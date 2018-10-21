package com.manu.domoback;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.manu.domoback.cliinterface.WindowCliInterface;
import junit.framework.TestCase;
import org.awaitility.Awaitility;
import org.awaitility.Duration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.concurrent.Callable;

@RunWith(MockitoJUnitRunner.class)
public class AppTest extends TestCase {

    @Test
    public void testApp() {

        final WindowCliInterface userInterface = (WindowCliInterface) App.getCliInterface();
        new Thread(() -> {
            try {
                Awaitility.await().atMost(Duration.ONE_SECOND).until(this.interfaceDisplayed(userInterface));
                userInterface.onInput(null, new KeyStroke(KeyType.Escape), null);
            } catch (Exception e) {
                fail();
            }
        }).start();
        App.main();
    }

    private Callable<Boolean> interfaceDisplayed(final WindowCliInterface userInterface) {
        return new Callable<Boolean>() {
            @Override
            public Boolean call() {
                return userInterface.isDisplayed();
            }
        };
    }
}
