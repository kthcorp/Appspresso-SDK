//
//  BridgeSessionManager.m
//  sail-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import "BridgeSessionManager.h"

@implementation BridgeSession

@synthesize key = _key;
@synthesize javaScriptEvaluationEnabled = _javaScriptEvaluationEnabled;
@synthesize initialized = _initialized;

-(id)initWithSessionKey:(NSString*)key {
    if (self = [super init]) {
        self.key = key;
        self.initialized = NO;
        self.javaScriptEvaluationEnabled = NO;
    }
    return self;
}

@end

@implementation BridgeSessionManager

static NSMutableDictionary* _ax_bridge_sessions_map = nil;

+(void)initialize {
    if (_ax_bridge_sessions_map == nil) {
        _ax_bridge_sessions_map = [NSMutableDictionary new];
    }
    [super initialize];
}

+(BridgeSession*)lookupWithSessionKey:(NSString*)key {
    @synchronized (_ax_bridge_sessions_map) {
        BridgeSession* session = [_ax_bridge_sessions_map objectForKey:key];
        if (!session) {
            session = [[BridgeSession alloc] initWithSessionKey:key];
            [_ax_bridge_sessions_map setObject:session forKey:key];
            [session release];
        }
        return session;
    }
}

@end
