package com.manu.domoback.teleinfo;

import com.manu.domoback.exceptions.TrameEndException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Trame {

    protected static boolean doubleStopBit = true;
    protected static final boolean[] LF = AsciiBitUtils.getBitsWithControlsForChar(ControlChars.LF, doubleStopBit);
    protected static final int CHAR_SIZE = doubleStopBit ? 11 : 10; //Taille d'un caractère avec les bits de controle

    protected List<Boolean> signal;
    protected int size;
    protected List<Character> caracteres;
    protected boolean inError = false;
    protected Map<String, String> infos = new HashMap<>();
    protected Map<String, String> formatedInfos = new HashMap<>();

    public Trame(List<Boolean> signal) {
        this.signal = signal;
        this.size = signal.size();
    }

    public void computeChars() {

        List<Character> result = new ArrayList<>();

        int i = 0;
        boolean treatList = true;
        while (treatList) {
            List<Boolean> currentChar = signal.subList(i, i + CHAR_SIZE);
            if (this.isCharError(currentChar)) {
                result.add((char) 0);
            } else {
                result.add(AsciiBitUtils.getCharFromBits(currentChar.subList(1, 8)));
            }
            i += CHAR_SIZE;
            if (result.get(result.size() - 1) == ControlChars.CR) {
                try {
                    i = getNextLfIndex(i);
                } catch (TrameEndException e) {
                    treatList = false;
                }
            }
            if (i + CHAR_SIZE >= size) {
                treatList = false;
            }
        }
        this.caracteres = result;
        checkTrame();
    }

    private int getNextLfIndex(int startIndex) throws TrameEndException {
        for (int j = 0; j < CHAR_SIZE; j++) {
            if (startIndex + j >= size) {
                throw new TrameEndException(); //Fin du signal
            } else if (signal.get(startIndex + j) != LF[j]) {//Un caractère est mauvais ou fin de la liste
                return getNextLfIndex(startIndex + 1);
            }
        }

        return startIndex;
    }

    public String getTrameTxt() {
        StringBuilder output = new StringBuilder();
        if (this.inError) {
            output.append("ERROR  :");
        } else {
            output.append("SUCCESS:");
        }
        for (Character character : caracteres) {
            if (character > 0x1F) {
                output.append(character);
            }
        }
        return output.toString();
    }

    private void addInfo(String key, String value) {
        if (!infos.containsKey(key)) {
            infos.put(key, value);
        }
    }

    public void parseInfos() {
        StringBuilder currentKey = new StringBuilder();
        StringBuilder currentValue = new StringBuilder();
        boolean keyMode = false;
        boolean valueMode = false;
        for (Character character : caracteres) {

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
                addInfo(currentKey.toString(), currentValue.toString());
            } else if (character == ControlChars.EOT) {//EOT : la trame a été interrompue
                break;
            }

        }
    }

    public void formatInfos() {
        for (Map.Entry<String, String> entry : this.infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            String formatedValue = "";

            if ("HCHC".equals(key) || "HCHP".equals(key)) {
                formatedValue = Integer.toString(Integer.parseInt(value));
            } else if ("OPTARIF".equals(key)) {
                formatedValue = formatOptarif(value);
            } else if ("HHPHC".equals(key)) {
                formatedValue = formatTrancheHoraire(value);
            } else {
                formatedValue = value;
            }
            formatedInfos.put(key, formatedValue);
        }
    }

    private String formatTrancheHoraire(String value) {
        String formatedValue = "";
        if ("1".equals(value)) {
            formatedValue = "Heures creuses";
        } else {
            formatedValue = "Heures pleines";
        }
        return formatedValue;
    }

    private String formatOptarif(String value) {
        String formatedValue = "";
        if ("BASE".equals(value)) {
            formatedValue = "Base";
        } else if ("HC..".equals(value)) {
            formatedValue = "Heures creuses";
        } else if (value == "HP..") {
            formatedValue = "Heures pleines";
        }
        return formatedValue;
    }

    private boolean isCharError(List<Boolean> caractere) {
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
            char checksum = this.caracteres.get(this.caracteres.size() - 2);
            if (checksum < 0x20 || checksum > 0x5F) {
                this.inError = true;
            }
        }
    }

    public Map<String, String> getInfos() {
        return infos;
    }

    public Map<String, String> getFormatedInfos() {
        return formatedInfos;
    }

    public boolean isInError() {
        return inError;
    }

}
