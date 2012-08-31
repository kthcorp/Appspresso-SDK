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
