//
//  ax_ext_ga_MyPlugin.m
//  ax.ext.ga
//
//  Copyright (c) 2012 KTH Corp.
//

#import "GANTracker.h"

#import "AxPluginContext.h"
#import "AxRuntimeContext.h"
#import "AxLog.h"

#import "ax_ext_ga_MyPlugin.h"

@implementation ax_ext_ga_MyPlugin

- (void)startTracker:(NSObject<AxPluginContext>*)context
{
	//AX_LOG_TRACE(@"%s: params=%@", __PRETTY_FUNCTION__, params);
	NSString *accountId = [context getParamAsString:0];
	NSInteger dispatchPeriod = [context getParamAsInteger:1];
    
	[context sendResult];
    dispatch_async(dispatch_get_main_queue(), ^{
        [[GANTracker sharedTracker] startTrackerWithAccountID:accountId
                                               dispatchPeriod:dispatchPeriod
                                                     delegate:nil];
    });
}

- (void)stopTracker:(NSObject<AxPluginContext>*)context
{
	//AX_LOG_TRACE(@"%s: params=%@", __PRETTY_FUNCTION__, params);
    
	[context sendResult];
    
    dispatch_async(dispatch_get_main_queue(), ^{
        [[GANTracker sharedTracker] stopTracker];   
    });
}

- (void)trackEvent:(NSObject<AxPluginContext>*)context
{
	//AX_LOG_TRACE(@"%s: params=%@", __PRETTY_FUNCTION__, params);
	NSString *category = [context getParamAsString:0];
	NSString *action = [context getParamAsString:1];
	NSString *label = [context getParamAsString:2];
	NSInteger value = [context getParamAsInteger:3];
    
	[context sendResult];
    
    
    dispatch_async(dispatch_get_main_queue(), ^{
        NSError *error;
        if (![[GANTracker sharedTracker] trackEvent:category
                                             action:action
                                              label:label
                                              value:value
                                          withError:&error]) {
            AX_LOG_TRACE(@"trackPageview error:%@", error);
        }
        [[GANTracker sharedTracker] dispatch];
    });
}

- (void)trackPageview:(NSObject<AxPluginContext>*)context
{
	//AX_LOG_TRACE(@"%s: params=%@", __PRETTY_FUNCTION__, params);
	NSString *page = [context getParamAsString:0];
    
	[context sendResult];
    
    
    dispatch_async(dispatch_get_main_queue(), ^{
        NSError *error;
        if (![[GANTracker sharedTracker] trackPageview:page withError:&error]) {
            AX_LOG_TRACE(@"trackPageview error:%@", error);
        }
        [[GANTracker sharedTracker] dispatch];
    });
}

@end
