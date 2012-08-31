//
//  KthWaikikiCamera.m
//  cameramanager-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import "KthWaikikiCameramanager.h"
#import "KthWaikikiCamera.h"
#import "AVCamCaptureManager.h"

#import "AxRuntimeContext.h"
#import "AxPluginContext.h"
#import "AxFileSystemManager.h"
#import "DefaultFileSystem.h"
#import "AxLog.h"
#import "AxError.h"

// camera plugin specific error messages
#define kErrMsgIoErrCameraAlreadyOpened @"Camera already opened"
#define kErrMsgIoErrCameraPreviewNotOpened @"Preview is not opened"
#define kErrMsgIoErrCameraRecordingIsInProgress @"Recording in progress"
#define kErrMsgIoErrCameraRecordingIsNotInProgress @"Recording is not in progress"
#define kErrMsgIoErrCameraNotSupportedFilesystem @"Not supported filesystem"
#define kErrMsgUnknownErrFailSession @"Failed to start session"
#define kErrMsgUnknownErrFailOnInitDevice @"Input Device Init Failed"
#define kErrMsgUnknownErrFailOnCaptureImage @"Failed to capture image"
#define kErrMsgUnknownErrInvalidPath  @"Invaild Path"

// plugin context custom attributes
#define KEY_CAPTURE_IMAGE_PATH @"captureImagePath"

#define JS_CALLBACK_STOPVIDEOCAPTUREOF @"deviceapis.camera.stopVideoCaptureOf"

#define WAC_CAMERA @"http://wacapps.net/api/camera"
//v2.0 camera.capture : Feature that allows capturing a picture or recording a video using the camera. It provides access to the full module except to the createPreviewNode() method.
#define WAC_CAMERA_CAPTURE @"http://wacapps.net/api/camera.capture" 
//v2.0 camera.show : Feature that allows displaying the viewfinder, provides access to the whole module except to captureImage() and startVideoCapture() methods.
#define WAC_CAMERA_SHOW @"http://wacapps.net/api/camera.show" 


@implementation KthWaikikiCameramanager

#pragma mark -

- (BOOL)_isActivatedFeatureCamera {
    return [[self runtimeContext]isActivatedFeature:WAC_CAMERA];
}

- (BOOL)_isActivatedFeatureCameraShow {
    return [[self runtimeContext]isActivatedFeature:WAC_CAMERA_SHOW];
}

- (BOOL)_isActivatedFeatureCameraCapture {
    return [[self runtimeContext]isActivatedFeature:WAC_CAMERA_CAPTURE];
}


- (BOOL)_isSupportedMimeType:(NSString *)filePathName mimeTypes:(NSArray *)mimeTypes
{
	NSRange period = [filePathName rangeOfString:@"." options:NSBackwardsSearch];
	if (period.location == NSNotFound) {
		return NO;
	}
	NSString *ext = [filePathName substringFromIndex:period.location+1];
	for (NSString *t in mimeTypes) {
		if (NSOrderedSame == [t compare:ext options:NSCaseInsensitiveSearch]) {
			return YES;
		}
	}
	return NO;
}

- (NSString *)_replaceExt:(NSString*)filePathName ext:(NSString *)ext
{
	NSRange period = [filePathName rangeOfString:@"." options:NSBackwardsSearch];
	if (NSNotFound == period.location) {
		return [filePathName stringByAppendingString:ext];
	}
	return [filePathName stringByReplacingCharactersInRange:NSMakeRange(period.location, [ext length]) withString:ext];
}

#if TARGET_OS_EMBEDDED
- (BOOL)_setSessionPreset:(NSString*)preset device:(AVCaptureDeviceInput *)input 
{
	if ([[input device] supportsAVCaptureSessionPreset:preset]) {
		AVCaptureSession *session = [_captureManager session];
		if (![preset isEqualToString:[_captureManager sessionPreset]] && [session canSetSessionPreset:preset]) {
			[session beginConfiguration];
			[session setSessionPreset:preset];
			[session commitConfiguration];
			return YES;
		}
	}
	return NO;
}

- (NSString *)_getRandomFileNameWithPrefix:(NSString *)prefix suffix:(NSString *)suffix desired:(NSString *)desiredPath{
	const NSInteger limit = 100;
	NSFileManager *fm = [[[NSFileManager alloc] init] autorelease];
	NSString *nativePath;
	NSString *result;
	
	if (nil != desiredPath) {
		//realPath = [KthWaikikiFilesystem realPathFromFullPath:desiredPath];
        //@@nativePath = [DocumentFileSystem toNativePath:desiredPath];
        //nativePath = [[self.runtimeContext getFileSystemManager] toNativePath:desiredPath];
		if (![fm fileExistsAtPath:desiredPath]) {
			return desiredPath;
		}
	}
	
    //	NSDateFormatter *outputFormatter = [[NSDateFormatter alloc] init];
    //	[outputFormatter setDateFormat:@"yyyy-MM-dd HH:mm:s"];
    //	NSString *name = [outputFormatter stringFromDate:[NSDate date]];
	
	// TODO: timeInteval is double
	NSString *name = [NSString stringWithFormat:@"%d", (NSInteger)[[NSDate date] timeIntervalSince1970]];					  
	
	NSInteger i;
	for (i=0; i < limit; ++i) {
		NSString *middle = (i == 0) ? name : [NSString stringWithFormat:@"%@ (%d)", name, i];
		result = [NSString stringWithFormat:@"%@%@%@", prefix, middle, suffix];
		//nativePath = [KthWaikikiFilesystem realPathFromFullPath:result];
        //@@nativePath = [DocumentFileSystem toNativePath:result];
        nativePath = [[self.runtimeContext getFileSystemManager] toNativePath:result];
		if (![fm fileExistsAtPath:nativePath]) {
			break;
		}
	}
	return (i == limit) ? nil : result;
}
#endif

- (void)saveWebViewOpacity {
// FIXME: 이거 하면... UI thread가 한참동안 얼어버림... 등등의 이상한 UI 동작이 있음. 어떻게 고치나...-_-;
//    UIWebView *webView = [self.runtimeContext getWebView];
//
//    _webViewOpaque = webView.opaque;
//    _webViewBackgroundColor = [webView.backgroundColor retain];
//	
//    webView.opaque = NO;
//    webView.backgroundColor = [UIColor clearColor];
//
//    [webView setNeedsLayout];
//    [webView setNeedsDisplay];
}

- (void)restoreWebViewOpacity {
// FIXME: 이거 하면... UI thread가 한참동안 얼어버림... 등등의 이상한 UI 동작이 있음. 어떻게 고치나...-_-;
//    UIWebView *webView = [self.runtimeContext getWebView];
//
//    webView.opaque = _webViewOpaque;
//    webView.backgroundColor = _webViewBackgroundColor;
//    [_webViewColor release];
//	
//    [webView setNeedsLayout];
//    [webView setNeedsDisplay];
}

#pragma mark UIScrollViewDelegate

- (void)scrollViewDidScroll:(UIScrollView *)scrollView
{
    if(!_previewView) { return; }
	CGRect frame = _previewView.frame;
	frame.origin.x = _previewPoint.x - scrollView.contentOffset.x;
	frame.origin.y = _previewPoint.y - scrollView.contentOffset.y;
    _previewView.frame = frame;
#if TARGET_OS_EMBEDDED
    // ...
#endif
}

#pragma mark UIDeviceOrientationDidChangeNotification

- (void) deviceOrientationDidChange: (id)object 
{
    if(!_previewView) { return; }
#if TARGET_OS_EMBEDDED
	if (nil == _captureManager || [_captureManager isRecording]) { return; }
    UIDeviceOrientation deviceOrientation = [[UIDevice currentDevice] orientation];
	
    AVCaptureSession *session = [_captureManager session];
    [session beginConfiguration];
	_captureManager.orientation = deviceOrientation;
	if ([_captureVideoPreviewLayer isOrientationSupported]) {
		_captureVideoPreviewLayer.orientation = deviceOrientation;
	}
    [session commitConfiguration];
#endif
}

#pragma mark AxPlugin

- (void)activate:(NSObject<AxRuntimeContext>*)runtimeContext
{
    [super activate:runtimeContext];    
    [runtimeContext requirePlugin:@"deviceapis"];
    [self.runtimeContext addApplicationDelegate:self];
	_supportedMimeTypesForImage = [[NSArray alloc] initWithObjects:@"JPG", @"JPEG", nil];
	_supportedMimeTypesForVideo = [[NSArray alloc] initWithObjects:@"MP4", nil];

    // 장치의 방향이 바뀌면 프리뷰의 방향도 같이 바뀌도록...
    [[NSNotificationCenter defaultCenter] addObserver:self 
											 selector:@selector(deviceOrientationDidChange:) 
												 name:UIDeviceOrientationDidChangeNotification
											   object:nil];
	[[UIDevice currentDevice] beginGeneratingDeviceOrientationNotifications];

    // 웹뷰가 스크롤되면 프리뷰도 같이 스크롤되도록...
	for (id sv in [runtimeContext getWebView].subviews) {
		if ([sv isKindOfClass:[UIScrollView class]]) {
			((UIScrollView*)sv).delegate = self;
		}
	}
}

- (void)deactivate:(NSObject<AxRuntimeContext>*)runtimeContext {
	[[UIDevice currentDevice] endGeneratingDeviceOrientationNotifications];
    [[NSNotificationCenter defaultCenter] removeObserver:self
                                                    name:UIDeviceOrientationDidChangeNotification
                                                  object:nil];

#if TARGET_OS_EMBEDDED
    [_captureManager release];
    [_captureVideoPreviewLayer release];
#endif
    [_previewView release];
	[_supportedMimeTypesForImage release];
	[_supportedMimeTypesForVideo release];
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
// getCameras
//
- (void)getCameras:(NSObject<AxPluginContext>*)context
{
#if TARGET_OS_EMBEDDED
	NSArray *devices = [AVCaptureDevice devicesWithMediaType:AVMediaTypeVideo];
	NSMutableArray *result = [NSMutableArray arrayWithCapacity:[devices count]];
	
	for (AVCaptureDevice *device in devices) {
        KthWaikikiCamera *camera = [[KthWaikikiCamera alloc] initWithAVCaptureDevice:device];
        // 0번 카메라는 후면 카메라...
        if(AVCaptureDevicePositionBack == [device position]) {
            [result insertObject:[camera returnData] atIndex:0];
        } else {
            [result addObject:[camera returnData]];
        }
        [camera release];
	}	
	[context sendResult:result];
#else	
    KthWaikikiCamera *camera = [[KthWaikikiCamera alloc] initWithAVCaptureDevice:nil];
	NSArray *result = [NSArray arrayWithObjects:camera, nil];
    [camera release];
	[context sendResult:result];
#endif
}

- (void)createPreview:(NSObject<AxPluginContext>*)context
{
    [context sendResult];
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
// startPreview
//

- (void)startPreview:(NSObject<AxPluginContext>*)context
{
    if ([self _isActivatedFeatureCameraCapture] && ![self _isActivatedFeatureCamera] && ![self _isActivatedFeatureCameraShow]) {
        [context sendError:AX_SECURITY_ERR message:AX_SECURITY_ERR_MSG];
        return;
    }
    if(_previewView) {
        AX_LOG_TRACE(@"Already opened");
        [context sendError:AX_UNKNOWN_ERR message:kErrMsgIoErrCameraAlreadyOpened];
        return;
    }
#if TARGET_OS_EMBEDDED
    if (nil != _captureManager) {
        AX_LOG_TRACE(@"Already opened");
        [context sendError:AX_UNKNOWN_ERR message:kErrMsgIoErrCameraAlreadyOpened];
        return;
    }
#endif
    
    dispatch_async(dispatch_get_main_queue(), ^{
        NSString *name = [context getParamAsString:0];
        NSInteger x = [context getParamAsInteger:1];
        NSInteger y = [context getParamAsInteger:2];
        NSInteger w = [context getParamAsInteger:3];
        NSInteger h = [context getParamAsInteger:4];
        _previewPoint = CGPointMake(x, y);
        CGRect previewFrame = CGRectMake(x, y, w, h);
        
#if TARGET_OS_EMBEDDED
        _captureManager = [[AVCamCaptureManager alloc] init];
        
        NSError *error = nil;
        NSInteger position = [KthWaikikiCamera positionFromName:name];
        _cameraName = [name retain];
        if (![_captureManager setupSessionWithPreset:AVCaptureSessionPresetHigh isBackfacingCamera:(position == AVCaptureDevicePositionBack) error:&error]) {
            AX_LOG_TRACE(@"failed to setup AVCapture session: error=%@", error);
            [_captureManager release];
            _captureManager = nil;
            [context sendError:AX_UNKNOWN_ERR message:kErrMsgUnknownErrFailOnInitDevice];
            return;
        }
            
        [[_captureManager session] startRunning];
        
        if (![[_captureManager session] isRunning]) {
            AX_LOG_TRACE(@"failed to start AVCapture session.");
            [_captureManager release];
            _captureManager = nil;
            [context sendError:AX_UNKNOWN_ERR message:kErrMsgUnknownErrFailSession];
            return;
        }
        
        _captureManager.delegate = self;

        // 프리뷰용 뷰 준비...
        _previewView = [[UIView alloc] initWithFrame:previewFrame];
        _previewView.autoresizesSubviews = YES;
        _previewView.autoresizingMask = UIViewAutoresizingFlexibleWidth|UIViewAutoresizingFlexibleHeight;

        // 프리뷰 레이어를 준비해서... 프리뷰용 뷰에 추가~
        _captureVideoPreviewLayer = [[AVCaptureVideoPreviewLayer alloc] initWithSession:[_captureManager session]];	
        _captureVideoPreviewLayer.frame = CGRectMake(0, 0, w, h);
        // AVLayerVideoGravityResize? AVLayerVideoGravityResizeAspect? AVLayerVideoGravityResizeAspectFill?
        _captureVideoPreviewLayer.videoGravity = AVLayerVideoGravityResizeAspectFill;  // it makes preview to fill for screen size. but before capture it look like zoom-in      
        [_previewView.layer addSublayer:_captureVideoPreviewLayer];
#else
        // 시뮬레이터일 경우에도 프리뷰 영역을 파란 사각형으로 보여주자... 왜?
        _previewView = [[UIView alloc] initWithFrame:previewFrame];
        _previewView.autoresizesSubviews = YES;
        _previewView.autoresizingMask = UIViewAutoresizingFlexibleWidth|UIViewAutoresizingFlexibleHeight;
        
        // 프리뷰 영역에는 뭘 보여줄까나...
        UILabel *previewLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, w, h)];
        previewLabel.text = name;
        previewLabel.textColor = [UIColor whiteColor];
        previewLabel.backgroundColor = [UIColor blueColor];
        previewLabel.textAlignment = UITextAlignmentCenter;
        [_previewView addSubview:previewLabel];
        [previewLabel release];
#endif
        [self saveWebViewOpacity];	

        // 웹뷰 맨 아래에 프리뷰를 추가
        //[[self.runtimeContext getWebView] insertSubview:_previewView atIndex:0];
        [[self.runtimeContext getWebView] addSubview:_previewView];

        [self deviceOrientationDidChange:nil];
    });
    [context sendResult];
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
// setPreviewLayout
//

- (void)setPreviewLayout:(NSObject<AxPluginContext>*)context
{
    dispatch_async(dispatch_get_main_queue(), ^{
        NSInteger x = [context getParamAsInteger:1];
        NSInteger y = [context getParamAsInteger:2];
        NSInteger w = [context getParamAsInteger:3];
        NSInteger h = [context getParamAsInteger:4];

        _previewPoint = CGPointMake(x, y);
        _previewView.frame = CGRectMake(x, y, w, h);
    });
    [context sendResult];
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
// stopPreview
//

- (void)stopPreview:(NSObject<AxPluginContext>*)context {
    dispatch_async(dispatch_get_main_queue(), ^{
#if TARGET_OS_EMBEDDED	
        if ([[_captureManager session] isRunning]) {
            [[_captureManager session] stopRunning];
            [_captureVideoPreviewLayer removeFromSuperlayer];
        }
        [_captureVideoPreviewLayer release];
        _captureVideoPreviewLayer = nil;
        [_captureManager release];
        _captureManager = nil;
#endif
        if (_previewView) {
            [_previewView removeFromSuperview];
            [_previewView release];
            _previewView = nil;
        }
        [self restoreWebViewOpacity];
    });
    [context sendResult];
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
// startVideoCapture
//

- (void)startVideoCapture:(NSObject<AxPluginContext>*)context {
    if ([self _isActivatedFeatureCameraShow] && ![self _isActivatedFeatureCamera] && ![self _isActivatedFeatureCameraCapture]) {
        [context sendError:AX_SECURITY_ERR message:AX_SECURITY_ERR_MSG];
        return;
    }
#if TARGET_OS_EMBEDDED
    
    dispatch_async(dispatch_get_main_queue(), ^{
        if (nil == _captureManager || ![[_captureManager session] isRunning]) {
            [context sendError:AX_UNKNOWN_ERR message:kErrMsgIoErrCameraPreviewNotOpened];
            return;
        }
        if ([_captureManager isRecording]) {
            [context sendError:AX_UNKNOWN_ERR message:kErrMsgIoErrCameraRecordingIsInProgress];
            return;
        }
        BOOL highRes = [context getParamAsBoolean:1 name:@"highRes" defaultValue:YES];
        NSString *des = [context getParamAsString:1 name:@"destinationFilename"];

        // XXX: 어디까지가 high고 어디까지가 low인지 모르겠음... 걍 대충 프리셋 중에서 골라잡았음. -_-;
        [self _setSessionPreset:((highRes) ? AVCaptureSessionPresetHigh : AVCaptureSessionPresetLow)
                         device:[_captureManager videoInput]];
        
        NSString *desired = (nil == des || [des isKindOfClass:[NSNull class]]) ? nil : des;
        NSString *path = [self _getRandomFileNameWithPrefix:@"videos/" suffix:@".MP4" desired:desired];
        if (![self _isSupportedMimeType:path mimeTypes:_supportedMimeTypesForVideo]) {
            // TODO: Permit only MP4 type
            path = [self _replaceExt:path ext:@".MP4"];
        }
        
        if (nil == path) {
            [context sendError:AX_UNKNOWN_ERR message:kErrMsgUnknownErrInvalidPath];
            return;
        }
        AX_LOG_TRACE(@"path : %@", path);
        [_captureManager startRecordingWithFilePath:[[self.runtimeContext getFileSystemManager] toNativePath:path]];
        
        [context sendResult:path];
    });
#else
    [context sendError:AX_NOT_SUPPORTED_ERR message:AX_NOT_SUPPORTED_ERR_SIMULATOR_MSG];
#endif		
}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
// stopVideoCapture
//

- (void)stopVideoCapture:(NSObject<AxPluginContext>*)context {
#if TARGET_OS_EMBEDDED
    dispatch_async(dispatch_get_main_queue(), ^{
        if (nil == _captureManager || ![_captureManager isRecording]) {
            [context sendError:AX_UNKNOWN_ERR message:kErrMsgIoErrCameraRecordingIsNotInProgress];
            return;
        }
        [_captureManager stopRecording];
        // recordingFinished will be called....
    });
    [context sendResult];
#else
    [context sendError:AX_NOT_SUPPORTED_ERR message:AX_NOT_SUPPORTED_ERR_SIMULATOR_MSG];
#endif	
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
// captureImage
//

- (void)captureImage:(NSObject<AxPluginContext>*)context {
    if ([self _isActivatedFeatureCameraShow] && ![self _isActivatedFeatureCamera] && ![self _isActivatedFeatureCameraCapture]) {
        [context sendError:AX_SECURITY_ERR message:AX_SECURITY_ERR_MSG];
        return;
    }
#if TARGET_OS_EMBEDDED	
	if (nil == _captureManager || ![[_captureManager session] isRunning]) {
		[context sendError:AX_UNKNOWN_ERR message:kErrMsgIoErrCameraPreviewNotOpened];
		return;
	}
	
	//NSArray *params = [context getParams];
	//NSDictionary *options = [params objectAtIndex:1];
	//NSNumber *highRes_ = [options objectForKey:@"highRes"];
	//BOOL highRes = (highRes_ == nil) ? YES : [highRes_ boolValue];
	NSString *des = [context getParamAsString:1 name:@"destinationFilename"];//[options objectForKey:@"destinationFilename"];
    
    // XXX: 흠... 저해상도 사진을 위한 프리셋이 없으므로... 걍 무시... highRes 지정하나 마나 똑같음 -_-;
    [self _setSessionPreset:AVCaptureSessionPresetPhoto
                     device:[_captureManager videoInput]];

	NSString *desired = (nil == des || [des isKindOfClass:[NSNull class]]) ? nil : des;
	NSString *path = [self _getRandomFileNameWithPrefix:@"images/" suffix:@".JPG" desired:desired];
	AX_LOG_TRACE(@"path org: %@", path);
	if (![self _isSupportedMimeType:path mimeTypes:_supportedMimeTypesForImage]) {
		// TODO: Permit only JPG type
		path = [self _replaceExt:path ext:@".JPG"];
	}
	
	if (nil == path) {
		[context sendError:AX_UNKNOWN_ERR message:kErrMsgUnknownErrInvalidPath];
		return;
	}
	AX_LOG_TRACE(@"path fin: %@", path);
	[context setAttribute:KEY_CAPTURE_IMAGE_PATH value:path];
    _contextCaptureImage = [context retain];
    [_captureManager captureStillImageWithFilePath:[[self.runtimeContext getFileSystemManager] toNativePath:path]];
#else
	[context sendError:AX_NOT_SUPPORTED_ERR message:AX_NOT_SUPPORTED_ERR_SIMULATOR_MSG];
#endif
}

#pragma mark - AVCamCaptureManagerDelegate

- (void) recordingFinished {
	AX_LOG_TRACE(@"%s", __PRETTY_FUNCTION__);
}

- (void) completedStillImageToGallery:(NSURL *)assetURL {
	AX_LOG_TRACE(@"%s assetURL=", __PRETTY_FUNCTION__, assetURL);
	// TODO: 갤러리로 저장하기 지원...
    //[context sendResult:[NSString stringWithFormat:@"gallery/%@", assetURL]];
	[_contextCaptureImage sendError:AX_UNKNOWN_ERR message:AX_UNKNOWN_ERR_MSG];
    [_contextCaptureImage release];
}

- (void) completedStillImageToFile:(NSString *)path {
	AX_LOG_TRACE(@"%s path=%@", __PRETTY_FUNCTION__, path);
    NSString *des = [_contextCaptureImage getAttribute:KEY_CAPTURE_IMAGE_PATH];
	[_contextCaptureImage sendResult:des];
    [_contextCaptureImage release];
}

- (void) captureStillImageFailedWithError:(NSError *)error {
	AX_LOG_TRACE(@"%s error=%@", __PRETTY_FUNCTION__, error);
	[_contextCaptureImage sendError:AX_UNKNOWN_ERR message:kErrMsgUnknownErrFailOnCaptureImage];
    [_contextCaptureImage release];
}

#pragma mark - UIApplicationDelegate
- (void) applicationDidEnterBackground:(UIApplication *)application {
}

- (void) applicationWillEnterForeground:(UIApplication *)application {
    AX_LOG_TRACE(@">>>>>>>>>> %s", __PRETTY_FUNCTION__);
#if TARGET_OS_EMBEDDED
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.runtimeContext executeJavaScriptFunction:JS_CALLBACK_STOPVIDEOCAPTUREOF,
         _cameraName, nil];
    });
#endif 

}
@end
