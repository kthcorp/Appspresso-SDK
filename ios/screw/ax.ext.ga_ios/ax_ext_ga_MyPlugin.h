//
//  ax_ext_ga_MyPlugin.h
//  ax.ext.ga
//
//  Copyright (c) 2012 KTH Corp.
//

#import <Foundation/Foundation.h>
#import "DefaultAxPlugin.h"

@interface ax_ext_ga_MyPlugin : DefaultAxPlugin {
}

- (void)startTracker:(NSObject<AxPluginContext>*)context;
- (void)stopTracker:(NSObject<AxPluginContext>*)context;
- (void)trackEvent:(NSObject<AxPluginContext>*)context;
- (void)trackPageview:(NSObject<AxPluginContext>*)context;

@end
