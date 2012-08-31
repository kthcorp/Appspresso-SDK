/*
 * Appspresso
 *
 * Copyright (c) 2011 KT Hitel Corp.
 *
 * This source is subject to Appspresso license terms.
 * Please see http://appspresso.com/ for more information.
 *
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY
 * KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A
 * PARTICULAR PURPOSE.
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
