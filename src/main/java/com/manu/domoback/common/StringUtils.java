package com.manu.domoback.common;

import java.util.Arrays;

public class StringUtils {

    private StringUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static String repeat(final char c, final int length) {
        final char[] data = new char[length];
        Arrays.fill(data, c);
        return new String(data);
    }
}
