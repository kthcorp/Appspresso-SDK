//
//  KthWaikikiMemoryUnitAspect.m
//  devicestatus-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import <Foundation/Foundation.h>
#import "Aspects.h"
#import "KthWaikikiAspectWatcher.h"

@interface KthWaikikiMemoryUnitAspectSize : KthWaikikiAspectWatcher
@end


@implementation KthWaikikiMemoryUnitAspectSize

- (id)getValue {
    NSDictionary *fsAttr = [[NSFileManager defaultManager] attributesOfFileSystemForPath:NSHomeDirectory() error:nil];
    return [fsAttr objectForKey:NSFileSystemSize];
}


- (NSString *)propertyName {
	return kMemoryUnitPropertySize;
}

@end


@interface KthWaikikiMemoryUnitAspectAvailableSize : KthWaikikiAspectWatcher 
@end


@implementation KthWaikikiMemoryUnitAspectAvailableSize
- (id)getValue {
    NSDictionary *fsAttr = [[NSFileManager defaultManager] attributesOfFileSystemForPath:NSHomeDirectory() error:nil];
    return [fsAttr objectForKey:NSFileSystemFreeSize];
}


- (NSString *)propertyName {
	return kMemoryUnitPropertyAvailableSize;
}
@end
