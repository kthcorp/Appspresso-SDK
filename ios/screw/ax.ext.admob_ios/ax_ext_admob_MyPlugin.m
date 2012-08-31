//
//  ax_ext_admob_MyPlugin.m
//  ax.ext.admob
//
//  Copyright (c) 2012 KTH Corp.
//

#import "GADBannerView.h"

#import "AxRuntimeContext.h"
#import "AxPluginContext.h"
#import "AxError.h"
#import "AxLog.h"

#import "ax_ext_admob_MyPlugin.h"

@implementation ax_ext_admob_MyPlugin

- (void)activate:(NSObject<AxRuntimeContext>*)context {
    [super activate:context];
    _bannerViews = [[NSMutableDictionary alloc] init];
}

- (void)deactivate:(NSObject<AxRuntimeContext>*)context {
    for(GADBannerView *bannerView in _bannerViews) {
        [bannerView removeFromSuperview];
    }
    [_bannerViews release];
    _bannerViews = nil;
    [super deactivate:context];
}

- (void)showAdmob:(NSObject<AxPluginContext>*)context {    
    dispatch_async(dispatch_get_main_queue(), ^{
        NSString *pubId = [context getParamAsString:0];

        int position = [context getParamAsInteger:1 name:@"position" defaultValue:-1];

        int left = [context getParamAsInteger:1 name:@"left" defaultValue:0];
        int top = [context getParamAsInteger:1 name:@"top" defaultValue:-1];
        int width = [context getParamAsInteger:1 name:@"width" defaultValue:-1];
        int height = [context getParamAsInteger:1 name:@"height" defaultValue:-1];
        
        CGRect screenBounds = [self.runtimeContext getWebView].frame;
        int currentWidth = screenBounds.size.width;
        int currentHeight = screenBounds.size.height;


        UIDeviceOrientation orientation = [UIDevice currentDevice].orientation;
        
        if (orientation == UIInterfaceOrientationLandscapeLeft || orientation == UIInterfaceOrientationLandscapeRight) {   // XXX: checking landscape - althjs
            int tmp = currentHeight;
            currentHeight = currentWidth;
            currentWidth = tmp;
        }      
        
        if (position > 0 && position < 7) {
            switch (position){
                case 1: case 2: case 3:
                    top = 0;
                    break;
                case 4: case 5: case 6:
                    top = currentHeight - GAD_SIZE_320x50.height;
                    break;
            }
            switch (position){
                case 1: case 4:
                    left = 0;
                    break;
                case 2: case 5:
                    left = currentWidth / 2 - GAD_SIZE_320x50.width / 2;
                    break;
                case 3: case 6:
                    left = currentWidth - GAD_SIZE_320x50.width;
                    break;
            }
        } else {
            //AX_LOG_TRACE(@"%s: pubId=%@,left=%d,top=%d,width=%d,height=%d", __PRETTY_FUNCTION__, pubId, left, top, width, height);
            if(top < 0) {
                // by default, bottom of screen
                top = currentHeight - GAD_SIZE_320x50.height;
            }
            if(left < 0) {
                left = 0;
            }
        }
        
        if(width <= 0) {
            width = GAD_SIZE_320x50.width;
        }
        if(height <= 0) {
            height = GAD_SIZE_320x50.height;
        }
        
        
        CGRect bannerViewFrame = CGRectMake(left, top, width, height);
        GADBannerView *bannerView = [_bannerViews objectForKey:pubId];
        if(bannerView == nil) {
            // create admob
            bannerView = [[GADBannerView alloc] initWithFrame:bannerViewFrame];
            bannerView.adUnitID = pubId;
            bannerView.rootViewController = [self.runtimeContext getViewController];
            [[self.runtimeContext getWebView] addSubview:bannerView];
            [[self.runtimeContext getWebView] bringSubviewToFront:bannerView];
            [bannerView loadRequest:[GADRequest request]];
            [_bannerViews setValue:bannerView forKey:pubId];
            [bannerView release];
        } else {
            // show admob
            bannerView.frame = bannerViewFrame;
            bannerView.hidden = NO;
        }
    });

	[context sendResult];
}

- (void)hideAdmob:(NSObject<AxPluginContext>*)context {
	NSString *pubId = [context getParamAsString:0];
    AX_LOG_TRACE(@"%s: pubId=%@", __PRETTY_FUNCTION__, pubId);
    
    GADBannerView *bannerView = [_bannerViews objectForKey:pubId];
    if(bannerView == nil) {
        [context sendError:AX_INVALID_VALUES_ERR message:AX_INVALID_VALUES_ERR_MSG];
        return;
    }
    
    dispatch_async(dispatch_get_main_queue(), ^{
        bannerView.hidden = YES;
    });
    
	[context sendResult];
}

- (void)refreshAdmob:(NSObject<AxPluginContext>*)context {
	NSString *pubId = [context getParamAsString:0];
    AX_LOG_TRACE(@"%s: pubId=%@", __PRETTY_FUNCTION__, pubId);
    
    GADBannerView *bannerView = [_bannerViews objectForKey:pubId];
    if(bannerView == nil) {
        [context sendError:AX_INVALID_VALUES_ERR message:AX_INVALID_VALUES_ERR_MSG];
        return;
    }
    
    dispatch_async(dispatch_get_main_queue(), ^{
        [bannerView loadRequest:[GADRequest request]];
    });

	[context sendResult];
}

@end
