package com.manu.domoback.teleinfo.signalmodifiers;

import java.util.ArrayList;
import java.util.List;


public class Carre {

    private int triggerVersHaut;
    private int triggerVersBas;
    private boolean inverseBits;

    private int pointsDansPeriode;
    private int pointsPourChanger;

    private List<Integer> inputSignal;
    private List<Boolean> outputRawSignal;
    private List<Boolean> outputTreatedSignal;


    public Carre(CarreParam params, List<Integer> inputSignal) {
        this.triggerVersHaut = params.triggerVersHaut;
        this.triggerVersBas = params.triggerVersBas;
        this.inverseBits = params.inverseBits;

        this.pointsDansPeriode = (params.sampleRate / params.frequence);
        this.pointsPourChanger = (int) (params.triggerTemps * (double) params.sampleRate / (double) params.frequence);

        this.inputSignal = inputSignal;
    }

    public List<Boolean> processSignal() {
        List<Boolean> output = new ArrayList<>(this.inputSignal.size());

        boolean currentOutput = false;
        boolean oldOutput = false;
        for (int i = 0; i < this.inputSignal.size(); i++) {
            int currentIndex = i;
            int oldIndex = i > 1 ? i - 1 : 0;

            if (this.inputSignal.get(currentIndex) > triggerVersHaut
                    && this.inputSignal.get(oldIndex) < this.inputSignal.get(currentIndex)
                    && !oldOutput) {
                currentOutput = true;
                oldOutput = true;
            } else if (this.inputSignal.get(currentIndex) < triggerVersBas
                    && this.inputSignal.get(oldIndex) > this.inputSignal.get(currentIndex)
                    && oldOutput) {
                currentOutput = false;
                oldOutput = false;
            } else {
                currentOutput = oldOutput;
            }
            output.add(currentOutput);
        }
        this.outputRawSignal = output;
        return output;
    }

    public List<Boolean> lisserSignal() {
        List<Boolean> output = new ArrayList<>(this.outputRawSignal.size());

        for (int i = 0; i < this.outputRawSignal.size(); i++) {
            boolean oldValue = this.outputRawSignal.get(i > 1 ? i - 1 : 0);

            if (oldValue != this.outputRawSignal.get(i)) {

                //Si on détecte un changement, on regarde les points suivants pour voir s'ils ont tous la même valeur
                boolean critereAccepte = acceptCritereLissage(i);
                //Si le critere n'a pas été accepté, on égalise tous les points au premier point qui a changé
                if (!critereAccepte) {
                    lisserPartSignal(i);
                }

            }

            output.add(this.outputRawSignal.get(i));

        }
        this.outputTreatedSignal = output;
        return output;
    }

    /**
     * Vérifie si le critère de changement d'état est accepté pour un point donné en regardant les points suivants
     *
     * @param startIndex index de départ
     * @return true si le critère est accepté
     */
    private boolean acceptCritereLissage(int startIndex) {
        boolean critereAccepte = true;
        for (int j = startIndex; j < startIndex + pointsPourChanger; j++) {
            if (j < this.outputRawSignal.size() && this.outputRawSignal.get(startIndex) != this.outputRawSignal.get(j)) {
                critereAccepte = false;
            }
        }
        return critereAccepte;
    }

    /**
     * Egalise les points qui suivent le point passé en paramètre, sur une longueur égale au critère de changement
     * au même état que le premier quand le critère de changement d'état n'est pas accepté
     *
     * @param startIndex
     */
    private void lisserPartSignal(int startIndex) {
        for (int j = startIndex; j < startIndex + pointsPourChanger; j++) {
            if (j < this.outputRawSignal.size()) {
                this.outputRawSignal.set(j, this.outputRawSignal.get(startIndex));
            }
        }
    }

    public List<Boolean> getBitsFromSignal() {
        List<Boolean> output = new ArrayList<>(this.outputTreatedSignal.size());

        int localSum = 0;
        for (int i = 1; i < this.outputTreatedSignal.size(); i++) {
            if (this.outputTreatedSignal.get(i - 1) != this.outputTreatedSignal.get(i)) {
                double periodNumberDouble = (double) localSum / (double) this.pointsDansPeriode;
                long periodNumberLong = Math.round(periodNumberDouble);
                //Remplissage de la liste de bits à partir de la liste de points traités
                fillBitlist(periodNumberLong, output);
                localSum = 0;
            } else {
                localSum += this.outputTreatedSignal.get(i) ? 1 : -1;
            }
        }

        return output;

    }

    private void fillBitlist(long periodNumber, List<Boolean> output) {
        if (periodNumber > 0) {
            for (int j = 0; j < periodNumber; j++) {
                output.add(!this.inverseBits);
            }
        } else if (periodNumber < 0) {
            for (int j = 0; j > periodNumber; j--) {
                output.add(this.inverseBits);
            }
        }
    }
}
