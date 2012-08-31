//
//  KthWaikikiPim.m
//  pim-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import "AxRuntimeContext.h"
#import "AxPluginContext.h"
#import "KthWaikikiPim.h"


@implementation KthWaikikiPim

- (void)activate:(id<AxRuntimeContext>)runtimeContext {
    [super activate:runtimeContext];
    [runtimeContext requirePlugin:@"deviceapis"];
}

@end
