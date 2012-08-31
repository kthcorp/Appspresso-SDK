//
//  ax_ext_net_MyPlugin.h
//  ax.ext.net
//
//  Copyright (c) 2012 KTH Corp.
//

#import <Foundation/Foundation.h>
#import <MessageUI/MessageUI.h>

#import "DefaultAxPlugin.h"

@interface ax_ext_net_MyPlugin : DefaultAxPlugin<MFMailComposeViewControllerDelegate> {
    NSMutableDictionary *_curlSet;
    NSString *string;
    int _sendMailListener;
}

- (void)__removeContext:(NSObject<AxPluginContext>*)context;
- (void)curl:(NSObject<AxPluginContext>*)context;
- (void)sendMail:(NSObject<AxPluginContext>*)context;

@end
