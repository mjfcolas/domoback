package com.manu.domoback.teleinfo;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class PointWriter {

    protected int size;
    protected String fileName;
    protected List<List<Integer>> integerSignals;
    protected List<BooleanSignal> booleanSignals;

    protected int startIndex = 0;
    protected int stopIndex = 0;

    public PointWriter(String fileName, List<List<Integer>> integerSignals, List<BooleanSignal> booleanSignals) {
        this.fileName = fileName;
        this.integerSignals = integerSignals;
        if (!integerSignals.isEmpty()) {
            this.size = integerSignals.get(0).size();
        } else {
            this.size = booleanSignals.get(0).getSignal().size();
        }
        this.booleanSignals = booleanSignals;
        this.startIndex = 0;
        this.stopIndex = this.size - 1;
    }

    public PointWriter(int startIndex, int stopIndex, String fileName, List<List<Integer>> integerSignals, List<BooleanSignal> booleanSignals) {
        this.fileName = fileName;
        this.integerSignals = integerSignals;
        if (!integerSignals.isEmpty()) {
            this.size = integerSignals.get(0).size();
        } else {
            this.size = booleanSignals.get(0).getSignal().size();
        }
        this.booleanSignals = booleanSignals;
        this.startIndex = startIndex;
        this.stopIndex = stopIndex;
    }


    public void printFile() throws IOException {
        PrintWriter fichier = new PrintWriter(new FileWriter(this.fileName), false);
        for (Integer i = startIndex; i < stopIndex; i++) {

            List<Integer> currentAmps = new ArrayList<>();

            for (List<Integer> signal : integerSignals) {
                currentAmps.add(signal.get(i));
            }
            for (BooleanSignal signal : booleanSignals) {
                currentAmps.add(signal.getSignal().get(i) ? signal.getHighAmp() : signal.getLowAmp());
            }
            StringBuilder toPrint = new StringBuilder(i.toString());
            for (Integer amplitude : currentAmps) {
                toPrint.append(" ").append(amplitude);
            }

            fichier.println(toPrint.toString());
        }
        fichier.close();
    }

}
