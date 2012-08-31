package com.appspresso.waikiki.devicestatus.components;

import android.content.Context;
import android.net.wifi.WifiManager;
import com.appspresso.api.AxError;
import com.appspresso.api.AxRuntimeContext;
import com.appspresso.waikiki.devicestatus.vocabulary.DeviceStatusVocabulary;

public class WiFiHardwareComponent implements Component {
    public static final String ON = "ON";
    public static final String OFF = "OFF";

    private WifiManager wifiManager;

    public WiFiHardwareComponent(AxRuntimeContext runtimeContext) {
        if (runtimeContext == null) { throw new NullPointerException(
                "AxRuntimeContext must not be null."); }

        try {
            wifiManager =
                    (WifiManager) runtimeContext.getActivity().getSystemService(
                            Context.WIFI_SERVICE);
        }
        catch (Exception requirePermission) {
            // required android.permission.INTERNET
            // required android.permission.ACCESS_WIFI_STATE
        }
    }

    @Override
    public Object getPropertyValue(String propertyName) throws AxError {
        if (DeviceStatusVocabulary.PROPERTY_STATUS.equals(propertyName)) {
            return getStatus();
        }
        else {
            return null;
        }
    }

    @Override
    public void watchPropertyChange(String propertyName, long watchId) throws AxError {
        // Nothing to do.
    }

    @Override
    public void clearWatch(long watchId) {
        // Nothing to do.
    }

    @Override
    public void clearWatch() {
        // Nothing to do.
    }

    public String getStatus() {
        try {
            return wifiManager.isWifiEnabled() ? ON : OFF;
        }
        catch (NullPointerException requirePermission) {
            throw new AxError(AxError.NOT_AVAILABLE_ERR, "");
        }
    }
}
