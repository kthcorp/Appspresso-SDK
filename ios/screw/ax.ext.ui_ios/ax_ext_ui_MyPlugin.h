//
//  ax_ext_ui_MyPlugin.h
//  ax.ext.ui
//
//  Copyright (c) 2012 KTH Corp.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

#import "DefaultAxPlugin.h"
#import "AxViewControllerDelegate.h"

@protocol AxPluginContext;

@interface ax_ext_ui_MyPlugin : DefaultAxPlugin<UIAlertViewDelegate, UIActionSheetDelegate, UIWebViewDelegate, UITextViewDelegate, AxViewControllerDelegate>  {
@private
    // confirm/prompt/pick/...
	NSInteger _buttonIndex;
    // for show/hideProgress
    //UIView *_progressView;
    // for add/removeWebView
    UIWebView *_webView;
    UIActivityIndicatorView *_webViewProgressView;
    long _webViewStartCallback;
    long _webViewFinishCallback;
    long _webViewErrorCallback;
    long _webViewLoadCallback;
    // for add/removeTextView
    UITextView *_textView;
    UILabel* _textViewLabel;    
    int _textViewMaxLength;
    //id<AxPluginContext> _pluginContext;
    int _currentOrientation;
    int _previousOrientation;
    NSMutableArray *_progressViewArray;
}

@property (nonatomic,retain) id<AxPluginContext> pluginContext;

- (void)alert:(NSObject<AxPluginContext>*)context;  
- (void)confirm:(NSObject<AxPluginContext>*)context;  
- (void)prompt:(NSObject<AxPluginContext>*)context;  
- (void)pick:(NSObject<AxPluginContext>*)context;  
- (void)open:(NSObject<AxPluginContext>*)context;  
- (void)showProgress:(NSObject<AxPluginContext>*)context;  
- (void)hideProgress:(NSObject<AxPluginContext>*)context;  
- (void)showStatusBar:(NSObject<AxPluginContext>*)context;
- (void)hideStatusBar:(NSObject<AxPluginContext>*)context;

- (void)setOrientation:(NSObject<AxPluginContext>*)context;
- (void)getOrientation:(NSObject<AxPluginContext>*)context;
- (void)resetOrientation:(NSObject<AxPluginContext>*)context;

// XXX: undocumented API
- (void)addWebView:(NSObject<AxPluginContext>*)context;
- (void)removeWebView:(NSObject<AxPluginContext>*)context;
- (void)webView_getURL:(NSObject<AxPluginContext>*)context;
- (void)webView_setURL:(NSObject<AxPluginContext>*)context;
- (void)addTextView:(NSObject<AxPluginContext>*)context;
- (void)removeTextView:(NSObject<AxPluginContext>*)context;
- (void)textView_getText:(NSObject<AxPluginContext>*)context;
- (void)textView_setText:(NSObject<AxPluginContext>*)context;

@end
