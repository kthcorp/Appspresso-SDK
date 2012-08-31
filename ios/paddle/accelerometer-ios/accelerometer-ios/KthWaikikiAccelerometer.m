//
//  KthWaikikiAccelerometer.m
//  accelerometer-ios
//                                                                                                                                                              
//  Copyright (c) 2012 KTH Corp.
// 

#import "KthWaikikiAccelerometer.h"

#import "AxRuntimeContext.h"
#import "AxPluginContext.h"
#import "AxError.h"
#import "AxLog.h"

#define kDefaultInterval 100
#define EarthGravity 9.8

@interface KthWaikikiAccelerometer ()
@property (nonatomic ,retain) CMMotionManager *motionManager;
@property (retain) NSMutableDictionary *watchHandles;
@end

@implementation KthWaikikiAccelerometer
@synthesize motionManager = _motionManager;
@synthesize watchHandles = _watchHandles;

- (void)activate:(id<AxRuntimeContext>)runtimeContext {
    [super activate:runtimeContext];
    [runtimeContext requirePlugin:@"deviceapis"];

    _listeners = 0;
    _watchHandles = [[NSMutableDictionary alloc] init];
    _motionManager = [[CMMotionManager alloc] init];
    [_motionManager startAccelerometerUpdates];
    
}

- (void)deactivate:(id<AxRuntimeContext>)runtimeContext {
	[_motionManager stopAccelerometerUpdates];
    [self setMotionManager:nil];
    [self setWatchHandles:nil];
    
}

- (NSDictionary *)dictionaryFromCoreMotion:(CMAccelerometerData *)accelerometerData
{
	return [NSDictionary dictionaryWithObjectsAndKeys:
			[NSNumber numberWithFloat:EarthGravity * accelerometerData.acceleration.x], @"xAxis",
			[NSNumber numberWithFloat:EarthGravity * accelerometerData.acceleration.y], @"yAxis",
			[NSNumber numberWithFloat:EarthGravity * accelerometerData.acceleration.z], @"zAxis", nil];	
}

/*
* getCurrentAcceleration(NSInteger handle, BOOL start)
* handle : Integer (called by watch)
*        : undefined (called by getPropertyValue)
* start : YES (If it is first call for watch)
*         NO  (If it is not first call for watch)
*         undefined (It is not called by watch)
*/
- (void)getCurrentAcceleration:(id<AxPluginContext>)context
{
#if !TARGET_OS_EMBEDDED
	[context sendError:AX_NOT_SUPPORTED_ERR message:AX_NOT_SUPPORTED_ERR_SIMULATOR_MSG];
	return;
#endif
	NSArray *params = [context getParams];
	NSNumber *handle = ([params count] > 0) ? [context getParamAsNumber:0] : nil;
	NSNumber *start = ([params count] > 0) ? [context getParamAsNumber:1] : nil;
	
    if (_motionManager.accelerometerActive && (0 != _listeners) && !!start && (NO == [start boolValue])) {
        [context sendResult:[self dictionaryFromCoreMotion:[_motionManager accelerometerData]]];
        return;
    }
	
    
	//UIAccelerometer *accelerometer = [UIAccelerometer sharedAccelerometer];
	@synchronized(self)
	{	
		if (0 == _listeners) {
            [_motionManager setAccelerometerUpdateInterval:kDefaultInterval/1000];
            [_motionManager startAccelerometerUpdates];
            
		}
		
		if (!!start && (YES == [start boolValue])) {
			[_watchHandles setObject:handle forKey:handle];
			++_listeners;
		}
		
		if (![_motionManager accelerometerData]) {
			++_listeners;
		}
	}
    
    [context sendResult:[self dictionaryFromCoreMotion:[_motionManager accelerometerData]]];
}

- (void)clearWatch:(id<AxPluginContext>)context
{
#if !TARGET_OS_EMBEDDED
	[context sendError:AX_NOT_SUPPORTED_ERR message:AX_NOT_SUPPORTED_ERR_SIMULATOR_MSG];
	return;
#endif
	NSNumber *handle = [context getParamAsNumber:0];
	
	if (nil == [_watchHandles objectForKey:handle]) {
        [context sendError:AX_TYPE_MISMATCH_ERR message:AX_TYPE_MISMATCH_ERR_MSG];
		return;
	}
	[_watchHandles removeObjectForKey:handle];
	
	@synchronized (self) 
	{
		if (0 == --_listeners) {
			// TODO: remove delegate only if context queue is empty
            [_motionManager stopAccelerometerUpdates];
            
		}		
	}
	
	[context sendResult];
}
@end
