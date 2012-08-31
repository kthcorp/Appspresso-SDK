//
//  ax_ext_media_MyPlugin.h
//  ax.ext.media
//
//  Copyright (c) 2012 KTH Corp.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import <AVFoundation/AVFoundation.h>
#import "DefaultAxPlugin.h"

@interface ax_ext_media_MyPlugin : DefaultAxPlugin<UIImagePickerControllerDelegate,UINavigationControllerDelegate,AVAudioPlayerDelegate> {
@private
	NSString *_outPath;
	NSMutableDictionary *_soundIds;
    UIPopoverController *_popover;
    NSMutableArray *_soundPlayerArray;
    id<AxPluginContext> _runningContext;
}

- (void)captureImage:(NSObject<AxPluginContext>*)context;
- (void)pickImage:(NSObject<AxPluginContext>*)context;
- (void)captureScreen:(NSObject<AxPluginContext>*)context;
- (void)transformImage:(NSObject<AxPluginContext>*)context;
- (void)addToGallery:(NSObject<AxPluginContext>*)context;
- (void)playAudio:(NSObject<AxPluginContext>*)context;
- (void)playSound:(NSObject<AxPluginContext>*)context;
- (void)stopSound:(NSObject<AxPluginContext>*)context;

@end
