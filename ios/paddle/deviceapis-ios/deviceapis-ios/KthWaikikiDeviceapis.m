//
//  deviceapis.m
//  deviceapis-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import "AxRuntimeContext.h"
#import "AxPluginContext.h"
#import "W3Feature.h"
#import "KthWaikikiDeviceapis.h"
#import "AxError.h"
#import "AxLog.h"

@implementation KthWaikikiDeviceapis

- (void)listAvailableFeatures:(id<AxPluginContext>)context
{
    NSArray *wacFeatures = [NSArray arrayWithObjects:
                            WAC_DEVICEAPIS,
                            WAC_ACCELERMETER,
                            WAC_ORIENTATION,
                            WAC_CAMERA,
                            WAC_CAMERA_SHOW,
                            WAC_CAMERA_CAPTURE,
                            WAC_DEVICESTATUS,
                            WAC_DEVICESTATUS_DEVICEINFO,
                            WAC_DEVICESTATUS_NETWORKINFO,
                            WAC_FILESYSTEM,
                            WAC_FILESYSTEM_READ,
                            WAC_FILESYSTEM_WRITE,
                            WAC_MESSAGE,
                            WAC_MESSAGE_SEND,
                            WAC_MESSAGE_FIND,
                            WAC_MESSAGE_SUBSCRIBE,
                            WAC_MESSAGE_WRITE,
                            WAC_GEOLOCATION,
                            WAC_CONTACT,
                            WAC_CONTACT_READ,
                            WAC_CONTACT_WRITE,
                            WAC_DEVICEINTERACTION,
                            WAC_TASK,
                            WAC_TASK_READ,
                            WAC_TASK_WRITE,
                            WAC_CALENDAR,
                            WAC_CALENDAR_READ,
                            WAC_CALENDAR_WRITE,
                            nil];
    
    NSMutableArray *res = [NSMutableArray array];
	
    for (id uri in wacFeatures) {
        [res addObject:[NSDictionary dictionaryWithObjectsAndKeys:
                        uri, @"uri",
                        [NSNumber numberWithBool:NO], @"required",
                        [NSNull null],@"params",
                        nil]];
    }
	
	[context sendResult:res];
}

- (void)listActivatedFeatures:(id<AxPluginContext>)context
{
    NSMutableArray *res = [NSMutableArray array];
	
	for(id<W3Feature> feature in [self.runtimeContext getActivatedFeatures]) {
		[res addObject:[NSDictionary dictionaryWithObjectsAndKeys:
						   [feature getName], @"uri",
						   [NSNumber numberWithBool:[feature isRequired]], @"required",
						   [feature getParams], @"params",
                        nil]];
	}
	
	[context sendResult:res];
}
//
//#pragma mark -
//#pragma mark Api for Appspresso Runtime (Not for JSON RPC)
//+ (ConfigXML *)configXML {
//	return [ConfigXML getInstance];
//}
//
//+ (NSArray *)activatedFeatures {
//	return [[ConfigXML getInstance] activatedFeatures];
//}
//
//+ (void)initializeWithWebView:(UIWebView *)webView {
//    NSArray *activatedFeatures = [[ConfigXML getInstance] activatedFeatures];
//    if ([activatedFeatures containsObject:@"http://waclists.org/api/camera"] || 
//        [activatedFeatures containsObject:@"http://waclists.org/api/camera.show"]) {
//		[webView setOpaque:NO];
//		[webView setBackgroundColor:[UIColor clearColor]];
//	}
//}
@end
