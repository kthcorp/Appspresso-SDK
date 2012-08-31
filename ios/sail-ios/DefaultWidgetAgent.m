//
//  DefaultWidgetAgent.m
//  sail-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import "DefaultWidgetAgent.h"
#import "AxApplicationDelegate.h"
#import "DefaultW3Widget.h"
#import "HydraWebServer.h"
#import "DefaultFileSystemManager.h"
#import "DefaultFileSystem.h"
#import "DefaultPluginManager.h"
#import "DefaultRuntimeContext.h"
#import "AxViewController.h"
#import "AuthenticateCookiePlanter.h"
#import "AxLog.h"
#import "AxConfig.h"

// location specifiers
#define K_DOCUMENTS_PREFIX @"documents"
#define K_IMAGES_PREFIX @"images"
#define K_VIDEOS_PREFIX @"videos"
#define K_MUSIC_PREFIX @"music"
#define K_DOWNLOADS_PREFIX @"downloads"
#define K_WGT_PRIVATE_PREFIX @"wgt-private"
#define K_WGT_PRIVATE_TMP_PREFIX @"wgt-private-tmp"
#define K_WGT_PACKAGE_PREFIX @"wgt-package"

// rotate option

#define INTERFACEORIENTATION @"UIInterfaceOrientation"
#define INTERFACEORIENTATIONPORTRAIT @"UIInterfaceOrientationPortrait"
#define INTERFACEORIENTATIONPORTRAITUPSIDEDOWN @"UIInterfaceOrientationPortraitUpsideDown"
#define INTERFACEORIENTATIONLANDSCAPELEFT @"UIInterfaceOrientationLandscapeLeft"
#define INTERFACEORIENTATIONLANDSCAPERIGHT @"UIInterfaceOrientationLandscapeRight"

@implementation DefaultWidgetAgent

@synthesize applicationDelegate = _applicationDelegate;
@synthesize fileSystemManager = _fileSystemManager;
@synthesize pluginManager = _pluginManager;
@synthesize server = _server;
@synthesize widget = _widget;
@synthesize runtimeContext = _runtimeContext;

- (id)initWithApplicationDelegate:(AxApplicationDelegate*)applicationDelegate {
    if((self = [super init])) {
        _applicationDelegate = [applicationDelegate retain];
        _fileSystemManager = [[DefaultFileSystemManager alloc] init];//TODO:...
        NSString *document = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) objectAtIndex:0];
        
        NSString *documentsPath = [document stringByAppendingPathComponent:K_DOCUMENTS_PREFIX];
        NSString *imagesPath = [document stringByAppendingPathComponent:K_IMAGES_PREFIX];
        NSString *videosPath = [document stringByAppendingPathComponent:K_VIDEOS_PREFIX];
        NSString *musicPath = [document stringByAppendingPathComponent:K_MUSIC_PREFIX];
        NSString *downloadsPath = [document stringByAppendingPathComponent:K_DOWNLOADS_PREFIX];
        NSString *privatePath = [document stringByAppendingPathComponent:K_WGT_PRIVATE_PREFIX];
        
        [_fileSystemManager mount:K_DOCUMENTS_PREFIX fileSystem:[DefaultFileSystem fileSystemWithBaseDirectory:documentsPath canRead:YES canWrite:YES] option:nil];
        [_fileSystemManager mount:K_IMAGES_PREFIX fileSystem:[DefaultFileSystem fileSystemWithBaseDirectory:imagesPath canRead:YES canWrite:YES] option:nil];
        [_fileSystemManager mount:K_VIDEOS_PREFIX fileSystem:[DefaultFileSystem fileSystemWithBaseDirectory:videosPath canRead:YES canWrite:YES] option:nil];
        [_fileSystemManager mount:K_MUSIC_PREFIX fileSystem:[DefaultFileSystem fileSystemWithBaseDirectory:musicPath canRead:YES canWrite:YES] option:nil];
        [_fileSystemManager mount:K_DOWNLOADS_PREFIX fileSystem:[DefaultFileSystem fileSystemWithBaseDirectory:downloadsPath canRead:YES canWrite:YES] option:nil];
        [_fileSystemManager mount:K_WGT_PRIVATE_PREFIX fileSystem:[DefaultFileSystem fileSystemWithBaseDirectory:privatePath canRead:YES canWrite:YES] option:nil];
        
        NSString *tempPath = NSTemporaryDirectory();
        NSFileManager *fm = [[NSFileManager alloc] init];
        NSArray *tmpTargets = [fm contentsOfDirectoryAtPath:tempPath error:NULL];
        for (NSString *t in tmpTargets) {
            [fm removeItemAtPath:[tempPath stringByAppendingPathComponent:t] error:NULL];
        }
        [fm release];
        NSMutableArray *pathComponet = [NSMutableArray arrayWithArray:[tempPath pathComponents]];
        if ([tempPath hasSuffix:@"/"]) {
            [pathComponet removeLastObject];
        }
        tempPath = nil;
        tempPath = [NSString pathWithComponents:pathComponet];
        [_fileSystemManager mount:K_WGT_PRIVATE_TMP_PREFIX fileSystem:[DefaultFileSystem fileSystemWithBaseDirectory:tempPath canRead:YES canWrite:YES] option:nil];
        
        NSString *wwwPath = [[[NSBundle mainBundle] bundlePath] stringByAppendingPathComponent:@"assets/ax_www/"];
        [_fileSystemManager mount:K_WGT_PACKAGE_PREFIX fileSystem:[DefaultFileSystem fileSystemWithBaseDirectory:wwwPath canRead:YES canWrite:NO] option:nil];
        
        //하위호환성을 위해 UIInterfaceOrientation 이 있는 경우, autorotation 을 끔.
        NSString* initialOrientation = [[[NSBundle mainBundle] infoDictionary] objectForKey:INTERFACEORIENTATION];
        
        if (initialOrientation == nil) {
            _interfaceOrientationPortrait=[AxConfig getAttributeAsBoolean:INTERFACEORIENTATIONPORTRAIT defaultValue:YES];
            _interfaceOrientationLandscapeLeft=[AxConfig getAttributeAsBoolean:INTERFACEORIENTATIONLANDSCAPELEFT defaultValue:YES];
            _interfaceOrientationLandscapeRight=[AxConfig getAttributeAsBoolean:INTERFACEORIENTATIONLANDSCAPERIGHT defaultValue:YES];
            _interfaceOrientationPortraitUpsideDown=[AxConfig getAttributeAsBoolean:INTERFACEORIENTATIONPORTRAITUPSIDEDOWN defaultValue:YES];
        }
        else {
            _interfaceOrientationPortrait=([initialOrientation isEqualToString:INTERFACEORIENTATIONPORTRAIT])?YES:[AxConfig getAttributeAsBoolean:INTERFACEORIENTATIONPORTRAIT defaultValue:NO];
            _interfaceOrientationLandscapeLeft=([initialOrientation isEqualToString:INTERFACEORIENTATIONLANDSCAPELEFT])?YES:[AxConfig getAttributeAsBoolean:INTERFACEORIENTATIONLANDSCAPELEFT defaultValue:NO];
            _interfaceOrientationLandscapeRight=([initialOrientation isEqualToString:INTERFACEORIENTATIONLANDSCAPERIGHT])?YES:[AxConfig getAttributeAsBoolean:INTERFACEORIENTATIONLANDSCAPERIGHT defaultValue:NO];
            _interfaceOrientationPortraitUpsideDown=([initialOrientation isEqualToString:INTERFACEORIENTATIONPORTRAITUPSIDEDOWN])?YES:[AxConfig getAttributeAsBoolean:INTERFACEORIENTATIONPORTRAITUPSIDEDOWN defaultValue:NO];
        }
        
        _pluginManager = [[DefaultPluginManager alloc] initWithWidgetAgent:self];
        _server = [[HydraWebServer alloc] initWithWidgetAgent:self];
        _runtimeContext = [[DefaultRuntimeContext alloc] initWithWidgetAgent:self];
        
        [_runtimeContext addApplicationDelegate:self];
        [_runtimeContext addApplicationDelegate:_pluginManager];
        [_runtimeContext addApplicationDelegate:_server];
        [_runtimeContext addViewControllerDelegate:self];

        _authCookiePlanter = nil;
        if (![AxConfig getAttributeAsBoolean:@"app.devel" defaultValue:NO]) {
            _authCookiePlanter = [[AuthenticateCookiePlanter alloc] initWithHost:[_server getHost] port:[_server getPort]];
            [_runtimeContext addApplicationDelegate:_authCookiePlanter];
        }
    }
    
    return self;
}

- (void)dealloc {
    [_widget release];
    [_runtimeContext release];
    [_server release];
    [_pluginManager release];
    [_fileSystemManager release];
    [_applicationDelegate release];
    [_authCookiePlanter release];
    [super dealloc];
}

#pragma mark WidgetAgent

-(AxViewController*)getViewController {
    return _applicationDelegate.viewController;
}

-(UIWebView*)getWebView {
    return _applicationDelegate.viewController.webView;
}

-(id<AxFileSystemManager>)getFileSystemManager {
    return [_fileSystemManager retain];
}

-(id<PluginManager>)getPluginManager {
    return [_pluginManager retain];
}

-(id<WebServer>)getWebServer {
    return [_server retain];
}

-(id<W3Widget>)getWidget {
    return [_widget retain];
}

-(id<AxRuntimeContext>)getAxRuntimeContext {
    return [_runtimeContext retain];
}

-(NSString*)getBaseDir {
    return @"assets/ax_www";
}

#pragma mark -

- (void)loadWidgetConfig {
    _widget = [[DefaultW3Widget alloc] initWithContentOfFile:@"assets/ax_www/config.xml"];
}

- (void)loadWidgetContent {
    // XXX: undocumented feature: content src starts with "file:///"
	NSURL *contentUrl;
    if([_widget.contentSrc hasPrefix:@"file:///"]) {
        contentUrl = [NSURL fileURLWithPath:[[[NSBundle mainBundle] bundlePath] stringByAppendingPathComponent:[_widget.contentSrc substringFromIndex:8]]];
    } else {
        contentUrl = [NSURL URLWithString:[NSString stringWithFormat:@"http://%@:%d/%@", [_server getHost], [_server getPort] , _widget.contentSrc]];
    }    
    [_applicationDelegate.viewController loadURL:contentUrl];
}

#pragma mark UIApplicationDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    AX_LOG_TRACE(@">>>>>>>>>> %s", __PRETTY_FUNCTION__);
    [_runtimeContext setLaunchOptions:launchOptions];
    [self loadWidgetConfig];
    [_server start];
    [self loadWidgetContent];    
    return YES;
}

#pragma mark AxViewControllerDelegate

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation {
    switch (interfaceOrientation) {
        case UIInterfaceOrientationPortrait:
            return _interfaceOrientationPortrait;
        case UIInterfaceOrientationLandscapeLeft:
            return _interfaceOrientationLandscapeLeft;
        case UIInterfaceOrientationLandscapeRight:
            return _interfaceOrientationLandscapeRight;
        default:
            return _interfaceOrientationPortraitUpsideDown;
    }
}

@end
