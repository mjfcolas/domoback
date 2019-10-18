package com.manu.domoback.teleinfo;

import com.manu.domoback.teleinfo.signalmodifiers.*;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class ProcessSignal {

    private final String filePath;
    private static final float SAMPLE_RATE = 96000;
    private final boolean processRecord;
    private final Reglage reglage;
    private final int mixerIndex;

    public ProcessSignal(final String filePath, final boolean processRecord, double gain, int offset, boolean redresser, boolean inverse, int mixer) {
        this.filePath = filePath;
        this.processRecord = processRecord;
        this.reglage =new Reglage(gain, offset, redresser, inverse);
        this.mixerIndex = mixer;
    }

    public List<Trame> getTrames(final long recordTime) throws UnsupportedAudioFileException, IOException {


        if (this.processRecord) {
            final JavaSoundRecorder recorder = new JavaSoundRecorder(this.filePath, SAMPLE_RATE, 16, 1, recordTime, mixerIndex);
            recorder.record();
        }

        final File file = new File(this.filePath);
        final WavFile wav = new WavFile(file);

        List<Integer> signal = wav.getSignal();

        List<Integer> signalRedresse = signal;
        if (reglage.isRedresser()) {
            final Redresseur redresseur = new Redresseur();
            signalRedresse = redresseur.processSignal(signal);
        }

        int fc = 1200;
        int fe = wav.getSampleRate();
        final OrderOne filtrePasseBas = new OrderOne(reglage.getGain(), fc, fe);
        List<Integer> signalLowCuted = filtrePasseBas.processSignal(signalRedresse, true);

        final Offset offset = new Offset(reglage.getOffset());
        List<Integer> offsetedSignal = offset.processSignal(signalLowCuted);

        final CarreParam carreParam = new CarreParam();
        carreParam.setInverseBits(reglage.isInverse());
        carreParam.setFrequence(fc);
        carreParam.setSampleRate(fe);
        final Carre carre = new Carre(carreParam, offsetedSignal);

        carre.processSignal();
        carre.lisserSignal();
        final List<Boolean> bitList = carre.getBitsFromSignal();

        final TraducteurSignal traducteur = new TraducteurSignal(bitList);
        traducteur.determinerTrames();

        return traducteur.getTrameListe();
    }
}
