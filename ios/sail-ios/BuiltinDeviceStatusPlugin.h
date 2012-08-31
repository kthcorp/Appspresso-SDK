//
//  BuiltinDeviceStatusPlugin.h
//  sail-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import <Foundation/Foundation.h>
#import "DefaultAxPlugin.h"
#import "DefaultPluginContext.h"

@interface BuiltinDeviceStatusPlugin : DefaultAxPlugin

- (void)getVendor:(DefaultPluginContext*)context;
- (void)getModel:(DefaultPluginContext*)context;

@end
