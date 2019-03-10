package com.manu.domoback.common;

public class NumberUtils {

    private NumberUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean tryParseDouble(String value) {
        try {
            Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
