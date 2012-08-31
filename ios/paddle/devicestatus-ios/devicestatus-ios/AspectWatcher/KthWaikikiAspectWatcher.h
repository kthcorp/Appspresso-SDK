//
//  KthWaikikiAspectWatcher.h
//  devicestatus-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import <Foundation/Foundation.h>

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
@interface KthWaikikiAspectWatcher : NSObject {
@private
	BOOL _started;
}
- (void)start;
- (void)stop;
- (void)clear;
- (id)getValue;
- (void)prepare;

- (NSString *)propertyName;

@property (nonatomic, assign, getter=isStarted, readonly) BOOL started;
@end
