//
//  KthWaikikiDeviceinteraction.m
//  deviceinteration-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import <AudioToolbox/AudioToolbox.h>

#import "KthWaikikiDeviceinteraction.h"

#import "AxRuntimeContext.h"
#import "AxPluginContext.h"
#import "AxError.h"
#import "AxLog.h"


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
@interface KthWaikikiDeviceinteraction ()
@property (nonatomic, retain, readwrite) NSObject<A440Player> *player;
@property (readonly, getter=isSilentMode) BOOL silentMode;
@property (readonly, getter=isPlaying) BOOL playing;
- (void)_vibe;
- (void)_stopSound;
@end


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
@implementation KthWaikikiDeviceinteraction
@synthesize playing;
@synthesize player = _player;


- (void)activate:(id<AxRuntimeContext>)runtimeContext {
    [super activate:runtimeContext];
    [runtimeContext requirePlugin:@"deviceapis"];

	_player = nil;
}

- (BOOL)isPlaying {
	return _player != nil;
}

-(BOOL)isSilentMode {
	// http://stackoverflow.com/questions/833304/how-to-detect-iphone-is-on-silent-mode
	CFStringRef state;
    UInt32 propertySize = sizeof(CFStringRef);
    AudioSessionInitialize(NULL, NULL, NULL, NULL);
    AudioSessionGetProperty(kAudioSessionProperty_AudioRoute, &propertySize, &state);
	
	return CFStringGetLength(state) == 0;
}

- (void)_stopSound {
	NSError * error = nil;
	[_player stop:&error];
	[self setPlayer:nil];
}

- (void)startNotify:(id<AxPluginContext>)context {
    [context sendError:AX_NOT_SUPPORTED_ERR message:AX_NOT_SUPPORTED_ERR_MSG];
//	if (!self.isPlaying) {
//		NSArray *params = [context params];	
//		id num;
//		NSTimeInterval duration = !!(num=[params objectAtIndex:0]) ? [num intValue]/1000.f : 1.f;
//		
//		if (self.isSilentMode) {
//			[self _vibe];
//		} else {
//			_player = [[A440AudioQueue alloc] init];
//			[self performSelector:@selector(_stopSound) withObject:nil afterDelay:duration];
//			NSError * error = nil;
//			if (![_player play:&error]) {
//				[self setPlayer:nil];
//				[context errorWithCode:kUnknownErr message:kErrMsgUnknownErr];
//				return;
//			}
//		}
//	}
//	[context succeeded];
}

- (void)stopNotify:(id<AxPluginContext>)context {
//	if (self.isPlaying) {
//		[NSObject cancelPreviousPerformRequestsWithTarget:self];
//		[self _stopSound];
//	}
//	[context succeeded];
	[context sendError:AX_NOT_SUPPORTED_ERR message:AX_NOT_SUPPORTED_ERR_MSG];
}

- (void)_vibe {
	AudioServicesPlayAlertSound(kSystemSoundID_Vibrate);
}

- (void)startVibrate:(id<AxPluginContext>)context {
    // TODO: duration
	//	http://www.kimballlarsen.com/2009/12/22/how-to-make-iphone-vibrate-for-a-long-time/
	//	NSArray *params = [context params];	
	//	id num;
	//	NSTimeInterval duration = !!(num=[params objectAtIndex:0]) ? [num intValue]/1000 : -1.f;
	//	NSTimeInterval delay = 0.3f;
	//	do {
	//		[self performSelector:@selector(vibe:) withObject:self afterDelay:delay];
	//		delay += 0.3f;
	//	} while (delay < duration);
	[self _vibe];
	[context sendResult];
}

- (void)stopVibrate:(id<AxPluginContext> )context {
    // TODO: duration is not supported
	[context sendResult];
}

- (void)lightOn:(id<AxPluginContext>)context {
    // TODO : duration ignored
	[[UIApplication sharedApplication] setIdleTimerDisabled:YES]; 
	[context sendResult];
}

- (void)lightOff:(id<AxPluginContext>)context {
    [[UIApplication sharedApplication] setIdleTimerDisabled:NO];
	[context sendResult];
}

- (void)setWallpaper:(id<AxPluginContext>)context {
    [context sendError:AX_NOT_SUPPORTED_ERR message:AX_NOT_SUPPORTED_ERR_MSG];
}
@end
