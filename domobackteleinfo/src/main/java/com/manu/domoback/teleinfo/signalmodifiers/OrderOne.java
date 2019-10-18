package com.manu.domoback.teleinfo.signalmodifiers;

import java.util.ArrayList;
import java.util.List;

public class OrderOne {

    private final double g;//Gain
    private final double wc;//Pulsation virtuelle de coupure
    //private final double b;// Coef devant la somme des sorties

    public OrderOne(final double g, final int fc, final int fe) {
        this.g = g;
        // Conversion entre le domaine temporel et le domaine numérique. wc => pulsation de coupure dans le domaine numérique
        this.wc = Math.PI * fc / fe;
    }

    public List<Integer> processSignal(final List<Integer> input, boolean passLow) {

        final List<Integer> output = new ArrayList<>(input.size());

        final double[] bufferInput = { 0 };
        final double[] bufferOutput = { 0 };

        for (final Integer current : input) {
            final double currentInput = current;

            //double currentOutput = this.g * currentInput + this.b * this.fe / this.fc * bufferOutput[0];
            //final double currentOutput = this.a * bufferOutput[0] + this.b * (currentInput + bufferInput[0]);
            final double currentOutput;

            if (passLow) {
                currentOutput = ((g * wc) / (1 + wc)) * (currentInput + bufferInput[0]) + ((1 - wc) / (1 + wc)) * bufferOutput[0];
            } else {
                currentOutput = (g / (1 + wc)) * (currentInput - bufferInput[0]) + ((1 - wc) / (1 + wc)) * bufferOutput[0];
            }

            bufferInput[0] = currentInput;
            bufferOutput[0] = currentOutput;

            output.add((int) currentOutput);
        }

        return output;

    }

}
