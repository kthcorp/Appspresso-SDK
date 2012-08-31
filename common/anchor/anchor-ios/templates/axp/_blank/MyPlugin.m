//
//  @IOS_CLASS@.m
//
//  Copyright 2011 none. All rights reserved.
//

#import "AxRuntimeContext.h"
#import "AxPluginContext.h"
#import "AxError.h"
#import "@IOS_CLASS@.h"

@implementation @IOS_CLASS@

@synthesize runtimeContext = _runtimeContext;

- (void)activate:(NSObject<AxRuntimeContext>*)runtimeContext {
    _runtimeContext = [runtimeContext retain];
}

- (void)deactivate:(NSObject<AxRuntimeContext>*)runtimeContext {
    [_runtimeContext release];
    _runtimeContext = nil;
}

- (void)execute:(id<AxPluginContext>)context {
    NSString* method = [context getMethod];

    if ([method isEqualToString:@"echo"]) {
        NSString* message = [context getParamAsString:(0)];
        [context sendResult:(message)];
    }
    else {
        [context sendError:(AX_NOT_AVAILABLE_ERR)];
    }
}

@end
