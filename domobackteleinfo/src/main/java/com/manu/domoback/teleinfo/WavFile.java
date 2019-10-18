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
    private static final Logger LOGGER = LoggerFactory.getLogger(com.manu.domoback.teleinfo.WavFile.class.getName());

    private final int sampleSize;
    private final long framesCount;
    private final int sampleRate;
    private final int channelsNum;
    private final byte[] data;      // wav bytes
    private final AudioFormat af;

    public WavFile(final File file) throws UnsupportedAudioFileException, IOException {
        if (!file.exists()) {
            throw new FileNotFoundException(file.getAbsolutePath());
        }

        final AudioInputStream ais;
        ais = AudioSystem.getAudioInputStream(file);

        this.af = ais.getFormat();
        this.framesCount = ais.getFrameLength();
        this.sampleRate = (int) this.af.getSampleRate();
        this.sampleSize = this.af.getSampleSizeInBits() / 8;
        this.channelsNum = this.af.getChannels();

        final long dataLength = this.framesCount * this.af.getSampleSizeInBits() * this.af.getChannels() / 8;

        this.data = new byte[(int) dataLength];
        if (ais.read(this.data) < 0) {
            LOGGER.error("Aucune données audio à lire");
            throw new IOException("Aucune données audio à lire");
        }
    }

    private AudioFormat getAudioFormat() {
        return this.af;
    }

    public int getSampleSize() {
        return this.sampleSize;
    }

    public double getDurationTime() {
        return this.getFramesCount() / this.getAudioFormat().getFrameRate();
    }

    private long getFramesCount() {
        return this.framesCount;
    }

    /**
     * Returns sample (amplitude value). Note that in case of stereo samples
     * go one after another. I.e. 0 - first sample of left channel, 1 - first
     * sample of the right channel, 2 - second sample of the left channel, 3 -
     * second sample of the rigth channel, etc.
     */
    private int getSampleInt(final int sampleNumber) {

        if (sampleNumber < 0 || sampleNumber >= this.data.length / this.sampleSize) {
            throw new IllegalArgumentException(
                    "sample number can't be < 0 or >= data.length/"
                            + this.sampleSize);
        }

        final byte[] sampleBytes = new byte[4]; //4byte = int

        for (int i = 0; i < this.sampleSize; i++) {
            sampleBytes[i] = this.data[sampleNumber * this.sampleSize * this.channelsNum + i];
        }

        return ByteBuffer.wrap(sampleBytes)
                .order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    public List<Integer> getSignal() {

        final long total = this.getFramesCount();
        final List<Integer> result = new ArrayList<>((int) total);

        final int maxPositive = (int) Math.pow(2, (double) this.sampleSize * 8) / 2;

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
        return this.sampleRate;
    }
}