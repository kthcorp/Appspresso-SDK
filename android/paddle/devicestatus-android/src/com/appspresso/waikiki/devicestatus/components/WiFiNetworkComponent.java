package com.appspresso.waikiki.devicestatus.components;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import com.appspresso.api.AxError;
import com.appspresso.api.AxRuntimeContext;
import com.appspresso.waikiki.devicestatus.vocabulary.DeviceStatusVocabulary;

public class WiFiNetworkComponent implements Component {
    public static final String CONNECTED = "connected";
    public static final String AVAILABLE = "available";
    public static final String FORBIDDEN = "forbidden";

    private ConnectivityManager connectivityManager;
    private WifiManager wifiManager;

    public WiFiNetworkComponent(AxRuntimeContext runtimeContext) {
        if (runtimeContext == null) { throw new NullPointerException(
                "AxRuntimeContext must not be null."); }

        try {
            connectivityManager =
                    (ConnectivityManager) runtimeContext.getActivity().getSystemService(
                            Context.CONNECTIVITY_SERVICE);
        }
        catch (Exception e) {
            // required android.permission.INTERNET
            // required android.permission.ACCESS_NETWORK_STATE
        }

        try {
            wifiManager =
                    (WifiManager) runtimeContext.getActivity().getSystemService(
                            Context.WIFI_SERVICE);
        }
        catch (Exception e) {
            // android.permission.ACCESS_WIFI_STATE
        }
    }

    @Override
    public Object getPropertyValue(String propertyName) throws AxError {
        if (DeviceStatusVocabulary.PROPERTY_SSID.equals(propertyName)) {
            return getSsid();
        }
        else if (DeviceStatusVocabulary.PROPERTY_SIGNAL_STRENGTH.equals(propertyName)) {
            return getSignalStrength();
        }
        else if (DeviceStatusVocabulary.PROPERTY_NETWORK_STATUS.equals(propertyName)) {
            return getNetworkStatus();
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
     * WiFi 네트워크 상태를 표시한다. "connected" - 이미 WiFi에 연결된 상태 "available" - WiFi에 연결되어 있지는 않으나 연결 가능
     * "forbidden" - WiFi에 연결 금지
     * 
     * @return WiFi 네트워크 상태
     */
    public String getNetworkStatus() {
        if (isConnectedOnWiFi()) return CONNECTED;

        try {
            return WifiManager.WIFI_STATE_ENABLED == wifiManager.getWifiState()
                    ? AVAILABLE
                    : FORBIDDEN;
        }
        catch (Exception e) {
            throw new AxError(AxError.NOT_AVAILABLE_ERR, "The current value cannot be retrieved.");
        }
    }

    /**
     * 현재 연결된 WiFi 네트워크의 신호세기를 가져온다.
     * 
     * @return WiFi 네트워크의 신호세기
     */
    public long getSignalStrength() {
        try {
            // XXX calculateSignalLevel의 두번째 매개변수를 11로 넣으니까 결과가 110이 자꾸 나와서 10으로
            // 변경
            // 레퍼런스에 의하면 maxLevel-1까지니까 10까지 나와야할텐데 왜 11이 나오게 되는걸까?
            return (WifiManager.calculateSignalLevel(wifiManager.getConnectionInfo().getRssi(), 10) * 10);
        }
        catch (Exception e) {
            throw new AxError(AxError.NOT_AVAILABLE_ERR, "The current value cannot be retrieved.");
        }
    }

    /**
     * 현재 연결된 WiFi 네트워크의 SSID를 가져온다. 만약 WiFi에 연결되어있지 않으면 빈 문자열을 반환한다.
     * 
     * @return 연결된 WiFi 네트워크의 SSID
     */
    public String getSsid() {
        try {
            return isConnectedOnWiFi() ? wifiManager.getConnectionInfo().getSSID() : "undefined";
        }
        catch (Exception e) {
            throw new AxError(AxError.NOT_AVAILABLE_ERR, "The current value cannot be retrieved.");
        }
    }

    /**
     * 현재 WiFi 네트워크에 연결된 상태인지 반환한다.
     * 
     * @return WiFi 네트워크에 연결 여부. 연결되어있다면 true, 그렇지 않다면 false를 반환.
     */
    private boolean isConnectedOnWiFi() {
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .isConnectedOrConnecting();
    }
}
