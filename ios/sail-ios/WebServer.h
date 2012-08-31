//
//  WebServer.h
//  sail-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import <Foundation/Foundation.h>

/**
 * 내장 웹서버.
 *
 * @see android 런타임의 WebServer
 */
@protocol WebServer

- (NSString*)getHost;
- (int)getPort;

@end
