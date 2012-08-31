//
//  KthWaikikiWacAspect.m
//  devicestatus-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import <Foundation/Foundation.h>
#import "Aspects.h"
#import "KthWaikikiAspectWatcher.h"

# pragma mark KthWaikikiWebRuntimeAspectWacVersion
@interface KthWaikikiWebRuntimeAspectWacVersion : KthWaikikiAspectWatcher
@end

@implementation KthWaikikiWebRuntimeAspectWacVersion
- (id)getValue {
    return @"WAC 2.0";
}


- (NSString *)propertyName {
	return kWebRuntimePropertyWacVersion;
}
@end

# pragma mark KthWaikikiWebRuntimeAspectSupportedImageFormats
@interface KthWaikikiWebRuntimeAspectSupportedImageFormats : KthWaikikiAspectWatcher
@end

@implementation KthWaikikiWebRuntimeAspectSupportedImageFormats
- (id)getValue {
    return @"gif, jpeg, png";
}


- (NSString *)propertyName {
	return kWebRuntimePropertySupportedImageFormats;
}
@end

# pragma mark KthWaikikiWebRuntimeAspectVersion
@interface KthWaikikiWebRuntimeAspectVersion : KthWaikikiAspectWatcher
@end

@implementation KthWaikikiWebRuntimeAspectVersion
- (id)getValue {
    return @"1.1.2";
}


- (NSString *)propertyName {
	return kWebRuntimePropertyVersion;
}
@end

# pragma mark KthWaikikiWebRuntimeAspectName
@interface KthWaikikiWebRuntimeAspectName : KthWaikikiAspectWatcher
@end

@implementation KthWaikikiWebRuntimeAspectName
- (id)getValue {
    return @"Appspresso";
}


- (NSString *)propertyName {
	return kWebRuntimePropertyName;
}
@end

# pragma mark KthWaikikiWebRuntimeAspectVendor
@interface KthWaikikiWebRuntimeAspectVendor : KthWaikikiAspectWatcher
@end

@implementation KthWaikikiWebRuntimeAspectVendor
- (id)getValue {
    return @"KTH";
}


- (NSString *)propertyName {
	return kWebRuntimePropertyVendor;
}
@end