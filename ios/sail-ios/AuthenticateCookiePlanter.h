//
//  AuthenticateCookiePlanter.h
//  sail-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

@interface AuthenticateCookiePlanter : NSObject<UIApplicationDelegate> {
    @private
    NSString *_host;
    NSString *_port;
}

- (id)initWithHost:(NSString*)host port:(int)port;

@end
