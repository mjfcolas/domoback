package com.manu.domoback.teleinfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class WavFile {
    private static final Logger LOGGER = LoggerFactory.getLogger(WavFile.class.getName());
    private static final int NOT_SPECIFIED = AudioSystem.NOT_SPECIFIED; // -1

    private int sampleSize = NOT_SPECIFIED;
    private long framesCount = NOT_SPECIFIED;
    private int sampleRate = NOT_SPECIFIED;
    private int channelsNum;
    private byte[] data;      // wav bytes
    private AudioFormat af;

    public WavFile(File file) throws UnsupportedAudioFileException, IOException {
        if (!file.exists()) {
            throw new FileNotFoundException(file.getAbsolutePath());
        }

        AudioInputStream ais;
        ais = AudioSystem.getAudioInputStream(file);

        af = ais.getFormat();
        framesCount = ais.getFrameLength();
        sampleRate = (int) af.getSampleRate();
        sampleSize = af.getSampleSizeInBits() / 8;
        channelsNum = af.getChannels();

        long dataLength = framesCount * af.getSampleSizeInBits() * af.getChannels() / 8;

        data = new byte[(int) dataLength];
        if (ais.read(data) < 0) {
            LOGGER.error("Aucune données audio à lire");
            throw new IOException("Aucune données audio à lire");
        }
    }

    public AudioFormat getAudioFormat() {
        return af;
    }

    public int getSampleSize() {
        return sampleSize;
    }

    public double getDurationTime() {
        return getFramesCount() / getAudioFormat().getFrameRate();
    }

    public long getFramesCount() {
        return framesCount;
    }


    /**
     * Returns sample (amplitude value). Note that in case of stereo samples
     * go one after another. I.e. 0 - first sample of left channel, 1 - first
     * sample of the right channel, 2 - second sample of the left channel, 3 -
     * second sample of the rigth channel, etc.
     */
    public int getSampleInt(int sampleNumber) {

        if (sampleNumber < 0 || sampleNumber >= data.length / sampleSize) {
            throw new IllegalArgumentException(
                    "sample number can't be < 0 or >= data.length/"
                            + sampleSize);
        }

        byte[] sampleBytes = new byte[4]; //4byte = int

        for (int i = 0; i < sampleSize; i++) {
            sampleBytes[i] = data[sampleNumber * sampleSize * channelsNum + i];
        }

        return ByteBuffer.wrap(sampleBytes)
                .order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    public List<Integer> getSignal() {

        long total = this.getFramesCount();
        List<Integer> result = new ArrayList<>((int) total);

        int maxPositive = (int) Math.pow(2, (double) this.sampleSize * 8) / 2;

        for (int i = 0; i < total; i++) {
            int amplitude = this.getSampleInt(i);

            if (amplitude > maxPositive) {
                amplitude -= 2 * maxPositive;
            }
            result.add(amplitude);
        }

        return result;
    }

    public int getSampleRate() {
        return sampleRate;
    }
}