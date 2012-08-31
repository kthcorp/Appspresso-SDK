//
//  MulticastApplicationDelegate.h
//  sail-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import <UIKit/UIKit.h>


/**
 * This interface will delegate all invocations on an UIApplicationDelagate
 * to multiple delegates(aka multicast listener)
 */
@interface MulticastApplicationDelegate : NSObject<UIApplicationDelegate> {
@private
    NSMutableArray *_delegates;
    NSMutableArray *_addDelegates;
    NSMutableArray *_removeDelegates;
}

- (void)addApplicationDelegate:(id)delegate;
- (void)removeApplicationDelegate:(id)delegate;
- (void)syncApplicationDelegate;

@end
