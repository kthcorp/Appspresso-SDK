package com.appspresso.waikiki.devicestatus.components;

import java.util.Locale;
import com.appspresso.api.AxError;
import com.appspresso.waikiki.devicestatus.vocabulary.DeviceStatusVocabulary;

public class OperatingSystemComponent implements Component {
    @Override
    public Object getPropertyValue(String propertyName) throws AxError {
        if (DeviceStatusVocabulary.PROPERTY_LANGUAGE.equals(propertyName)) {
            return getLanguage();
        }
        else if (DeviceStatusVocabulary.PROPERTY_VERSION.equals(propertyName)) {
            return getVersion();
        }
        else if (DeviceStatusVocabulary.PROPERTY_NAME.equals(propertyName)) {
            return getName();
        }
        else if (DeviceStatusVocabulary.PROPERTY_VENDOR.equals(propertyName)) {
            return getVendor();
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

    /**
     * A language tag according to the rules specified by RFC 4646
     * 
     * @return
     */
    public String getLanguage() {
        return Locale.getDefault().getLanguage();
    }

    /**
     * The operating system version assigned by the vendor
     * 
     * @return
     */
    public String getVersion() {
        return System.getProperty("os.version");
    }

    /**
     * The operating system name assigned by the vendor
     * 
     * @return
     */
    public String getName() {
        return System.getProperty("os.name");
    }

    /**
     * The operating system vendor
     * 
     * @return
     */
    public String getVendor() {
        return System.getProperty("java.vendor");
    }
}
