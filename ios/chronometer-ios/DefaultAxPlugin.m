/*
 * Appspresso
 *
 * Copyright (c) 2011 KT Hitel Corp.
 *
 * Appspresso SDK may be freely distributed under the MIT license.
 */

#import "DefaultAxPlugin.h"
#import "AxRuntimeContext.h"
#import "AxPluginContext.h"

@implementation DefaultAxPlugin

@synthesize runtimeContext = _runtimeContext;

- (void)activate:(id<AxRuntimeContext>)context {
    _runtimeContext = context;
}

- (void)deactivate:(id<AxRuntimeContext>)context {
    _runtimeContext = nil;
}

- (void)execute:(id<AxPluginContext>)context {
    SEL sel = NSSelectorFromString([[context getMethod] stringByAppendingString:@":"]);
	if ([self respondsToSelector:sel]) {
		[self performSelector:sel withObject:context];
		return;
    }
    [context sendError:-1 message:[NSString stringWithFormat:@"Method %@.%@ not found", [context getPrefix], [context getMethod]]];
}

@end
