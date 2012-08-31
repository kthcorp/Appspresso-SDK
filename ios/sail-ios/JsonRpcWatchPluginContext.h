//
//  JsonRpcWatchPluginContext.h
//  sail-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import "JsonRpcAsyncPluginContext.h"

@class BridgeSession;

@interface JsonRpcWatchPluginContext : JsonRpcAsyncPluginContext

- (id)initWithRequestJson:(NSDictionary*)json session:(BridgeSession*)session;

@end
