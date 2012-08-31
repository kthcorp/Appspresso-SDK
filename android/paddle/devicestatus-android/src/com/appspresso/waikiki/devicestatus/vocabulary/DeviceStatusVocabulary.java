package com.appspresso.waikiki.devicestatus.vocabulary;

public class DeviceStatusVocabulary {
    public static final String ASPECT_BATTERY = "Battery";
    public static final String ASPECT_CELLULAR_HARDWARE = "CellularHardware";
    public static final String ASPECT_CELLULAR_NETWORK = "CellularNetwork";
    public static final String ASPECT_DEVICE = "Device";
    public static final String ASPECT_DISPLAY = "Display";
    public static final String ASPECT_MEMORY_UNIT = "MemoryUnit";
    public static final String ASPECT_OPERATING_SYSTEM = "OperatingSystem";
    public static final String ASPECT_WEB_RUNTIME = "WebRuntime";
    public static final String ASPECT_WIFI_HARDWARE = "WiFiHardware";
    public static final String ASPECT_WIFI_NETWORK = "WiFiNetwork";

    public static final String[] DEVICE_INFO_ASPECTS = new String[] {ASPECT_BATTERY, ASPECT_DEVICE,
            ASPECT_DISPLAY, ASPECT_MEMORY_UNIT, ASPECT_OPERATING_SYSTEM, ASPECT_WEB_RUNTIME};

    public static final String[] NETWORK_INFO_ASPECTS = new String[] {ASPECT_CELLULAR_HARDWARE,
            ASPECT_CELLULAR_NETWORK, ASPECT_WIFI_HARDWARE, ASPECT_WIFI_NETWORK};

    public static final String PROPERTY_BATTERY_LEVEL = "batteryLevel";
    public static final String PROPERTY_BATTERY_BEING_CHARGED = "batteryBeingCharged";
    public static final String PROPERTY_STATUS = "status";
    public static final String PROPERTY_IS_IN_ROAMING = "isInRoaming";
    public static final String PROPERTY_MCC = "mcc";
    public static final String PROPERTY_MNC = "mnc";
    public static final String PROPERTY_SIGNAL_STRENGTH = "signalStrength";
    public static final String PROPERTY_OPERATOR_NAME = "operatorName";
    public static final String PROPERTY_SIZE = "size";
    public static final String PROPERTY_REMOVABLE = "removable";
    public static final String PROPERTY_AVAILABLE_SIZE = "availableSize";

    public static final String PROPERTY_IMEI = "imei";
    public static final String PROPERTY_MODEL = "model";
    public static final String PROPERTY_VERSION = "version";
    public static final String PROPERTY_VENDOR = "vendor";

    public static final String PROPERTY_RESOLUTION_WIDTH = "resolutionWidth";
    public static final String PROPERTY_RESOLUTION_HEIGHT = "resolutionHeight";
    public static final String PROPERTY_DPI_X = "dpiX";
    public static final String PROPERTY_DPI_Y = "dpiY";
    public static final String PROPERTY_PIXEL_ASPECT_RATIO = "pixelAspectRatio";
    public static final String PROPERTY_COLOR_DEPTH = "colorDepth";

    public static final String PROPERTY_LANGUAGE = "language";
    public static final String PROPERTY_NAME = "name";
    public static final String PROPERTY_WAC_VERSION = "wacVersion";
    public static final String PROPERTY_SUPPORTED_IMAGE_FORMATS = "supportedImageFormats";
    public static final String PROPERTY_SSID = "ssid";
    public static final String PROPERTY_NETWORK_STATUS = "networkStatus";
}
