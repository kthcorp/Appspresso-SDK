package com.appspresso.waikiki.devicestatus.components;

import android.content.ContentResolver;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import com.appspresso.api.AxError;
import com.appspresso.api.AxRuntimeContext;
import com.appspresso.waikiki.devicestatus.vocabulary.DeviceStatusVocabulary;

public class CellularHardwareComponent implements Component {
    public static final String ON = "ON";
    public static final String OFF = "OFF";

    private ContentResolver contentResolver;

    public CellularHardwareComponent(AxRuntimeContext runtimeContext) {
        if (runtimeContext == null) { throw new NullPointerException(
                "AxRuntimeContext must not be null."); }

        this.contentResolver = runtimeContext.getActivity().getContentResolver();
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
        // Nothing to do
    }

    @Override
    public void clearWatch(long watchId) {
        // Nothing to do
    }

    @Override
    public void clearWatch() {
        // Nothing to do.
    }

    public String getStatus() {
        try {
            return (0 == Settings.System.getInt(contentResolver, Settings.System.AIRPLANE_MODE_ON))
                    ? ON
                    : OFF;
        }
        catch (SettingNotFoundException e) {
            throw new AxError(AxError.NOT_AVAILABLE_ERR, "The current value cannot be retrieved.");
        }
    }
}
