//
//  AppspressoRpcPollResponse.m
//  sail-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import "AppspressoRpcPollResponse.h"

#import "AxLog.h"
#import "RpcPollResultStore.h"
#import "MimeTypeUtils.h"
#import "JSONKit.h"

#define WAIT_FOR_EMPTY_RESPONSE 10

@implementation AppspressoRpcPollResponse

- (NSString*)sessionIdFromUrl:(NSURL*)url {
    return [[self queryParametersMapfromUrl:url] objectForKey:@"session"];
}

- (NSString*)serialize:(NSArray*)results {
    NSUInteger size = [results count];
    if (size == 0) {
        // not possible..
        return @"";
    }

    if (size == 1) {
        NSDictionary* result = [results objectAtIndex:0];
        return [result JSONString];
    }

    NSDictionary* wrap = [NSDictionary dictionaryWithObjectsAndKeys:
                           [NSNull null], @"id",
                           @"ax.bridge.jsonrpc.plural", @"method",
                           results, @"params",
                           nil];
    return [wrap JSONString];
}

- (void)sendResponse:(NSString*)json {
    [self setContentType:MIME_TYPE_JSON];
    [self replyResponse:[json dataUsingEncoding:NSUTF8StringEncoding]];
}

- (void)sendEmptyResponse {
    [self sendResponse:@""];
}

//- (void)pushTestResultsToSession:(NSString*)key {
//    RpcPollResultStore* store = [RpcPollResultStore instance];
//
//    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_LOW, 0), ^{
//        NSDictionary* dic = [[NSDictionary alloc] initWithObjectsAndKeys:[[NSNumber alloc] initWithInt:42], @"id", @"success", @"result", [NSNull null], @"error", nil];
//        [store putResult:dic toQueue:key];
//        dic = [[NSDictionary alloc] initWithObjectsAndKeys:[[NSNumber alloc] initWithInt:45], @"id", @"success", @"result", [NSNull null], @"error", nil];
//        [store putResult:dic toQueue:key];
//    });
//}

- (void)specificHandlerForApi:(NSString *)name value:(NSString *)value {
    NSString* session = [self sessionIdFromUrl:[NSURL URLWithString:[name stringByAppendingString:value]]];
    if (!session) {
        AX_LOG_DEBUG(@"session key missing for rpcpoll handler");
        [self sendEmptyResponse];
        return;
    }

    RpcPollResultStore* store = [RpcPollResultStore instance];

//    [self pushTestResultsToSession:session];

    BOOL hasResult = [store waitForQueue:session untilTimeout:WAIT_FOR_EMPTY_RESPONSE];
    if (hasResult) {
        [self sendResponse:[self serialize:[store drainQueue:session]]];
        return;
    }

    [self sendEmptyResponse];
}

@end
