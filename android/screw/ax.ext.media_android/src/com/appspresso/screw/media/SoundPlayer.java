package com.appspresso.screw.media;

import android.media.AudioManager;
import android.media.SoundPool;

public class SoundPlayer {
    public static final int MAX_LOAD_TIME = 3000;
    public static final int RELOAD_TIME = 200;

    private static SoundPlayer INSTANCE;
    protected SoundPool soundPool;

    public static SoundPlayer getInstance() {
        if (INSTANCE == null) {
            synchronized (SoundPlayer.class) {
                INSTANCE = createSoundPlayer();
            }
        }

        return INSTANCE;
    }

    private static SoundPlayer createSoundPlayer() {
        return new SoundPlayer();
    }

    private SoundPlayer() {
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
    }

    public synchronized int play(String source, float leftVolume, float rightVolume, int priority,
            int loop, float rate) {
        int soundId = soundPool.load(source, 1);

        int streamId = 0;
        int loadTime = 0;

        while (true) {
            streamId = soundPool.play(soundId, leftVolume, rightVolume, priority, loop, rate);

            if (streamId != 0 || loadTime > MAX_LOAD_TIME) {
                break;
            }
            else {
                try {
                    loadTime += RELOAD_TIME;
                    Thread.sleep(RELOAD_TIME);
                }
                catch (InterruptedException e) {
                    break;
                }
            }
        }

        return streamId;
    }

    public void stop(int streamId) {
        soundPool.stop(streamId);
    }
}
