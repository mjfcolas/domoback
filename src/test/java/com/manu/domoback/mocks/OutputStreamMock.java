package com.manu.domoback.mocks;

import java.io.IOException;
import java.io.OutputStream;

public class OutputStreamMock extends OutputStream {
    @Override
    public void write(final int b) throws IOException {
        //Do nothing
    }
}
