package com.manu.domoback.teleinfo.signalmodifiers;

import java.util.ArrayList;
import java.util.List;

public class Limiteur {
    protected int limite;

    public Limiteur(int limite) {
        this.limite = limite;
    }

    public List<Integer> processSignal(List<Integer> input) {
        List<Integer> output = new ArrayList<>(input.size());

        for (int i = 0; i < input.size(); i++) {
            int currentOutput = 0;
            if (input.get(i) > limite) {
                currentOutput = limite;
            } else if (input.get(i) < -limite) {
                currentOutput = -limite;
            } else {
                currentOutput = input.get(i);
            }
            output.add(currentOutput);
        }
        return output;
    }
}
