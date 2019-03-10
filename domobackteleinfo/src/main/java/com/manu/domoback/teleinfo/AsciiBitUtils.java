package com.manu.domoback.teleinfo;

import java.util.List;

public class AsciiBitUtils {

    private AsciiBitUtils() {
        throw new IllegalStateException("Utility class");
    }

    /*
     * Retourne un char en tableau de boolean inversé (stx = 0000010 -> 0100000)
     */
    public static boolean[] getInversedBitsForChar(char character) {

        boolean[] bits = new boolean[7];
        //Transformation d'un char en tableau de bit inversé (stx = 0000010 -> 0100000)
        for (int i = 6; i >= 0; i--) {
            bits[i] = (character & (1 << i)) != 0;
        }
        return bits;
    }

    /*
     * Retourne un tableau de bits qui doit
     * représenter un charactere donné avec
     * bit de start, bit de fin et parité paire
     */
    public static boolean[] getBitsWithControlsForChar(char character, boolean doubleStopBit) {
        int size = doubleStopBit ? 11 : 10;
        boolean[] bits = new boolean[size];

        boolean pair = true;

        bits[0] = false;//Bit de start
        //Transformation d'un char en tableau de bit inversé (stx = 0000010 -> 0100000)
        for (int i = 6; i >= 0; i--) {
            bits[i + 1] = (character & (1 << i)) != 0;

            if (bits[i + 1]) {//Ajout d'un 1, changement d'état du pair
                pair = !pair;
            }
        }
        bits[8] = !pair;//Bit de parité
        bits[9] = true;//Bit de stop
        if (doubleStopBit) {
            bits[10] = true;
        }

        return bits;

    }

    public static char getCharFromBits(List<Boolean> bitList) {
        char caractere = 0;
        for (int i = 0; i < 7; i++) {
            int value = bitList.get(i) ? 1 : 0;
            caractere += value * Math.pow(2, i);
        }
        return caractere;
    }

}
