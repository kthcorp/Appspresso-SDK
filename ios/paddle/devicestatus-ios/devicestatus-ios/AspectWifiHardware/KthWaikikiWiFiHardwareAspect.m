//
//  KthWaikikiWiFiHardwareAspectStatus.m
//  devicestatus-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import <UIKit/UIKit.h>
#import "Aspects.h"
#import "KthWaikikiAspectWatcher.h"
#import "Reachability.h"


@interface KthWaikikiWiFiHardwareAspectStatus : KthWaikikiAspectWatcher
{
@private
	Reachability *_wifiReach;
	NetworkStatus _status;
}
@property (nonatomic, retain, readwrite) Reachability *wifiReach;
- (void)_reachabilityChanged:(NSNotification* )notification;
- (NSString *)_stringFromStatus:(NetworkStatus)status;
@end


@implementation KthWaikikiWiFiHardwareAspectStatus
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
	return kWiFiHardwarePropertyStatus;
}

- (void)_reachabilityChanged:(NSNotification* )notification
{
	Reachability* curReach = [notification object];
	NSParameterAssert([curReach isKindOfClass: [Reachability class]]);
	_status = [curReach currentReachabilityStatus];
}

- (NSString *)_stringFromStatus:(NetworkStatus)status 
{
	return (ReachableViaWiFi == status) ? @"ON" : @"OFF";
}

- (void)dealloc 
{
	[self setWifiReach:nil];
	
	[super dealloc];
}
@end
