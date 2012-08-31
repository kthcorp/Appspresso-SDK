package com.appspresso.waikiki.deviceinteraction;

import android.app.Activity;
import android.content.Context;
import android.os.PowerManager;
import android.view.WindowManager;

public class Lighter {
    public static final String TAG = Lighter.class.getSimpleName();
    // public static final float BRIGHTNESS_MIN_VALUE = 0.01f;

    private static Activity activity;
    private static PowerManager powerManager;
    private static PowerManager.WakeLock wakeLock;

    public static void init(Activity _activity) {
        activity = _activity;
        powerManager = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
    }

    public static boolean lightOn() {
        if (null == wakeLock) {
            wakeLock =
                    powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK,
                            Lighter.class.getSimpleName());
            wakeLock.acquire();
        }

        WindowManager.LayoutParams param = activity.getWindow().getAttributes();
        param.screenBrightness = WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON;
        activity.getWindow().setAttributes(param);

        return true;
    }

    public static boolean lightOff() {
        if (null != wakeLock && wakeLock.isHeld()) {
            wakeLock.release();
            wakeLock = null;
        }
        return true;
    }
}
