//
//  AxViewController.h
//  sail-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import <UIKit/UIKit.h>

@class MulticastWebViewDelegate;
@class MulticastViewControllerDelegate;
@class AxApplicationDelegate;

/**
 * WebView의 컨테이너.
 *
 * UIWebViewDelegate 멀티캐스팅.
 *
 * NOTE: WebView와 무관한 부분은 다른 곳으로 옮김.
 *
 * @see 안드로이드 런타임의 WidgetView
 */
@interface AxViewController : UIViewController<UIWebViewDelegate, UIApplicationDelegate>
{
@private
    MulticastWebViewDelegate *_multicastWebViewDelegate;
    MulticastViewControllerDelegate *_multicastViewControllerDelegate;
	UIWebView *_webView;
    //NSArray* _supportedOrientations;
    
}
@property (nonatomic,readonly,retain) MulticastWebViewDelegate *multicastWebViewDelegate;
@property (nonatomic,readonly,retain) MulticastViewControllerDelegate *multicastViewControllerDelegate;
@property (nonatomic,readonly,retain) UIWebView *webView;

- (id)initWithApplicationDelegate:(AxApplicationDelegate*)applicationDelegate;

-(void)loadURL:(NSURL*)url;

@end
