package com.manu.domoback.mocks;

import java.io.IOException;
import java.io.OutputStream;

public class OutputStreamMock extends OutputStream {

    private final boolean writeError;

    public OutputStreamMock(final boolean writeError) {
        this.writeError = writeError;
    }

    @Override
    public void write(final int b) throws IOException {
        if (this.writeError) {
            throw new IOException();
        }
    }
}
