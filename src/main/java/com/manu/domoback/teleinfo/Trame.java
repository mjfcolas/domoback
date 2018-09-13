package com.manu.domoback.teleinfo;

import com.manu.domoback.exceptions.TrameEndException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Trame {

    private static final boolean doubleStopBit = true;
    private static final boolean[] LF = AsciiBitUtils.getBitsWithControlsForChar(ControlChars.LF, doubleStopBit);
    private static final int CHAR_SIZE = doubleStopBit ? 11 : 10; //Taille d'un caractère avec les bits de controle

    private final List<Boolean> signal;
    protected int size;
    private List<Character> caracteres;
    private boolean inError = false;
    protected Map<String, String> infos = new HashMap<>();
    private final Map<String, String> formatedInfos = new HashMap<>();

    Trame(final List<Boolean> signal) {
        this.signal = signal;
        this.size = signal.size();
    }

    void computeChars() {

        final List<Character> result = new ArrayList<>();

        int i = 0;
        boolean treatList = true;
        while (treatList) {
            final List<Boolean> currentChar = this.signal.subList(i, i + CHAR_SIZE);
            if (this.isCharError(currentChar)) {
                result.add((char) 0);
            } else {
                result.add(AsciiBitUtils.getCharFromBits(currentChar.subList(1, 8)));
            }
            i += CHAR_SIZE;
            if (result.get(result.size() - 1) == ControlChars.CR) {
                try {
                    i = this.getNextLfIndex(i);
                } catch (final TrameEndException e) {
                    treatList = false;
                }
            }
            if (i + CHAR_SIZE >= this.size) {
                treatList = false;
            }
        }
        this.caracteres = result;
        this.checkTrame();
    }

    private int getNextLfIndex(final int startIndex) throws TrameEndException {
        for (int j = 0; j < CHAR_SIZE; j++) {
            if (startIndex + j >= this.size) {
                throw new TrameEndException(); //Fin du signal
            } else if (this.signal.get(startIndex + j) != LF[j]) {//Un caractère est mauvais ou fin de la liste
                return this.getNextLfIndex(startIndex + 1);
            }
        }

        return startIndex;
    }

    public String getTrameTxt() {
        final StringBuilder output = new StringBuilder();
        if (this.inError) {
            output.append("ERROR  :");
        } else {
            output.append("SUCCESS:");
        }
        for (final Character character : this.caracteres) {
            if (character > 0x1F) {
                output.append(character);
            }
        }
        return output.toString();
    }

    private void addInfo(final String key, final String value) {
        if (!this.infos.containsKey(key)) {
            this.infos.put(key, value);
        }
    }

    public void parseInfos() {
        StringBuilder currentKey = new StringBuilder();
        StringBuilder currentValue = new StringBuilder();
        boolean keyMode = false;
        boolean valueMode = false;
        for (final Character character : this.caracteres) {

            if (keyMode && character != ControlChars.SP) {//Caractere de la clé
                currentKey.append(character);
            } else if (valueMode && character != ControlChars.SP) {//Charactère de la valeur
                currentValue.append(character);
            } else if (character == ControlChars.LF) {//Début de la clé
                currentKey = new StringBuilder();
                currentValue = new StringBuilder();
                keyMode = true;
                valueMode = false;
            } else if (character == ControlChars.SP && keyMode) {//Début de la valeur
                keyMode = false;
                valueMode = true;
            } else if (character == ControlChars.SP && valueMode) {//Début du groupe de controle
                valueMode = false;
            } else if (character == ControlChars.CR) {//Fin du groupe d'information
                keyMode = false;
                valueMode = false;
                this.addInfo(currentKey.toString(), currentValue.toString());
            } else if (character == ControlChars.EOT) {//EOT : la trame a été interrompue
                break;
            }

        }
    }

    public void formatInfos() {
        for (final Map.Entry<String, String> entry : this.infos.entrySet()) {
            final String key = entry.getKey();
            final String value = entry.getValue();
            final String formatedValue;

            if ("HCHC".equals(key) || "HCHP".equals(key)) {
                formatedValue = Integer.toString(Integer.parseInt(value));
            } else if ("OPTARIF".equals(key)) {
                formatedValue = this.formatOptarif(value);
            } else if ("HHPHC".equals(key)) {
                formatedValue = this.formatTrancheHoraire(value);
            } else {
                formatedValue = value;
            }
            this.formatedInfos.put(key, formatedValue);
        }
    }

    private String formatTrancheHoraire(final String value) {
        final String formatedValue;
        if ("1".equals(value)) {
            formatedValue = "Heures creuses";
        } else {
            formatedValue = "Heures pleines";
        }
        return formatedValue;
    }

    private String formatOptarif(final String value) {
        String formatedValue = "";
        if ("BASE".equals(value)) {
            formatedValue = "Base";
        } else if ("HC..".equals(value)) {
            formatedValue = "Heures creuses";
        } else if ("HP..".equals(value)) {
            formatedValue = "Heures pleines";
        }
        return formatedValue;
    }

    private boolean isCharError(final List<Boolean> caractere) {
        //Bit de start
        if (caractere.get(0)) {
            this.inError = true;
            return true;
        }
        //bit de stop
        if (!caractere.get(9)) {
            this.inError = true;
            return true;
        }
        //Parité
        boolean isPair = true;
        for (int i = 1; i < 9; i++) {
            if (caractere.get(i)) {
                isPair = !isPair;
            }
        }
        if (!isPair) {
            this.inError = true;
            return true;
        }
        return false;
    }

    private void checkTrame() {
        if (!this.inError) {
            if (this.caracteres.get(0) != ControlChars.STX) {
                this.inError = true;
            }
            if (this.caracteres.get(1) != ControlChars.LF) {
                this.inError = true;
            }
            if (this.caracteres.get(this.caracteres.size() - 1) != ControlChars.CR) {
                this.inError = true;
            }
            final char checksum = this.caracteres.get(this.caracteres.size() - 2);
            if (checksum < 0x20 || checksum > 0x5F) {
                this.inError = true;
            }
        }
    }

    public Map<String, String> getInfos() {
        return this.infos;
    }

    public Map<String, String> getFormatedInfos() {
        return this.formatedInfos;
    }

    public boolean isInError() {
        return this.inError;
    }

}
