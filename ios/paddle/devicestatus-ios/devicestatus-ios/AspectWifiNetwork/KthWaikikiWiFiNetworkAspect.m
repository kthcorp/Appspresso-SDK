//
//  KthWaikikiWiFiNetworkAspect.m
//  devicestatus-ios
//
//  Copyright (c) 2012 KTH Corp.
//


#import <UIKit/UIKit.h>
#import <SystemConfiguration/CaptiveNetwork.h>
#import "Aspects.h"
#import "KthWaikikiAspectWatcher.h"
#import "Reachability.h"

@interface KthWaikikiWiFiNetworkAspectNetworkStatus : KthWaikikiAspectWatcher
{
@private
	Reachability *_wifiReach;
	NetworkStatus _status;
}
@property (nonatomic, retain, readwrite) Reachability *wifiReach;
- (void)_reachabilityChanged:(NSNotification* )notification;
- (NSString *)_stringFromStatus:(NetworkStatus)status;
@end


@implementation KthWaikikiWiFiNetworkAspectNetworkStatus
@synthesize wifiReach = _wifiReach;

- (void)prepare {
	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(reachabilityChanged:) name:kReachabilityChangedNotification object:nil];
	[self setWifiReach:[Reachability reachabilityForInternetConnection]];
	[_wifiReach startNotifier];
	_status = [_wifiReach currentReachabilityStatus];
}

- (void)clear {
	[[NSNotificationCenter defaultCenter] removeObserver:self name:kReachabilityChangedNotification object:nil];
	[_wifiReach stopNotifier];
}

- (id)getValue {
	NetworkStatus status = [self isStarted] ? _status : [[Reachability reachabilityForInternetConnection] currentReachabilityStatus];
	return [self _stringFromStatus:status];
}

-(NSString *)propertyName {
	return kWiFiNetworkPropertyNetworkStatus;
}

- (void)_reachabilityChanged:(NSNotification* )notification
{
	Reachability* curReach = [notification object];
	NSParameterAssert([curReach isKindOfClass: [Reachability class]]);
	_status = [curReach currentReachabilityStatus];
}

- (NSString *)_stringFromStatus:(NetworkStatus)status 
{
	if (ReachableViaWiFi == status) {
		return @"connected";
	} else {
		// ReachableViaWWAN, NotReachable
		return @"forbidden"; 
	}
}

- (void)dealloc 
{
	[self setWifiReach:nil];
	
	[super dealloc];
}
@end

#pragma mark -
//KthWaikikiWiFiNetworkAspectSsid
@interface KthWaikikiWiFiNetworkAspectSsid : KthWaikikiAspectWatcher

@end

@implementation KthWaikikiWiFiNetworkAspectSsid
//http://stackoverflow.com/questions/5198716/iphone-get-ssid-without-private-library

- (id)getValue {

    NSArray *ifs = (id)CNCopySupportedInterfaces();
    
    NSDictionary* info = nil;
    for (NSString *ifnam in ifs) {
        info = (id)CNCopyCurrentNetworkInfo((CFStringRef)ifnam);
        if (info && [info count]) {
            break;
        }
    }
    [ifs release];
    NSString *ssid = ([info objectForKey:@"SSID"] == nil) ? @"" : [NSString stringWithString:[info objectForKey:@"SSID"]];
    [info release];
    return ssid;
}

- (NSString *)propertyName {
	return kWiFiNetworkPropertySsid;
}

@end
