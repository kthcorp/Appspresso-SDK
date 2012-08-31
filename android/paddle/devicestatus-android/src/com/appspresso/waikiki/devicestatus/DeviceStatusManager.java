package com.appspresso.waikiki.devicestatus;

import org.apache.commons.logging.Log;
import com.appspresso.api.AxLog;
import com.appspresso.api.AxRuntimeContext;
import com.appspresso.api.AxError;
import com.appspresso.api.AxPluginContext;
import com.appspresso.api.DefaultAxPlugin;

public class DeviceStatusManager extends DefaultAxPlugin {
    /**
     * Access to all the module. The methods getPropertyValue() and watchPropertyValue() can only be
     * used for deviceinfo aspects; - Battery (not supported) - Device (not supported) - Display
     * (not supported) - MemoryUnit (not supported) - OperatingSystem - WebRuntime (not supported)
     */
    public static final String FEATURE_DEVICE_INFO =
            "http://wacapps.net/api/devicestatus.deviceinfo";

    /**
     * Access to all the module. The methods getPropertyValue() and watchPropertyValue() can only be
     * used for networkinfo aspects; - CellularHardware - CellularNetwork - WiFiHardware -
     * WiFiNetwork
     */
    public static final String FEATURE_NETWORK_INFO =
            "http://wacapps.net/api/devicestatus.networkinfo";

    /**
     * Provides access to all the module features.
     */
    public static final String FEATURE_DEFAULT = "http://wacapps.net/api/devicestatus";

    public static final Log L = AxLog.getLog(DeviceStatusManager.class);
    private DeviceStatusManagerImpl manager;

    @Override
    public void activate(AxRuntimeContext runtimeContext) {
        runtimeContext.requirePlugin("deviceapis");
        initDeviceStatusManager(runtimeContext);
        super.activate(runtimeContext);
    }

    @Override
    public void deactivate(AxRuntimeContext runtimeContext) {
        manager.clearPropertyChange();
        manager = null;
        super.deactivate(runtimeContext);
    }

    private void initDeviceStatusManager(AxRuntimeContext runtimeContext) {
        if (!runtimeContext.isActivatedFeature(FEATURE_DEFAULT)) {
            boolean usableDeviceInfo = runtimeContext.isActivatedFeature(FEATURE_DEVICE_INFO);
            boolean usableNetworkInfo = runtimeContext.isActivatedFeature(FEATURE_NETWORK_INFO);

            if (usableDeviceInfo || usableNetworkInfo) {
                manager =
                        new DeviceStatusManagerImpl(runtimeContext, usableDeviceInfo,
                                usableNetworkInfo);
            }
            else {
                throw new AxError(AxError.SECURITY_ERR, null); // TODO Error
                                                               // code, message
            }
        }
        else {
            manager = new DeviceStatusManagerImpl(runtimeContext, true, true);
        }
    }

    public void getComponents(AxPluginContext context) {
        String aspect = context.getParamAsString(0);
        String[] components = manager.getComponents(aspect);
        context.sendResult(components);
    }

    public void isSupported(AxPluginContext context) {
        String aspect = context.getParamAsString(0);
        String property = null;

        try {
            property = context.getParamAsString(1);
        }
        catch (Exception ignore) {}

        boolean isSupported = manager.isSupported(aspect, property);
        context.sendResult(isSupported);
    }

    public void getPropertyValue(AxPluginContext context) {
        long watchId = -1;
        try {
            watchId = context.getParamAsNumber(1).longValue();
            Object value = manager.getPropertyValue(watchId);
            context.sendResult(value);
            return;
        }
        catch (Exception ignore) {}

        String aspect = context.getNamedParamAsString(0, "aspect");
        String property = context.getNamedParamAsString(0, "property");
        String component = context.getNamedParamAsString(0, "component");

        try {
            Object value = null;
            if (watchId == -1) {
                value = manager.getPropertyValue(aspect, component, property);
            }
            else {
                value = manager.getPropertyValue(aspect, component, property, watchId);
            }

            if (L.isTraceEnabled()) {
                L.info("[" + aspect + " | " + component + " | " + property + "] " + value);
            }

            context.sendResult(value);
        }
        catch (AxError knownError) {
            context.sendError(knownError);
        }
        catch (Exception unknownError) {
            unknownError.printStackTrace();
            context.sendError(AxError.UNKNOWN_ERR, "An unknown error has occured.");
        }
    }

    public void clearPropertyChange(AxPluginContext context) {
        long watchId = context.getParamAsNumber(0).longValue();
        manager.clearPropertyChange(watchId);
        context.sendResult();
    }
}
