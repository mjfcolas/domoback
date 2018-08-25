package com.manu.domoback.teleinfo;

public class ControlChars {

    private ControlChars() {
        throw new IllegalStateException("Utility class");
    }

    public static final char STX = (char) 0x2;
    public static final char ETX = (char) 0x3;
    public static final char SP = (char) 0x20;
    public static final char LF = (char) 0x0A;
    public static final char CR = (char) 0x0D;
    public static final char EOT = (char) 0x4;

}
