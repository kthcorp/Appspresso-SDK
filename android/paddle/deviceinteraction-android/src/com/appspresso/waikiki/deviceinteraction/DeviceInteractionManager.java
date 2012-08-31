package com.appspresso.waikiki.deviceinteraction;

import org.apache.commons.logging.Log;

import android.app.Activity;

import com.appspresso.api.AxError;
import com.appspresso.api.AxLog;
import com.appspresso.api.AxPluginContext;
import com.appspresso.api.AxRuntimeContext;
import com.appspresso.api.DefaultAxPlugin;
import com.appspresso.api.activity.ActivityAdapter;

public class DeviceInteractionManager extends DefaultAxPlugin {
    public static final String FEATURE_DEFAULT = "http://wacapps.net/api/deviceinteraction";
    public final Log L = AxLog.getLog("DeviceInteractionManager");
    private DeviceInteraction deviceInteraction;
    private DeviceInteractionAdapter deviceInteractionAdapter;

    @Override
    public void activate(AxRuntimeContext runtimeContext) {
        if (!runtimeContext.isActivatedFeature(FEATURE_DEFAULT)) { throw new AxError(
                AxError.SECURITY_ERR, null); }

        super.activate(runtimeContext);

        runtimeContext.requirePlugin("deviceapis");
        deviceInteraction = new DeviceInteraction(runtimeContext);
        deviceInteractionAdapter = new DeviceInteractionAdapter();
        runtimeContext.addActivityListener(deviceInteractionAdapter);
    }

    @Override
    public void deactivate(AxRuntimeContext runtimeContext) {
        super.deactivate(runtimeContext);

        runtimeContext.removeActivityListener(deviceInteractionAdapter);
        deviceInteractionAdapter = null;
        deviceInteraction = null;
    }

    public void startNotify(AxPluginContext context) {
        long duration = context.getParamAsNumber(0).longValue();

        try {
            deviceInteraction.startNotify(duration);
            context.sendResult();
        }
        catch (AxError error) {
            context.sendError(error);
        }
    }

    public void stopNotify(AxPluginContext context) {
        try {
            deviceInteraction.stopNotify();
            context.sendResult();
        }
        catch (AxError error) {
            context.sendError(error);
        }
    }

    public void startVibrate(AxPluginContext context) {
        long duration = 0;
        String pattern = null;

        Object value = context.getParamAsNumber(0);
        if (value != null) duration = ((Number) value).longValue();

        try {
            pattern = context.getParamAsString(1);
        }
        catch (AxError ignore) {}

        try {
            deviceInteraction.startVibrate(duration, pattern);
            context.sendResult();
        }
        catch (AxError error) {
            context.sendError(error);
        }
    }

    public void stopVibrate(AxPluginContext context) {
        try {
            deviceInteraction.stopVibrate();
            context.sendResult();
        }
        catch (AxError error) {
            context.sendError(error);
        }
    }

    public void lightOn(AxPluginContext context) {
        long duration = context.getParamAsNumber(0).longValue();

        try {
            deviceInteraction.lightOn(duration);
            context.sendResult();
        }
        catch (AxError error) {
            context.sendError(error);
        }
    }

    public void lightOff(AxPluginContext context) {
        try {
            deviceInteraction.lightOff();
            context.sendResult();
        }
        catch (AxError error) {
            context.sendError(error);
        }
    }

    public void setWallpaper(AxPluginContext context) {
        String virtualFilePath = context.getParamAsString(0);

        try {
            deviceInteraction.setWallpaper(virtualFilePath);
            context.sendResult();
        }
        catch (AxError error) {
            context.sendError(error);
        }
    }

    public class DeviceInteractionAdapter extends ActivityAdapter {

        @Override
        public void onActivityStop(Activity activity) {
            deviceInteraction.stopNotify();
            deviceInteraction.stopVibrate();
            deviceInteraction.lightOff();
            super.onActivityStop(activity);
        }

    }
}
