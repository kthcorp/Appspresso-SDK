//
//  KthWaikikiCamera.h
//  cameramanager-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import <UIKit/UIKit.h>
#import "DefaultAxPlugin.h"
#import "AVCamCaptureManager.h"

@interface KthWaikikiCameramanager : DefaultAxPlugin<AVCamCaptureManagerDelegate, UIScrollViewDelegate, UIApplicationDelegate> {
@private
	AVCamCaptureManager *_captureManager;
	AVCaptureVideoPreviewLayer *_captureVideoPreviewLayer;
	//BOOL _webViewOpaque;
	//UIColor *_webViewBackgroundColor;
	CGPoint _previewPoint;
	UIView *_previewView;
	NSArray *_supportedMimeTypesForImage;
	NSArray *_supportedMimeTypesForVideo;
    NSObject<AxPluginContext> *_contextCaptureImage;
    NSString *_cameraName;
}
- (void)getCameras:(NSObject<AxPluginContext>*)context;
- (void)startPreview:(NSObject<AxPluginContext>*)context;
- (void)stopPreview:(NSObject<AxPluginContext>*)context;
- (void)captureImage:(NSObject<AxPluginContext>*)context;

/**
 * 프리뷰 영역의 좌표/크기 설정.
 *
 * NOTE:비표준 API
 *
 * 파라메터: [ name:string, x:number, y:number, width:number, height:number ]
 */
- (void)setPreviewLayout:(NSObject<AxPluginContext>*)context;

@end
