package com.manu.domoback.teleinfo;

import com.manu.domoback.teleinfo.signalmodifiers.*;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class ProcessSignal {

    private final String filePath;
    private final float sampleRate = 96000;
    private final boolean processRecord;

    public ProcessSignal(final String filePath, final boolean processRecord) {
        this.filePath = filePath;
        this.processRecord = processRecord;
    }

    public List<Trame> getTrames(final long recordTime) throws UnsupportedAudioFileException, IOException {

        //Reglage asus : 100, 0, 1000, true
        //Reglage old eeePC: 20, -17000, 1000, false
        final Reglage reglage = new Reglage(20, -17000, 1000, false);

        if (this.processRecord) {
            final JavaSoundRecorder recorder = new JavaSoundRecorder(this.filePath, this.sampleRate, 16, 1, recordTime);
            recorder.record();
        }

        final File file = new File(this.filePath);
        final WavFile wav = new WavFile(file);

        List<Integer> signal = wav.getSignal();

        final Offset offset = new Offset(reglage.offset);
        signal = offset.processSignal(signal);
        final Limiteur limiteur = new Limiteur(reglage.limite);
        signal = limiteur.processSignal(signal);

        final int fSignal = 1200;
        final int fEchant = wav.getSampleRate();

        final List<Integer> signalFiltre;
        final PasseBas filtre = new PasseBas(reglage.gain, fSignal, fEchant);
        signalFiltre = filtre.processSignal(signal);

        final CarreParam carreParam = new CarreParam();
        carreParam.setInverseBits(reglage.inverse);
        carreParam.setFrequence(fSignal);
        carreParam.setSampleRate(fEchant);
        final Carre carre = new Carre(carreParam, signalFiltre);

        carre.processSignal();
        carre.lisserSignal();
        final List<Boolean> bitList = carre.getBitsFromSignal();

        final TraducteurSignal traducteur = new TraducteurSignal(bitList);
        traducteur.determinerTrames();

        return traducteur.getTrameListe();
    }
}
