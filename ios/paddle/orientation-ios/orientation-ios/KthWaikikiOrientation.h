//
//  KthWaikikiOrientation.h
//  orientation-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import <Foundation/Foundation.h>
#import <CoreMotion/CoreMotion.h>
#import <CoreLocation/CoreLocation.h>
#import "DefaultAxPlugin.h"

@interface KthWaikikiOrientation : DefaultAxPlugin<CLLocationManagerDelegate> {
	NSInteger _listeners;
	NSMutableDictionary *_watchHandles;
    CMMotionManager *_motionManager;
    CLLocationManager *_locationManager;
    BOOL _gyroAvailable;
    long _alpha;
    long _beta;
    long _gamma;
}
- (void)getCurrentOrientation:(id<AxPluginContext>)context;
- (void)clearWatch:(id<AxPluginContext>)context;
@end
