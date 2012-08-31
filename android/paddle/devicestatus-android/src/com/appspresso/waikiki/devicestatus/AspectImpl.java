package com.appspresso.waikiki.devicestatus;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import com.appspresso.api.AxError;
import com.appspresso.waikiki.devicestatus.components.Component;

public class AspectImpl implements Aspect {
    private Map<String, Component> components;
    private static String defulatComponentName = Component.NAME_DEFAULT;
    private Set<String> properties;

    public AspectImpl() {
        components = new HashMap<String, Component>();
        properties = new HashSet<String>();
    }

    @Override
    public boolean isSupported(String propertyName) {
        if (propertyName == null) { return properties.size() > 0; }

        return properties.contains(propertyName);
    }

    @Override
    public String[] getComponents() {
        return components.keySet().toArray(new String[] {});
    }

    @Override
    public Object getPropertyValue(String componentName, String propertyName) throws AxError {
        Component component = getComponent(componentName);
        Object value = component.getPropertyValue(propertyName);
        if (value == null) { throw new AxError(AxError.NOT_FOUND_ERR, "The property \""
                + propertyName + "\" is unsupported."); }

        return value;
    }

    /**
     * 컴포넌트 이름에 해당하는 컴포넌트를 반환한다. 컴포넌트 이름이 null일 경우 default로 설정된 컴포넌트를 반환한다. 해당되는 컴포넌트가 존재하지 않을 시
     * NOT_FOUND_ERR와 함께 에러를 발생시킨다.
     * 
     * @param componentName 컴포넌트 이름
     * @return 컴포넌트
     * @throws AxError 컴포넌트가 존재하지 않을 시
     */
    @Override
    public Component getComponent(String componentName) throws AxError {
        if (componentName == null) {
            componentName = defulatComponentName;
        }

        Component component = components.get(componentName);
        if (component == null) { throw new AxError(AxError.NOT_FOUND_ERR, "The component name \""
                + componentName + "\" is invalid."); }

        return component;
    }

    /**
     * 지원가능한 컴포넌트 이름과 컴포넌트를 추가한다.
     * 
     * @param componentName 컴포넌트 이름
     * @param component 컴포넌트
     */
    public void addComponent(String componentName, Component component) {
        if (componentName == null) { throw new NullPointerException(
                "The component name must be not null."); }

        if (component == null) { throw new NullPointerException("Component must be not null."); }

        components.put(componentName, component);
    }

    /**
     * 지원가능한 프로퍼티 이름을 추가한다.
     * 
     * @param propertyName 프로퍼티 이름
     */
    public void addProperty(String propertyName) {
        if (propertyName == null) { throw new NullPointerException(
                "The property name must be not null."); }

        properties.add(propertyName);
    }
}
