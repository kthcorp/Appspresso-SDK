//
//  KthWaikikiMessaging.m
//  messaging-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import "KthWaikikiMessaging.h"

#import "AxRuntimeContext.h"
#import "AxPluginContext.h"
#import "AxError.h"
#import "AxLog.h"

#define WAC_MESSAGE @"http://wacapps.net/api/messaging"
#define WAC_MESSAGE_SEND @"http://wacapps.net/api/messaging.send"
#define WAC_MESSAGE_FIND @"http://wacapps.net/api/messaging.find"
#define WAC_MESSAGE_SUBSCRIBE @"http://wacapps.net/api/messaging.subscribe"
#define WAC_MESSAGE_WRITE @"http://wacapps.net/api/messaging.write"

#define JS_CALLBACK_SENDMESSAGE @"deviceapis.messaging.callSuccessCallback"
#define JS_ERRORBACK_SENDMESSAGE @"deviceapis.messaging.callErrorCallback"

@implementation KthWaikikiMessaging

- (BOOL)_isActivatedFeatureMessage {
    return [[self runtimeContext]isActivatedFeature:WAC_MESSAGE];
}

- (BOOL)_isActivatedFeatureMessageSend {
    return [[self runtimeContext]isActivatedFeature:WAC_MESSAGE_SEND];
}

- (BOOL)_isActivatedFeatureMessageFind {
    return [[self runtimeContext]isActivatedFeature:WAC_MESSAGE_FIND];
}

- (BOOL)_isActivatedFeatureMessageSubscribe {
    return [[self runtimeContext]isActivatedFeature:WAC_MESSAGE_SUBSCRIBE];
}

- (BOOL)_isActivatedFeatureMessageWrite {
    return [[self runtimeContext]isActivatedFeature:WAC_MESSAGE_WRITE];
}


- (void)activate:(id<AxRuntimeContext>)runtimeContext {
    [super activate:runtimeContext];
    [runtimeContext requirePlugin:@"deviceapis"];
}

- (void)messageComposeViewController:(MFMessageComposeViewController *)controller didFinishWithResult:(MessageComposeResult)result
{
    BOOL succeeded = NO;
	switch (result)
	{
		case MessageComposeResultSent:
			succeeded = YES;
			break;
			
		case MessageComposeResultCancelled:
		case MessageComposeResultFailed:
		default:
			// TODO: faild
			break;
	}
	
	// FIXME: Corrent ViewContoller
	[[super.runtimeContext getViewController] dismissModalViewControllerAnimated:YES];
	
	// todo : it is possible, end-user delete the recipient before message send. but [controller recipients] never change...
    if (YES == succeeded) {
        [self.runtimeContext executeJavaScriptFunction:JS_CALLBACK_SENDMESSAGE,
         [NSNumber numberWithInt:_handle],[NSNumber numberWithBool:YES],[controller recipients], nil];
	} else {
        [self.runtimeContext executeJavaScriptFunction:JS_ERRORBACK_SENDMESSAGE,
         [NSNumber numberWithInt:_handle],[NSNumber numberWithBool:YES],[controller recipients], nil];
	}
    [_contextSendMessage release];
}

- (void)sendMessage:(id<AxPluginContext>)context {
	if (![self _isActivatedFeatureMessageSend] && ![self _isActivatedFeatureMessage]) {
        [context sendError:AX_SECURITY_ERR message:AX_SECURITY_ERR_MSG];
        return;
    }
    dispatch_async(dispatch_get_main_queue(), ^{
        NSArray *to = [context getParamAsArray:0 name:@"to"];
        NSString *body = [context getParamAsString:0 name:@"body"];
        KthWaikikiMessagingType type = [context getParamAsInteger:0 name:@"type"];
        
        int handle = [context getParamAsInteger:1];
        
        // TODO: unable to attach the file.
        
        // TODO: support for mms, email
        if (type != KthWaikikiMessagingTypeSMS) {
            [context sendError:AX_NOT_SUPPORTED_ERR message:AX_NOT_SUPPORTED_ERR_MSG];
            return;
        }
        
        
        if (![MFMessageComposeViewController canSendText]) {
            [context sendError:AX_NOT_SUPPORTED_ERR message:AX_NOT_SUPPORTED_ERR_MSG];
            return;
        }
        
        // TODO: Locale
        MFMessageComposeViewController *picker = [[MFMessageComposeViewController alloc] init];
        picker.messageComposeDelegate = self;
        picker.recipients = to;
        if (body && ![body isKindOfClass:[NSNull class]]) {
            picker.body = body;
        }
		
        _contextSendMessage = [context retain];
        _handle = handle;
		
        // FIXME: Corrent ViewContoller
        [[super.runtimeContext getViewController] presentModalViewController:picker animated:YES];
        [picker release];
    
    });
}

- (void)findMessage:(id<AxPluginContext>)context {
    [context sendError:AX_NOT_SUPPORTED_ERR message:AX_NOT_SUPPORTED_ERR_MSG];
}

- (void)onSMS:(id<AxPluginContext>)context {
	[context sendError:AX_NOT_SUPPORTED_ERR message:AX_NOT_SUPPORTED_ERR_MSG];
}

- (void)onMMS:(id<AxPluginContext>)context {
    [context sendError:AX_NOT_SUPPORTED_ERR message:AX_NOT_SUPPORTED_ERR_MSG];
}

- (void)onEmail:(id<AxPluginContext>)context {
    [context sendError:AX_NOT_SUPPORTED_ERR message:AX_NOT_SUPPORTED_ERR_MSG];
}

- (void)unsubscribe:(id<AxPluginContext>)context {
    [context sendError:AX_NOT_SUPPORTED_ERR message:AX_NOT_SUPPORTED_ERR_MSG];
}

- (void)update:(id<AxPluginContext>)context {
    [context sendError:AX_NOT_SUPPORTED_ERR message:AX_NOT_SUPPORTED_ERR_MSG];
}
@end
