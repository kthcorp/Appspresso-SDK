//
//  KthWaikikiAccelerometer.h
//  accelerometer-ios
//                                                                                                                                                                                                                                                                             
//  Copyright (c) 2012 KTH Corp.
// 

#import <Foundation/Foundation.h>
#import <CoreMotion/CoreMotion.h>

#import "DefaultAxPlugin.h"

@interface KthWaikikiAccelerometer : DefaultAxPlugin {
	NSInteger _listeners;
	//UIAcceleration *_currentAcceleration;
	NSMutableDictionary *_watchHandles;
    NSObject<AxPluginContext> *_contextGetCurrentAcceleration;
    CMMotionManager *_motionManager;
}
- (void)getCurrentAcceleration:(id<AxPluginContext>)context;
- (void)clearWatch:(id<AxPluginContext>)context;
@end
