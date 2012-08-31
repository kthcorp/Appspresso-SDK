//
//  MulticastWebViewDelegate.m
//  sail-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import "MulticastWebViewDelegate.h"

//
// XXX: undocumented features!
//
//#define AX_USE_IOS_UNDOCUMETED_FEATURES
#undef AX_USE_IOS_UNDOCUMETED_FEATURES
#ifdef AX_USE_IOS_UNDOCUMETED_FEATURES
@interface UIWebView (JavaScriptAlert) 
@end

@implementation UIWebView (JavaScriptAlert) 

static int _webViewDelegate_alertView_buttonIndex;

- (void)webView:(UIWebView *)sender runJavaScriptAlertPanelWithMessage:(NSString *)message initiatedByFrame:(WebFrame *)frame {
    
    UIAlertView* alertView = [[UIAlertView alloc] initWithTitle:nil message:message delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
    
    [alertView show];
    
    [alertView autorelease];    
}

- (BOOL)webView:(UIWebView *)sender runJavaScriptConfirmPanelWithMessage:(NSString *)message initiatedByFrame:(WebFrame *)frame {
    
    UIAlertView* alertView = [[UIAlertView alloc] initWithTitle:nil message:message delegate:self cancelButtonTitle:@"Cancel" otherButtonTitles:@"OK", nil];
    
    [alertView show];
    
    [alertView autorelease];
    
    while (!alertView.hidden && alertView.superview) {
		[[NSRunLoop mainRunLoop] runUntilDate:[NSDate dateWithTimeIntervalSinceNow:0.05]];
	}
    
    return _webViewDelegate_alertView_buttonIndex != alertView.cancelButtonIndex;
}

//- (NSString *)webView:(UIWebView *)sender runJavaScriptTextInputPanelWithPrompt:(NSString *)prompt defaultText:(NSString *)defaultText initiatedByFrame:(WebFrame *)frame {
//    
//}

- (void)alertView:(UIAlertView *)alertView willDismissWithButtonIndex:(NSInteger)buttonIndex {
    _webViewDelegate_alertView_buttonIndex = buttonIndex;
}

@end
#endif

@implementation MulticastWebViewDelegate

- (id)init {
    self = [super init];
    if(self) {
        _delegates = [[NSMutableArray alloc] init];
    }
    return self;
}

- (void)dealloc {
    [_delegates release];
    [super dealloc];
}

-(void)addWebViewDelegate:(id)delegate {
    if([delegate conformsToProtocol:@protocol(UIWebViewDelegate)]) {
        [_delegates addObject:delegate];
    }
}

-(void)removeWebViewDelegate:(id)delegate {
    if([delegate conformsToProtocol:@protocol(UIWebViewDelegate)]) {
        [_delegates removeObject:delegate];
    }
}

#pragma UIWebViewDelegate

- (BOOL)webView:(UIWebView *)theWebView shouldStartLoadWithRequest:(NSURLRequest *)request navigationType:(UIWebViewNavigationType)navigationType {
    for (id delegate in _delegates) {
        if([delegate respondsToSelector:@selector(webView:shouldStartLoadWithRequest:navigationType:)]) {
            return [delegate webView:theWebView shouldStartLoadWithRequest:request navigationType:navigationType];
        }
    }
    //thankyou cordova
    if (![[[request URL]scheme]isEqual:@"http"] && ![[[request URL]scheme]isEqual:@"https"]) {
        [[UIApplication sharedApplication] openURL:[request URL]];
        return NO;
    }
    return YES;
}

- (void)webViewDidStartLoad:(UIWebView *)webView {
    for (id delegate in _delegates) {
        if([delegate respondsToSelector:@selector(webViewDidStartLoad:)]) {
            [delegate webViewDidStartLoad:webView];
        }
    }
}

- (void)webViewDidFinishLoad:(UIWebView *)webView {
    for (id delegate in _delegates) {
        if([delegate respondsToSelector:@selector(webViewDidFinishLoad:)]) {
            [delegate webViewDidFinishLoad:webView];
        }
    }
}

- (void)webView:(UIWebView *)webView didFailLoadWithError:(NSError *)error {
    for (id delegate in _delegates) {
        if([delegate respondsToSelector:@selector(webView:didFailLoadWithError:)]) {
            [delegate webView:webView didFailLoadWithError:error];
        }
    }
}

@end
