package com.appspresso.waikiki.deviceinteraction;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class DeviceInteractionHandler extends Handler {
    public DeviceInteractionHandler() {
        super(Looper.getMainLooper());
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case DeviceInteraction.NOTIFY_ON:
                Notifier.startNotify(msg.arg1);
                break;
            case DeviceInteraction.NOTIFY_OFF:
                Notifier.stopNotify();
                break;
            case DeviceInteraction.VIBRATE_ON:
                Vibrator.startVibrate(msg.arg1);
                break;
            case DeviceInteraction.VIBRATE_PATTERN_ON:
                Vibrator.startVibrate((long[]) msg.obj);
                break;
            case DeviceInteraction.VIBRATE_PATTERN_ON_REPEAT:
                Vibrator.startVibrateRepeat((long[]) msg.obj);
                break;
            case DeviceInteraction.VIBRATE_OFF:
                Vibrator.stopVibrate();
                break;
            case DeviceInteraction.LIGHT_ON:
                Lighter.lightOn();
                break;
            case DeviceInteraction.LIGHT_OFF:
                Lighter.lightOff();
                break;
        }
    }
}
