package com.manu.domoback.teleinfo.signalmodifiers;

import java.util.ArrayList;
import java.util.List;

public class Gain {

    private final double g;//Gain

    public Gain(final double g) {
        this.g = g;
    }

    public List<Integer> processSignal(final List<Integer> input) {

        final List<Integer> output = new ArrayList<>(input.size());

        for (final Integer currentInput : input) {
            final double currentOutput = g*currentInput;
            output.add((int) currentOutput);
        }

        return output;

    }

}
