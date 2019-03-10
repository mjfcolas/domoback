package com.manu.domoback.test.mocks;

import java.io.IOException;
import java.io.InputStream;

public class InputStreamMock extends InputStream {

    private final boolean error;

    public InputStreamMock(final boolean error) {
        this.error = error;
    }

    @Override
    public int read() throws IOException {
        if (this.error) {
            throw new IOException();
        }
        return 0;
    }
}
