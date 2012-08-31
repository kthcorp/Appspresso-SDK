package com.appspresso.waikiki.devicestatus;

import java.util.HashMap;
import java.util.Map;
import com.appspresso.api.AxError;
import com.appspresso.api.AxRuntimeContext;
import com.appspresso.waikiki.devicestatus.components.Component;
import com.appspresso.waikiki.devicestatus.vocabulary.DeviceStatusVocabulary;

public class DeviceStatusManagerImpl {
    private AxRuntimeContext runtimeContext;
    private boolean usableDeviceInfo;
    private boolean usableNetworkInfo;
    private Map<String, Aspect> deviceAspects;
    private Map<String, Aspect> networkAspects;
    private Map<Long, WatchingComponent> watchingComponents;

    public DeviceStatusManagerImpl(AxRuntimeContext runtimeContext, boolean usableDeviceInfo,
            boolean usableNetworkInfo) {
        if (runtimeContext == null) { throw new NullPointerException(
                "AxRuntimeContext must not be null."); }

        this.runtimeContext = runtimeContext;
        this.usableDeviceInfo = usableDeviceInfo;
        this.usableNetworkInfo = usableNetworkInfo;

        deviceAspects = new HashMap<String, Aspect>();
        networkAspects = new HashMap<String, Aspect>();
        watchingComponents = new HashMap<Long, WatchingComponent>();
    }

    /**
     * @param aspectName
     * @return
     */
    public String[] getComponents(String aspectName) {
        Aspect aspect = getAspect(aspectName);
        return (aspect == null) ? null : aspect.getComponents();
    }

    /**
     * @param aspectName
     * @param propertyName
     * @return
     */
    public boolean isSupported(String aspectName, String propertyName) {
        Aspect aspect = getAspect(aspectName);
        if (aspect == null) return false;

        return aspect.isSupported(propertyName);
    }

    /**
     * @param aspectName
     * @param componentName
     * @param propertyName
     * @return
     * @throws AxError
     */
    public Object getPropertyValue(String aspectName, String componentName, String propertyName)
            throws AxError {
        Aspect aspect = getAspect(aspectName);
        if (aspect == null) { throw new AxError(AxError.NOT_FOUND_ERR, "The aspect \"" + aspectName
                + "\" is invalid."); // TODO Error message
        }

        return aspect.getPropertyValue(componentName, propertyName);
    }

    /**
     * @param aspectName
     * @param componentName
     * @param propertyName
     * @param watchId
     * @return
     * @throws AxError
     */
    public Object getPropertyValue(String aspectName, String componentName, String propertyName,
            long watchId) throws AxError {
        Aspect aspect = getAspect(aspectName);
        if (aspect == null) { throw new AxError(AxError.NOT_FOUND_ERR, "The aspect \"" + aspectName
                + "\" is invalid."); }

        Component component = aspect.getComponent(componentName);
        component.watchPropertyChange(propertyName, watchId);
        watchingComponents.put(watchId, new WatchingComponent(component, propertyName));

        return aspect.getPropertyValue(componentName, propertyName);
    }

    /**
     * @param watchId
     */
    public void clearPropertyChange(long watchId) {
        if (!watchingComponents.containsKey(watchId)) throw new AxError(0, null);

        WatchingComponent component = watchingComponents.remove(watchId);
        component.getComponent().clearWatch(watchId);
    }

    /**
	 * 
	 */
    public void clearPropertyChange() {
        for (WatchingComponent component : watchingComponents.values()) {
            component.getComponent().clearWatch();
        }
    }

    /**
     * @param watchId
     * @return
     * @throws AxError
     */
    public Object getPropertyValue(long watchId) throws AxError {
        if (!watchingComponents.containsKey(watchId)) throw new AxError(0, null);
        return watchingComponents.get(watchId).getValue();
    }

    /**
     * @param aspectName Aspect 이름
     * @return 해당 이름에 대한 Aspect 객체. 혹은 유효하지 않은 이름일 경우 null
     * @throws AxError 유효한 Aspect이나 이 Aspect를 위한 feature가 지정되지 않았을 경우
     */
    private Aspect getAspect(String aspectName) throws AxError {
        Aspect aspect =
                getAspect(deviceAspects, DeviceStatusVocabulary.DEVICE_INFO_ASPECTS, aspectName);
        if (aspect != null) {
            if (!usableDeviceInfo) { throw new AxError(AxError.SECURITY_ERR, ""); // TODO Error code
            }
            return aspect;
        }

        aspect = getAspect(networkAspects, DeviceStatusVocabulary.NETWORK_INFO_ASPECTS, aspectName);
        if (aspect != null) {
            if (!usableNetworkInfo) { throw new AxError(AxError.SECURITY_ERR, ""); // TODO Error
                                                                                   // code
            }
            return aspect;
        }

        return null;
    }

    private Aspect getAspect(Map<String, Aspect> aspects, String[] aspectNames, String aspectName) {
        if (aspects.containsKey(aspectName)) return aspects.get(aspectName);

        int count = aspectNames.length;
        for (int i = 0; i < count; i++) {
            if (aspectNames[i].equals(aspectName)) {
                Aspect aspect = AspectFactory.createAspect(runtimeContext, aspectName);
                if (aspect != null) {
                    aspects.put(aspectName, aspect);
                    return aspect;
                }
            }
        }

        return null;
    }

    private class WatchingComponent {
        private Component component;
        private String propertyName;

        WatchingComponent(Component component, String propertyName) {
            this.component = component;
            this.propertyName = propertyName;
        }

        public Object getValue() {
            return component.getPropertyValue(propertyName);
        }

        public Component getComponent() {
            return component;
        }
    }
}
