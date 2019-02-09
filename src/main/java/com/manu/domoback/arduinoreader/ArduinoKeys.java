package com.manu.domoback.arduinoreader;

import java.util.Arrays;
import java.util.List;

public enum ArduinoKeys {
    QT,
    N,
    D,
    C;

    private static final List<String> ALL_VALUES = Arrays.asList(QT.name(), N.name(), D.name(), C.name());

    public static List<String> allValues() {
        return ALL_VALUES;
    }
}
