package com.manu.domoback.teleinfo.signalmodifiers;

import java.util.ArrayList;
import java.util.List;

public class Redresseur {

    public Redresseur() {

    }

    public List<Integer> processSignal(final List<Integer> input) {

        final List<Integer> output = new ArrayList<>(input.size());

        for (final Integer currentInput : input) {
            final double currentOutput = Math.abs(currentInput);
            output.add((int) currentOutput);
        }

        return output;

    }

}
