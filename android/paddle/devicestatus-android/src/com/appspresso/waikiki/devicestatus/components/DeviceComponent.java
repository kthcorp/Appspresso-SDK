package com.appspresso.waikiki.devicestatus.components;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import com.appspresso.api.AxError;
import com.appspresso.api.AxRuntimeContext;
import com.appspresso.waikiki.devicestatus.vocabulary.DeviceStatusVocabulary;

public class DeviceComponent implements Component {
    private AxRuntimeContext runtimeContext;

    public DeviceComponent(AxRuntimeContext runtimeContext) {
        if (runtimeContext == null) { throw new NullPointerException(
                "AxRuntimeContext must not be null."); }

        this.runtimeContext = runtimeContext;
    }

    @Override
    public Object getPropertyValue(String propertyName) throws AxError {
        if (DeviceStatusVocabulary.PROPERTY_IMEI.equals(propertyName)) {
            return getIMEI();
        }
        else if (DeviceStatusVocabulary.PROPERTY_MODEL.equals(propertyName)) {
            return getModel();
        }
        else if (DeviceStatusVocabulary.PROPERTY_VERSION.equals(propertyName)) {
            return getVersion();
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

    /**
     * IMEI (단말기에 할당된 식별번호)를 할당한다. android.permission.READ_PHONE_STATE 퍼미션이 필요하다.
     * 
     * @return 단말기 IMEI
     */
    public String getIMEI() {
        return ((TelephonyManager) runtimeContext.getActivity().getSystemService(
                Context.TELEPHONY_SERVICE)).getDeviceId();
    }

    /**
     * 단말기 모델명을 반환한다.
     * 
     * @return 단말기 모델명
     */
    public String getModel() {
        return Build.DEVICE;
    }

    /**
     * 단말기 제조사가 할당한 모델의 버전을 반환한다.
     * 
     * @return 단말기 버전
     */
    public String getVersion() {
        return Build.VERSION.RELEASE; // TODO
    }

    /**
     * 단말기 제조사를 반환한다.
     * 
     * @return 단말기 제조사
     */
    public String getVendor() {
        return Build.BRAND;
    }
}
