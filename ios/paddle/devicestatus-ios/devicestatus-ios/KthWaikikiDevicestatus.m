//
//  KthWaikikiDevicestatus.m
//  devicestatus-ios
//
//  Copyright (c) 2012 KTH Corp.
//


#import "KthWaikikiDevicestatus.h"
#import "KthWaikikiAspectWatcher.h"
#import "Aspects.h"

#import "AxRuntimeContext.h"
#import "AxPluginContext.h"
#import "AxError.h"
#import "AxLog.h"

// devicestatus plugin specific error messages
#define kErrMsgNotFoundErrNonValidAspect @"Aspect is not valid"
#define kErrMsgNotFoundErrNonValidProperty @"Property is not valid"
#define kErrMsgNotFoundErrNotAvailableError @"hardware is not included in the device, or there is a temporary problem that makes the value unavailable"
#define kKeyGetPropertyValue @"GetPropertyValue"

#define WAC_DEVICESTATUS @"http://wacapps.net/api/devicestatus"

//wac v2.0 devicestatus.deviceinfo : Acccess to all the module. The methods getPropertyValue() and watchPropertyValue() can only be used for deviceinfo aspects
//Battery,Device,Display,MemoryUnit,OperatingSystem,WebRuntime
#define WAC_DEVICESTATUS_DEVICE @"http://wacapps.net/api/devicestatus.deviceinfo"

//wac v2.0 devicestatus.networkinfo : Access to all the module. The methods getPropertyValue() and watchPropertyValue() can only be used for networkinfo aspects;
//CellularHardware,CellularNetwork,WiFiHardware,WiFiNetwork.
#define WAC_DEVICESTATUS_NETWORK @"http://wacapps.net/api/devicestatus.networkinfo"

@interface KthWaikikiDevicestatus ()
@property (nonatomic, retain, readwrite) NSMutableDictionary *watchObjects;
@end

static NSDictionary *_aspects;

@implementation KthWaikikiDevicestatus
//@synthesize webView =_webView;
@synthesize watchObjects = _watchObjects;

- (BOOL)_isActivatedFeatureDeviceStatus {
    return [[self runtimeContext]isActivatedFeature:WAC_DEVICESTATUS];
}

- (BOOL)_isActivatedFeatureDevicestatusDeviceInfo {
    return [[self runtimeContext]isActivatedFeature:WAC_DEVICESTATUS_DEVICE];
}

- (BOOL)_isActivatedFeatureDevicestatusNetworkInfo {
    return [[self runtimeContext]isActivatedFeature:WAC_DEVICESTATUS_NETWORK];
}

- (id)init {
	self = [super init];
	if (!!self) {
		_watchObjects = [[NSMutableDictionary alloc] init];
		
		if (nil == _aspects) {
			_aspects = [[NSDictionary alloc] initWithObjectsAndKeys:
						[NSDictionary dictionaryWithObjectsAndKeys:
						 kBatteryPropertyBatteryLevelObj, kBatteryPropertyBatteryLevel,
						 kBatteryPropertyBatteryBeingChargedObj, kBatteryPropertyBatteryBeingCharged,
						 nil],  kAspectsBattery,
						

						[NSDictionary dictionaryWithObjectsAndKeys:
						 kCellularHardwarePropertyStatusObj, kCellularHardwarePropertyStatus, 
						 nil], kAspectsCellularHardware,
						
						[NSDictionary dictionaryWithObjectsAndKeys:
						 kCellularNetworkPropertyIsInRoamingObj, kCellularNetworkPropertyIsInRoaming,
                         kCellularNetworkPropertyMccObj, kCellularNetworkPropertyMcc,
                         kCellularNetworkPropertyMncObj, kCellularNetworkPropertyMnc,
						 kCellularNetworkPropertySignalStrengthObj, kCellularNetworkPropertySignalStrength, 
						 kCellularNetworkPropertyOperatorNameObj, kCellularNetworkPropertyOperatorName, 
						 nil], kAspectsCellularNetwork,
						
						[NSDictionary dictionaryWithObjectsAndKeys:
						 kDevicePropertyImeiObj, kDevicePropertyImei, 
						 kDevicePropertyModelObj, kDevicePropertyModel,
						 kDevicePropertyVersionObj, kDevicePropertyVersion,
						 kDevicePropertyVendorObj, kDevicePropertyVendor,
						 nil], kAspectsDevice, 
						
						[NSDictionary dictionaryWithObjectsAndKeys:
						 kDisplayPropertyResolutionHeightObj, kDisplayPropertyResolutionHeight,
						 kDisplayPropertyPixelAspectRatioObj, kDisplayPropertyPixelAspectRatio,
						 kDisplayPropertyDpiYObj, kDisplayPropertyDpiY,
						 kDisplayPropertyResolutionWidthObj, kDisplayPropertyResolutionWidth,
						 kDisplayPropertyDpiXObj, kDisplayPropertyDpiX,
						 kDisplayPropertyColorDepthObj, kDisplayPropertyColorDepth,
						 nil], kAspectsDisplay,
						
						[NSDictionary dictionaryWithObjectsAndKeys:
						 kMemoryUnitPropertySizeObj, kMemoryUnitPropertySize, 
						 kMemoryUnitPropertyRemovableObj, kMemoryUnitPropertyRemovable, 
						 kMemoryUnitPropertyAvailableSizeObj, kMemoryUnitPropertyAvailableSize, 
						 nil], kAspectsMemoryUnit, 
						
						[NSDictionary dictionaryWithObjectsAndKeys:
						 kOperatingSystemPropertyLanguageObj, kOperatingSystemPropertyLanguage, 
						 kOperatingSystemPropertyVersionObj, kOperatingSystemPropertyVersion, 
						 kOperatingSystemPropertyNameObj, kOperatingSystemPropertyName, 
						 kOperatingSystemPropertyVendorObj, kOperatingSystemPropertyVendor, 
						 nil], kAspectsOperatingSystem,
						
						[NSDictionary dictionaryWithObjectsAndKeys:
						 kWebRuntimePropertyWacVersionObj, kWebRuntimePropertyWacVersion,
						 kWebRuntimePropertySupportedImageFormatsObj, kWebRuntimePropertySupportedImageFormats, 
						 kWebRuntimePropertyVersionObj, kWebRuntimePropertyVersion, 
						 kWebRuntimePropertyNameObj, kWebRuntimePropertyName, 
						 kWebRuntimePropertyVendorObj, kWebRuntimePropertyVendor, 
						 nil], kAspectsWebRuntime, 
						
						[NSDictionary dictionaryWithObjectsAndKeys:
						 kWiFiHardwarePropertyStatusObj, kWiFiHardwarePropertyStatus, 
						 nil], kAspectsWiFiHardware,
						
						[NSDictionary dictionaryWithObjectsAndKeys:
						 kWiFiNetworkPropertySsidObj, kWiFiNetworkPropertySsid, 
						 kWiFiNetworkPropertySignalStrengthObj, kWiFiNetworkPropertySignalStrength, 
						 kWiFiNetworkPropertyNetworkStatusObj, kWiFiNetworkPropertyNetworkStatus, 
						 nil], kAspectsWiFiNetwork,
						nil];		
		}	
	}
	return self;
}

- (void)activate:(id<AxRuntimeContext>)runtimeContext {
    [super activate:runtimeContext];
    [runtimeContext requirePlugin:@"deviceapis"];
//	_webView = [[[env webViews] objectAtIndex:0] retain];
}

- (void)deactivate:(id<AxRuntimeContext>)runtimeContext {
//	[self setWebView:nil];
	[self setWatchObjects:nil];
    [super deactivate:runtimeContext];
}

/**********************************************************************************************************************************************************************/
/* StringArray      getComponents(DOMString aspect)                                                                                                                   */
/* boolean          isSupported(DOMString aspect, DOMString property)                                                                                                 */
/* PendingOperation getPropertyValue(GetPropertySuccessCallback successCallback, ErrorCallback? errorCallback, PropertyRef prop)                                      */
/* unsigned long    watchPropertyChange(PropertyChangeSuccessCallback successCallback, ErrorCallback? errorCallback, PropertyRef prop, WatchOptions options)          */
/* void             clearPropertyChange(unsigned long watchHandler)                                                                                                   */
/**********************************************************************************************************************************************************************/

static NSDictionary *_components = nil;
- (void)getComponents:(id<AxPluginContext>)context {
    // TODO: It is TEMPORARY FIX
	const NSString *DEFAULT = @"_default";
	const NSString *ACTIVE = @"_active";
	if (nil == _components) {
		_components = [[NSDictionary alloc] initWithObjectsAndKeys:
					   [NSArray arrayWithObjects:DEFAULT, nil], kAspectsBattery,
					   [NSArray arrayWithObjects:DEFAULT, nil], kAspectsCellularHardware,
					   [NSArray arrayWithObjects:DEFAULT, nil], kAspectsCellularNetwork,
					   [NSArray arrayWithObjects:DEFAULT, nil], kAspectsDevice,										
					   [NSArray arrayWithObjects:DEFAULT, ACTIVE, nil], kAspectsDisplay,								
					   [NSArray arrayWithObjects:DEFAULT, nil], kAspectsMemoryUnit,										
					   [NSArray arrayWithObjects:DEFAULT, ACTIVE, nil], kAspectsOperatingSystem,
					   [NSArray arrayWithObjects:DEFAULT, ACTIVE, nil], kAspectsWebRuntime,										
					   [NSArray arrayWithObjects:DEFAULT, nil], kAspectsWiFiHardware,
					   [NSArray arrayWithObjects:DEFAULT, nil], kAspectsWiFiNetwork,
					   nil];
	}
	
	NSString *aspect = [context getParamAsString:0];
    
	NSArray *res = [_components objectForKey:aspect];
	if (res) {
		[context sendResult:res];
	} else {
		[context sendResult:[NSNull null]];
	}
}

- (void)isSupported:(id<AxPluginContext> )context {
    NSString *aspect = [context getParamAsString:0];
    NSString *property = [context getParamAsString:1];
    
	NSNumber *res;
	if ([property isKindOfClass:[NSNull class]]) {
		res = [NSNumber numberWithBool:[KthWaikikiDevicestatus hasAspects:aspect]];
	} else {
		res = [NSNumber numberWithBool:[KthWaikikiDevicestatus hasProperty:property ofAspect:aspect]];
	}
	
	[context sendResult:res];
}

/*
 * getPropertyValue(NSArray *info, NSInteger identifier, BOOL start)
 * info[0] : NSString *aspect
 * info[1] : NSString *property
 * identifier : Integer (called by watch)
 *            : undefined (called by getPropertyValue)
 * start : YES (If it is first call for watch)
 *         NO  (If it is not first call for watch)
 *         undefined (It is not called by watch)
 */
- (void)getPropertyValue:(id<AxPluginContext>)context {
	
    NSString *aspect = [context getParamAsString:0 name:@"aspect"];
    if (aspect == kAspectsCellularHardware || aspect == kAspectsCellularNetwork || aspect == kAspectsWiFiHardware || aspect == kAspectsWiFiNetwork) {
        if ([self _isActivatedFeatureDevicestatusDeviceInfo] && ![self _isActivatedFeatureDeviceStatus] && ![self _isActivatedFeatureDevicestatusNetworkInfo]) {
            [context sendError:AX_SECURITY_ERR message:AX_SECURITY_ERR_MSG];
            return;
        }
    }
    else {
        if ([self _isActivatedFeatureDevicestatusNetworkInfo] && ![self _isActivatedFeatureDeviceStatus] && ![self _isActivatedFeatureDevicestatusDeviceInfo]) {
            [context sendError:AX_SECURITY_ERR message:AX_SECURITY_ERR_MSG];
            return;
        }
    }
    
    
    NSString *property = [context getParamAsString:0 name:@"property"];
    
    NSArray *params = [context getParams];
	NSNumber *identifier = (1 < [params count]) ? [context getParamAsNumber:1] : nil;
	NSNumber *start = (1 < [params count]) ? [context getParamAsNumber:2] : nil;
	
	NSDictionary *d = [_aspects objectForKey:aspect];
	if (!d) {
		[context sendError:AX_NOT_FOUND_ERR message:kErrMsgNotFoundErrNonValidAspect];
		return;
	}
	
	NSString *clsName = [d objectForKey:property];
	if (!clsName) {
		[context sendError:AX_NOT_FOUND_ERR message:kErrMsgNotFoundErrNonValidProperty];
		return;
	}
	
	Class cls = NSClassFromString(clsName);
	KthWaikikiAspectWatcher *obj = [[cls alloc] init];
	if (!obj) {
		[context sendError:AX_NOT_AVAILABLE_ERR message:kErrMsgNotFoundErrNotAvailableError];
		return;
	}
	
					   
	if ((nil != start) && (YES == [start boolValue])) {
		[obj start];
		[_watchObjects setObject:obj forKey:identifier];
	}
	[context sendResult:[obj getValue]];
    [obj release];
}

- (void)clearPropertyChange:(id<AxPluginContext>)context {
    NSNumber *handle = [context getParamAsNumber:0];

	KthWaikikiAspectWatcher *obj = [_watchObjects objectForKey:handle];
	if (nil == obj) {
		[context sendError:AX_TYPE_MISMATCH_ERR message:AX_TYPE_MISMATCH_ERR_MSG];
		return;
	}
    
    [obj stop];
    [_watchObjects removeObjectForKey:handle];
	[context sendResult];
}

+ (BOOL)hasAspects:(NSString *)aspects {
	return (nil != [_aspects objectForKey:aspects]);
}

+ (BOOL)hasProperty:(NSString *)property ofAspect:(NSString *)aspects {
	NSDictionary *aspect = [_aspects objectForKey:aspects];
	if (!aspect) {
		return NO;
	}
	
	return (nil != [aspect objectForKey:property]);
}
@end
