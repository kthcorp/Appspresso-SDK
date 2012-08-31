//
//  @IOS_CLASS@.h
//  @IOS_CLASS@
//
//  Copyright 2011 none. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "AxPlugin.h"

@protocol AxContext;
@protocol AxPluginContext;

@interface @IOS_CLASS@ : NSObject<AxPlugin>{
@private
    NSObject<AxRuntimeContext> *_runtimeContext;
}

@property (nonatomic,readonly,retain) NSObject<AxRuntimeContext>* runtimeContext;

- (void)activate:(NSObject<AxRuntimeContext>*)runtimeContext;
- (void)deactivate:(NSObject<AxRuntimeContext>*)runtimeContext;
- (void)execute:(NSObject<AxPluginContext>*)context;

 @end
