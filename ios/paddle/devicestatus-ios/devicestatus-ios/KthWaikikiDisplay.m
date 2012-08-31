//
//  KthWaikikiDisplay.m
//  devicestatus-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import "Aspects.h"
#import "KthWaikikiAspectWatcher.h"

# pragma mark KthWaikikiDisplayAspectResolutionHeight
@interface KthWaikikiDisplayAspectResolutionHeight : KthWaikikiAspectWatcher

@end

@implementation KthWaikikiDisplayAspectResolutionHeight
- (id)getValue {
    //return [NSNumber numberWithFloat:[[UIScreen mainScreen]applicationFrame].size.height];
    return [NSNumber numberWithFloat:[UIApplication sharedApplication].keyWindow.frame.size.height];
}


- (NSString *)propertyName {
	return kDisplayPropertyResolutionHeight;
}
@end

# pragma mark KthWaikikiDisplayAspectResolutionWidth
@interface KthWaikikiDisplayAspectResolutionWidth : KthWaikikiAspectWatcher

@end

@implementation KthWaikikiDisplayAspectResolutionWidth
- (id)getValue {
    return [NSNumber numberWithFloat:[UIApplication sharedApplication].keyWindow.frame.size.width];
}


- (NSString *)propertyName {
	return kDisplayPropertyResolutionWidth;
}

@end

# pragma mark KthWaikikiDisplayAspectPixelAspectRatio
@interface KthWaikikiDisplayAspectPixelAspectRatio : KthWaikikiAspectWatcher

@end

@implementation KthWaikikiDisplayAspectPixelAspectRatio
- (id)getValue {
    float width = [UIApplication sharedApplication].keyWindow.frame.size.width;
    float height = [UIApplication sharedApplication].keyWindow.frame.size.height;
    return [NSNumber numberWithFloat:(height >= width) ? height/width : width/height];
}


- (NSString *)propertyName {
	return kDisplayPropertyPixelAspectRatio;
}

@end

