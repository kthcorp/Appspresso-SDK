//
//  KthWaikikiAspectWatcher.m
//  devicestatus-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import "KthWaikikiAspectWatcher.h"

#import "AxLog.h"

#define kDefaultMinNotificationInterval 40

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
@interface KthWaikikiAspectWatcher ()
@end

@implementation KthWaikikiAspectWatcher
@synthesize started = _started;

- (id)init {
	if (nil != (self = [super init])) {
		_started = NO;
	}
    return self;
}

- (void)start {
	_started = YES;
	[self prepare];
}

- (void)prepare {
}

- (id)getValue {
	return nil;
}

- (void)stop {
    _started = NO;
	[self clear];
}
- (void)clear {
}

- (void)dealloc {
	[super dealloc];
}

- (NSString *)propertyName {
	// OVerride
	return nil;
}
@end