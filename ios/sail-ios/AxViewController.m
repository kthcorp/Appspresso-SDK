//
//  AxViewController.m
//  sail-ios
//
//  Copyright (c) 2012 KTH Corp.
//
#import "AxViewController.h"

#import "MulticastWebViewDelegate.h"
#import "MulticastViewControllerDelegate.h"
#import "AxApplicationDelegate.h"
#import "AxConfig.h"

#define EMULATEORIENTATION @"EmulateOrientation"
#define JSCALLBACK_ORIENTATION @"window.__defineGetter__('orientation',function(){return %d;});(function(){ var evt = document.createEvent('Events');evt.initEvent('orientationchange', true, true);window.dispatchEvent(evt);})(); "
#define USESCALETOFIT @"ScalesPageToFit"
#define ENABLECHACHE @"webview.cache.enable"
#define APPDEVELOPMENT @"app.devel"
#define JS_ENTER_FOREGROUND @"ax.event.onRestoreState();"
#define JS_ENTER_BACKGROUND @"ax.event.onSaveState();"
#define JS_HIDE_SPLASH @"ax.event.onHideSplash();"
#define MILISECOND_VALUE 1000

@implementation AxViewController

@synthesize multicastWebViewDelegate = _multicastWebViewDelegate;
@synthesize multicastViewControllerDelegate = _multicastViewControllerDelegate;
@synthesize webView = _webView;

#pragma mark -

- (id)initWithApplicationDelegate:(AxApplicationDelegate*)applicationDelegate {
    if((self = [super init])) {
        _multicastWebViewDelegate = [[MulticastWebViewDelegate alloc] init];
        _multicastViewControllerDelegate = [[MulticastViewControllerDelegate alloc] init];

        _webView = [[UIWebView alloc] initWithFrame:[[UIScreen mainScreen] applicationFrame]];
        [_webView setDelegate:self];
        
        //하위호환성을 위해 UIInterfaceOrientation 이 있는 경우, autorotation 을 끔.
        if ([AxConfig getAttributeAsBoolean:USESCALETOFIT defaultValue:NO]) {
            [_webView setScalesPageToFit:YES];
        }
    }
    return self;
}

- (void)dealloc 
{
	[_webView release];
    [_multicastViewControllerDelegate release];
    [_multicastWebViewDelegate release];
    [super dealloc];
}

#pragma mark -

-(void)loadURL:(NSURL*)url {
    [_webView loadRequest:[NSURLRequest requestWithURL:url cachePolicy:NSURLRequestUseProtocolCachePolicy timeoutInterval:20.0]];
}

#pragma mark -

// Implement loadView to create a view hierarchy programmatically, without using a nib.
- (void)loadView 
{
	self.view = _webView;
}

// Implement viewDidLoad to do additional setup after loading the view, typically from a nib.
- (void)viewDidLoad 
{
    [super viewDidLoad];
}

- (void)viewDidUnload 
{
    [super viewDidUnload];
	// Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
}

#pragma mark splash support

// TODO: make as a plugin

static NSString * const DEF_SPLASH_RES_NAME = @"Default";
static NSString * const DEF_SPLASH_LST_NAME = @".png";
static NSString * const DEF_SPLASH_RETINA = @"@2x";
static NSString * const DEF_SPLASH_IPAD = @"~ipad";
static NSString * const DEF_SPLASH_LANDSCAPE = @"-Landscape";
static NSString * const DEF_SPLASH_PORTRAIT = @"-Portrait";

// 스플래시의 최소 지속 시간(page load후에도 이 시간 동안 무조건 스플래시를 표시)
static const NSTimeInterval DEF_SPLASH_DURATION_MIN = 0.0f;
// 스플래시의 최대 지속 시간(이 시간이 넘어가면 page load 여부에 관계없이 무조건 스플래시를 숨김)
static const NSTimeInterval DEF_SPLASH_DURATION_MAX = 0;

// NOTE: undocumented 스플래시 지속시간 설정 appspresso-config 키 이름
static NSString * const CONFIG_SPLASH_DURATION_MIN = @"splash.duration.min";
static NSString * const CONFIG_SPLASH_DURATION_MAX = @"splash.duration.max";

static UIImageView *_splashView;
static NSTimeInterval _splashDurationMin;
static NSTimeInterval _splashDurationMax;

//http://stackoverflow.com/questions/3294100/how-to-differentiate-between-iphone4-and-iphone-3
- (BOOL)isRetinaDisplay {
    // since we call this alot, cache it
    static CGFloat scale = 0.0;
    if (scale == 0.0) {
        // NOTE: In order to detect the Retina display reliably on all iOS devices,
        // you need to check if the device is running iOS4+ and if the 
        // [UIScreen mainScreen].scale property is equal to 2.0. 
        // You CANNOT assume a device is running iOS4+ if the scale property exists,
        // as the iPad 3.2 also contains this property.
        // On an iPad running iOS3.2, scale will return 1.0 in 1x mode, and 2.0
        // in 2x mode -- even though we know that device does not contain a Retina display.
        // Apple changed this behavior in iOS4.2 for the iPad: it returns 1.0 in both
        // 1x and 2x modes. You can test this yourself in the simulator.
        // I test for the -displayLinkWithTarget:selector: method on the main screen
        // which exists in iOS4.x but not iOS3.2, and then check the screen's scale:
        
        if ([[UIScreen mainScreen] respondsToSelector:@selector(displayLinkWithTarget:selector:)] && 
            ([UIScreen mainScreen].scale == 2.0)) {
            scale = 2.0;
            return YES;
        } else {
            scale = 1.0;
            return NO;
        }   
        
    }
    return scale > 1.0;
}

-(CGFloat)makeRotationAngleForOrientation:(UIInterfaceOrientation)interfaceOrientation {
    switch (interfaceOrientation) {
        case UIInterfaceOrientationPortraitUpsideDown:
           return M_PI;
        case UIInterfaceOrientationLandscapeRight:
            return M_PI/2;
        case UIInterfaceOrientationLandscapeLeft:
            return (M_PI/2) * (-1);
        default:
            return 0;
    }
}

-(CGRect)makeFrameForOrientation:(UIInterfaceOrientation)interfaceOrientation {
    CGFloat statusbarHeight = 0;
    CGRect windowFrame = [[[UIApplication sharedApplication]keyWindow]frame];
    
    CGRect statusBarFrame = [[UIApplication sharedApplication] statusBarFrame];
    if ([[UIDevice currentDevice] userInterfaceIdiom] == UIUserInterfaceIdiomPad) {
        if (statusBarFrame.size.width == 20 || statusBarFrame.size.height == 20) {  
            statusbarHeight = 20; 
        }
    }
    
    switch (interfaceOrientation) {
        case UIInterfaceOrientationPortraitUpsideDown:
            //viewBound = CGRectMake(0, 0, 768, 1004);
            return CGRectMake(windowFrame.origin.x, windowFrame.origin.y, windowFrame.size.width, windowFrame.size.height-statusbarHeight);
        case UIInterfaceOrientationLandscapeRight:
            //viewBound = CGRectMake(0, 0, 748, 1024);
            return CGRectMake(windowFrame.origin.x, windowFrame.origin.y, windowFrame.size.width-statusbarHeight, windowFrame.size.height);
        case UIInterfaceOrientationLandscapeLeft:
            //viewBound = CGRectMake(20, 0, 748, 1024);
            return CGRectMake(windowFrame.origin.x+statusbarHeight, windowFrame.origin.y, windowFrame.size.width-statusbarHeight, windowFrame.size.height);
        default:
            //viewBound = CGRectMake(0, 20, 768, 1004)
            return CGRectMake(windowFrame.origin.x, windowFrame.origin.y+statusbarHeight, windowFrame.size.width, windowFrame.size.height-statusbarHeight);
    }
}

- (void)showSplash {
    UIInterfaceOrientation iOrientation = [[UIDevice currentDevice]orientation];
    NSString *splashImageFile = [NSString stringWithString:DEF_SPLASH_RES_NAME];
    if ([[UIDevice currentDevice] userInterfaceIdiom] == UIUserInterfaceIdiomPhone) {
        if ([self isRetinaDisplay]) {
            splashImageFile = [splashImageFile stringByAppendingString:DEF_SPLASH_RETINA];
        }
    }
    else { //if ([[UIDevice currentDevice] userInterfaceIdiom] == UIUserInterfaceIdiomPad) {
        if ( iOrientation == UIInterfaceOrientationLandscapeLeft || iOrientation == UIInterfaceOrientationLandscapeRight) {
            splashImageFile = [splashImageFile stringByAppendingString:DEF_SPLASH_LANDSCAPE];
        }
        else {
            splashImageFile = [splashImageFile stringByAppendingString:DEF_SPLASH_PORTRAIT];
        }
        
        if ([self isRetinaDisplay]) {
            splashImageFile = [splashImageFile stringByAppendingString:DEF_SPLASH_RETINA];
        }
        
        splashImageFile = [splashImageFile stringByAppendingString:DEF_SPLASH_IPAD];
    }
    
    splashImageFile = [splashImageFile stringByAppendingString:DEF_SPLASH_LST_NAME];
    
    UIImage *splashImage = [UIImage imageNamed:splashImageFile];
    if(splashImage != nil) {
        UIWindow *window = [UIApplication sharedApplication].keyWindow;
        
        _splashView = [[UIImageView alloc] init];
        [_splashView setImage:splashImage];
        
        
        CGRect splashViewFrame = [self makeFrameForOrientation:iOrientation];
        //NSLog(@"splashViewFrame x = %f, y = %f , w = %f, h = %f",splashViewFrame.origin.x,splashViewFrame.origin.y,splashViewFrame.size.width,splashViewFrame.size.height);
        
        if ([[UIDevice currentDevice] userInterfaceIdiom] == UIUserInterfaceIdiomPad) { 
            //ipad 일때 splash view 를 돌릴 필요가 있음.
            _splashView.transform =CGAffineTransformMakeRotation([self makeRotationAngleForOrientation:iOrientation]);
            CGRect imageFrame = CGRectMake(0, 0, splashImage.size.width, splashImage.size.height);
            if (iOrientation == UIInterfaceOrientationLandscapeLeft || iOrientation == UIInterfaceOrientationLandscapeRight) {
                //landscape 의 이미지는 좌우로 넓지만 뷰는 그대로 위아래로 넓은게 그 방향으로 돌아가는 구조임.
                imageFrame = CGRectMake(0, 0, splashImage.size.height, splashImage.size.width);
            }
            //NSLog(@"imageFrame x = %f, y = %f , w = %f, h = %f",imageFrame.origin.x,imageFrame.origin.y,imageFrame.size.width,imageFrame.size.height);
            
            float windowPerImage = splashViewFrame.size.width / imageFrame.size.width;
            if (windowPerImage == 1.0) {
                windowPerImage = splashViewFrame.size.height / imageFrame.size.height;
            }
            float rotateCorrectionValue = 0;
            if (iOrientation == UIInterfaceOrientationPortrait) {
                splashViewFrame = (windowPerImage > 1.0)?CGRectMake(splashViewFrame.origin.x, splashViewFrame.origin.y, imageFrame.size.width * windowPerImage, imageFrame.size.height * windowPerImage) : splashViewFrame;
            }
            else if ( iOrientation == UIInterfaceOrientationLandscapeLeft) {
                rotateCorrectionValue = (windowPerImage > 1.0)?((imageFrame.size.height * windowPerImage) - imageFrame.size.height):0;
                splashViewFrame = (windowPerImage > 1.0)?CGRectMake(splashViewFrame.origin.x, splashViewFrame.origin.y-rotateCorrectionValue, imageFrame.size.width * windowPerImage, imageFrame.size.height * windowPerImage) : splashViewFrame;
            }
            else if (iOrientation == UIInterfaceOrientationLandscapeRight) {
                splashViewFrame = (windowPerImage > 1.0)?CGRectMake(splashViewFrame.origin.x, splashViewFrame.origin.y, imageFrame.size.width * windowPerImage, imageFrame.size.height * windowPerImage) : splashViewFrame; 
            }
            else {
                rotateCorrectionValue = (windowPerImage > 1.0)?((imageFrame.size.width * windowPerImage) - imageFrame.size.width):0;
                splashViewFrame = (windowPerImage > 1.0)?CGRectMake(splashViewFrame.origin.x-rotateCorrectionValue, splashViewFrame.origin.y, imageFrame.size.width * windowPerImage, imageFrame.size.height * windowPerImage):splashViewFrame;
            }
            
        }
        _splashView.frame = splashViewFrame;
        //NSLog(@"_splashView.frame x = %f, y = %f , w = %f, h = %f",_splashView.frame.origin.x,_splashView.frame.origin.y,_splashView.frame.size.width,_splashView.frame.size.height);
        [window addSubview:_splashView];
    }
    else {
        splashImageFile = [NSString stringWithFormat:@"%@%@",DEF_SPLASH_RES_NAME,DEF_SPLASH_LST_NAME];
        splashImage = [UIImage imageNamed:splashImageFile];
        UIWindow *window = [UIApplication sharedApplication].keyWindow;
        CGRect imageFrame = CGRectMake(0, 0, splashImage.size.width, splashImage.size.height);
        CGRect windowFrame = [window frame];
        CGRect statusBarFrame = [[UIApplication sharedApplication] statusBarFrame];
        CGRect splashFrame= windowFrame;
        //TODO : (ipad) 768*1024로 된 이미지는 그 사이즈에 꽉 차도록 출력된다. || 768*1004 로 된 캔버스에 640*960 으로 된 이미지를 붙여넣는 경우, 이미지는 768*1152 사이즈가 된다. 이것은 너비에 맞추기 위한 비율 1.2로 이미지 자체를 키운 값이다.
        //TODO : (iphone) 320*480 이외의 이미지는 위의 수식을 따른다. || 320*480 사이즈의 이미지는 그 사이즈에 꽉 차도록 출력된다.
        if (!CGRectEqualToRect(imageFrame, windowFrame)) {
            windowFrame = [[UIScreen mainScreen] applicationFrame];
            if ([[UIDevice currentDevice]orientation] == UIDeviceOrientationLandscapeLeft || [[UIDevice currentDevice]orientation] == UIDeviceOrientationLandscapeRight) {
                windowFrame.origin.y -= statusBarFrame.size.height;
                windowFrame.size.width -= statusBarFrame.size.height;
            } 
            float windowPerImage = windowFrame.size.width / imageFrame.size.width;
            splashFrame = (windowPerImage > 1.0)?CGRectMake(windowFrame.origin.x, windowFrame.origin.y, imageFrame.size.width * windowPerImage, imageFrame.size.height * windowPerImage) : windowFrame;
        }
        _splashView = [[UIImageView alloc] initWithFrame:splashFrame];
        [_splashView setImage:splashImage];
        //ipad 일때 splash image 가 항상 좌측으로 눕도록 나옴. orientation 이 UIDeviceOrientationLandscapeRight 으로 나올때는 현재의 뷰를 180도 돌릴 필요가 있음. 
        //if ([[UIDevice currentDevice] userInterfaceIdiom] == UIUserInterfaceIdiomPad) { 
        //    window.transform = ([[UIDevice currentDevice]orientation] == UIDeviceOrientationLandscapeRight) ? CGAffineTransformMakeRotation(M_PI) : CGAffineTransformMakeRotation(0);
        //}
        [window addSubview:_splashView];
    }
}

- (void)hideSplash {
    if(_splashView != nil) {
        [_splashView removeFromSuperview];
        [_splashView release];
        _splashView = nil;
    }
    dispatch_async(dispatch_get_main_queue(), ^{
        [_webView stringByEvaluatingJavaScriptFromString:JS_HIDE_SPLASH];
    });
}

- (void)resetCacheIfNeeded {
    BOOL cacheEnabled = [AxConfig getAttributeAsBoolean:ENABLECHACHE defaultValue:YES];
    BOOL isDevel = [AxConfig getAttributeAsBoolean:APPDEVELOPMENT defaultValue:NO];
    
    if (isDevel || !cacheEnabled) {
        [[NSURLCache sharedURLCache]removeAllCachedResponses];
        NSString *cacheDirectory = [NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, YES) objectAtIndex:0];
        [[NSFileManager defaultManager] removeItemAtPath:cacheDirectory error:nil];
    }
}

#pragma UIApplicationDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    [self resetCacheIfNeeded];

    [self showSplash];
    if(_splashView) {
        _splashDurationMin = [AxConfig getAttributeAsInteger:CONFIG_SPLASH_DURATION_MIN
                                                defaultValue:DEF_SPLASH_DURATION_MIN];
        _splashDurationMax = [AxConfig getAttributeAsInteger:CONFIG_SPLASH_DURATION_MAX
                                                defaultValue:DEF_SPLASH_DURATION_MAX];
        if(_splashDurationMax > 0) {
            [self performSelector:@selector(hideSplash)
                       withObject:nil
                       afterDelay:(_splashDurationMax/MILISECOND_VALUE)];
        }
    }
    return YES;
}

//wake up..after hydra server restarted
- (void)applicationWillEnterForeground:(UIApplication *)application { 
    dispatch_async(dispatch_get_main_queue(), ^{
        [_webView stringByEvaluatingJavaScriptFromString:JS_ENTER_FOREGROUND];
    });
}

//go to bed..before hydra server will stop
-(void)applicationWillResignActive:(UIApplication *)application { 
    dispatch_async(dispatch_get_main_queue(), ^{
        [_webView stringByEvaluatingJavaScriptFromString:JS_ENTER_BACKGROUND];
    });
}

#pragma UIWebViewDelegate

- (BOOL)webView:(UIWebView *)webView shouldStartLoadWithRequest:(NSURLRequest *)request navigationType:(UIWebViewNavigationType)navigationType {
    return [_multicastWebViewDelegate webView:webView shouldStartLoadWithRequest:request navigationType:navigationType];
}

- (void)webViewDidStartLoad:(UIWebView *)webView {
    [_multicastWebViewDelegate webViewDidStartLoad:webView];
}

- (void)webViewDidFinishLoad:(UIWebView *)webView {
    if(_splashView) {
        if(_splashDurationMin > 0) {
            [self performSelector:@selector(hideSplash)
                       withObject:nil
                       afterDelay:(_splashDurationMin/MILISECOND_VALUE)];
        } else {
            [self hideSplash];
        }
    }
    [_multicastWebViewDelegate webViewDidFinishLoad:webView];
}

- (void)webView:(UIWebView *)webView didFailLoadWithError:(NSError *)error {
    if(_splashView) {
        if(_splashDurationMin > 0) {
            [self performSelector:@selector(hideSplash)
                       withObject:nil
                       afterDelay:(_splashDurationMin/MILISECOND_VALUE)];
        } else {
            [self hideSplash];
        }
    }
    [_multicastWebViewDelegate webView:webView didFailLoadWithError:error];
}

#pragma multicast to all AxViewControllerDelegate

- (void)viewWillAppear:(BOOL)animated {
    [_multicastViewControllerDelegate viewWillAppear:animated];
    [super viewWillAppear:animated];
}

- (void)viewDidAppear:(BOOL)animated {
    [_multicastViewControllerDelegate viewDidAppear:animated];
    [super viewDidAppear:animated];
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    [_multicastViewControllerDelegate viewWillDisappear:animated];
}

- (void)viewDidDisappear:(BOOL)animated {
    [_multicastViewControllerDelegate viewDidDisappear:animated];
    [super viewDidDisappear:animated];
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation {
    return [_multicastViewControllerDelegate shouldAutorotateToInterfaceOrientation:interfaceOrientation];
}

- (void)willRotateToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation duration:(NSTimeInterval)duration {
    [super willRotateToInterfaceOrientation:toInterfaceOrientation duration:duration];
    [_multicastViewControllerDelegate willRotateToInterfaceOrientation:toInterfaceOrientation duration:duration];
    // 회전에 따라서 0,90,180,-90 보내기. UIInterfaceOrientationLandscapeLeft 는 홈버튼이 왼쪽에 있다는 뜻이다.
    if ([[[UIDevice currentDevice] systemVersion] intValue] < 5) { //ios5 부터는 onorientationchange 가 기본 탑재.
        NSString *js;
        switch (toInterfaceOrientation) {
            case UIInterfaceOrientationPortrait:
                js = [NSString stringWithFormat:JSCALLBACK_ORIENTATION,0];
                break;
            case UIInterfaceOrientationLandscapeLeft:
                js = [NSString stringWithFormat:JSCALLBACK_ORIENTATION,-90];
                break;  
            case UIInterfaceOrientationLandscapeRight:
                js = [NSString stringWithFormat:JSCALLBACK_ORIENTATION,90];
                break;  
            default:
                js = [NSString stringWithFormat:JSCALLBACK_ORIENTATION,180];
                break;
        }
        dispatch_async(dispatch_get_main_queue(), ^{
            [_webView stringByEvaluatingJavaScriptFromString:js];
        });
    }
}

- (void)willAnimateRotationToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation duration:(NSTimeInterval)duration {
    [super willAnimateRotationToInterfaceOrientation:interfaceOrientation duration:duration];
    [_multicastViewControllerDelegate willAnimateRotationToInterfaceOrientation:interfaceOrientation duration:duration];
}

- (void)didRotateFromInterfaceOrientation:(UIInterfaceOrientation)fromInterfaceOrientation {
    [_multicastViewControllerDelegate didRotateFromInterfaceOrientation:fromInterfaceOrientation];
    [super didRotateFromInterfaceOrientation:fromInterfaceOrientation];
}

- (void)willAnimateFirstHalfOfRotationToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation duration:(NSTimeInterval)duration {
    [super willAnimateFirstHalfOfRotationToInterfaceOrientation:toInterfaceOrientation duration:duration];
    [_multicastViewControllerDelegate willAnimateFirstHalfOfRotationToInterfaceOrientation:toInterfaceOrientation duration:duration];
}

- (void)didAnimateFirstHalfOfRotationToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation {
    [_multicastViewControllerDelegate didAnimateFirstHalfOfRotationToInterfaceOrientation:toInterfaceOrientation];
    [super didAnimateFirstHalfOfRotationToInterfaceOrientation:toInterfaceOrientation];
}

- (void)willAnimateSecondHalfOfRotationFromInterfaceOrientation:(UIInterfaceOrientation)fromInterfaceOrientation duration:(NSTimeInterval)duration {
    [super willAnimateSecondHalfOfRotationFromInterfaceOrientation:fromInterfaceOrientation duration:duration];
    [_multicastViewControllerDelegate willAnimateSecondHalfOfRotationFromInterfaceOrientation:fromInterfaceOrientation duration:duration];
}

- (void)didReceiveMemoryWarning {
    [_multicastViewControllerDelegate didReceiveMemoryWarning];
    [super didReceiveMemoryWarning];
}

@end
