package com.appspresso.waikiki.devicestatus;

import com.appspresso.api.AxRuntimeContext;
import com.appspresso.waikiki.devicestatus.components.BatteryComponent;
import com.appspresso.waikiki.devicestatus.components.BuiltInMemoryUnitComponent;
import com.appspresso.waikiki.devicestatus.components.CellularHardwareComponent;
import com.appspresso.waikiki.devicestatus.components.CellularNetworkComponent;
import com.appspresso.waikiki.devicestatus.components.Component;
import com.appspresso.waikiki.devicestatus.components.DeviceComponent;
import com.appspresso.waikiki.devicestatus.components.DisplayComponent;
import com.appspresso.waikiki.devicestatus.components.ExtensionMemoryUnitComponent;
import com.appspresso.waikiki.devicestatus.components.OperatingSystemComponent;
import com.appspresso.waikiki.devicestatus.components.WebRuntimeComponent;
import com.appspresso.waikiki.devicestatus.components.WiFiHardwareComponent;
import com.appspresso.waikiki.devicestatus.components.WiFiNetworkComponent;
import com.appspresso.waikiki.devicestatus.vocabulary.DeviceStatusVocabulary;

public class AspectFactory {
    public static Aspect createAspect(AxRuntimeContext runtimeContext, String aspectName) {
        if (DeviceStatusVocabulary.ASPECT_BATTERY.equals(aspectName)) {
            return createBatteryAspect(runtimeContext);
        }
        else if (DeviceStatusVocabulary.ASPECT_CELLULAR_HARDWARE.equals(aspectName)) {
            return createCellularHardwareAspect(runtimeContext);
        }
        else if (DeviceStatusVocabulary.ASPECT_CELLULAR_NETWORK.equals(aspectName)) {
            return createCellularNetworkAspect(runtimeContext);
        }
        else if (DeviceStatusVocabulary.ASPECT_DEVICE.equals(aspectName)) {
            return createDeviceAspect(runtimeContext);
        }
        else if (DeviceStatusVocabulary.ASPECT_DISPLAY.equals(aspectName)) {
            return createDisplayAspect(runtimeContext);
        }
        else if (DeviceStatusVocabulary.ASPECT_MEMORY_UNIT.equals(aspectName)) {
            return createMemoryUnitAspect(runtimeContext);
        }
        else if (DeviceStatusVocabulary.ASPECT_OPERATING_SYSTEM.equals(aspectName)) {
            return createOperatingSystemAspect(runtimeContext);
        }
        else if (DeviceStatusVocabulary.ASPECT_WEB_RUNTIME.equals(aspectName)) {
            return createWebRuntimeAspect(runtimeContext);
        }
        else if (DeviceStatusVocabulary.ASPECT_WIFI_HARDWARE.equals(aspectName)) {
            return createWiFiHardwareAspect(runtimeContext);
        }
        else if (DeviceStatusVocabulary.ASPECT_WIFI_NETWORK.equals(aspectName)) { return createWiFiNetworkAspect(runtimeContext); }

        return null;
    }

    public static Aspect createBatteryAspect(AxRuntimeContext runtimeContext) {
        AspectImpl batteryAspect = new AspectImpl();

        batteryAspect.addProperty(DeviceStatusVocabulary.PROPERTY_BATTERY_LEVEL);
        batteryAspect.addProperty(DeviceStatusVocabulary.PROPERTY_BATTERY_BEING_CHARGED);
        batteryAspect.addComponent(Component.NAME_DEFAULT, new BatteryComponent(runtimeContext));

        return batteryAspect;
    }

    public static Aspect createCellularHardwareAspect(AxRuntimeContext runtimeContext) {
        AspectImpl cellularHardwareAspect = new AspectImpl();

        cellularHardwareAspect.addProperty(DeviceStatusVocabulary.PROPERTY_STATUS);
        cellularHardwareAspect.addComponent(Component.NAME_DEFAULT, new CellularHardwareComponent(
                runtimeContext));

        return cellularHardwareAspect;
    }

    public static Aspect createCellularNetworkAspect(AxRuntimeContext runtimeContext) {
        AspectImpl cellularHardwareAspect = new AspectImpl();

        cellularHardwareAspect.addProperty(DeviceStatusVocabulary.PROPERTY_IS_IN_ROAMING);
        cellularHardwareAspect.addProperty(DeviceStatusVocabulary.PROPERTY_MCC);
        cellularHardwareAspect.addProperty(DeviceStatusVocabulary.PROPERTY_MNC);
        cellularHardwareAspect.addProperty(DeviceStatusVocabulary.PROPERTY_SIGNAL_STRENGTH);
        cellularHardwareAspect.addProperty(DeviceStatusVocabulary.PROPERTY_OPERATOR_NAME);

        cellularHardwareAspect.addComponent(Component.NAME_DEFAULT, new CellularNetworkComponent(
                runtimeContext));

        return cellularHardwareAspect;
    }

    public static Aspect createDeviceAspect(AxRuntimeContext runtimeContext) {
        AspectImpl deviceAspect = new AspectImpl();

        deviceAspect.addProperty(DeviceStatusVocabulary.PROPERTY_IMEI);
        deviceAspect.addProperty(DeviceStatusVocabulary.PROPERTY_MODEL);
        deviceAspect.addProperty(DeviceStatusVocabulary.PROPERTY_VERSION);
        deviceAspect.addProperty(DeviceStatusVocabulary.PROPERTY_VENDOR);

        deviceAspect.addComponent(Component.NAME_DEFAULT, new DeviceComponent(runtimeContext));

        return deviceAspect;
    }

    public static Aspect createDisplayAspect(AxRuntimeContext runtimeContext) {
        AspectImpl displayAspect = new AspectImpl();

        displayAspect.addProperty(DeviceStatusVocabulary.PROPERTY_RESOLUTION_WIDTH);
        displayAspect.addProperty(DeviceStatusVocabulary.PROPERTY_RESOLUTION_HEIGHT);
        displayAspect.addProperty(DeviceStatusVocabulary.PROPERTY_DPI_X);
        displayAspect.addProperty(DeviceStatusVocabulary.PROPERTY_DPI_Y);
        displayAspect.addProperty(DeviceStatusVocabulary.PROPERTY_PIXEL_ASPECT_RATIO);
        displayAspect.addProperty(DeviceStatusVocabulary.PROPERTY_COLOR_DEPTH);

        Component component = new DisplayComponent(runtimeContext);
        displayAspect.addComponent(Component.NAME_DEFAULT, component);
        displayAspect.addComponent(Component.NAME_ACTIVE, component);

        return displayAspect;
    }

    public static Aspect createMemoryUnitAspect(AxRuntimeContext runtimeContext) {
        AspectImpl memoryUnitAspect = new AspectImpl();

        memoryUnitAspect.addProperty(DeviceStatusVocabulary.PROPERTY_SIZE);
        memoryUnitAspect.addProperty(DeviceStatusVocabulary.PROPERTY_REMOVABLE);
        memoryUnitAspect.addProperty(DeviceStatusVocabulary.PROPERTY_AVAILABLE_SIZE);

        Component component = new BuiltInMemoryUnitComponent();
        memoryUnitAspect.addComponent(Component.NAME_DEFAULT, component);
        memoryUnitAspect.addComponent(BuiltInMemoryUnitComponent.NAME, component);
        memoryUnitAspect.addComponent(ExtensionMemoryUnitComponent.NAME,
                new ExtensionMemoryUnitComponent());

        return memoryUnitAspect;
    }

    public static Aspect createOperatingSystemAspect(AxRuntimeContext runtimeContext) {
        AspectImpl operatingSystemAspect = new AspectImpl();

        operatingSystemAspect.addProperty(DeviceStatusVocabulary.PROPERTY_LANGUAGE);
        operatingSystemAspect.addProperty(DeviceStatusVocabulary.PROPERTY_VERSION);
        operatingSystemAspect.addProperty(DeviceStatusVocabulary.PROPERTY_NAME);
        operatingSystemAspect.addProperty(DeviceStatusVocabulary.PROPERTY_VENDOR);

        Component component = new OperatingSystemComponent();
        operatingSystemAspect.addComponent(Component.NAME_DEFAULT, component);
        operatingSystemAspect.addComponent(Component.NAME_ACTIVE, component);

        return operatingSystemAspect;
    }

    public static Aspect createWebRuntimeAspect(AxRuntimeContext runtimeContext) {
        AspectImpl webRuntimeAspect = new AspectImpl();

        webRuntimeAspect.addProperty(DeviceStatusVocabulary.PROPERTY_WAC_VERSION);
        webRuntimeAspect.addProperty(DeviceStatusVocabulary.PROPERTY_SUPPORTED_IMAGE_FORMATS);
        webRuntimeAspect.addProperty(DeviceStatusVocabulary.PROPERTY_VERSION);
        webRuntimeAspect.addProperty(DeviceStatusVocabulary.PROPERTY_NAME);
        webRuntimeAspect.addProperty(DeviceStatusVocabulary.PROPERTY_VENDOR);

        Component component = new WebRuntimeComponent();
        webRuntimeAspect.addComponent(Component.NAME_DEFAULT, component);
        webRuntimeAspect.addComponent(Component.NAME_ACTIVE, component);

        return webRuntimeAspect;
    }

    public static Aspect createWiFiHardwareAspect(AxRuntimeContext runtimeContext) {
        AspectImpl wifiHardwareAspect = new AspectImpl();

        wifiHardwareAspect.addProperty(DeviceStatusVocabulary.PROPERTY_STATUS);
        wifiHardwareAspect.addComponent(Component.NAME_DEFAULT, new WiFiHardwareComponent(
                runtimeContext));

        return wifiHardwareAspect;
    }

    public static Aspect createWiFiNetworkAspect(AxRuntimeContext runtimeContext) {
        AspectImpl wifiNetworkAspect = new AspectImpl();

        wifiNetworkAspect.addProperty(DeviceStatusVocabulary.PROPERTY_SSID);
        wifiNetworkAspect.addProperty(DeviceStatusVocabulary.PROPERTY_SIGNAL_STRENGTH);
        wifiNetworkAspect.addProperty(DeviceStatusVocabulary.PROPERTY_NETWORK_STATUS);

        Component component = new WiFiNetworkComponent(runtimeContext);
        wifiNetworkAspect.addComponent(Component.NAME_DEFAULT, component);
        wifiNetworkAspect.addComponent(Component.NAME_ACTIVE, component);

        return wifiNetworkAspect;
    }
}
