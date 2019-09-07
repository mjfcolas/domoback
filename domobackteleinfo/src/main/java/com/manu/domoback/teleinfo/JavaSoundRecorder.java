package com.manu.domoback.teleinfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 * A sample program is to demonstrate how to record sound in Java
 * author: www.codejava.net
 */
public class JavaSoundRecorder {

    private static final Logger LOGGER = LoggerFactory.getLogger(JavaSoundRecorder.class.getName());

    // record duration, in milliseconds
    private long recordTime;

    //File
    private File wavFile;
    private float sampleRate;
    private int sampleSizeInBits;
    private int channels;

    // format of audio file
    AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;

    // the line from which audio data is captured
    TargetDataLine line;

    public JavaSoundRecorder(String filePath, float sampleRate, int sampleSizeInBits, int channels, long recordTime) {
        this.wavFile = new File(filePath);
        this.sampleRate = sampleRate;
        this.sampleSizeInBits = sampleSizeInBits;
        this.channels = channels;
        this.recordTime = recordTime;
    }

    /**
     * Defines an audio format
     */
    AudioFormat getAudioFormat() {
        return new AudioFormat(this.sampleRate, this.sampleSizeInBits,
                this.channels, true, true);
    }

    /**
     * Captures the sound and record into a WAV file
     */
    void start() {
        try {
            AudioFormat format = getAudioFormat();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

            // checks if system supports the data line
            if (!AudioSystem.isLineSupported(info)) {
                LOGGER.error("Line not supported");
                System.exit(0);
            }
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();   // start capturing

            AudioInputStream ais = new AudioInputStream(line);

            // start recording
            AudioSystem.write(ais, fileType, wavFile);

        } catch (LineUnavailableException | IOException ex) {
            LOGGER.error("An error occured", ex);
        }
    }

    /**
     * Closes the target data line to finish capturing and recording
     */
    void finish() {
        line.stop();
        line.close();
    }

    /**
     * Entry to run the program
     *
     * @throws InterruptedException
     */
    public void record() {
        // start recording

        JavaSoundRecorder self = this;
        //Stopper
        new Thread(() -> {
            try {
                Thread.sleep(self.recordTime);
            } catch (InterruptedException ex) {
                self.finish();
                LOGGER.error("An error occured", ex);
                Thread.currentThread().interrupt();
            }
            self.finish();
        }).start();

        this.start();
    }
}