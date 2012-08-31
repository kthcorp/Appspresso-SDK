package com.appspresso.waikiki.devicestatus.components;

import com.appspresso.api.AxError;

public interface Component {
    public static final String NAME_DEFAULT = "_default";
    public static final String NAME_ACTIVE = "_active";

    /**
     * @param propertyName 값을 가져오기 위한 property 이름
     * @return 해당 property의 값. 지원하지 않는 property일 경우 null을 반환
     * @throws AxError 지원하는 property이나 정상적으로 값을 가지고 올 수 없을 경우
     */
    public Object getPropertyValue(String propertyName) throws AxError;

    public void watchPropertyChange(String propertyName, long watchId) throws AxError;

    public void clearWatch(long watchId);

    public void clearWatch();
}
