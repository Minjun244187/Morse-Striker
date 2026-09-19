// AppSoundPlayer.java
package com.example.morsestriker;

import android.content.Context;
import android.media.MediaPlayer;
import java.util.HashMap;
import android.os.Handler;

public class AppSoundPlayer {
    private static final HashMap<Integer, MediaPlayer> players = new HashMap<>();

    public static void playEffect(Context context, int resId) {
        MediaPlayer mp = players.get(resId);
        if (mp == null) {
            mp = MediaPlayer.create(context, resId);
            players.put(resId, mp);
        }
        if (mp.isPlaying()) {
            mp.seekTo(0);
        }
        mp.start();
    }

    //  Play specific section (startMs ~ endMs)
    public static void playSegment(Context context, int resId, int startMs, int endMs) {
        MediaPlayer mp = MediaPlayer.create(context, resId);
        if (mp == null) return;

        mp.setOnSeekCompleteListener(player -> {
            player.start();

            int duration = endMs - startMs;
            if (duration > 0) {
                new Handler().postDelayed(() -> {
                    if (player.isPlaying()) {
                        player.pause();
                        player.release();
                    }
                }, duration);
            } else {
                player.release(); // if the section is invalid, release
            }
        });

        mp.seekTo(startMs);  // move to the start point
    }

    public static void releaseAll() {
        for (MediaPlayer mp : players.values()) {
            if (mp != null) {
                mp.release();
            }
        }
        players.clear();
    }
}
