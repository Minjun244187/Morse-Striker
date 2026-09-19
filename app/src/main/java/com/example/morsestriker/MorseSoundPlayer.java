// MorseSoundPlayer.java
package com.example.morsestriker;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import java.util.concurrent.Semaphore;

public class MorseSoundPlayer {
    private static final int SAMPLE_RATE = 44100;
    private static final Semaphore SEMAPHORE = new Semaphore(5); // up to 5 tones

    // User settings
    private static int frequencyHz_tone = 800;
    private static int volume = 100;

    private static int dotSoundDuration = 140;
    private static int dashSoundDuration = 420;
    private static int interSymbolDelay_nosound = 140;
    private static int interLetterDelay_nosound = 450;

    public static void setFrequency(int freqHz) { frequencyHz_tone = freqHz; }
    public static void setVolume(int volPercent) { volume = Math.max(0, Math.min(volPercent, 100)); }
    public static void setDurations(int dotMs, int dashMs, int symbolDelayMs, int letterDelayMs) {
        dotSoundDuration = dotMs;
        dashSoundDuration = dashMs;
        interSymbolDelay_nosound = symbolDelayMs;
        interLetterDelay_nosound = letterDelayMs;
    }

    private static short[] generateToneBuffer(int durationMs) {
        int samples = (int)((durationMs / 1000.0) * SAMPLE_RATE);
        short[] buffer = new short[samples];
        double angleIncrement = 2.0 * Math.PI * frequencyHz_tone / SAMPLE_RATE;
        double amp = volume / 100.0 * Short.MAX_VALUE;

        for (int i = 0; i < samples; i++) {
            buffer[i] = (short)(Math.sin(i * angleIncrement) * amp);
        }
        return buffer;
    }

    public static void playTone(int durationMs) {
        new Thread(() -> {
            try {
                SEMAPHORE.acquire(); // allow up to 5 at a time
                short[] buffer = generateToneBuffer(durationMs);

                int minSize = AudioTrack.getMinBufferSize(
                        SAMPLE_RATE,
                        AudioFormat.CHANNEL_OUT_MONO,
                        AudioFormat.ENCODING_PCM_16BIT);

                AudioTrack track = new AudioTrack(
                        AudioManager.STREAM_MUSIC,
                        SAMPLE_RATE,
                        AudioFormat.CHANNEL_OUT_MONO,
                        AudioFormat.ENCODING_PCM_16BIT,
                        minSize,
                        AudioTrack.MODE_STREAM);

                track.play();
                track.write(buffer, 0, buffer.length);
                track.stop();
                track.release(); // free the AudioTrack

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                SEMAPHORE.release();
            }
        }).start();
    }

    public static void playDotSound() { playTone(dotSoundDuration); }
    public static void playDashSound() { playTone(dashSoundDuration); }

    public static int getDotSoundDuration() { return dotSoundDuration; }
    public static int getDashSoundDuration() { return dashSoundDuration; }
    public static int getInterSymbolNOSoundDelay() { return interSymbolDelay_nosound; }
    public static int getInterLetterNOSoundDelay() { return interLetterDelay_nosound; }

    public static void playMorseString(String morse) {
        new Thread(() -> {
            try {
                for (char c : morse.toCharArray()) {
                    if (c == '.') {
                        playDotSound();
                    } else if (c == '-') {
                        playDashSound();
                    }
                    Thread.sleep(getInterSymbolNOSoundDelay());
                }
                Thread.sleep(getInterLetterNOSoundDelay());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
