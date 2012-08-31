//
//  DefaultWidgetAgent.h
//  sail-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

#import "WidgetAgent.h"
#import "AxViewControllerDelegate.h"

@class AxApplicationDelegate;
@class DefaultPluginManager;
@class DefaultFileSystemManager;
@class HydraWebServer;
@class DefaultRuntimeContext;
@class DefaultW3Widget;
@class AuthenticateCookiePlanter;

@interface DefaultWidgetAgent : NSObject<WidgetAgent,UIApplicationDelegate,AxViewControllerDelegate> {
@private
    AxApplicationDelegate *_applicationDelegate;
    DefaultPluginManager *_pluginManager;
    DefaultFileSystemManager *_fileSystemManager;
    HydraWebServer *_server;
    DefaultRuntimeContext *_runtimeContext;
    DefaultW3Widget *_widget;
    AuthenticateCookiePlanter *_authCookiePlanter;
    
    BOOL _interfaceOrientationPortrait;
    BOOL _interfaceOrientationLandscapeLeft;
    BOOL _interfaceOrientationLandscapeRight;
    BOOL _interfaceOrientationPortraitUpsideDown;
}

@property (nonatomic,readonly,retain) AxApplicationDelegate *applicationDelegate;
@property (nonatomic,readonly,retain) DefaultPluginManager *pluginManager;
@property (nonatomic,readonly,retain) DefaultFileSystemManager *fileSystemManager;
@property (nonatomic,readonly,retain) HydraWebServer *server;
@property (nonatomic,readonly,retain) DefaultRuntimeContext *runtimeContext;
@property (nonatomic,readonly,retain) DefaultW3Widget *widget;

- (id)initWithApplicationDelegate:(AxApplicationDelegate*)applicationDelegate;

@end
