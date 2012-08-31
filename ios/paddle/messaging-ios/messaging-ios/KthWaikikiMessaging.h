//
//  KthWaikikiMessaging.h
//  messaging-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import <Foundation/Foundation.h>
#import <MessageUI/MessageUI.h>
#import "DefaultAxPlugin.h"

typedef enum {
	KthWaikikiMessagingTypeSMS = 1,
	KthWaikikiMessagingTypeMMS = 2,
	KthWaikikiMessagingTypeEMail = 3
} KthWaikikiMessagingType;

@interface KthWaikikiMessaging : DefaultAxPlugin<MFMessageComposeViewControllerDelegate> {
@private
    NSObject<AxPluginContext> *_contextSendMessage;
    int _handle;
}

@end
