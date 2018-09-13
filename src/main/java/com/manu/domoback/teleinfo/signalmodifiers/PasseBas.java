package com.manu.domoback.teleinfo.signalmodifiers;

import java.util.ArrayList;
import java.util.List;

public class PasseBas {

    private final double g;//Gain
    private final double fc;//Fréquence de coupure
    private final double wc;//Pulsation virtuelle de coupure
    protected double a;// Coef devant le le buffer des sorties
    private final double b;// Coef devant la somme des sorties

    public PasseBas(final double g, final double fc, final double fe) {
        this.g = g;
        this.fc = fc;
        this.wc = Math.tan(Math.PI * fc / fe);
        this.a = (1 - this.wc) / (1 + this.wc);
        this.b = (g * this.wc) / (1 + (1 / this.wc));
    }

    public List<Integer> processSignal(final List<Integer> input) {

        final List<Integer> output = new ArrayList<>(input.size());

        final double[] bufferInput = { 0 };
        final double[] bufferOutput = { 0 };

        for (final Integer current : input) {
            final double currentInput = current;
            final double currentOutput = this.a * bufferOutput[0] + this.b * (currentInput + bufferInput[0]);
            bufferInput[0] = currentInput;
            bufferOutput[0] = currentOutput;

            output.add((int) currentOutput);
        }

        return output;

    }

}
