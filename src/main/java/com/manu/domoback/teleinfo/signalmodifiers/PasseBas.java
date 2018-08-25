package com.manu.domoback.teleinfo.signalmodifiers;

import java.util.ArrayList;
import java.util.List;

public class PasseBas {

    protected double g;//Gain
    protected double fc;//Fréquence de coupure
    protected double fe; //Fréquence d'échantillonage
    protected double wc;//Pulsation virtuelle de coupure
    protected double a;// Coef devant le le buffer des sorties
    protected double b;// Coef devant la somme des sorties

    public PasseBas(double g, double fc, double fe) {
        this.g = g;
        this.fc = fc;
        this.wc = Math.tan(Math.PI * fc / fe);
        this.a = (1 - wc) / (1 + wc);
        this.b = (g * wc) / (1 + (1 / wc));
    }

    public List<Integer> processSignal(List<Integer> input) {

        List<Integer> output = new ArrayList<>(input.size());

        double[] bufferInput = {0};
        double[] bufferOutput = {0};

        for (int i = 0; i < input.size(); i++) {
            double currentInput = input.get(i);
            double currentOutput = this.a * bufferOutput[0] + this.b * (currentInput + bufferInput[0]);
            bufferInput[0] = currentInput;
            bufferOutput[0] = currentOutput;

            output.add((int) currentOutput);
        }

        return output;

    }

}
