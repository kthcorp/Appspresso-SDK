//
//  AppspressoResponse.m
//  sail-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import "AppspressoResponse.h"
#import "DefaultWidgetAgent.h"
#import "HydraWebServer.h"
#import "AxConfig.h"
#import "AxSessionKeyHolder.h"
#import "MimeTypeUtils.h"
#import "AxLog.h"

@implementation AppspressoResponse
@synthesize widgetAgent = _widgetAgent;
@synthesize connection = _connection;
@synthesize data = _data;
@synthesize status = _status;
@synthesize httpHeaders = _httpHeaders;

// =====================from +Private
#pragma mark -

- (id)initWithWidgetAgent:(DefaultWidgetAgent*)widgetAgent connection:(HydraConnection*)connection {
    if ((self = [self init])) {
        _widgetAgent = [widgetAgent retain];
        _connection = [connection retain];
        _data = nil;
        _status = 200;
        _httpHeaders = [[NSMutableDictionary alloc] init];
        _developMode = [AxConfig getAttributeAsBoolean:@"app.devel" defaultValue:NO];
    }
    return self;
}

- (void)dealloc
{
    [_httpHeaders release];
    [_data release];
	[_connection release];
    [_widgetAgent release];
	[super dealloc];
}

#pragma mark -

- (UInt64)contentLength
{
	UInt64 result = (UInt64)[_data length];
	
	//AX_LOG_TRACE(@"%s: contentLength - %llu", __PRETTY_FUNCTION__, result);
	
	return result;
}

- (UInt64)offset
{
	return _offset;
}

- (void)setOffset:(UInt64)offsetParam
{
	//AX_LOG_TRACE(@"%s: setOffset:%llu", __PRETTY_FUNCTION__, _offset);
	
	_offset = (NSUInteger)offsetParam;
}

- (NSData *)readDataOfLength:(NSUInteger)lengthParameter
{
	//AX_LOG_TRACE(@"%s: readDataOfLength:%lu", __PRETTY_FUNCTION__, (unsigned long)lengthParameter);
	
	NSUInteger remaining = [_data length] - _offset;
	NSUInteger length = lengthParameter < remaining ? lengthParameter : remaining;
	
	void *bytes = (void *)([_data bytes] + _offset);
	
	_offset += length;
	
	return [NSData dataWithBytesNoCopy:bytes length:length freeWhenDone:NO];
}

- (BOOL)isDone
{
	BOOL result = (_offset == [_data length]);

	//AX_LOG_TRACE(@"%s: isDone - %@", __PRETTY_FUNCTION__, (result ? @"YES" : @"NO"));
	
    return result;
}

- (void)replyResponse:(id)response
{
	[self setData:response];
	[_connection responseHasAvailableData:self];
}

- (BOOL)delayResponeHeaders
{
	return _data == nil;
}

- (NSString*)getClientSessionId {
    NSString *raw = [_connection getRequestHeaderValueForKey:@"Cookie"];
    if (raw == nil)
        return @"[not exist]";

    NSArray *cookies = [raw componentsSeparatedByString:@";"];
    for (NSString *cookie in cookies) {
        NSArray *pair = [cookie componentsSeparatedByString:@"="];
        NSString *name = [[pair objectAtIndex:0] stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]];
        if ([name compare:@"AXSESSIONID"] == NSOrderedSame) {
            return [[pair objectAtIndex:1] stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]];
        }
    }
    return @"[not exist]";
}

- (void)handleRequestForApi:(NSString *)name value:(NSString *)value
{
    if (_developMode || [[self getClientSessionId] compare:[[AxSessionKeyHolder instance] key]] == NSOrderedSame) {
        [self specificHandlerForApi:name value:value];
        return;
    }

    [self replyError:403 withMessage:@"403 Forbidden"];
}

- (NSDictionary*)queryParametersMapfromUrl:(NSURL*)url {
    NSMutableDictionary* map = [[NSMutableDictionary new] autorelease];

    NSString* query = [url query];
    NSArray* params = [query componentsSeparatedByString:@"&"];
    for (NSString* component in params) {
        NSArray* pair = [component componentsSeparatedByString:@"="];
        [map setObject:[pair objectAtIndex:1] forKey:[pair objectAtIndex:0]];
    }

    return map;
}

- (void)replyStringResponse:(NSString*)string {
    [self replyResponse:[string dataUsingEncoding:NSUTF8StringEncoding]];
}

- (void)replyError:(NSInteger)code withMessage:(NSString*)message {
    self.status = code;
    [self setContentType:MIME_TYPE_TEXT];
    [self replyStringResponse:message];
}

- (void)specificHandlerForApi:(NSString *)name value:(NSString *)value {
    // should override this method
    [self doesNotRecognizeSelector:_cmd];
}

#pragma mark -

- (void)setHeaderValue:(NSString*)value forName:(NSString*)name {
    [_httpHeaders setObject:value forKey:name];
}

- (void)setContentType:(NSString*)contentType {
    [self setHeaderValue:contentType forName:@"Content-Type"];
}

- (void)setCacheHeader {
    if ([self isLocalConnection]) {
        // 1-year expire :)
        [self setHeaderValue:@"public, max-age=31557600" forName:@"Cache-Control"];
        return;
    }

    // ADE 모드에서 크롬이 접속할 경우 캐쉬하지 못하게 막는다.
    [self setNoCacheHeader];
}

- (void)setNoCacheHeader {
    [self setHeaderValue:@"0" forName:@"Expires"];
    [self setHeaderValue:@"no-cache" forName:@"Pragma"];
    [self setHeaderValue:@"no-store, no-cache, must-revalidate" forName:@"Cache-Control"];
}

// UA를 가지고 UIWebView에서 접속했는지, 외부(i.e. ADE chrome extension)에서 접속했는지 판단한다.
- (BOOL)isLocalConnection {
    NSString *ua = [self.connection getRequestHeaderValueForKey:@"User-Agent"];
    if (ua == nil
        || [ua rangeOfString:@"AppleWebKit"].location == NSNotFound
        || [ua rangeOfString:@"Mobile"].location == NSNotFound)
        return NO;

    return YES;
}

@end
