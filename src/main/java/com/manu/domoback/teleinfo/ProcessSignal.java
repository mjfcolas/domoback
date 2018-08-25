package com.manu.domoback.teleinfo;

import com.manu.domoback.teleinfo.signalmodifiers.*;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class ProcessSignal {

    protected String filePath;
    protected float sampleRate = 96000;
    private boolean processRecord = true;

    public ProcessSignal(String filePath, boolean processRecord) {
        this.filePath = filePath;
        this.processRecord = processRecord;
    }


    public List<Trame> getTrames(long recordTime) throws UnsupportedAudioFileException, IOException {

        //Reglage asus : 100, 0, 1000, true
        //Reglage old eeePC: 20, -17000, 1000, false
        Reglage reglage = new Reglage(20, -17000, 1000, false);

        if (processRecord) {
            JavaSoundRecorder recorder = new JavaSoundRecorder(this.filePath, this.sampleRate, 16, 1, recordTime);
            recorder.record();
        }

        File file = new File(this.filePath);
        WavFile wav = new WavFile(file);

        List<Integer> signal = wav.getSignal();

        Offset offset = new Offset(reglage.offset);
        signal = offset.processSignal(signal);
        Limiteur limiteur = new Limiteur(reglage.limite);
        signal = limiteur.processSignal(signal);

        int fSignal = 1200;
        int fEchant = wav.getSampleRate();

        List<Integer> signalFiltre;
        PasseBas filtre = new PasseBas(reglage.gain, fSignal, fEchant);
        signalFiltre = filtre.processSignal(signal);

        CarreParam carreParam = new CarreParam();
        carreParam.setInverseBits(reglage.inverse);
        carreParam.setFrequence(fSignal);
        carreParam.setSampleRate(fEchant);
        Carre carre = new Carre(carreParam, signalFiltre);

        carre.processSignal();
        carre.lisserSignal();
        List<Boolean> bitList = carre.getBitsFromSignal();

        TraducteurSignal traducteur = new TraducteurSignal(bitList);
        traducteur.determinerTrames();


        return traducteur.getTrameListe();
    }
}
