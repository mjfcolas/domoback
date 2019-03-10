package com.manu.domoback.common;

public class UnsureBoolean {

    private boolean state = false;
    private boolean isSure = false;

    public UnsureBoolean(boolean state) {
        this.state = state;
        this.isSure = true;
    }

    public UnsureBoolean(boolean state, boolean isSure) {
        this.state = state;
        this.isSure = isSure;
    }

    public boolean state() {
        return state;
    }

    public boolean isSure() {
        return isSure;
    }
}
