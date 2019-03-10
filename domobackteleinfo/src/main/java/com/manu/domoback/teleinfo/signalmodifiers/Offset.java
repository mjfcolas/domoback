package com.manu.domoback.teleinfo.signalmodifiers;

import java.util.ArrayList;
import java.util.List;

public class Offset {

    protected int offsetValue;

    public Offset(int offset) {
        this.offsetValue = offset;
    }

    public List<Integer> processSignal(List<Integer> input) {
        List<Integer> output = new ArrayList<>(input.size());

        for (int i = 0; i < input.size(); i++) {
            int currentOutput = 0;
            currentOutput = input.get(i) + this.offsetValue;
            output.add(currentOutput);
        }
        return output;
    }
}
