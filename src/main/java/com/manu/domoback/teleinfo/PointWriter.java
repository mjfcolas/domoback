package com.manu.domoback.teleinfo;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class PointWriter {

    protected int size;
    private final String fileName;
    private final List<List<Integer>> integerSignals;
    private final List<BooleanSignal> booleanSignals;

    private final int startIndex;
    private final int stopIndex;

    public PointWriter(final String fileName, final List<List<Integer>> integerSignals, final List<BooleanSignal> booleanSignals) {
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

    public PointWriter(final int startIndex, final int stopIndex, final String fileName, final List<List<Integer>> integerSignals, final List<BooleanSignal> booleanSignals) {
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
        final PrintWriter fichier = new PrintWriter(new FileWriter(this.fileName), false);
        for (Integer i = this.startIndex; i < this.stopIndex; i++) {

            final List<Integer> currentAmps = new ArrayList<>();

            for (final List<Integer> signal : this.integerSignals) {
                currentAmps.add(signal.get(i));
            }
            for (final BooleanSignal signal : this.booleanSignals) {
                currentAmps.add(signal.getSignal().get(i) ? signal.getHighAmp() : signal.getLowAmp());
            }
            final StringBuilder toPrint = new StringBuilder(i.toString());
            for (final Integer amplitude : currentAmps) {
                toPrint.append(" ").append(amplitude);
            }

            fichier.println(toPrint.toString());
        }
        fichier.close();
    }

}
