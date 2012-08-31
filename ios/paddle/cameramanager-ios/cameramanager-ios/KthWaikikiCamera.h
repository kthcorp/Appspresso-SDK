//
//  KthWaikikiCamera.h
//  cameramanager-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import <Foundation/Foundation.h>
#import "AVCamCaptureManager.h"

@class AVCaptureDevice;
@interface KthWaikikiCamera : NSObject {
@private
	NSString *_name;
}
- (id)initWithAVCaptureDevice:(AVCaptureDevice *)device;
- (NSDictionary*)returnData;
+ (NSInteger)positionFromName:(NSString *)name;
@end
