package com.appspresso.waikiki.deviceinteraction;

import java.io.File;
import java.io.IOException;
import org.apache.commons.logging.Log;
import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import com.appspresso.api.AxError;
import com.appspresso.api.AxLog;
import com.appspresso.api.AxRuntimeContext;
import com.appspresso.api.fs.AssetFile;
import com.appspresso.api.fs.AssetFilePeer;
import com.appspresso.api.fs.AxFile;
import com.appspresso.api.fs.DefaultFile;
import com.appspresso.api.fs.FileSystemUtils;
import com.appspresso.waikiki.deviceinteraction.Vibrator.Pattern;

public class DeviceInteraction {
    public static final int NOTIFY_ON = 0;
    public static final int NOTIFY_OFF = 1;
    public static final int VIBRATE_ON = 2;
    public static final int VIBRATE_PATTERN_ON = 3;
    public static final int VIBRATE_PATTERN_ON_REPEAT = 4;
    public static final int VIBRATE_OFF = 5;
    public static final int LIGHT_ON = 6;
    public static final int LIGHT_OFF = 7;

    public final Log L = AxLog.getLog("DeviceInteractionManager");
    private AxRuntimeContext runtimeContext;
    private DeviceInteractionHandler handler;
    private int notifyMode;

    public DeviceInteraction(AxRuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext;
        handler = new DeviceInteractionHandler();
        notifyMode = -1;

        Notifier.init(runtimeContext.getActivity());
        Vibrator.init(runtimeContext.getActivity());
        Lighter.init(runtimeContext.getActivity());
    }

    public boolean startNotify(long duration) {
        // 기존의 것을 중지
        stopNotify();

        notifyMode = Notifier.getMode();
        switch (notifyMode) {
            case Notifier.MODE_NOFITY:
                Message msg = handler.obtainMessage(NOTIFY_ON, (int) duration, 0);
                handler.sendMessage(msg);
                return true;
            case Notifier.MODE_VIBRATE:
                return startVibrate(duration, null);
            case Notifier.MODE_SILENT:
                return lightOn(duration);
            default:
                notifyMode = -1;
                return false;
        }
    }

    public void stopNotify() {
        int mode = notifyMode;
        notifyMode = -1;

        switch (mode) {
            case Notifier.MODE_NOFITY:
                handler.removeMessages(NOTIFY_ON);
                handler.removeMessages(NOTIFY_OFF);
                handler.sendMessageAtFrontOfQueue(handler.obtainMessage(NOTIFY_OFF));
                return;
            case Notifier.MODE_VIBRATE:
                stopVibrate();
                return;
            case Notifier.MODE_SILENT:
                lightOff();
                return;
        }
    }

    public boolean startVibrate(long duration, String pattern) {
        if (pattern == null) {
            handler.sendMessage(handler.obtainMessage(VIBRATE_ON, (int) duration, 0));
        }
        else {
            Pattern vp = new Vibrator.Pattern(pattern);
            // handler.sendMessageAtFrontOfQueue(handler.obtainMessage(VIBRATE_PATTERN_ON,
            // -1, 0, vp.getInitailPattern()));
            //
            // if(duration != -1) {
            // handler.sendMessageDelayed(handler.obtainMessage(VIBRATE_PATTERN_ON,
            // 0, 0, vp.getPattern()), vp.getDelay());
            // }
            //
            // handler.sendMessageDelayed(handler.obtainMessage(VIBRATE_OFF),
            // duration);

            if (duration > 0) {
                handler.sendMessageDelayed(
                        handler.obtainMessage(VIBRATE_PATTERN_ON_REPEAT, 0, 0, vp.getPattern()),
                        vp.getDelay());
                handler.sendMessageDelayed(handler.obtainMessage(VIBRATE_OFF), duration);
            }
            else {
                handler.sendMessageDelayed(
                        handler.obtainMessage(VIBRATE_PATTERN_ON, 0, 0, vp.getPattern()),
                        vp.getDelay());
            }
        }

        return true;
    }

    public void stopVibrate() {
        handler.removeMessages(VIBRATE_ON);
        handler.removeMessages(VIBRATE_PATTERN_ON);
        handler.removeMessages(VIBRATE_OFF);
        handler.sendMessageAtFrontOfQueue(handler.obtainMessage(VIBRATE_OFF));
    }

    public boolean lightOn(long duration) {
        handler.removeMessages(LIGHT_OFF);
        handler.sendMessageAtFrontOfQueue(handler.obtainMessage(LIGHT_ON));
        handler.sendMessageDelayed(handler.obtainMessage(LIGHT_OFF), duration);

        return true;
    }

    public void lightOff() {
        handler.removeMessages(LIGHT_ON);
        handler.removeMessages(LIGHT_OFF);
        handler.sendMessageAtFrontOfQueue(handler.obtainMessage(LIGHT_OFF));
    }

    public void setWallpaper(String virtualPath) throws AxError {
        Bitmap wallpaper = getBitmap(virtualPath);

        try {
            WallpaperManager manager = WallpaperManager.getInstance(runtimeContext.getActivity());
            manager.setBitmap(wallpaper);
        }
        catch (IOException e) {
            throw new AxError(AxError.UNKNOWN_ERR, "An unknown error has occurred.");
        }
    }

    private Bitmap getBitmap(String virtualPath) throws AxError {
        AxFile axFile = runtimeContext.getFileSystemManager().getFile(virtualPath);
        if (axFile == null || !axFile.exists() || axFile.isDirectory()) { throw new AxError(
                AxError.INVALID_VALUES_ERR, "The path is invalid."); }

        if (axFile instanceof DefaultFile) {
            File image = (File) axFile.getPeer();
            return BitmapFactory.decodeFile(image.getAbsolutePath());
        }

        if (!(axFile instanceof AssetFile)) {
            if (L.isErrorEnabled()) {
                L.error("The file type must be DefaultFile or AssetFile.");
            }
            throw new AxError(AxError.UNKNOWN_ERR, "An unknown error has occurred.");
        }

        AssetFilePeer peer = (AssetFilePeer) axFile.getPeer();
        File image = null;
        try {
            image = File.createTempFile("axFile", null);
            FileSystemUtils.copy(peer.createInputStream(), image.getAbsolutePath(), true);
            return BitmapFactory.decodeFile(image.getAbsolutePath());
        }
        catch (IOException e) {
            throw new AxError(AxError.UNKNOWN_ERR, "An unknown error has occurred.");
        }
        finally {
            if (image != null) {
                image.delete();
            }
        }
    }
}
