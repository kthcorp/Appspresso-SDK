//
//  MulticastApplicationDelegate.m
//  sail-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import "MulticastApplicationDelegate.h"


@implementation MulticastApplicationDelegate

- (id)init {
    self = [super init];
    if(self) {
        _delegates = [[NSMutableArray alloc] init];
        _addDelegates = [[NSMutableArray alloc] init];
        _removeDelegates = [[NSMutableArray alloc] init];
    }
    return self;
}

- (void)dealloc {
    [_delegates release];
    [_addDelegates release];
    [_removeDelegates release];
    [super dealloc];
}

- (void)addApplicationDelegate:(id)delegate {
    if([delegate conformsToProtocol:@protocol(UIApplicationDelegate)]) {
        [_addDelegates addObject:delegate];
    }
}

- (void)removeApplicationDelegate:(id)delegate {
    if([delegate conformsToProtocol:@protocol(UIApplicationDelegate)]) {
        [_removeDelegates addObject:delegate];    
    }
}

- (void)syncApplicationDelegate {
    [_delegates addObjectsFromArray:_addDelegates];
    [_addDelegates removeAllObjects];
    [_delegates removeObjectsInArray:_removeDelegates];
    [_removeDelegates removeAllObjects];
}

#pragma UIApplicationDelegate

- (void)application:(UIApplication *)application didChangeStatusBarFrame:(CGRect)oldStatusBarFrame {
    for(id delegate in _delegates) {
        if([delegate respondsToSelector:@selector(application:didChangeStatusBarFrame:)]) {
            [delegate application:application didChangeStatusBarFrame:oldStatusBarFrame];
        }
    }
    [self syncApplicationDelegate];
}

- (void)application:(UIApplication *)application didChangeStatusBarOrientation:(UIInterfaceOrientation)oldStatusBarOrientation {
    for(id delegate in _delegates) {
        if([delegate respondsToSelector:@selector(application:didChangeStatusBarOrientation:)]) {
            [delegate application:application didChangeStatusBarOrientation:oldStatusBarOrientation];
        }
    }
    [self syncApplicationDelegate];
}

- (void)application:(UIApplication *)application didFailToRegisterForRemoteNotificationsWithError:(NSError *)error {
    for(id delegate in _delegates) {
        if([delegate respondsToSelector:@selector(application:didFailToRegisterForRemoteNotificationsWithError:)]) {
            [delegate application:application didFailToRegisterForRemoteNotificationsWithError:error];
        }
    }
    [self syncApplicationDelegate];
}

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    for(id delegate in _delegates) {
        if([delegate respondsToSelector:@selector(application:didFinishLaunchingWithOptions:)]) {
            if(![delegate application:application didFinishLaunchingWithOptions:launchOptions]) {
                [self syncApplicationDelegate];
                return NO;//XXX:yes or no?
            }
        }
    }
    // all delegates should returns YES!
    [self syncApplicationDelegate];
    return YES;//XXX:yes or no?
}

// ios 4.0
- (void)application:(UIApplication *)application didReceiveLocalNotification:(UILocalNotification *)notification {
    for(id delegate in _delegates) {
        if([delegate respondsToSelector:@selector(application:didReceiveLocalNotification:)]) {
            [delegate application:application didReceiveLocalNotification:notification];
        }
    }
    [self syncApplicationDelegate];
}

- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo {
    for(id delegate in _delegates) {
        if([delegate respondsToSelector:@selector(application:didReceiveRemoteNotification:)]) {
            [delegate application:application didReceiveRemoteNotification:userInfo];
        }
    }
    [self syncApplicationDelegate];
}

- (void)application:(UIApplication *)application didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken {
    for(id delegate in _delegates) {
        if([delegate respondsToSelector:@selector(application:didRegisterForRemoteNotificationsWithDeviceToken:)]) {
            [delegate application:application didRegisterForRemoteNotificationsWithDeviceToken:deviceToken];
        }
    }
    [self syncApplicationDelegate];
}

// deprecated
- (BOOL)application:(UIApplication *)application handleOpenURL:(NSURL *)url {
    for(id delegate in _delegates) {
        if([delegate respondsToSelector:@selector(application:handleOpenURL:)]) {
            if([delegate application:application handleOpenURL:url]) {
                [self syncApplicationDelegate];
                return YES;//XXX:yes or no?
            }
        }
    }
    [self syncApplicationDelegate];
    return NO;//XXX:yes or no?
}

// ios 4.2
- (BOOL)application:(UIApplication *)application openURL:(NSURL *)url sourceApplication:(NSString *)sourceApplication annotation:(id)annotation {
    for(id delegate in _delegates) {
        if([delegate respondsToSelector:@selector(application:handleOpenURL:)]) {
            if([delegate application:application openURL:url sourceApplication:sourceApplication annotation:annotation]) {
                [self syncApplicationDelegate];
                return YES;//XXX:yes or no?
            }
        }
    }
    [self syncApplicationDelegate];
    return NO;//XXX:yes or no?
}

- (void)application:(UIApplication *)application willChangeStatusBarFrame:(CGRect)newStatusBarFrame {
    for(id delegate in _delegates) {
        if([delegate respondsToSelector:@selector(application:willChangeStatusBarFrame:)]) {
            [delegate application:application willChangeStatusBarFrame:newStatusBarFrame];
        }
    }
    [self syncApplicationDelegate];
}

- (void)application:(UIApplication *)application willChangeStatusBarOrientation:(UIInterfaceOrientation)newStatusBarOrientation duration:(NSTimeInterval)duration {
    for(id delegate in _delegates) {
        if([delegate respondsToSelector:@selector(application:willChangeStatusBarOrientation:duration:)]) {
            [delegate application:application willChangeStatusBarOrientation:newStatusBarOrientation duration:duration];
        }
    }
    [self syncApplicationDelegate];
}

- (void)applicationDidBecomeActive:(UIApplication *)application {
    for(id delegate in _delegates) {
        if([delegate respondsToSelector:@selector(applicationDidBecomeActive:)]) {
            [delegate applicationDidBecomeActive:application];
        }
    }
    [self syncApplicationDelegate];
}

// ios 4.0
- (void)applicationDidEnterBackground:(UIApplication *)application {
    for(id delegate in _delegates) {
        if([delegate respondsToSelector:@selector(applicationDidEnterBackground:)]) {
            [delegate applicationDidEnterBackground:application];
        }
    }
    [self syncApplicationDelegate];
}

- (void)applicationDidFinishLaunching:(UIApplication *)application {
    for(id delegate in _delegates) {
        if([delegate respondsToSelector:@selector(applicationDidFinishLaunching:)]) {
            [delegate applicationDidFinishLaunching:application];
        }
    }
    [self syncApplicationDelegate];
}

- (void)applicationDidReceiveMemoryWarning:(UIApplication *)application {
    for(id delegate in _delegates) {
        if([delegate respondsToSelector:@selector(applicationDidReceiveMemoryWarning:)]) {
            [delegate applicationDidReceiveMemoryWarning:application];
        }
    }
    [self syncApplicationDelegate];
}

// ios 4.0
- (void)applicationProtectedDataDidBecomeAvailable:(UIApplication *)application {
    for(id delegate in _delegates) {
        if([delegate respondsToSelector:@selector(applicationProtectedDataDidBecomeAvailable:)]) {
            [delegate applicationProtectedDataDidBecomeAvailable:application];
        }
    }
    [self syncApplicationDelegate];
}

// ios 4.0
- (void)applicationProtectedDataWillBecomeUnavailable:(UIApplication *)application {
    for(id delegate in _delegates) {
        if([delegate respondsToSelector:@selector(applicationProtectedDataWillBecomeUnavailable:)]) {
            [delegate applicationProtectedDataWillBecomeUnavailable:application];
        }
    }
    [self syncApplicationDelegate];
}

- (void)applicationSignificantTimeChange:(UIApplication *)application {
    for(id delegate in _delegates) {
        if([delegate respondsToSelector:@selector(applicationSignificantTimeChange:)]) {
            [delegate applicationSignificantTimeChange:application];
        }
    }
    [self syncApplicationDelegate];
}

- (void)applicationWillEnterForeground:(UIApplication *)application {
    for(id delegate in _delegates) {
        if([delegate respondsToSelector:@selector(applicationWillEnterForeground:)]) {
            [delegate applicationWillEnterForeground:application];
        }
    }
    [self syncApplicationDelegate];
}

- (void)applicationWillResignActive:(UIApplication *)application {
    for(id delegate in _delegates) {
        if([delegate respondsToSelector:@selector(applicationWillResignActive:)]) {
            [delegate applicationWillResignActive:application];
        }
    }
    [self syncApplicationDelegate];
}

- (void)applicationWillTerminate:(UIApplication *)application {
    for(id delegate in _delegates) {
        if([delegate respondsToSelector:@selector(applicationWillTerminate:)]) {
            [delegate applicationWillTerminate:application];
        }
    }
    [self syncApplicationDelegate];
}

@end
