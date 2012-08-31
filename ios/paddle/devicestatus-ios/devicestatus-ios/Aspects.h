//
//  Aspect.h
//  devicestatus-ios
//
//  Copyright (c) 2012 KTH Corp.
//

/*********************************************************************************************************************/
/* Battery          batteryLevel,  batteryBeingCharged                                             _default          */
/* CellularHardware status                                                                         _default          */
/* CellularNetwork  isInRoaming, mcc, mnc, signalStrength, operatorName                            _default          */
/* Device           imei, model, version, vendor                                                   _default          */
/* Display          resolutionHeight, pixelAspectRatio, dpiY, resolutionWidth, dpiX, colorDepth    _active, _default */
/* MemoryUnit       size, removable, availableSize                                                 _default          */
/* OperatingSystem  language, version, name, vendor                                                _active, _default */
/* WebRuntime       wacVersion, supportedImageFormats, version, name, vendor                       _active, _default */
/* WiFiHardware     status                                                                         _default          */
/* WiFiNetwork      ssid, signalStrength, networkStatus								                                 */
/*********************************************************************************************************************/


////////////////////////////////////////////////////////////////////////////////////////////////////
// Battery
#define kAspectsBattery @"Battery"

#define kBatteryPropertyBatteryLevel @"batteryLevel"
#define kBatteryPropertyBatteryBeingCharged @"batteryBeingCharged"

#define kBatteryPropertyBatteryLevelObj @"KthWaikikiBatteryAspectBatteryLevel"
#define kBatteryPropertyBatteryBeingChargedObj @"KthWaikikiBatteryAspectBatteryBeingCharged"



////////////////////////////////////////////////////////////////////////////////////////////////////
// CellularHardware
#define kAspectsCellularHardware @"CellularHardware" 

#define kCellularHardwarePropertyStatus @"status"

#define kCellularHardwarePropertyStatusObj @"KthWaikikiCellularHardwareAspectStatus"



////////////////////////////////////////////////////////////////////////////////////////////////////
// CellularNetwork
#define kAspectsCellularNetwork @"CellularNetwork"  

#define kCellularNetworkPropertyIsInRoaming @"isInRoaming"
#define kCellularNetworkPropertyMcc @"mcc"
#define kCellularNetworkPropertyMnc @"mnc"
#define kCellularNetworkPropertySignalStrength @"signalStrength"
#define kCellularNetworkPropertyOperatorName @"operatorName"

#define kCellularNetworkPropertyIsInRoamingObj @"KthWaikikiCellularNetworkAspectIsInRoaming"
#define kCellularNetworkPropertyMccObj @"KthWaikikiCellularNetworkAspectMcc"
#define kCellularNetworkPropertyMncObj @"KthWaikikiCellularNetworkAspectMnc"
#define kCellularNetworkPropertySignalStrengthObj @"KthWaikikiCellularNetworkAspectSignalStrength"
#define kCellularNetworkPropertyOperatorNameObj @"KthWaikikiCellularNetworkAspectOperatorName"



////////////////////////////////////////////////////////////////////////////////////////////////////
//Device
#define kAspectsDevice @"Device"

#define kDevicePropertyImei @"imei"
#define kDevicePropertyModel @"model"
#define kDevicePropertyVersion @"version"
#define kDevicePropertyVendor @"vendor"

#define kDevicePropertyImeiObj @"KthWaikikiDeviceAspectImei"
#define kDevicePropertyModelObj @"KthWaikikiDeviceAspectModel"
#define kDevicePropertyVersionObj @"KthWaikikiDeviceAspectVersion"
#define kDevicePropertyVendorObj @"KthWaikikiDeviceAspectVendor"



////////////////////////////////////////////////////////////////////////////////////////////////////
//Display
#define kAspectsDisplay @"Display"        

#define kDisplayPropertyResolutionHeight @"resolutionHeight"
#define kDisplayPropertyPixelAspectRatio @"pixelAspectRatio"
#define kDisplayPropertyDpiY @"dpiY"
#define kDisplayPropertyResolutionWidth @"resolutionWidth"
#define kDisplayPropertyDpiX @"dpiX"
#define kDisplayPropertyColorDepth @"colorDepth"

#define kDisplayPropertyResolutionHeightObj @"KthWaikikiDisplayAspectResolutionHeight"
#define kDisplayPropertyPixelAspectRatioObj @"KthWaikikiDisplayAspectPixelAspectRatio"
#define kDisplayPropertyDpiYObj @"KthWaikikiDisplayAspectDpiY"
#define kDisplayPropertyResolutionWidthObj @"KthWaikikiDisplayAspectResolutionWidth"
#define kDisplayPropertyDpiXObj @"KthWaikikiDisplayAspectDpiX"
#define kDisplayPropertyColorDepthObj @"KthWaikikiDisplayAspectColorDepth"



////////////////////////////////////////////////////////////////////////////////////////////////////
//MemoryUnit
#define kAspectsMemoryUnit @"MemoryUnit"       

#define kMemoryUnitPropertySize @"size"
#define kMemoryUnitPropertyRemovable @"removable"
#define kMemoryUnitPropertyAvailableSize @"availableSize"

#define kMemoryUnitPropertySizeObj @"KthWaikikiMemoryUnitAspectSize"
#define kMemoryUnitPropertyRemovableObj @"KthWaikikiMemoryUnitAspectRemovable"
#define kMemoryUnitPropertyAvailableSizeObj @"KthWaikikiMemoryUnitAspectAvailableSize"


////////////////////////////////////////////////////////////////////////////////////////////////////
//OperatingSystem
#define kAspectsOperatingSystem @"OperatingSystem"  

#define kOperatingSystemPropertyLanguage @"language"
#define kOperatingSystemPropertyVersion @"version"
#define kOperatingSystemPropertyName @"name"
#define kOperatingSystemPropertyVendor @"vendor"

#define kOperatingSystemPropertyLanguageObj @"KthWaikikiOperatingSystemAspectLanguage"
#define kOperatingSystemPropertyVersionObj @"KthWaikikiOperatingSystemAspectVersion"
#define kOperatingSystemPropertyNameObj @"KthWaikikiOperatingSystemAspectName"
#define kOperatingSystemPropertyVendorObj @"KthWaikikiOperatingSystemAspectVendor"



////////////////////////////////////////////////////////////////////////////////////////////////////
//WebRuntime
#define kAspectsWebRuntime @"WebRuntime"    

#define kWebRuntimePropertyWacVersion @"wacVersion"
#define kWebRuntimePropertySupportedImageFormats @"supportedImageFormats"
#define kWebRuntimePropertyVersion @"version"
#define kWebRuntimePropertyName @"name"
#define kWebRuntimePropertyVendor @"vendor"

#define kWebRuntimePropertyWacVersionObj @"KthWaikikiWebRuntimeAspectWacVersion"
#define kWebRuntimePropertySupportedImageFormatsObj @"KthWaikikiWebRuntimeAspectSupportedImageFormats"
#define kWebRuntimePropertyVersionObj @"KthWaikikiWebRuntimeAspectVersion"
#define kWebRuntimePropertyNameObj @"KthWaikikiWebRuntimeAspectName"
#define kWebRuntimePropertyVendorObj @"KthWaikikiWebRuntimeAspectVendor"


////////////////////////////////////////////////////////////////////////////////////////////////////
//WiFiHardware
#define kAspectsWiFiHardware @"WiFiHardware"

#define kWiFiHardwarePropertyStatus @"status"

#define kWiFiHardwarePropertyStatusObj @"KthWaikikiWiFiHardwareAspectStatus"



////////////////////////////////////////////////////////////////////////////////////////////////////
//WiFiNetwork
#define kAspectsWiFiNetwork @"WiFiNetwork"      

#define kWiFiNetworkPropertySsid @"ssid"
#define kWiFiNetworkPropertySignalStrength @"signalStrength"
#define kWiFiNetworkPropertyNetworkStatus @"networkStatus"

#define kWiFiNetworkPropertySsidObj @"KthWaikikiWiFiNetworkAspectSsid"
#define kWiFiNetworkPropertySignalStrengthObj @"KthWaikikiWiFiNetworkAspectSignalStrength"
#define kWiFiNetworkPropertyNetworkStatusObj @"KthWaikikiWiFiNetworkAspectNetworkStatus"
