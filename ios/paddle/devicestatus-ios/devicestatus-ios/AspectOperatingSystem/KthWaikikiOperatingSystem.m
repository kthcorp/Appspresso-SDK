//
//  KthWaikikiOperationSystem.m
//  devicestatus-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import <UIKit/UIKit.h>
#import "Aspects.h"
#import "KthWaikikiAspectWatcher.h"


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark Language
@interface KthWaikikiOperatingSystemAspectLanguage : KthWaikikiAspectWatcher 
@end

@implementation KthWaikikiOperatingSystemAspectLanguage


- (id)getValue {
	return [[[NSUserDefaults standardUserDefaults] objectForKey:@"AppleLanguages"] objectAtIndex:0];//_language;
}

- (NSString *)propertyName {
	return kOperatingSystemPropertyLanguage;
}

- (void)dealloc {	
	[super dealloc];
}
@end

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark Version
@interface KthWaikikiOperatingSystemAspectVersion : KthWaikikiAspectWatcher
@end

@implementation KthWaikikiOperatingSystemAspectVersion

- (id)getValue {
	return [[UIDevice currentDevice] systemVersion];
}

- (NSString *)propertyName {
	return kOperatingSystemPropertyVersion;
}

- (void)dealloc {
	
	[super dealloc];
}
@end


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark Name
@interface KthWaikikiOperatingSystemAspectName : KthWaikikiAspectWatcher
@end

@implementation KthWaikikiOperatingSystemAspectName

- (id)getValue {
	return [[UIDevice currentDevice] systemName];
}
 
- (NSString *)propertyName {
	return kOperatingSystemPropertyName;
}

- (void)dealloc {
    [super dealloc];
}
@end


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark Vendor


@interface KthWaikikiOperatingSystemAspectVendor : KthWaikikiAspectWatcher
@end

@implementation KthWaikikiOperatingSystemAspectVendor

- (id)getValue {
	return @"Apple, Inc";
}

- (NSString *)propertyName {
	return kOperatingSystemPropertyVendor;
}
@end