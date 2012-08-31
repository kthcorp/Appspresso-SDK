//
//  JsonRpcAsyncPluginContext.h
//  sail-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import "DefaultPluginContext.h"

@class BridgeSession;

@interface JsonRpcAsyncPluginContext : DefaultPluginContext {
    id<AxRuntimeContext> _runtimeContext;
}

- (id)initWithRequestJson:(NSDictionary*)json session:(BridgeSession*)session;

- (id)setRuntimeContext:(id<AxRuntimeContext>)runtimeContext;
- (void)sendRpcResponse:(id)object;

@end
