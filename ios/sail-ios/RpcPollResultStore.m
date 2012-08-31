//
//  RpcPollStore.m
//  sail-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import "RpcPollResultStore.h"

@implementation RpcPollResultStore

- (id)init {
    if (self = [super init]) {
        _sessionResults = [NSMutableDictionary new];
        _sessionConditions = [NSMutableDictionary new];
    }
    return self;
}

- (void)dealloc {
    [_sessionResults removeAllObjects];
    [_sessionResults release];
    [_sessionConditions removeAllObjects];
    [_sessionConditions release];

    [super dealloc];
}

+ (RpcPollResultStore*)instance {
    static dispatch_once_t onceToken;
    static RpcPollResultStore* theInstance;
    dispatch_once(&onceToken, ^{
        theInstance = [self new];
    });
    return theInstance;
}

- (NSMutableArray*)getQueue:(NSString*)session {
    @synchronized (_sessionResults) {
        NSMutableArray* queue = [_sessionResults objectForKey:session];
        if (queue == nil) {
            queue = [NSMutableArray new];
            [_sessionResults setObject:queue forKey:session];
            NSCondition* cond = [NSCondition new];
            [_sessionConditions setObject:cond forKey:session];
        }
        return queue;
    }
}

- (BOOL)waitForQueue:(NSString*)session untilTimeout:(NSTimeInterval)timeout {
    NSMutableArray* queue = [self getQueue:session];
    NSCondition* cond = [_sessionConditions objectForKey:session];

    [cond lock];

    BOOL notEmpty = true;
    if ([queue count] == 0) {
        BOOL signaled = [cond waitUntilDate:[NSDate dateWithTimeIntervalSinceNow:timeout]];
        notEmpty = signaled;
    }

    [cond unlock];

    return notEmpty;
}

- (NSArray*)drainQueue:(NSString*)session {
    NSMutableArray* queue = [self getQueue:session];
    NSCondition* cond = [_sessionConditions objectForKey:session];

    [cond lock];

    NSArray* ret = [NSArray arrayWithArray:queue];
    [queue removeAllObjects];

    [cond unlock];

    return ret;
}

- (void)putResult:(NSObject*)result toQueue:(NSString*)session {
    NSMutableArray* queue = [self getQueue:session];
    NSCondition* cond = [_sessionConditions objectForKey:session];

    [cond lock];

    [queue addObject:result];
    [cond broadcast];

    [cond unlock];
}

@end
