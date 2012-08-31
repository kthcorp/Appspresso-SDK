//
//  MulticastViewControllerDelegate.h
//  sail-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

#import "AxViewControllerDelegate.h"

@interface MulticastViewControllerDelegate : NSObject<AxViewControllerDelegate> {
@private
NSMutableArray *_delegates;
}

- (void)addViewControllerDelegate:(id)delegate;
- (void)removeViewControllerDelegate:(id)delegate;

@end
