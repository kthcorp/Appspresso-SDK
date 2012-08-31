//
//  AppspressoResponse.h
//  sail-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import <Foundation/Foundation.h>
#import "HTTPResponse.h"

@class DefaultWidgetAgent;
@class HydraConnection;
@class HTTPMessage;

@interface AppspressoResponse : NSObject <HTTPResponse>
{
@private
    DefaultWidgetAgent *_widgetAgent;
	HydraConnection *_connection;
	NSData *_data;
	NSUInteger _offset;
    NSInteger _status;
    NSMutableDictionary *_httpHeaders;
    BOOL _developMode;
}
@property (nonatomic,retain) DefaultWidgetAgent *widgetAgent;
@property (nonatomic,retain) HydraConnection *connection;
@property (nonatomic,retain) NSData *data;
@property (nonatomic,assign) NSInteger status;
@property (nonatomic,retain) NSMutableDictionary* httpHeaders;

- (id)initWithWidgetAgent:(DefaultWidgetAgent*)widgetAgent connection:(HydraConnection*)connection;
-(void)setHeaderValue:(NSString*)value forName:(NSString*)name;
-(void)setContentType:(NSString*)contentType;
-(void)setCacheHeader;
-(void)setNoCacheHeader;
-(BOOL)isLocalConnection;

- (void)handleRequestForApi:(NSString *)name value:(NSString *)value;
- (NSDictionary*)queryParametersMapfromUrl:(NSURL*)url;
- (void)replyStringResponse:(NSString*)string;
- (void)replyError:(NSInteger)code withMessage:(NSString*)message;
// Override
- (void)replyResponse:(id)response;
- (void)specificHandlerForApi:(NSString *)name value:(NSString *)value;
@end