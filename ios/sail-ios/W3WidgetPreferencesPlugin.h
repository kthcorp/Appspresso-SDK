//
//  W3WidgetPreferencesPlugin.h
//  sail-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import <Foundation/Foundation.h>

#import "AxPlugin.h"
#import "AxRuntimeContext.h"
#import "W3Widget.h"

@protocol AxRuntimeContext;
@protocol W3Widget;
@protocol W3Storage;

@interface W3WidgetPreferencesPlugin : NSObject<AxPlugin, UIApplicationDelegate>
{
@private
    id<W3Widget> _widget;
}

@end
