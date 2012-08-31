//
//  KthWaikikiCamera.m
//  cameramanager-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import "KthWaikikiCamera.h"

static NSMutableDictionary *gCameras;

@interface KthWaikikiCamera ()
@property (nonatomic, retain) NSString *name;
@end


@implementation KthWaikikiCamera
@synthesize name = _name;
- (id)initWithAVCaptureDevice:(AVCaptureDevice *)device {
	if (self = [super init]) {
		NSString *name;
		NSInteger position;
#if TARGET_OS_EMBEDDED	
		name = [device localizedName];
		position = [device position];
		[self setName:name];
#else
		name = @"SimulatorCamera";
		[self setName:name];
		position = 0;
#endif
		if (gCameras == nil) {
			gCameras = [[NSMutableDictionary alloc] init];
		}
		[gCameras setObject:[NSNumber numberWithInt:position] forKey:name];
	}
	return self;
}

- (NSDictionary*)returnData {
	NSMutableDictionary *res = [NSMutableDictionary dictionaryWithObjectsAndKeys:
								_name, @"id",
								nil];
	return res;
}

- (void)dealloc {
	[self setName:nil];
	
	[super dealloc];
}

+ (NSInteger)positionFromName:(NSString *)name {
	if (nil == gCameras) {
		return -1;
	}
	
	return [[gCameras objectForKey:name] intValue];
}
@end
