//
//  HydraWebServer.h
//  sail-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

#import "HTTPServer.h"
#import "HTTPConnection.h"

#import "WebServer.h"
#import "AppspressoResponse.h"

@class DefaultWidgetAgent;
@class HTTPServer;
@class AppspressoResponse;

//=======================================================

/**
 * CocoaHttpServer를 사용한 내장 웹서버 구현체.
 *
 * @see http://code.google.com/p/cocoahttpserver/
 * @see android 런타임의 KrakenWebServer
 */
@interface HydraWebServer : NSObject<WebServer, UIApplicationDelegate> {
@private
    DefaultWidgetAgent *_widgetAgent;
    HTTPServer *_hydra;
    NSString *_host;
    int _port;
    NSMutableDictionary *_handlers;
    NSMutableArray *_handlersInOrder;
    NSString *_startPage;
}

@property (nonatomic,assign,readonly) NSString *startPage;

- (id)initWithWidgetAgent:(DefaultWidgetAgent*)widgetAgent;

-(void)start;

-(HydraWebServer*)generateAuthPageWithStartPage:(NSString*)startPage;
-(NSString*)getAuthPageUrl;

@end

//=======================================================

/**
 * 요청을 받아서 HydraWebServer로 처리를 위임해서 처리하는 구현체...
 */
@interface HydraConnection : HTTPConnection

@property (nonatomic,retain,readonly) HTTPMessage* request;

-(NSString*)requestMethod;
-(NSData*)requestBody;
-(NSString*)requestBodyAsString;
-(NSString*)getRequestHeaderValueForKey:(NSString*)key;

@end
