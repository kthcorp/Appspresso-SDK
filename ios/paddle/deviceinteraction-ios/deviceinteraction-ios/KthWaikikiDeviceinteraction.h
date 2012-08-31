//
//  KthWaikikiDeviceinteraction.h
//  deviceinteration-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import <Foundation/Foundation.h>
#import "A440AudioQueue.h"
#import "DefaultAxPlugin.h"


@interface KthWaikikiDeviceinteraction : DefaultAxPlugin 
{
@private
	NSObject<A440Player> *_player;
}


/******************************************************************************************************************************************/
/* DeviceapisDeviceInteractionManager                                                                                                     */
/* void                         void startNotify(unsigned long duration)                                                                  */
/* void                         stopNotify()                                                                                              */
/* void                         startVibrate(unsigned long duration, DOMString pattern)                                                   */
/* void                         stopVibrate()                                                                                             */
/* void                         lightOn(unsigned long duration)                                                                           */
/* void                         lightOff()                                                                                                */
/* PendingOperation             setWallpaper(SuccessCallback successCallback, ErrorCallback? errorCallback, DOMString fileName)           */
/******************************************************************************************************************************************/
- (void)startNotify:(id<AxPluginContext>)context;
- (void)stopNotify:(id<AxPluginContext>)context;
- (void)startVibrate:(id<AxPluginContext>)context;
- (void)stopVibrate:(id<AxPluginContext>)context;
- (void)lightOn:(id<AxPluginContext>)context;
- (void)lightOff:(id<AxPluginContext>)context;
- (void)setWallpaper:(id<AxPluginContext>)context;
@end
