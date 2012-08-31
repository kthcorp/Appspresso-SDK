//
//  ax_ext_ui_MyPlugin.m
//  ax.ext.ui
//
//  Copyright (c) 2012 KTH Corp.
//

#import "AxPluginContext.h"
#import "AxRuntimeContext.h"
#import "AxError.h"
#import "AxLog.h"

#import "ax_ext_ui_MyPlugin.h"

//=========================================================
// apple reject the app, because of following method
//=========================================================
/*
@interface UIAlertView (TextFields)
- (UITextField*)addTextFieldWithValue:(NSString*)aValue label:(NSString*)aLabel;
- (UITextField*)textFieldAtIndex:(NSInteger)index;
- (NSInteger)textFieldCount;
- (UITextField*)textField;
- (id)keyboard;
@end

@implementation UIAlertView (TextFieldsKeyboard)
- (id)keyboard {
	return nil;
}
@end
*/
//=========================================================

@implementation ax_ext_ui_MyPlugin

@synthesize pluginContext;

BOOL _customOrientationMode = NO;
//
//
//

// XXX: i18n?
#define DEF_POSITIVE @"OK"
#define DEF_NEGATIVE @"Cancel"

#define AX_ALERT 1
#define AX_CONFIRM 2
#define AX_PROMPT 3

#define DEFAULT_ORIENTATION 0
#define AX_PORTRAIT 1
#define AX_LANDSCAPE 2
#define AX_REVERSE_PORTRAIT 3
#define AX_REVERSE_LANDSCAPE 4

/*- (void)activate:(NSObject<AxRuntimeContext>*)runtimeContext {
    _customOrientationMode = NO;
}*/

- (void)activate:(NSObject<AxRuntimeContext>*)context {
    [super activate:context];
    _progressViewArray = [[NSMutableArray alloc]init];
}

- (void)deactivate:(NSObject<AxRuntimeContext>*)context {
    [super deactivate:context];
    [_progressViewArray release];
}


-(void)dealloc {
    [pluginContext release];
    [super dealloc];
}

- (void)alert:(NSObject<AxPluginContext>*)context
{
    dispatch_sync(dispatch_get_main_queue(), ^{
        [self setPluginContext:context];
        NSString *message = [context getParamAsString:0];
        NSString *title = [context getParamAsString:1 name:@"title" defaultValue:nil];
        NSString *positive = [context getParamAsString:1 name:@"positive" defaultValue:DEF_POSITIVE];
        //AX_LOG_TRACE(@"%s: message=%@", __PRETTY_FUNCTION__, message);
        UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:title
                                                            message:message 
                                                           delegate:self 
                                                  cancelButtonTitle:positive 
                                                  otherButtonTitles:nil];
        [alertView setTag:AX_ALERT];
        [alertView show];
        [alertView release];
    });
}

//
//
//

- (void)confirm:(NSObject<AxPluginContext>*)context
{
    dispatch_sync(dispatch_get_main_queue(), ^{
		[self setPluginContext:context];
        NSString *message = [context getParamAsString:0];
		NSString *title = [context getParamAsString:1 name:@"title" defaultValue:nil];
		NSString *positive = [context getParamAsString:1 name:@"positive" defaultValue:DEF_POSITIVE];
		NSString *negative = [context getParamAsString:1 name:@"negative" defaultValue:DEF_NEGATIVE];
		//AX_LOG_TRACE(@"%s: message=%@", __PRETTY_FUNCTION__, message);
        
        
		UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:title
															message:message 
														   delegate:self 
												  cancelButtonTitle:negative 
												  otherButtonTitles:positive, nil];
		[alertView setTag:AX_CONFIRM];
        [alertView show];
		[alertView release];
	});
	
}               

- (void)prompt:(NSObject<AxPluginContext>*)context
{
    dispatch_sync(dispatch_get_main_queue(), ^{
		[self setPluginContext:context];
        NSString *message = [context getParamAsString:0];
		NSString *value = [context getParamAsString:1];
		NSString *title = [context getParamAsString:2 name:@"title" defaultValue:nil];
		NSString *positive = [context getParamAsString:2 name:@"positive" defaultValue:DEF_POSITIVE];
		NSString *negative = [context getParamAsString:2 name:@"negative" defaultValue:DEF_NEGATIVE];
        NSString *placeholder = [context getParamAsString:2 name:@"placeholder" defaultValue:@""];
		//AX_LOG_TRACE(@"%s: message=%@, value=%@", __PRETTY_FUNCTION__, message, value);
		
        UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:title
															message:([[[UIDevice currentDevice] systemVersion] intValue] >= 5) ? message : [NSString stringWithFormat:@"%@\n\n", message]
														   delegate:self 
												  cancelButtonTitle:negative 
												  otherButtonTitles:positive, nil];
		[alertView setTag:AX_PROMPT];
        if ([[[UIDevice currentDevice] systemVersion] intValue] >= 5) {
            [alertView setAlertViewStyle:UIAlertViewStylePlainTextInput];
            [[alertView textFieldAtIndex:0] setText:value];
            [[alertView textFieldAtIndex:0] setPlaceholder:placeholder];
            [alertView show];
        }
        else {
            UITextField *textField = [[UITextField alloc] initWithFrame:CGRectMake(12, 68, 260, 30)]; 
            [textField setText:value];
            [textField setBorderStyle:UITextBorderStyleRoundedRect];
            [textField setPlaceholder:placeholder];
            [alertView addSubview:textField];
            [alertView show];
            [textField becomeFirstResponder];
        }
		[alertView release];
		
	});
}

//
//
//

- (void)pick:(NSObject<AxPluginContext>*)context
{
	dispatch_sync(dispatch_get_main_queue(), ^{
		[self setPluginContext:context];
        NSArray *items = [context getParamAsArray:0];
		NSString *title = [context getParamAsString:1 name:@"title"];
		NSString *cancel = [context getParamAsString:1 name:@"cancel" defaultValue:nil];//ios only
		NSString *destructive = [context getParamAsString:1 name:@"destructive" defaultValue:nil];//ios only
		//AX_LOG_TRACE(@"%s: items=%@, opts=%@", __PRETTY_FUNCTION__, items, opts);
		
		UIActionSheet *actionSheet = [[UIActionSheet alloc] initWithTitle:title
																 delegate:self
														cancelButtonTitle:nil
												   destructiveButtonTitle:nil
														otherButtonTitles:nil];
		for(NSString *item in items) {
			[actionSheet addButtonWithTitle:item];
		}
        // add cancel button after normal buttons
        if(cancel != nil && cancel.length > 0) {
            actionSheet.cancelButtonIndex = [actionSheet addButtonWithTitle:cancel];
        }
        // add destructive button after normal buttons
        if(destructive != nil && destructive.length > 0) {
            actionSheet.destructiveButtonIndex = [actionSheet addButtonWithTitle:destructive];
        }
		[actionSheet showInView:[self.runtimeContext getWebView]];
		[actionSheet release];
	});
}

//
//
//

- (void)open:(NSObject<AxPluginContext>*)context
{
	NSString *url = [context getParamAsString:0];
	//NSString *opts = [context getParamAsDictionary:1];
	AX_LOG_TRACE(@"%s: url=%@", __PRETTY_FUNCTION__, url);

    [context sendResult];
        
    // external browser(or launch app)
    [[UIApplication sharedApplication] openURL:[NSURL URLWithString:url]];
}

- (void)showProgress:(NSObject<AxPluginContext>*)context
{
	dispatch_async(dispatch_get_main_queue(), ^{
		NSString *message = [context getParamAsString:0];
        //NSString *opts = [context getParamAsDictionary:1];
		//AX_LOG_TRACE(@"%s: message=%@", __PRETTY_FUNCTION__, message);
        
		
        CGRect rect = [[UIScreen mainScreen] bounds];
        UIView * progressView = [[UIView alloc] initWithFrame:rect];
        progressView.backgroundColor = [[UIColor alloc] initWithRed:0 green:0 blue:0 alpha:0.6];
        
        UIActivityIndicatorView *indicator = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleWhiteLarge];
        indicator.center = CGPointMake(CGRectGetWidth(rect)/2, CGRectGetHeight(rect)/2);
        [progressView addSubview:indicator];
        
		if(message && message.length > 0) {
            UILabel *label = [[UILabel alloc] initWithFrame:CGRectMake(CGRectGetMinX(rect), CGRectGetMaxY(indicator.frame) + 5, CGRectGetWidth(rect), 30)];
            label.font = [UIFont systemFontOfSize:16];
            label.text = message;
            label.textColor = [UIColor whiteColor];
            label.textAlignment = UITextAlignmentCenter;
            label.backgroundColor = [UIColor clearColor];
            label.opaque = YES;
            [progressView addSubview:label];
        }
        
        [[self.runtimeContext getWebView] addSubview:progressView];
        [_progressViewArray addObject:progressView];
        [progressView release];
        [indicator startAnimating];
	});
	
	[context sendResult];
}

- (void)hideProgress:(NSObject<AxPluginContext>*)context
{
	dispatch_async(dispatch_get_main_queue(), ^{
		//AX_LOG_TRACE(@"%s: params=%@", __PRETTY_FUNCTION__, params);
		
        /*if(_progressView) {
            [_progressView removeFromSuperview];
            [_progressView release];
            _progressView = nil;
        }*/
        if ([_progressViewArray count] > 0) {
            UIView *progressView = [_progressViewArray objectAtIndex:0];
            [_progressViewArray removeObjectAtIndex:0];
            [progressView removeFromSuperview];
        }
        
	});
	
	[context sendResult];
}

- (void)showStatusBar:(NSObject<AxPluginContext>*)context
{
	dispatch_async(dispatch_get_main_queue(), ^{
        AX_LOG_TRACE(@"%s:", __PRETTY_FUNCTION__);
        [[UIApplication sharedApplication] setStatusBarHidden:NO];
        [[self.runtimeContext getWebView] setFrame:[[UIScreen mainScreen] applicationFrame]];
    });
    
	[context sendResult];
}

- (void)hideStatusBar:(NSObject<AxPluginContext>*)context
{
	dispatch_async(dispatch_get_main_queue(), ^{
        AX_LOG_TRACE(@"%s:", __PRETTY_FUNCTION__);
        [[UIApplication sharedApplication] setStatusBarHidden:YES];
        [[self.runtimeContext getWebView] setFrame:[[UIScreen mainScreen] applicationFrame]];
    });
    
	[context sendResult];
}

- (void)addWebView:(NSObject<AxPluginContext>*)context
{
    if (_webView != nil) {
        [context sendError:AX_INVALID_ACCESS_ERR message:@"No more Web View"];
        return;
    }
    NSString* url = [context getParamAsString:0 defaultValue:nil];
	//AX_LOG_TRACE(@"%s: url=%@", __PRETTY_FUNCTION__, url);
    if(url == nil) {
        [context sendError:AX_INVALID_VALUES_ERR message:AX_INVALID_VALUES_ERR_MSG];
        return;
    }


    // ui opts
    int top = [context getParamAsInteger:1 name:@"top" defaultValue:0];
    int left = [context getParamAsInteger:1 name:@"left" defaultValue:0];
    int width = [context getParamAsInteger:1 name:@"width" defaultValue:-1];
    int height = [context getParamAsInteger:1 name:@"height" defaultValue:-1];
    if(width == -1) {
        width = (int)[self.runtimeContext getWebView].bounds.size.width;
    }
    if(height == -1) {
        height = (int)[self.runtimeContext getWebView].bounds.size.height;
    }
    
    // event opts
    _webViewStartCallback = [context getParamAsInteger:1 name:@"start" defaultValue:-1];
    _webViewFinishCallback = [context getParamAsInteger:1 name:@"finish" defaultValue:-1];
    _webViewErrorCallback = [context getParamAsInteger:1 name:@"error" defaultValue:-1];
    _webViewLoadCallback = [context getParamAsInteger:1 name:@"load" defaultValue:-1];
    
    
    // TODO: returns handle for removeWebView()
    // althjs. 웹뷰 생성 전 일단 handle 리턴하고 나머지는 watch listener를 통해서 전달~ 
    [context sendResult:@"_webView"];
    
    dispatch_async(dispatch_get_main_queue(), ^{
        // TODO: support multiple textviews at once
        _webView = [[UIWebView alloc] initWithFrame:CGRectMake(left, top, width, height)];
        _webView.delegate = self;
        _webView.scalesPageToFit = YES;
        _webView.autoresizesSubviews = YES;
        
        _webViewProgressView = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleGray];
        _webViewProgressView.center = CGPointMake(width/2, height/2);
        _webViewProgressView.hidesWhenStopped = YES;
        [_webView addSubview:_webViewProgressView];

        [_webView loadRequest:[NSURLRequest requestWithURL:[NSURL URLWithString:url]]];
        [[self.runtimeContext getWebView] addSubview:_webView];

    });

}

- (void)removeWebView:(NSObject<AxPluginContext>*)context
{
    id handle = [context getParam:0];
    AX_LOG_TRACE(@"%s: handle=%@", __PRETTY_FUNCTION__, handle);

	dispatch_async(dispatch_get_main_queue(), ^{
        // TODO: support multiple webviews at once
        NSString *result = _webView.request.URL.absoluteString;
        [_webView removeFromSuperview];
        [_webView release];
        _webView = nil;
        [_webViewProgressView release];
        _webViewProgressView = nil;
        [context sendResult:result];
    });    
}

- (void)webView_getURL:(NSObject<AxPluginContext>*)context
{
    id handle = [context getParam:0];
    AX_LOG_TRACE(@"%s: handle=%@", __PRETTY_FUNCTION__, handle);
    
	dispatch_async(dispatch_get_main_queue(), ^{
        // TODO: support multiple webviews at once
        NSString *result = _webView.request.URL.absoluteString;
        [context sendResult:result];
    });    
}

- (void)webView_setURL:(NSObject<AxPluginContext>*)context
{
    id handle = [context getParam:0];
    NSString *url = [context getParamAsString:1];
    AX_LOG_TRACE(@"%s: handle=%@", __PRETTY_FUNCTION__, handle);
    
	dispatch_async(dispatch_get_main_queue(), ^{
        // TODO: support multiple webviews at once
        [_webView loadRequest:[NSURLRequest requestWithURL:[NSURL URLWithString:url]]];
        [context sendResult];
    });    
}

- (void)addTextView:(NSObject<AxPluginContext>*)context
{
    if (_textView != nil) {
        [context sendError:AX_INVALID_ACCESS_ERR message:@"No more Text View"];
        return;
    }
    NSString* text = [context getParamAsString:0];
	//AX_LOG_TRACE(@"%s: text=%@", __PRETTY_FUNCTION__, text);

    dispatch_async(dispatch_get_main_queue(), ^{
        int top = [context getParamAsInteger:1 name:@"top" defaultValue:0];
        int left = [context getParamAsInteger:1 name:@"left" defaultValue:0];
        int width = [context getParamAsInteger:1 name:@"width" defaultValue:-1];
        int height = [context getParamAsInteger:1 name:@"height" defaultValue:-1];
        int maxLength = [context getParamAsInteger:1 name:@"maxLength" defaultValue:-1];
        if(width == -1) {
            width = (int)[self.runtimeContext getWebView].bounds.size.width;
        }
        if(height == -1) {
            height = (int)[self.runtimeContext getWebView].bounds.size.height;
        }

        // TODO: support multiple textviews at once
        _textView = [[UITextView alloc] initWithFrame:CGRectMake(left, top, width, height)];
        _textView.delegate = self;
        _textView.font = [UIFont systemFontOfSize:16];
        if(text) {
            _textView.text = text;
        }
        [[self.runtimeContext getWebView] addSubview:_textView];

        if(maxLength > 0) {
            _textViewMaxLength = maxLength;
            _textViewLabel = [[UILabel alloc] initWithFrame:CGRectMake(left + width - 60, top + height - 30, 50, 30)];
            _textViewLabel.text = [NSString stringWithFormat:@"%d", _textViewMaxLength - _textView.text.length];
            _textViewLabel.textAlignment = UITextAlignmentRight;
            [[self.runtimeContext getWebView] addSubview:_textViewLabel];
        } else {
            _textViewMaxLength = -1;
            _textViewLabel = nil;
        }

        //show screen keyboard
        [_textView becomeFirstResponder];

        // TODO: returns handle for removeTextView()
        [context sendResult:@"_textView"];
    });
}

- (void)removeTextView:(NSObject<AxPluginContext>*)context
{
    id handle = [context getParam:0];
	AX_LOG_TRACE(@"%s: handle=%@", __PRETTY_FUNCTION__, handle);
    
    dispatch_async(dispatch_get_main_queue(), ^{
        // TODO: support multiple textviews at once
        NSString *result = _textView.text;        
        if(_textView) {
            [_textView removeFromSuperview];
            [_textView release];
            _textView = nil;
        }
        if(_textViewLabel) {
            [_textViewLabel removeFromSuperview];
            [_textViewLabel release];
            _textViewLabel = nil;
        }
        //returns text value
        [context sendResult:result];
    });
}

- (void)textView_getText:(NSObject<AxPluginContext>*)context
{
    id handle = [context getParam:0];
	AX_LOG_TRACE(@"%s: handle=%@", __PRETTY_FUNCTION__, handle);
    
    dispatch_async(dispatch_get_main_queue(), ^{
        // TODO: support multiple textviews at once
        NSString *result = _textView.text;        
        [context sendResult:result];
    });
}

- (void)textView_setText:(NSObject<AxPluginContext>*)context
{
    id handle = [context getParam:0];
    NSString *text = [context getParamAsString:1];
	AX_LOG_TRACE(@"%s: handle=%@,text=%@", __PRETTY_FUNCTION__, handle, text);
    
    dispatch_async(dispatch_get_main_queue(), ^{
        // TODO: support multiple textviews at once
        _textView.text = text;        
        [context sendResult];
    });
}

//
//
//

#pragma mark UIAlertViewDelegate

-(void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex {
    NSString *textFieldValue;
    if (pluginContext == nil) {
        return;
    }
    switch (alertView.tag) {
        case AX_ALERT:
            [[self pluginContext]sendResult];
            break;
        case AX_CONFIRM:
            [[self pluginContext] sendResult:[NSNumber numberWithBool:(buttonIndex != 0)]];
            break;
        case AX_PROMPT:
            if ([[[UIDevice currentDevice] systemVersion] intValue] >= 5) {
                textFieldValue = [NSString stringWithString:[[alertView textFieldAtIndex:0] text]];
            }
            else {
                for (id cls in alertView.subviews) {
                    if ([cls isKindOfClass:[UITextField class]]) {
                        textFieldValue = [NSString stringWithString:[cls text]];
                    }
                }
            }
            [[self pluginContext] sendResult:((buttonIndex != 0) ? textFieldValue : nil)];	
            break;
        default:
            break;
    }
    if (pluginContext != nil) {
        [self setPluginContext:nil];
    }
}

//
//
//

#pragma mark UIActionSheetDelegate

- (void) actionSheet:(UIActionSheet *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex {
    if (pluginContext == nil) {
        return;
    }
    
    [[self pluginContext] sendResult:[NSNumber numberWithInt:buttonIndex]];
    
    if (pluginContext != nil) {
        [self setPluginContext:nil];
    }
}

//
//
//

#pragma mark UIWebViewDelegate

#define JS_CALLBACK_WEBVIEW_ONSTART @"ax.ext.ui.onStart"
#define JS_CALLBACK_WEBVIEW_ONFINISH @"ax.ext.ui.onFinish"
#define JS_CALLBACK_WEBVIEW_ONERROR @"ax.ext.ui.onError"
#define JS_CALLBACK_WEBVIEW_ONLOAD @"ax.ext.ui.onLoad"

- (void)webViewDidStartLoad:(UIWebView *)webView {
    [_webViewProgressView startAnimating];
    if(_webViewStartCallback != -1) {
        NSString *handle = @"_webView";
        NSString *url = webView.request.URL.absoluteString;
        [self.runtimeContext executeJavaScriptFunction:JS_CALLBACK_WEBVIEW_ONSTART,
         handle, url, nil];
    }
}

- (void)webViewDidFinishLoad:(UIWebView *)webView {
    [_webViewProgressView stopAnimating];
    if(_webViewFinishCallback != -1) {
        NSString *handle = @"_webView";
        NSString *url = webView.request.URL.absoluteString;
        [self.runtimeContext executeJavaScriptFunction:JS_CALLBACK_WEBVIEW_ONFINISH,
         handle, url, nil];
    }
}

- (void)webView:(UIWebView *)webView didFailLoadWithError:(NSError *)error {
    [_webViewProgressView removeFromSuperview];
    if(_webViewErrorCallback != -1) {
        NSString *handle = @"_webView";
        NSString *url = webView.request.URL.absoluteString;
        [self.runtimeContext executeJavaScriptFunction:JS_CALLBACK_WEBVIEW_ONERROR,
         handle, url, [error description], [NSNumber numberWithInt:[error code]], nil];
    }
}

- (BOOL)webView:(UIWebView *)webView shouldStartLoadWithRequest:(NSURLRequest *)request navigationType:(UIWebViewNavigationType)navigationType {
    if(_webViewLoadCallback != -1) {
        NSString *handle = @"_webView";
        [self.runtimeContext executeJavaScriptFunction:JS_CALLBACK_WEBVIEW_ONLOAD,
         handle, [request.URL absoluteString], [NSNumber numberWithInt:navigationType], nil];
        // XXX: return NO for host without '.'(assume it custom hook)
        return [request.URL.host rangeOfString:@"."].location != NSNotFound;
    }
    //if meet URL scheme like sms:,tel: and etc, send to safari and he check this scheme.
    if (![[[request URL]scheme]isEqual:@"http"] && ![[[request URL]scheme]isEqual:@"https"]) {
        [[UIApplication sharedApplication] openURL:[request URL]];
        return NO;
    }
    return YES;
}

//
//
//

#pragma mark UITextViewDelegate

- (void)textViewDidChange:(UITextView *)textView {
    if(_textViewMaxLength > 0) {
        _textViewLabel.text = [NSString stringWithFormat:@"%d", (_textViewMaxLength - textView.text.length)];
    }
}

- (BOOL)textView:(UITextView *)textView shouldChangeTextInRange:(NSRange)range replacementText:(NSString *)text {
    if(range.length > text.length) {
        return YES;
    }
    if(textView.text.length + text.length > _textViewMaxLength){
        return NO;
    }
    return YES;
}

#pragma mark ui.orientation

-(CGRect)makeFrameForOrientation:(UIInterfaceOrientation)interfaceOrientation {
    CGFloat statusbarHeight = 0;
    CGRect windowFrame = [[[UIApplication sharedApplication]keyWindow]frame];
    
    CGRect statusBarFrame = [[UIApplication sharedApplication] statusBarFrame];
    if (statusBarFrame.size.width == 20 || statusBarFrame.size.height == 20) {
        statusbarHeight = 20; 
    }
    
    switch (interfaceOrientation) {
        //case UIInterfaceOrientationPortrait:
        //    return CGRectMake(windowFrame.origin.x, windowFrame.origin.y+statusbarHeight, windowFrame.size.width, windowFrame.size.height-statusbarHeight);
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

-(CGFloat)makeRotationAngleForOrientation:(UIInterfaceOrientation)interfaceOrientation {
    switch (interfaceOrientation) {
            //case UIInterfaceOrientationPortrait:
            //    return 0;
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

-(int)convertInterfaceOrientationToAxOrientation:(UIInterfaceOrientation)interfaceOrientation {
    switch (interfaceOrientation) {
        //case UIInterfaceOrientationPortrait:
        //    return 1;
        case UIInterfaceOrientationLandscapeLeft:
            return AX_REVERSE_LANDSCAPE;
        case UIInterfaceOrientationPortraitUpsideDown:
            return AX_REVERSE_PORTRAIT;
        case UIInterfaceOrientationLandscapeRight:
            return AX_LANDSCAPE;
        default:
            return AX_PORTRAIT;
    }
}

-(UIInterfaceOrientation)convertAxOrientationToInterfaceOrientation:(int)AxOrientation {
    switch (AxOrientation) {
        case AX_PORTRAIT :
            return UIInterfaceOrientationPortrait;
        case AX_REVERSE_LANDSCAPE :
            return UIInterfaceOrientationLandscapeLeft;
        case AX_REVERSE_PORTRAIT :
            return UIInterfaceOrientationPortraitUpsideDown;
        case AX_LANDSCAPE :
            return UIInterfaceOrientationLandscapeRight;
        default:
            return -1;
    }
}


- (void)setOrientation:(NSObject<AxPluginContext>*)context {
    _previousOrientation = _currentOrientation;
    _currentOrientation = [context getParamAsInteger:0 defaultValue:0];;
    
    UIInterfaceOrientation interfaceOrientation;
    //CGFloat rotationAngle;
    //CGRect viewBound;
    
    switch (_currentOrientation) {
        case DEFAULT_ORIENTATION:
            interfaceOrientation = [[UIDevice currentDevice]orientation];
            break;
        case AX_PORTRAIT:
            //bottom home button
            interfaceOrientation = UIInterfaceOrientationPortrait;
            //rotationAngle = 0;
            //viewBound = CGRectMake(0, 20, 768, 1004);        
            break;
        case AX_REVERSE_LANDSCAPE:
            //right home button
            //rotationAngle = -M_PI/2;
            interfaceOrientation = UIInterfaceOrientationLandscapeLeft;
            //rotationAngle = -M_PI/2;
            //viewBound = CGRectMake(20, 0, 748, 1024);
            break; 
        case AX_REVERSE_PORTRAIT:
            //top home button
            interfaceOrientation = UIInterfaceOrientationPortraitUpsideDown;
            //rotationAngle = M_PI;
            //viewBound = CGRectMake(0, 0, 768, 1004);
            break;
        case AX_LANDSCAPE:
            //left home button
            interfaceOrientation = UIInterfaceOrientationLandscapeRight;
            //rotationAngle = M_PI/2;
            //viewBound = CGRectMake(0, 0, 748, 1024);
            break;  
        default:
            _currentOrientation = _previousOrientation;
            [context sendError:0];
            return;
            break;
    }
    
    dispatch_async(dispatch_get_main_queue(), ^{
        // first, rotate status bar
        [[UIApplication sharedApplication] setStatusBarOrientation:interfaceOrientation animated:NO];
        // rotate main view, in this sample the view of navigation controller is the root view in main window
        [[self.runtimeContext getWebView] setTransform:CGAffineTransformMakeRotation([self makeRotationAngleForOrientation:interfaceOrientation])];
        // set size of view
        [[self.runtimeContext getWebView] setFrame:[self makeFrameForOrientation:interfaceOrientation]];
        
        // add viewcontrollerDelegate
        [self.runtimeContext addViewControllerDelegate:self]; 
        _customOrientationMode = YES;
    });
    [context sendResult];
}
- (void)getOrientation:(NSObject<AxPluginContext>*)context {
    if (_customOrientationMode) {
        [context sendResult:[NSNumber numberWithInt:_currentOrientation]];
    }
    else {
        //[[UIDevice currentDevice]orientation]
        [context sendResult:[NSNumber numberWithInt:[self convertInterfaceOrientationToAxOrientation:[[UIDevice currentDevice]orientation]]]];
    }
    
}
- (void)resetOrientation:(NSObject<AxPluginContext>*)context {
    dispatch_async(dispatch_get_main_queue(), ^{
        // first, rotate status bar
        [[UIApplication sharedApplication] setStatusBarOrientation:[[UIDevice currentDevice]orientation] animated:NO];
        // rotate main view, in this sample the view of navigation controller is the root view in main window
        [[self.runtimeContext getWebView] setTransform:CGAffineTransformMakeRotation([self makeRotationAngleForOrientation:[[UIDevice currentDevice]orientation]])];
        // set size of view
        [[self.runtimeContext getWebView] setFrame:[self makeFrameForOrientation:[[UIDevice currentDevice]orientation]]];
        
        // remove viewcontrollerDelegate
        _customOrientationMode = NO;
        [self.runtimeContext removeViewControllerDelegate:self];
    });
    
    [context sendResult];
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation {
    if (_customOrientationMode) {
        switch (_currentOrientation) {
            case DEFAULT_ORIENTATION :
                if (([[UIDevice currentDevice] userInterfaceIdiom] == UIUserInterfaceIdiomPhone) && (interfaceOrientation == UIInterfaceOrientationPortraitUpsideDown) ) {
                    return NO;
                }
                return YES;
                break;
            case AX_PORTRAIT:
                //UIInterfaceOrientationPortrait;
                if (interfaceOrientation == UIInterfaceOrientationPortrait) {
                    return YES;
                }
                break;
            case AX_REVERSE_LANDSCAPE:
                //UIInterfaceOrientationLandscapeLeft;
                if (interfaceOrientation == UIInterfaceOrientationLandscapeLeft) {
                    return YES;
                }
                break;    
            case AX_REVERSE_PORTRAIT:
                //UIInterfaceOrientationPortraitUpsideDown;
                if (interfaceOrientation == UIInterfaceOrientationPortraitUpsideDown) {
                    return YES;
                }
                break;
            case AX_LANDSCAPE:
                //UIInterfaceOrientationLandscapeRight;
                if (interfaceOrientation == UIInterfaceOrientationLandscapeRight) {
                    return YES;
                }
                break;
            default:
                return NO;
        }
    }
    return NO;
}

@end
