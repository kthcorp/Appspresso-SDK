//
//  JsonRpcWatchPluginContext.m
//  sail-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import "JsonRpcWatchPluginContext.h"
#import "AxLog.h"
#import "JSONKit.h"
#import "BridgeSessionManager.h"

#define WATCH_SAMPLE_METHOD     @"ax.watch.sample"

@implementation JsonRpcWatchPluginContext

- (id)initWithRequestJson:(NSDictionary*)json session:(BridgeSession*)session {
    return self = [super initWithRequestJson:json session:session];
}

#pragma mark -

- (void)sendResult:(id)result {
    AX_LOG_TRACE(@"sendResult(%@) called on JsonRpcWatchPluginContext(%d). it will be discarded.", [result description], [self.identifier intValue]);
}

- (void)sendError:(NSInteger)code message:(NSString *)message {
    [self sendWatchError:code message:message];
}

- (void)sendRpcNotify:(NSString*)method withParam:(id)param {
    NSDictionary* map = [NSDictionary dictionaryWithObjectsAndKeys:
                         [NSNull null], @"id",
                         method, @"method",
                         [NSArray arrayWithObjects:param, nil], @"params",
                         nil];
    [super sendRpcResponse:map];
}

- (void)sendWatchResult:(id)result {
    [super makeSuccessResult:result];
    [self sendRpcNotify:WATCH_SAMPLE_METHOD withParam:self.result];
}

- (void)sendWatchError:(NSInteger)code message:(NSString *)message {
    [super makeErrorResult:code message:message];
    [self sendRpcNotify:WATCH_SAMPLE_METHOD withParam:self.result];
}

@end
