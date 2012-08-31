//
//  AppspressoPluginResponse.m
//  sail-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import "AppspressoPluginResponse.h"
#import "HydraWebServer.h"
#import "JSONKit.h"
#import "AppspressoResponse.h"
#import "AxPluginContext.h"
#import "DefaultPluginContext.h"
#import "DefaultPluginManager.h"
#import "DefaultWidgetAgent.h"
#import "AxError.h"
#import "AxLog.h"
#import "MimeTypeUtils.h"
#import "JsonRpcAsyncPluginContext.h"
#import "JsonRpcWatchPluginContext.h"
#import "DefaultRuntimeContext.h"
#import "BridgeSessionManager.h"


#define kPluginIdentifier @"id"
#define kPluginMethod @"method"
#define kPluginParams @"params"

#define kPluginResponseIdentifier @"id"
#define kPluginResponseResult @"result"
#define kPluginResponseError @"error"

#define ERR_CODE_INVALID_REQUEST -1
#define kErrMsgInvalidRequest @"Invalid request"


@implementation AppspressoPluginResponse

- (BOOL)isAsync:(NSDictionary*)params {
    return [@"true" compare:[params objectForKey:@"async"]] == NSOrderedSame;
}

- (BOOL)isWatch:(NSDictionary*)params {
    return [@"true" compare:[params objectForKey:@"watch"]] == NSOrderedSame;
}

- (void)sendEmptyResponse {
    [self replyStringResponse:@""];
}

- (void)sendResponse:(NSDictionary*)result {
    [self setContentType:MIME_TYPE_JSON];
    NSString* responseStr = [result JSONString];
	AX_LOG_TRACE(@"responseStr: %@", responseStr);

    [self replyStringResponse:responseStr];
}

- (id<AxPluginContext>)createPluginContext:(NSDictionary*)requestObj withSession:(BridgeSession*)session andWatchp:(BOOL)watchp {
    if (watchp) {
        JsonRpcWatchPluginContext* watchContext = [[JsonRpcWatchPluginContext alloc] initWithRequestJson:requestObj session:session];
        [watchContext setRuntimeContext:self.widgetAgent.runtimeContext];
        if ([watchContext isMalformedRequest]) {
            [watchContext release];
            return nil;
        }
        return watchContext;
    }

    JsonRpcAsyncPluginContext* asyncContext = [[JsonRpcAsyncPluginContext alloc] initWithRequestJson:requestObj session:session];
    [asyncContext setRuntimeContext:self.widgetAgent.runtimeContext];
    if ([asyncContext isMalformedRequest]) {
        [asyncContext release];
        return nil;
    }

    return asyncContext;
}

- (void)handleAsync:(NSDictionary*)requestObj withSession:(BridgeSession*)session andWatchp:(BOOL)watchp {
    id<AxPluginContext> context = [self createPluginContext:requestObj withSession:session andWatchp:watchp];
    if (context == nil) {
        [context sendError:ERR_CODE_INVALID_REQUEST message:kErrMsgInvalidRequest];
        [context release];
        return;
    }

    [self sendEmptyResponse];

    id plugin = [self.widgetAgent.pluginManager requirePlugin:[context getPrefix]];
    // Check wether plugin implements exec method or no. Actually, plugin must implements exec or extends AppspressoPlugin.
    SEL execute = @selector(execute:);

    if (plugin == nil || ![plugin respondsToSelector:execute]) {
        [context sendError:ERR_CODE_INVALID_REQUEST message:kErrMsgInvalidRequest];
        [context release];
		return;
    }

    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        @try {
            [plugin performSelector:execute withObject:context];
        }
        @catch (AxError* e) {
            [context sendError:[e code] message:[e message]];
        }
        @catch (NSException* e) {
            [context sendError:AX_UNKNOWN_ERR message:[NSString stringWithFormat:@"unknown exception %@: %@", e.name, e.reason]];
        }
        @finally {
            [context release];
        }
    });
}

- (void)handleSync:(NSDictionary*)requestObj withSession:(BridgeSession*)session {
    DefaultPluginContext *context = [[DefaultPluginContext alloc] initWithRequestJson:requestObj session:session];
    @try {
        if ([context isMalformedRequest]) {
            [context sendError:ERR_CODE_INVALID_REQUEST message:kErrMsgInvalidRequest];
            return;
        }

        id plugin = [self.widgetAgent.pluginManager requirePlugin:context.prefix];
        // Check wether plugin implements exec method or no. Actually, plugin must implements exec or extends AppspressoPlugin.
        SEL execute = @selector(execute:);

        if (plugin == nil || ![plugin respondsToSelector:execute]) {
            [context sendError:ERR_CODE_INVALID_REQUEST message:kErrMsgInvalidRequest];
            return;
        }

        [plugin performSelector:execute withObject:context];
    }
    @catch (AxError* e) {
        [context sendError:[e code] message:[e message]];
    }
    @catch (NSException* e) {
        [context sendError:AX_UNKNOWN_ERR message:[NSString stringWithFormat:@"unknown exception %@: %@", e.name, e.reason]];
    }
    @finally {
        [self sendResponse:context.result];
        [context release];
    }
}

- (BridgeSession*)getSessionFromKey:(NSString*)key {
    BridgeSession* session = [BridgeSessionManager lookupWithSessionKey:key];
    // need mutex to prevent multi-initialization of same session... but it's *harmless* ;)
    if (session.initialized == NO) {
        session.initialized = YES;
        session.javaScriptEvaluationEnabled = [self isLocalConnection];
    }
    return session;
}

- (void)specificHandlerForApi:(NSString *)name value:(NSString *)value
{
	NSDictionary* requestObj = [[JSONDecoder decoder] mutableObjectWithData:[self.connection requestBody]];
	AX_LOG_TRACE(@"Request Object = %@", [requestObj description]);
    if (!requestObj) {
        [self replyError:400 withMessage:@"400 Bad Request"];
        return;
    }

    NSDictionary* params = [self queryParametersMapfromUrl:[NSURL URLWithString:[name stringByAppendingString:value]]];
    BridgeSession* session = [self getSessionFromKey:[params objectForKey:@"session"]];

    if ([self isAsync:params]) {
        [self handleAsync:requestObj withSession:session andWatchp:[self isWatch:params]];
        return;
    }

    [self handleSync:requestObj withSession:session];
}

@end
