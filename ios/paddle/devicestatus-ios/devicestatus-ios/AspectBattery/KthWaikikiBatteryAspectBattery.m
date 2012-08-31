//
//  KthWaikikiBatteryAspectBatteryLevel.m
//  devicestatus-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import <UIKit/UIKit.h>

#import "Aspects.h"
#import "KthWaikikiAspectWatcher.h"

#import "AxLog.h"


@interface KthWaikikiBatteryAspectBatteryLevel : KthWaikikiAspectWatcher
- (id)getValue;

@end


@implementation KthWaikikiBatteryAspectBatteryLevel

-(id)init {
    if ((self = [super init])) {
        [[UIDevice currentDevice] setBatteryMonitoringEnabled:YES];
    }
    return self;
}

- (id)getValue {
    float betteryLevel;
    betteryLevel = [[UIDevice currentDevice] batteryLevel];
    
    AX_LOG_TRACE(@"level = %f", betteryLevel);

	return [NSNumber numberWithInt:(int)(betteryLevel * 100)];
}


- (NSString *)propertyName {
	return kBatteryPropertyBatteryLevel;
}

-(void) dealloc {
    [[UIDevice currentDevice] setBatteryMonitoringEnabled:NO];
    [super dealloc];
}

@end

#pragma mark -

@interface KthWaikikiBatteryAspectBatteryBeingCharged : KthWaikikiAspectWatcher
- (id)getValue;

@end


@implementation KthWaikikiBatteryAspectBatteryBeingCharged
-(id)init {
    if ((self = [super init])) {
        [[UIDevice currentDevice] setBatteryMonitoringEnabled:YES];
    }
    return self;
}

- (id)getValue {
    if ([[UIDevice currentDevice] batteryState] == UIDeviceBatteryStateUnplugged || [[UIDevice currentDevice] batteryState] == UIDeviceBatteryStateUnknown) {
        return [NSNumber numberWithBool:NO];
    }
    return [NSNumber numberWithBool:YES];
}

- (NSString *)propertyName {
	return kBatteryPropertyBatteryBeingCharged;
}

-(void) dealloc {
    [[UIDevice currentDevice] setBatteryMonitoringEnabled:NO];
    [super dealloc];
}

@end

