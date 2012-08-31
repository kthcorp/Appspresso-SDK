//
//  KthWaikikiCellularHardware.m
//  devicestatus-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import <UIKit/UIKit.h>
#import "Aspects.h"
#import "KthWaikikiAspectWatcher.h"
#import "Reachability.h"


@interface KthWaikikiCellularHardwareAspectStatus : KthWaikikiAspectWatcher 
{
@private
	Reachability *_internetReach;
	NetworkStatus _status;
}
@property (nonatomic, retain, readwrite) Reachability *internetReach;
- (void)_reachabilityChanged:(NSNotification* )notification;
- (NSString *)_stringFromStatus:(NetworkStatus)status;
@end


@implementation KthWaikikiCellularHardwareAspectStatus
@synthesize internetReach = _internetReach;

- (void)prepare {
	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(reachabilityChanged:) name:kReachabilityChangedNotification object:nil];
	[self setInternetReach:[Reachability reachabilityForInternetConnection]];
	[_internetReach startNotifier];
	_status = [_internetReach currentReachabilityStatus];
}

- (id)getValue {
	NetworkStatus status = [self isStarted] ? _status : [[Reachability reachabilityForInternetConnection] currentReachabilityStatus];
	return [self _stringFromStatus:status];
}

- (void)clear {
	[[NSNotificationCenter defaultCenter] removeObserver:self name:kReachabilityChangedNotification object:nil];
	[_internetReach stopNotifier];
}

-(NSString *)propertyName {
	return kCellularHardwarePropertyStatus;
}

- (void)_reachabilityChanged:(NSNotification* )notification
{
	Reachability* curReach = [notification object];
	NSParameterAssert([curReach isKindOfClass: [Reachability class]]);
	_status = [curReach currentReachabilityStatus];
}

- (NSString *)_stringFromStatus:(NetworkStatus)status 
{
	return (ReachableViaWWAN == status) ? @"ON" : @"OFF";
}

- (void)dealloc 
{
	[self setInternetReach:nil];
	
	[super dealloc];
}
@end
