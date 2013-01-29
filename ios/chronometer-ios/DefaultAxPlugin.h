/*
 * Appspresso
 *
 * Copyright (c) 2011 KT Hitel Corp.
 *
 * Appspresso SDK may be freely distributed under the MIT license.
 */

#import <Foundation/Foundation.h>
#import "AxPlugin.h"

@protocol AxRuntimeContext;
@protocol AxPluginContext;

@interface DefaultAxPlugin : NSObject<AxPlugin>{
@private
    id<AxRuntimeContext> _runtimeContext;
}

@property (nonatomic,readonly,retain) id<AxRuntimeContext> runtimeContext;

- (void)activate:(id<AxRuntimeContext>)context; 
- (void)deactivate:(id<AxRuntimeContext>)context;
- (void)execute:(id<AxPluginContext>)context;


@end
