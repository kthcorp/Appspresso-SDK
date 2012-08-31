package com.appspresso.waikiki.devicestatus;

import com.appspresso.api.AxError;
import com.appspresso.waikiki.devicestatus.components.Component;

public interface Aspect {
    /**
     * 매개변수로 전달된 이름의 property가 지원되면 true, 지원되지 않으면 false를 반환한다. 만약 property 이름이 null일 경우 이 aspect가
     * 하나 이상의 어떤 property를 지원한다면 true, 그렇지 않으면 false이다.
     * 
     * @param propertyName property 이름
     * @return 해당 property가 지원되거나 혹은 aspect가 하나 이상의 property를 지원할 때 true를 반환
     */
    public boolean isSupported(String propertyName);

    /**
     * component 이름의 배열을 반환한다.
     * 
     * @return component 이름의 배열
     */
    public String[] getComponents();

    /**
     * 어떤 component의 특정 property의 현재 값을 반환한다. component가 null일 경우 기본 component의 값을 가져오며, 이는
     * componentName을 "_default"로 준 것과 같다. componentName이나 propertyName이 유효하지 않을 경우 Error code
     * AxError.NOT_FOUND_ERR를 가진 AxError 예외를 던진다.
     * 
     * @param componentName component 이름
     * @param propertyName property 이름
     * @return 해당 property의 값
     * @throws AxError 해당 component와 property가 지원되지 않을 경우
     */
    public Object getPropertyValue(String componentName, String propertyName) throws AxError;

    public Component getComponent(String componentName) throws AxError;
}
