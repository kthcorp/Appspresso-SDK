//
//  MulticastWebViewDelegate.h
//  sail-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import <UIKit/UIKit.h>


/**
 * This interface will delegate all invocations on an UIWebViewDelegate to
 * multiple delegates(aka multicast listener)
 */
@interface MulticastWebViewDelegate : NSObject<UIWebViewDelegate> {
@private
    NSMutableArray *_delegates;
}

- (void)addWebViewDelegate:(id)delegate;
- (void)removeWebViewDelegate:(id)delegate;

@end
