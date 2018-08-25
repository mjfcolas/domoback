package com.manu.domoback.teleinfo;

import com.manu.domoback.exceptions.TrameEndException;

import java.util.ArrayList;
import java.util.List;

public class TraducteurSignal {

    protected static boolean doubleStopBit = true;

    protected static final boolean[] STX = AsciiBitUtils.getBitsWithControlsForChar(ControlChars.STX, doubleStopBit);
    protected static final boolean[] ETX = AsciiBitUtils.getBitsWithControlsForChar(ControlChars.ETX, doubleStopBit);
    protected static final int CHAR_SIZE = doubleStopBit ? 11 : 10; //Taille d'un caractère avec les bits de controle

    protected int size;
    protected List<Boolean> signal;
    protected List<int[]> debutsFins = new ArrayList<>();
    protected List<Trame> trameListe = new ArrayList<>();

    public TraducteurSignal(List<Boolean> signal) {
        this.signal = signal;
        this.size = signal.size();
    }

    private boolean getStartTrame(int startIndex) throws TrameEndException {
        boolean correct = true;
        for (int j = 0; j < CHAR_SIZE; j++) {
            if (startIndex + j >= size) {
                throw new TrameEndException(); //Fin du signal
            } else if (signal.get(startIndex + j) != STX[j]) {//Un caractère est mauvais
                correct = false;
                break;
            }
        }
        //Tous les caractères sont bons
        return correct;
    }

    private boolean getEndTrame(int startIndex) throws TrameEndException {
        boolean correct = true;
        for (int j = 0; j < CHAR_SIZE; j++) {
            if (startIndex + j >= size) {
                throw new TrameEndException(); //Fin du signal
            } else if (signal.get(startIndex + j) != ETX[j]) {//Un caractère est mauvais ou fin de la liste
                correct = false;
                break;
            }
        }
        //Un etx en fin de trame est suivi de plusieurs 1. On en vérifie 5 pour confirmer
        if (correct) {
            for (int j = CHAR_SIZE; j < 15; j++) {
                if (startIndex + j >= size) {
                    throw new TrameEndException(); //Fin du signal
                }
                if (!signal.get(startIndex + j)) {
                    correct = false;
                    break;
                }
            }
        }

        return correct;
    }

    private void fillTrames() {
        for (int[] debutFin : debutsFins) {
            List<Boolean> currentTrame;
            currentTrame = signal.subList(debutFin[0], debutFin[1] + 1);
            Trame trame = new Trame(currentTrame);
            trame.computeChars();
            this.trameListe.add(trame);
        }
    }

    private boolean findStx(int startIndex, int[] stxEtxCurrent) throws TrameEndException {
        boolean correct = getStartTrame(startIndex);
        if (correct) {
            //Tous les caractères sont bons
            stxEtxCurrent[0] = startIndex;
            return true;
        } else {
            return false;
        }
    }

    private boolean findEtx(int startIndex, int[] stxEtxCurrent) throws TrameEndException {
        boolean correct = getEndTrame(startIndex);
        if (correct) {
            //Tous les caractères sont bons
            stxEtxCurrent[1] = startIndex + CHAR_SIZE - 1;
            return true;
        } else {
            return false;
        }
    }

    public void determinerTrames() {
        int[] stxEtxCurrent = new int[2];

        boolean foundStx = false;
        boolean foundEtx = false;

        int i = 0;
        while (i < size) {

            boolean foundEtxOrStx = false;

            try {
                if (!foundStx) {//Recherche début de trame
                    foundStx = findStx(i, stxEtxCurrent);
                    foundEtxOrStx = foundStx;
                } else {//Recherche fin de trame
                    foundEtx = findEtx(i, stxEtxCurrent);
                    foundEtxOrStx = foundEtx;
                }
            } catch (TrameEndException e) {//Fin de la trame, on arrète le traitement
                break;
            }

            if (foundEtxOrStx) {//On a trouvé un stx ou un etx, on avance de 10 bits, taille d'un caractère
                i += CHAR_SIZE;
            } else {//On a rien trouvé, on avance de 1 bit
                i++;
            }
            if (foundEtx) {//foundStx est forcément true si foundEtx est true
                foundEtx = false;
                foundStx = false;
                int[] toAdd = new int[2];
                toAdd[0] = stxEtxCurrent[0];
                toAdd[1] = stxEtxCurrent[1];
                this.debutsFins.add(toAdd);
            }

        }

        fillTrames();

    }

    public List<Trame> getTrameListe() {
        return trameListe;
    }
}
