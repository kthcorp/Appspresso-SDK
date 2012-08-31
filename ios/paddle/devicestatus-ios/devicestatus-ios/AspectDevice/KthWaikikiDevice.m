//
//  KthWaikikiDevice.m
//  devicestatus-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import <UIKit/UIKit.h>
#import "Aspects.h"
#import "KthWaikikiAspectWatcher.h"


#include <sys/types.h>
#include <sys/sysctl.h>


@interface KthWaikikiDeviceAspectModel : KthWaikikiAspectWatcher 
@end

@implementation KthWaikikiDeviceAspectModel

// http://stackoverflow.com/questions/448162/determine-device-iphone-ipod-touch-with-iphone-sdk
- (NSString *)platform {
    size_t size;
    sysctlbyname("hw.machine", NULL, &size, NULL, 0);
    char *machine = malloc(size);
    sysctlbyname("hw.machine", machine, &size, NULL, 0);
    NSString *platform = [NSString stringWithUTF8String:machine];
    free(machine);
    return platform;
}

- (id)getValue {
	return [self platform];
}

- (NSString *)propertyName {
	return kDevicePropertyModel;
}

- (void)dealloc {
	[super dealloc];
}
@end


#pragma mark - 

@interface KthWaikikiDeviceAspectVendor : KthWaikikiAspectWatcher 
@end

@implementation KthWaikikiDeviceAspectVendor


- (id)getValue {
	//TODO : find API
    return @"Apple, Inc";
}


- (NSString *)propertyName {
	return kDevicePropertyVendor;
}

- (void)dealloc {
	[super dealloc];
}
@end
