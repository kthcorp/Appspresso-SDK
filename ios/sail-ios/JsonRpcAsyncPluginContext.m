//
//  JsonRpcAsyncPluginContext.m
//  sail-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import "JsonRpcAsyncPluginContext.h"
#import "AxRuntimeContext.h"
#import "BridgeSessionManager.h"
#import "RpcPollResultStore.h"
#import "JSONKit.h"

#define BRIDGE_JSONRPC_METHOD   @"ax.bridge.jsonrpc"

@implementation JsonRpcAsyncPluginContext

- (id)initWithRequestJson:(NSDictionary*)json session:(BridgeSession*)session {
    return self = [super initWithRequestJson:json session:session];
}

- (id)setRuntimeContext:(id<AxRuntimeContext>)runtimeContext {
    _runtimeContext = runtimeContext;
    return self;
}

#pragma mark -

- (void)sendRpcResponse:(id)object {
    if (self.session.javaScriptEvaluationEnabled) {
        NSString* js = [NSString stringWithFormat:@"window.setTimeout(function(){%@(%@);},1);", BRIDGE_JSONRPC_METHOD, [object JSONString]];
        [_runtimeContext executeJavaScript:js];
        return;
    }

    [[RpcPollResultStore instance] putResult:object toQueue:self.session.key];
}

- (void)sendResult:(id)result {
    [super makeSuccessResult:result];
    [self sendRpcResponse:self.result];
}

- (void)sendError:(NSInteger)code message:(NSString *)message {
    [super makeErrorResult:code message:message];
    [self sendRpcResponse:self.result];
}

@end
