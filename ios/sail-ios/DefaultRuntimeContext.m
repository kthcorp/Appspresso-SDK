//
//  DefaultRuntimeContext.m
//  sail-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import "DefaultRuntimeContext.h"
#import "DefaultPluginManager.h"
#import "AxViewController.h"
#import "JSONKit.h"
#import "AxApplicationDelegate.h"
#import "DefaultFileSystemManager.h"
#import "DefaultW3Widget.h"
#import "DefaultW3Feature.h"
#import "DefaultWidgetAgent.h"
#import "AxLog.h"

const NSString * const kWatchSuccessListener = @"ax.bridge.invokeWatchSuccessListener";
const NSString * const kWatchErrorListener = @"ax.bridge.invokeWatchErrorListener";


@implementation DefaultRuntimeContext

@synthesize launchOptions=_launchOptions;

-(id)initWithWidgetAgent:(DefaultWidgetAgent*)wigetAgent {
    if ((self = [super init])) {
        _widgetAgent = [wigetAgent retain];
        _attrs = [[NSMutableDictionary alloc] init];
    }
    return self;
}

-(void)dealloc {
    [_attrs release];
    [_widgetAgent release];
    [_launchOptions release];
    [super dealloc];
}

#pragma mark AxContext

-(UIViewController*)getViewController {
    return _widgetAgent.applicationDelegate.viewController;
}

-(UIWebView*)getWebView {
    return _widgetAgent.applicationDelegate.viewController.webView;
}

-(id<W3Widget>)getWidget{
    return _widgetAgent.widget;
}

-(id<AxFileSystemManager>)getFileSystemManager {
    return _widgetAgent.fileSystemManager;
}

#pragma mark -

-(bool)isActivatedFeature:(NSString*)featureUri {
    return [_widgetAgent.widget.features objectForKey:featureUri] != nil;
}

-(NSArray*)getActivatedFeatures {
    return [_widgetAgent.widget.features allValues];
}

-(id<AxPlugin>)requirePlugin:(NSString*)pluginId{
    return [_widgetAgent.pluginManager requirePlugin:pluginId];
}

-(id<AxPlugin>)requirePluginWithFeature:(NSString*)featureUri{
    return [_widgetAgent.pluginManager requirePluginWithFeature:featureUri];
}

#pragma mark -

//20111028: invoke async function, for exmaple alert(), they make deadlock. so insert gcd async block
- (void)executeJavaScript:(NSString*)script {
    AX_LOG_TRACE(@"callback script: %@", script);
    dispatch_async(dispatch_get_main_queue(), ^{
        [[self getWebView] stringByEvaluatingJavaScriptFromString:script];
    });
}

- (void)executeJavaScriptFunction:(NSString*)functionName, ... {
    NSMutableString *script = [[NSMutableString alloc] initWithFormat:@"%@(", functionName];
    NSMutableArray *funcArgs = [[NSMutableArray alloc] init];

    id arg;
    va_list args;
    va_start(args, functionName); 
    while((arg = va_arg(args, id))) {
        [funcArgs addObject:arg];
    }    
    va_end(args);
    
    NSString *argumentJson = [funcArgs JSONString];
    [funcArgs release];

    // strip bracket - [foo, bar, baz] -> foo, bar, baz
    [script appendString:[argumentJson substringWithRange:NSMakeRange(1, [argumentJson length] - 2)]];
    [script appendString:@")"];

    [self executeJavaScript:script];
    [script release];
}

- (void)invokeWatchSuccessListener:(NSInteger)identifier result:(id)result {
    NSDictionary *responseObj = [[NSDictionary alloc] initWithObjectsAndKeys:
    result, @"result",
            nil];

    NSString *responseStr = [responseObj JSONString];
    [responseObj release];

    NSString *script = [NSString stringWithFormat:@"%@(%d, %@);", kWatchSuccessListener, identifier, responseStr];
    [self executeJavaScript:script];
    //[[self getWebView] stringByEvaluatingJavaScriptFromString:script];
}

- (void)invokeWatchErrorListener:(NSInteger)identifier code:(NSInteger)code message:(NSString *)message {
    NSDictionary *errorObj =  [NSDictionary dictionaryWithObjectsAndKeys:
    [NSNumber numberWithUnsignedShort:code], @"code",
            message, @"message",
            nil];

    NSDictionary *responseObj = [[NSDictionary alloc] initWithObjectsAndKeys:
    errorObj, @"error",
            nil];

    NSString *responseStr = [responseObj JSONString];
    [responseObj release];

    NSString *script = [NSString stringWithFormat:@"%@(%d, %@);", kWatchErrorListener, identifier, responseStr];
    [self executeJavaScript:script];
    //[[self getWebView] stringByEvaluatingJavaScriptFromString:script];
}

#pragma mark -

-(void)setAttribute:(NSString*)key value:(id)value{
    [_attrs setObject:value forKey:key];
}

-(id)getAttribute:(NSString*)key{
    return [_attrs objectForKey:key];
}

-(void)removeAttribute:(NSString*)key {
    [_attrs removeObjectForKey:key];
}

#pragma mark -

- (void)addWebViewDelegate:(id<UIWebViewDelegate>)delegate {
    [_widgetAgent.applicationDelegate.viewController.multicastWebViewDelegate addWebViewDelegate:delegate];
}

- (void)removeWebViewDelegate:(id<UIWebViewDelegate>)delegate {
    [_widgetAgent.applicationDelegate.viewController.multicastWebViewDelegate removeWebViewDelegate:delegate];
}

- (void)addApplicationDelegate:(id<UIApplicationDelegate>)delegate {
    [_widgetAgent.applicationDelegate.multicastApplicationDelegate addApplicationDelegate:delegate];
}

- (void)removeApplicationDelegate:(id<UIApplicationDelegate>)delegate {
    [_widgetAgent.applicationDelegate.multicastApplicationDelegate removeApplicationDelegate:delegate];
}

- (void)addViewControllerDelegate:(id<AxViewControllerDelegate>)delegate {
    [_widgetAgent.applicationDelegate.viewController.multicastViewControllerDelegate addViewControllerDelegate:delegate];
}

- (void)removeViewControllerDelegate:(id<AxViewControllerDelegate>)delegate {
    [_widgetAgent.applicationDelegate.viewController.multicastViewControllerDelegate removeViewControllerDelegate:delegate];
}

#pragma mark -

- (NSDictionary*) getLaunchOptions {
    return _launchOptions;
}


@end
