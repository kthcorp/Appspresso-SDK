//
//  KthWaikikiDevicestatus.h
//  devicestatus-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import <Foundation/Foundation.h>
#import "DefaultAxPlugin.h"


@interface KthWaikikiDevicestatus : DefaultAxPlugin {
//	UIWebView *_webView;
	NSMutableDictionary *_watchObjects;
}

/**********************************************************************************************************************************************************/
/* StringArray getComponents(DOMString aspect)                                                                                                            */
/* boolean isSupported(DOMString aspect, DOMString property)                                                                                              */
/* PendingOperation getPropertyValue(GetPropertySuccessCallback successCallback, ErrorCallback? errorCallback, PropertyRef prop)                          */
/* unsigned long watchPropertyChange(PropertyChangeSuccessCallback successCallback, ErrorCallback? errorCallback, PropertyRef prop, WatchOptions options) */
/* void clearPropertyChange(unsigned long watchHandler)                                                                                                   */
/**********************************************************************************************************************************************************/

- (void)getComponents:(id<AxPluginContext>)context;
- (void)isSupported:(id<AxPluginContext>)context;
- (void)getPropertyValue:(id<AxPluginContext>)context;
- (void)clearPropertyChange:(id<AxPluginContext>)context;

+ (BOOL)hasAspects:(NSString *)aspects;
+ (BOOL)hasProperty:(NSString *)property ofAspect:(NSString *)aspects;
@end
