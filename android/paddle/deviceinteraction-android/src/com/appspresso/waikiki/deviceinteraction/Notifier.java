package com.appspresso.waikiki.deviceinteraction;

import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;

public class Notifier {
    public static final int MODE_NOFITY = 0;
    public static final int MODE_VIBRATE = 1;
    public static final int MODE_SILENT = 2;

    private static AudioManager audioManager;
    private static ToneGenerator toneGenerator;

    public static void init(Context context) {
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    public static int getMode() {
        switch (audioManager.getRingerMode()) {
            case AudioManager.RINGER_MODE_NORMAL:
                return MODE_NOFITY;
            case AudioManager.RINGER_MODE_VIBRATE:
                return MODE_VIBRATE;
            case AudioManager.RINGER_MODE_SILENT:
                return MODE_SILENT;
        }

        return -1;
    }

    public static void startNotify(int duration) {
        int volume = audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
        toneGenerator =
                new ToneGenerator(AudioManager.STREAM_NOTIFICATION, convertBeepVolumn(volume));
        toneGenerator.startTone(ToneGenerator.TONE_DTMF_5, duration);
    }

    public static void stopNotify() {
        toneGenerator.stopTone();
        toneGenerator.release();
    }

    public static int convertBeepVolumn(int volume) {
        return ToneGenerator.MAX_VOLUME * volume
                / audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION);
    }
}
