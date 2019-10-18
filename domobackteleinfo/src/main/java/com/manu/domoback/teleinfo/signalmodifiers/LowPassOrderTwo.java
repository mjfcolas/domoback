package com.manu.domoback.teleinfo.signalmodifiers;

import java.util.ArrayList;
import java.util.List;

public class LowPassOrderTwo {

    private final double g; //gain
    private final double q;//Facteur de qualité
    private final double wc;//Pulsation virtuelle de coupure

    private final double a0;
    private final double a1;
    private final double a2;
    private final double a3;

    public LowPassOrderTwo(final double g, final double q, final int fc, final int fe) {
        this.g = g;
        this.q = q;
        // Conversion entre le domaine temporel et le domaine numérique. wc => pulsation de coupure dans le domaine numérique
        this.wc = Math.PI * fc / fe;

        double wcwcinv = 1/(wc*wc);
        double qwcinv = 1/(q*wc);

        this.a0 = 1 + wcwcinv + qwcinv;
        this.a1 = 3 - wcwcinv + qwcinv;
        this.a2 = 3-wcwcinv-qwcinv;
        this.a3 =1+wcwcinv-qwcinv;

    }

    public List<Integer> processSignal(final List<Integer> input) {

        final List<Integer> output = new ArrayList<>(input.size());

        final double[] bufferInput = { 0, 0, 0 };
        final double[] bufferOutput = { 0, 0, 0 };

        for (final Integer current : input) {
            final double currentInput = current;

            final double currentOutput = g / a0 * currentInput - (a1 / a0 * bufferOutput[0] + a2 / a0 * bufferOutput[1] + a3 / a0 * bufferOutput[2]);

            bufferInput[2] = bufferInput[1];
            bufferOutput[2] = bufferOutput[1];
            bufferInput[1] = bufferInput[0];
            bufferOutput[1] = bufferOutput[0];
            bufferInput[0] = currentInput;
            bufferOutput[0] = currentOutput;

            output.add((int) currentOutput);
        }

        return output;

    }

}
