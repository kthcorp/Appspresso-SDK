//
//  AxApplicationDelegate.m
//  sail-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import "AxApplicationDelegate.h"
#import "MulticastApplicationDelegate.h"
#import "AxViewController.h"
#import "DefaultWidgetAgent.h"
#import "AxLog.h"

@implementation AxApplicationDelegate

@synthesize multicastApplicationDelegate = _multicastApplicationDelegate;
@synthesize window = _window;
@synthesize viewController = _viewController;
@synthesize widgetAgent = _widgetAgent;

- (id) init {
    if ((self = [super init])) {
        _multicastApplicationDelegate = [[MulticastApplicationDelegate alloc] init];
        
        // create window
        _window = [[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
        _window.backgroundColor = [UIColor whiteColor];
        
        // create viewController and webView
        _viewController = [[AxViewController alloc] initWithApplicationDelegate:self];
        [_window addSubview:_viewController.view];
        [_window makeKeyAndVisible];
        
        // create widget Agent
        _widgetAgent = [[DefaultWidgetAgent alloc] initWithApplicationDelegate:self];
        
        [_multicastApplicationDelegate addApplicationDelegate:_viewController];
        [_multicastApplicationDelegate syncApplicationDelegate];
    }
    return self;
}

- (void)dealloc {
    [_widgetAgent release];
    [_viewController release];
    [_window release];
    [_multicastApplicationDelegate release];
    [super dealloc];
}

#pragma UIApplicationDelegate

//
// Monitoring Application State Changes
//

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    AX_LOG_TRACE(@">>>>>>>>>> %s", __PRETTY_FUNCTION__);
    return [_multicastApplicationDelegate application:application didFinishLaunchingWithOptions:launchOptions];
}

- (void)applicationDidBecomeActive:(UIApplication *)application {
    AX_LOG_TRACE(@">>>>>>>>>> %s", __PRETTY_FUNCTION__);
    [_multicastApplicationDelegate applicationDidBecomeActive:application];
}

- (void)applicationWillResignActive:(UIApplication *)application {
    AX_LOG_TRACE(@">>>>>>>>>> %s", __PRETTY_FUNCTION__);
    [_multicastApplicationDelegate applicationWillResignActive:application];
}

// ios 4.0
- (void)applicationDidEnterBackground:(UIApplication *)application {
    AX_LOG_TRACE(@">>>>>>>>>> %s", __PRETTY_FUNCTION__);
    [_multicastApplicationDelegate applicationDidEnterBackground:application];
}

// ios 4.0
- (void)applicationWillEnterForeground:(UIApplication *)application {
    AX_LOG_TRACE(@">>>>>>>>>> %s", __PRETTY_FUNCTION__);
    [_multicastApplicationDelegate applicationWillEnterForeground:application];
}

- (void)applicationWillTerminate:(UIApplication *)application {
    AX_LOG_TRACE(@">>>>>>>>>> %s", __PRETTY_FUNCTION__);
    [_multicastApplicationDelegate applicationWillTerminate:application];
}

- (void)applicationDidFinishLaunching:(UIApplication *)application {
    AX_LOG_TRACE(@">>>>>>>>>> %s", __PRETTY_FUNCTION__);
    [_multicastApplicationDelegate applicationDidFinishLaunching:application];
}

//
// Opening a URL Resource
//

// deprecated
- (BOOL)application:(UIApplication *)application handleOpenURL:(NSURL *)url {
    AX_LOG_TRACE(@">>>>>>>>>> %s", __PRETTY_FUNCTION__);
    return [_multicastApplicationDelegate application:application handleOpenURL:url];
}

// ios 4.2
- (BOOL)application:(UIApplication *)application openURL:(NSURL *)url sourceApplication:(NSString *)sourceApplication annotation:(id)annotation {
    AX_LOG_TRACE(@">>>>>>>>>> %s", __PRETTY_FUNCTION__);
    return [_multicastApplicationDelegate application:application openURL:url sourceApplication:sourceApplication annotation:annotation];
}

//
// Managing Status Bar Changes
//

- (void)application:(UIApplication *)application willChangeStatusBarOrientation:(UIInterfaceOrientation)newStatusBarOrientation duration:(NSTimeInterval)duration {
    AX_LOG_TRACE(@">>>>>>>>>> %s", __PRETTY_FUNCTION__);
    [_multicastApplicationDelegate application:application willChangeStatusBarOrientation:newStatusBarOrientation duration:duration];
}

- (void)application:(UIApplication *)application didChangeStatusBarOrientation:(UIInterfaceOrientation)oldStatusBarOrientation {
    AX_LOG_TRACE(@">>>>>>>>>> %s", __PRETTY_FUNCTION__);
    [_multicastApplicationDelegate application:application didChangeStatusBarOrientation:oldStatusBarOrientation];
}

- (void)application:(UIApplication *)application willChangeStatusBarFrame:(CGRect)newStatusBarFrame {
    AX_LOG_TRACE(@">>>>>>>>>> %s", __PRETTY_FUNCTION__);
    [_multicastApplicationDelegate application:application willChangeStatusBarFrame:newStatusBarFrame];
}

- (void)application:(UIApplication *)application didChangeStatusBarFrame:(CGRect)oldStatusBarFrame {
    AX_LOG_TRACE(@">>>>>>>>>> %s", __PRETTY_FUNCTION__);
    [_multicastApplicationDelegate application:application didChangeStatusBarFrame:oldStatusBarFrame];
}

//
// Responding to System Notifications
//

- (void)applicationDidReceiveMemoryWarning:(UIApplication *)application {
    AX_LOG_TRACE(@">>>>>>>>>> %s", __PRETTY_FUNCTION__);
    [_multicastApplicationDelegate applicationDidReceiveMemoryWarning:application];
}

- (void)applicationSignificantTimeChange:(UIApplication *)application {
    AX_LOG_TRACE(@">>>>>>>>>> %s", __PRETTY_FUNCTION__);
    [_multicastApplicationDelegate applicationSignificantTimeChange:application];
}

//
// Handling Remote Notifications
//

- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo {
    AX_LOG_TRACE(@">>>>>>>>>> %s", __PRETTY_FUNCTION__);
    [_multicastApplicationDelegate application:application didReceiveRemoteNotification:userInfo];
}

- (void)application:(UIApplication *)application didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken {
    AX_LOG_TRACE(@">>>>>>>>>> %s", __PRETTY_FUNCTION__);
    [_multicastApplicationDelegate application:application didRegisterForRemoteNotificationsWithDeviceToken:deviceToken];
}

- (void)application:(UIApplication *)application didFailToRegisterForRemoteNotificationsWithError:(NSError *)error {
    AX_LOG_TRACE(@">>>>>>>>>> %s", __PRETTY_FUNCTION__);
    [_multicastApplicationDelegate application:application didFailToRegisterForRemoteNotificationsWithError:error];
}

//
// Handling Local Notifications
//

// ios 4.0
- (void)application:(UIApplication *)application didReceiveLocalNotification:(UILocalNotification *)notification {
    AX_LOG_TRACE(@">>>>>>>>>> %s", __PRETTY_FUNCTION__);
    [_multicastApplicationDelegate application:application didReceiveLocalNotification:notification];
}

//
// Responding to Content Protection Changes
//

// ios 4.0
- (void)applicationProtectedDataWillBecomeUnavailable:(UIApplication *)application {
    AX_LOG_TRACE(@">>>>>>>>>> %s", __PRETTY_FUNCTION__);
    [_multicastApplicationDelegate applicationProtectedDataWillBecomeUnavailable:application];
}

// ios 4.0
- (void)applicationProtectedDataDidBecomeAvailable:(UIApplication *)application {
    AX_LOG_TRACE(@">>>>>>>>>> %s", __PRETTY_FUNCTION__);
    [_multicastApplicationDelegate applicationProtectedDataDidBecomeAvailable:application];
}

@end
