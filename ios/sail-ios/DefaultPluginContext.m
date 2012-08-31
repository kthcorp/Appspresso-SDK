//
//  DefaultPluginContext.m
//  sail-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import "DefaultPluginContext.h"
#import "DefaultRuntimeContext.h"
#import "AppspressoPluginResponse.h"
#import "DefaultPluginManager.h"
#import "AxError.h"
#import "BridgeSessionManager.h"

#define kPluginResponseIdentifier @"id"
#define kPluginResponseResult @"result"
#define kPluginResponseError @"error"

@implementation DefaultPluginContext
@synthesize identifier = _identifier;
@synthesize prefix = _prefix;
@synthesize method = _method;
@synthesize params = _params;
@synthesize session = _session;
@synthesize result = _result;

- (id)initWithRequestJson:(NSDictionary*)json session:(BridgeSession*)session {
    if (self = [super init]) {
        self.identifier = [json objectForKey:@"id"];
        self.params = [json objectForKey:@"params"];
        self.session = session;
        _attrs = [NSMutableDictionary new];

        NSString* method = [json objectForKey:@"method"];
        NSRange r = [method rangeOfString:@"." options:NSBackwardsSearch];
        if (r.location != NSNotFound) {
            self.prefix = [method substringToIndex:r.location];
            self.method = [method substringFromIndex:r.location + 1];
            _malformed = self.prefix == nil ? YES : NO;
        } else {
            _malformed = YES;
        }
    }
    return self;
}

- (void)dealloc
{
    // retain properties
    self.identifier = nil;
    self.prefix = nil;
    self.method = nil;
    self.params = nil;
    self.session = nil;

    [_result release];
    [_attrs release];

	[super dealloc];
}

//- (oneway void)release {
//    int c = [self retainCount];
//    NSLog(@"plugin context release %d -> %d", c, c - 1);
//
//    [super release];
//}
//
//- (id)retain {
//    int c = [self retainCount];
//    NSLog(@"plugin context retain %d -> %d", c, c + 1);
//
//    return [super retain];
//}

- (BOOL)isMalformedRequest {
    return _malformed;
}

#pragma mark AxPluginContext

-(NSNumber*)getId {
    return _identifier;
}

-(NSString*)getMethod {
    return _method;
}

-(NSString*)getPrefix {
    return _prefix;
}

-(NSArray*)getParams{
    return _params;
}

#pragma mark AxPluginContext _ getParam extention series

// TODO: 좀 더 정교한 에러처리!!!
// 일단, boolean 형 파라메터가 무조건 기본값으로 매핑되는 버그만 고침.
// 기본값이 없는 유형의 함수들은: 유효한 값이 아니면 나름대로 형변환 따위를 시도하거나 나름대로 기본값을 반환하지 말고, 무조건 에러를 던져야 함.
// 기본값이 있는 유형의 함수들은: 유효한 값이 아니더라도 절대 에러를 던지지 말고, 기본값을 반환해야 함.
// [NSString integerValue] 등의 함수는 나름대로 형변환/기본값을 갖고 있음 -_-;

-(NSDictionary*)getParamAsDictionary:(int)index {
    return [_params objectAtIndex:index];
}

-(NSDictionary*)getParamAsDictionary:(int)index defaultValue:(NSDictionary*)defaultValue {
    NSDictionary *result = [_params objectAtIndex:index];
    if(result == nil || [result class] == [NSNull class]) {
        return defaultValue;
    }
    return result;
}

-(NSDictionary*)getParamAsDictionary:(int)index name:(NSString*)name {
    return [[self getParamAsDictionary:index] objectForKey:name];
}

-(NSDictionary*)getParamAsDictionary:(int)index name:(NSString*)name defaultValue:(NSDictionary*)defaultValue {
    NSDictionary *result = [self getParamAsDictionary:index name:name];
    if(result == nil || [result class] == [NSNull class]) {
        return defaultValue;
    }
    return result;
}

-(id)getParam:(int)index {
    return [_params objectAtIndex:index];
}

-(id)getParam:(int)index defaultValue:(id)defaultValue {
    id result = [self getParam:index];
    if (result == nil) {
        return defaultValue;
    }
    return result;
}

-(id)getParam:(int)index name:(id)name {
    return [[self getParamAsDictionary:index] objectForKey:name];
}

-(id)getParam:(int)index name:(NSString*)name defaultValue:(id)defaultValue {
    id result = [self getParam:index name:name];
    if (result == nil) {
        return defaultValue;
    }
    return result;
}

-(NSString*)getParamAsString:(int)index {
    return [_params objectAtIndex:index];
}
-(NSString*)getParamAsString:(int)index defaultValue:(NSString*)defaultValue {
    NSString *result = [self getParamAsString:index];
    if (result == nil || [result class] == [NSNull class]) {
        return defaultValue;
    }
    return result;
}
-(NSString*)getParamAsString:(int)index name:(NSString*)name {
    return [[self getParamAsDictionary:index] objectForKey:name];

}
-(NSString*)getParamAsString:(int)index name:(NSString*)name defaultValue:(NSString*)defaultValue {
    NSString *result = [self getParamAsString:index name:name];
    if (result == nil || [result class] == [NSNull class]) {
        return defaultValue;
    }
    return result;
}

-(BOOL)getParamAsBoolean:(int)index {
    @try {
        return [[_params objectAtIndex:index] boolValue];
    } @catch(id e) {
        @throw [AxError errorWithCode:AX_INVALID_VALUES_ERR message:AX_INVALID_VALUES_ERR_MSG cause:e];
    }
}

-(BOOL)getParamAsBoolean:(int)index defaultValue:(BOOL)defaultValue{
    @try {
        return [[_params objectAtIndex:index] boolValue];
    } @catch(id e) {
        return defaultValue;
    }
}

-(BOOL)getParamAsBoolean:(int)index name:(NSString*)name{
    @try {
        return [[[_params objectAtIndex:index] objectForKey:name] boolValue];
    } @catch(id e) {
        @throw [AxError errorWithCode:AX_INVALID_VALUES_ERR message:AX_INVALID_VALUES_ERR_MSG cause:e];
    }
}

-(BOOL)getParamAsBoolean:(int)index name:(NSString*)name defaultValue:(BOOL)defaultValue{
    @try {
        return [[[_params objectAtIndex:index] objectForKey:name] boolValue];
    } @catch(id e) {
        return defaultValue;
    }
}

-(NSNumber*)getParamAsNumber:(int)index{
    return [_params objectAtIndex:index];
}
-(NSNumber*)getParamAsNumber:(int)index defaultValue:(NSNumber*)defaultValue{
    NSNumber *result = [self getParamAsNumber:index];
    if (result == nil || [result class] == [NSNull class]) {
        return defaultValue;
    }
    return result;
}
-(NSNumber*)getParamAsNumber:(int)index name:(NSString*)name{
    return [[self getParamAsDictionary:index] objectForKey:name];
}
-(NSNumber*)getParamAsNumber:(int)index name:(NSString*)name defaultValue:(NSNumber*)defaultValue{
    NSNumber *result = [self getParamAsNumber:index name:name];
    if (result == nil || [result class] == [NSNull class]) {
        return defaultValue;
    }
    return result;
}

-(NSInteger)getParamAsInteger:(int)index {
    return [[self getParamAsNumber:index] integerValue];
}
-(NSInteger)getParamAsInteger:(int)index defaultValue:(NSInteger)defaultValue {
    if ([self getParamAsNumber:index] == nil) {
        return defaultValue;
    }
    return [[self getParamAsNumber:index defaultValue:(NSNumber*)defaultValue] integerValue];
}
-(NSInteger)getParamAsInteger:(int)index name:(NSString*)name{
    return [[self getParamAsNumber:index name:name] integerValue];
}
-(NSInteger)getParamAsInteger:(int)index name:(NSString*)name defaultValue:(NSInteger)defaultValue {
    if ([self getParamAsNumber:index name:name] == nil) {
        return defaultValue;
    }
    return [[self getParamAsNumber:index name:name defaultValue:(NSNumber*)defaultValue]integerValue];
}

-(NSArray*)getParamAsArray:(int)index {
    return [_params objectAtIndex:index];
}
-(NSArray*)getParamAsArray:(int)index defaultValue:(NSArray*)defaultValue {
    NSArray *result = [self getParamAsArray:index];
    if (result == nil || [result class] == [NSNull class]) {
        return defaultValue;
    }
    return result;
}
-(NSArray*)getParamAsArray:(int)index name:(NSString*)name {
    return [[self getParamAsDictionary:index] objectForKey:name];
}
-(NSArray*)getParamAsArray:(int)index name:(NSString*)name defaultValue:(NSArray*)defaultValue {
    NSArray *result = [self getParamAsArray:index name:name];
    if (result == nil || [result class] == [NSNull class]) {
        return defaultValue;
    }
    return result;
}
#pragma mark -

- (void)makeSuccessResult:(id)result {
    _result = [[NSDictionary alloc] initWithObjectsAndKeys:
                   self.identifier, kPluginResponseIdentifier,
                   result, kPluginResponseResult,
                   [NSNull null], kPluginResponseError,
                   nil];
}

- (void)sendResult:(id)result {
    [self makeSuccessResult:result];
}

- (void)sendResult {
	[self sendResult:[NSNull null]];
}

- (void)makeErrorResult:(NSInteger)code message:(NSString *)message {
	if (nil == message) {
		message = @"";
	}
	NSDictionary* error = [NSDictionary dictionaryWithObjectsAndKeys:
                           [NSNumber numberWithUnsignedShort:code], @"code",
                           message, @"message",
                           nil];
    _result = [[NSDictionary alloc] initWithObjectsAndKeys:
                   self.identifier, kPluginResponseIdentifier,
                   [NSNull null], kPluginResponseResult,
                   error, kPluginResponseError,
                   nil];
}

- (void)sendError:(NSInteger)code message:(NSString *)message {
    [self makeErrorResult:code message:message];
}

- (void)sendError:(NSInteger)code {
    [self sendError:code message:@""];
}

- (void)sendWatchResult:(id)result {
    @throw [AxError errorWithCode:AX_NOT_SUPPORTED_ERR message:@"sendWatchResult() is not supported in non watch jsonrpc context." cause:nil];
}

- (void)sendWatchError:(NSInteger)code message:(NSString *)message {
    @throw [AxError errorWithCode:AX_NOT_SUPPORTED_ERR message:@"sendWatchError() is not supported in non watch jsonrpc context." cause:nil];
}

#pragma mark -

-(void)setAttribute:(NSString*)key value:(id)value{
    [_attrs setObject:value forKey:key];
}

-(id)getAttribute:(NSString*)key {
    return [_attrs objectForKey:key];
}

-(void)removeAttribute:(NSString*)key{
    [_attrs removeObjectForKey:key];
}


@end
