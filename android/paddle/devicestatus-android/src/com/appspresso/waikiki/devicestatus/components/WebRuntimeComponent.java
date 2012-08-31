package com.appspresso.waikiki.devicestatus.components;

import com.appspresso.api.AxError;
import com.appspresso.waikiki.devicestatus.vocabulary.DeviceStatusVocabulary;

public class WebRuntimeComponent implements Component {

    @Override
    public Object getPropertyValue(String propertyName) throws AxError {
        if (DeviceStatusVocabulary.PROPERTY_WAC_VERSION.equals(propertyName)) {
            return getWacVersion();
        }
        else if (DeviceStatusVocabulary.PROPERTY_SUPPORTED_IMAGE_FORMATS.equals(propertyName)) {
            return getSupportedImageFormats();
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
     * 런타임이 지원하는 WAC 버전
     * 
     * @return 런타임이 지원하는 WAC 버전
     */
    public String getWacVersion() {
        return "WAC 2.0";
    }

    public String[] getSupportedImageFormats() {
        return new String[] {"gif", "jpeg", "png"};
    }

    /**
     * 벤더가 할당한 웹 런타임 버전
     * 
     * @return 웹 런타임 버전
     */
    public String getVersion() {
        return "1.1.2";
    }

    /**
     * 벤더가 할당한 웹 런타임 이름
     * 
     * @return 웹 런타임 이름
     */
    public String getName() {
        return "Appspresso";
    }

    /**
     * 웹 런타임 벤더
     * 
     * @return 웹 런타임 벤더
     */
    public String getVendor() {
        return "KTH";
    }
}
