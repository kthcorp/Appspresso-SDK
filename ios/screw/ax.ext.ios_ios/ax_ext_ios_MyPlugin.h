//
//  ax_ext_ios_MyPlugin.h
//  ax.ext.ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import <Foundation/Foundation.h>
#import "DefaultAxPlugin.h"

@interface ax_ext_ios_MyPlugin : DefaultAxPlugin {

}

- (void)finish:(id<AxPluginContext>)context;
- (void)getUUID:(id<AxPluginContext>)context;

@end
