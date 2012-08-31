//
//  KthWaikikiOrientation.m
//  orientation-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import "KthWaikikiOrientation.h"

#import "AxRuntimeContext.h"
#import "AxPluginContext.h"
#import "AxError.h"
#import "AxLog.h"

#define kDefaultInterval 100
#define EarthGravity 9.8

@interface KthWaikikiOrientation ()
@property (nonatomic ,retain) CMMotionManager *motionManager;
@property (retain) NSMutableDictionary *watchHandles;
@property (nonatomic, retain) CLLocationManager *locationManager;
@end

@implementation KthWaikikiOrientation
@synthesize motionManager = _motionManager;
@synthesize watchHandles = _watchHandles;
@synthesize locationManager = _locationManager;

- (void)activate:(id<AxRuntimeContext>)runtimeContext {
    [super activate:runtimeContext];
    [runtimeContext requirePlugin:@"deviceapis"];
    
    _listeners = 0;
    _alpha = 0;
    _beta = 0;
    _gamma = 0;
    _watchHandles = [[NSMutableDictionary alloc] init];
    _motionManager = [[CMMotionManager alloc] init];
    _locationManager = [[CLLocationManager alloc] init];
    _gyroAvailable = _motionManager.gyroAvailable;
    if (_gyroAvailable) {
        [_motionManager startDeviceMotionUpdates];
    }
    else {
        [_motionManager startAccelerometerUpdates];
        [_locationManager startUpdatingHeading];
        [_locationManager setDelegate:self];
    }
    
}

- (void)deactivate:(id<AxRuntimeContext>)runtimeContext {
	if (_gyroAvailable) { 
        [_motionManager stopDeviceMotionUpdates];
    }
    else {
        [_motionManager stopAccelerometerUpdates];
        [self setLocationManager:nil];
    }
    [self setMotionManager:nil];
    [self setWatchHandles:nil];
    
}

// This delegate method is invoked when the location manager has heading data.
- (void)locationManager:(CLLocationManager *)manager didUpdateHeading:(CLHeading *)heading {
	_alpha = [heading magneticHeading];
}

- (NSDictionary *)dictionaryFromAccelerometerData:(CMAccelerometerData *)accelerometerData
{
    // TODO: Cannot handle G-force
	if (_alpha > 360)	{ _alpha = 360; }
	if (_alpha < -360)	{ _alpha = -360; }
	if (_beta > 180)	{ _beta = 180; }
	if (_beta < -180)	{ _beta = -180; }
	if (_gamma > 90)	{ _gamma = 90; }
	if (_gamma < -90)	{ _gamma = 90; }
    
    if (_alpha < 0) {
        _alpha *= -1;
    }
    _beta = (accelerometerData.acceleration.z+1.0) * 90.0;
    if (accelerometerData.acceleration.y > 0) {
        _beta *= -1;
    }
    _gamma = accelerometerData.acceleration.x * 90.0;
	
    return [NSDictionary dictionaryWithObjectsAndKeys:
			[NSNumber numberWithLong:_alpha], @"alpha",
			[NSNumber numberWithLong:_beta], @"beta",
			[NSNumber numberWithLong:_gamma], @"gamma", nil];	
}


- (NSDictionary *)dictionaryFromDeviceMotion:(CMDeviceMotion*) deviceMotion
{
    //alpha 0 ~ 360
    //beta -180~180
    //gamme -90~90
    
    return [NSDictionary dictionaryWithObjectsAndKeys:
            [NSNumber numberWithLong:((deviceMotion.attitude.yaw/M_PI)+1.0f)*180],@"alpha",
            [NSNumber numberWithLong:(deviceMotion.attitude.pitch/M_PI)*180],@"beta",
            [NSNumber numberWithLong:(deviceMotion.attitude.roll/M_PI)*90],@"gamma",nil];
            //[NSNumber numberWithLong:(deviceMotion.attitude.yaw+1.0f)*kRadian], @"alpha",                                        
            //[NSNumber numberWithLong:deviceMotion.attitude.pitch * kRadian], @"beta",
            //[NSNumber numberWithLong:(deviceMotion.attitude.roll* kRadian)/2] , @"gamma", nil];
}

/*
 * getCurrentAcceleration(NSInteger handle, BOOL start)
 * handle : Integer (called by watch)
 *        : undefined (called by getPropertyValue)
 * start : YES (If it is first call for watch)
 *         NO  (If it is not first call for watch)
 *         undefined (It is not called by watch)
 */
- (void)getCurrentOrientation:(id<AxPluginContext>)context
{
#if !TARGET_OS_EMBEDDED
	[context sendError:AX_NOT_SUPPORTED_ERR message:AX_NOT_SUPPORTED_ERR_SIMULATOR_MSG];
	return;
#endif
	NSArray *params = [context getParams];
	NSNumber *handle = ([params count] > 0) ? [context getParamAsNumber:0] : nil;
	NSNumber *start = ([params count] > 0) ? [context getParamAsNumber:1] : nil;
	
    if ( (0 != _listeners) && !!start && (NO == [start boolValue])) {
        if (_gyroAvailable && _motionManager.deviceMotionActive) {
            [context sendResult:[self dictionaryFromDeviceMotion:[_motionManager deviceMotion]]];
        }
        else if (!_gyroAvailable && _motionManager.accelerometerActive) {
            [context sendResult:[self dictionaryFromAccelerometerData:[_motionManager accelerometerData]]];
        }
        
        return;
    }
	
	@synchronized(self)
	{	
		if (0 == _listeners) {
            if (_gyroAvailable) {
                [_motionManager setDeviceMotionUpdateInterval:kDefaultInterval/1000];
                [_motionManager startDeviceMotionUpdates];
            }
            else {
                [_motionManager setAccelerometerUpdateInterval:kDefaultInterval/1000];
                [_motionManager startAccelerometerUpdates];
                [_locationManager startUpdatingHeading];
                [_locationManager setDelegate:self];
            }
		}
		
		if (!!start && (YES == [start boolValue])) {
			[_watchHandles setObject:handle forKey:handle];
			++_listeners;
		}
		
		if (![_motionManager accelerometerData]) {
			++_listeners;
		}
	}
    if (_gyroAvailable) {
        [context sendResult:[self dictionaryFromDeviceMotion:[_motionManager deviceMotion]]];
    }
    else {
        [context sendResult:[self dictionaryFromAccelerometerData:[_motionManager accelerometerData]]];
    }
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
            if (_gyroAvailable) {
                [_motionManager stopDeviceMotionUpdates];
            }
            else {
                [_motionManager stopAccelerometerUpdates];
                [_locationManager stopUpdatingHeading];
            }
            
		}		
	}
	
	[context sendResult];
}
@end
