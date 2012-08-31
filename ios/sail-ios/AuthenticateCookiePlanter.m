//
//  AuthenticateCookiePlanter.m
//  sail-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import "AuthenticateCookiePlanter.h"
#import "AxSessionKeyHolder.h"

@implementation AuthenticateCookiePlanter

- (id)initWithHost:(NSString*)host port:(int)port {
    if (self = [super init]) {
        _host = [host retain];
        _port = [[NSString stringWithFormat:@"%d", port] retain];
    }

    return self;
}

- (void)dealloc {
    [_host release];
    [_port release];
    [super dealloc];
}

- (void)applicationDidBecomeActive:(UIApplication *)application {
    [[NSHTTPCookieStorage sharedHTTPCookieStorage] setCookieAcceptPolicy:NSHTTPCookieAcceptPolicyAlways];

    NSMutableDictionary *cookieProperties = [NSMutableDictionary dictionary];
    [cookieProperties setObject:@"AXSESSIONID" forKey:NSHTTPCookieName];
    [cookieProperties setObject:[[AxSessionKeyHolder instance] generate] forKey:NSHTTPCookieValue];
    [cookieProperties setObject:_host forKey:NSHTTPCookieDomain];
    [cookieProperties setObject:_port forKey:NSHTTPCookiePort];
    [cookieProperties setObject:@"/" forKey:NSHTTPCookiePath];

    NSHTTPCookie *cookie = [NSHTTPCookie cookieWithProperties:cookieProperties];
    [[NSHTTPCookieStorage sharedHTTPCookieStorage] setCookie:cookie];
}

@end
