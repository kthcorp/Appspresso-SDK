//
//  ax_ext_net_MyPlugin.m
//  ax.ext.net
//
//  Copyright (c) 2012 KTH Corp.
//

#import <MessageUI/MessageUI.h>
#import <MessageUI/MFMailComposeViewController.h>
#import <MobileCoreServices/UTType.h>

#import "AxPluginContext.h"
#import "AxRuntimeContext.h"
#import "AxFileSystemManager.h"
#import "AxError.h"
#import "AxLog.h"
#import "MimeTypeUtils.h"

#import "HttpClient.h"
#import "ax_ext_net_MyPlugin.h"

@implementation ax_ext_net_MyPlugin

#define JS_CALLBACK_CURL_ONSENT @"ax.ext.net.onSent"
#define JS_CALLBACK_CURL_ONRECEIVED @"ax.ext.net.onReceived"
#define JS_CALLBACK_ONSENDMAIL @"ax.ext.net.onSendMail"

#define SUCCESS_CALLBACK @"success"
#define ERROR_CALLBACK @"error"
#define SENT_CALLBACK @"sent"
#define RECEIVED_CALLBACK @"received"

#define CALLBACK_TYPE @"type"
#define CALLBACK_PARAM @"params"

-(void)activate:(NSObject<AxRuntimeContext>*)context {
    [super activate:context];
    _curlSet = [[NSMutableDictionary alloc]init];
}

- (void)deactivate:(NSObject<AxRuntimeContext>*)context {
    [_curlSet release];
    [super deactivate:context];
}

- (void)curl:(NSObject<AxPluginContext>*)context {
    dispatch_async(dispatch_get_main_queue(), ^{
        //AX_LOG_TRACE(@"%s: opts=%@", __PRETTY_FUNCTION__, opts);
        NSString *method = [[context getParamAsString:0 name:@"method" defaultValue:@"GET"] uppercaseString];
        NSURL *url = [NSURL URLWithString:[context getParamAsString:0 name:@"url"]];
        NSDictionary *headers = [context getParamAsDictionary:0 name:@"headers"];
        NSDictionary *params = [context getParamAsDictionary:0 name:@"params"];
        NSDictionary *files = [context getParamAsDictionary:0 name:@"files"];
        NSString *download = [context getParamAsString:0 name:@"download" defaultValue:nil];
        NSStringEncoding encoding = [HttpClient encodingFromString:[context getParamAsString:0 name:@"encoding" defaultValue:@"UTF-8"]];
        BOOL sent = [context getParamAsBoolean:0 name:SENT_CALLBACK defaultValue:NO];
        BOOL received = [context getParamAsBoolean:0 name:RECEIVED_CALLBACK defaultValue:NO];

        // download
        NSString *downloadNativePath = nil;
        if ([download length] > 0) {
            downloadNativePath = [[self.runtimeContext getFileSystemManager] toNativePath:download];
            if (downloadNativePath == nil) {
                [context sendWatchError:AX_INVALID_VALUES_ERR message:[NSString stringWithFormat:@"invalid download file path: %@", download]];
                return;
            }
        }

        BOOL isUpload = [files count] > 0;
        NSMutableDictionary *uploadParams = nil;

        // upload - turn path string in files intro NSURL object, and add it into params.
        if (isUpload) {
            uploadParams = [NSMutableDictionary dictionaryWithCapacity:([params count] + [files count])];
            [uploadParams addEntriesFromDictionary:params];
            id<AxFileSystemManager> fsm = [self.runtimeContext getFileSystemManager];
            for (NSString *filename in files) {
                NSString *nativePath = [fsm toNativePath:[files valueForKey:filename]];
                if (nativePath == nil) {
                    [context sendWatchError:AX_INVALID_VALUES_ERR message:[NSString stringWithFormat:@"invalid upload file path: %@", [files valueForKey:filename]]];
                    return;
                }
                [uploadParams setValue:[NSURL fileURLWithPath:nativePath] forKey:filename];
            }
        }

        // closed under GCD blocks
        id<AxPluginContext> closedContext = context;

        // HTTP 요청을 시작하기 전에 그냥~ 리턴~
        // 이후로는 결과를 watch listener를 통해서 전달~
        [context sendResult];//ax.nop

        NSURLRequest *request = [HttpClient newHttpRequest:method
                                                       url:url
                                                   headers:headers
                                                      data:isUpload ? uploadParams : params
                                                  encoding:encoding
                                                 multipart:isUpload ? YES : NO];

        HttpClient *httpClient = [HttpClient new];
        httpClient.download = downloadNativePath;
        httpClient.encoding = encoding;

        httpClient.onSuccessBlock = ^(HttpClient *_httpClient, int status, NSString *data, NSDictionary *headers) {
            AX_LOG_TRACE(@"%s httpclient success: status=%d", __PRETTY_FUNCTION__, status);
            NSDictionary *result = [NSDictionary dictionaryWithObjectsAndKeys:
                                    data, @"data",
                                    [NSNumber numberWithInt:status], @"status",
                                    headers, @"headers",
                                    nil];
            [closedContext sendWatchResult:[NSDictionary dictionaryWithObjectsAndKeys:
                                            SUCCESS_CALLBACK, @"kind",
                                            result, @"payload", nil]];
            [_httpClient release];
        };

        httpClient.onErrorBlock = ^(HttpClient *_httpClient, int code, NSString *message) {
            AX_LOG_TRACE(@"%s httpclient error: code=%d, message=%@", __PRETTY_FUNCTION__, code, message);
            [closedContext sendWatchError:code message:message];
            [_httpClient release];
        };

        if (sent) {
            httpClient.onSentBlock = ^(HttpClient *_httpClient, int sentBytes, int totalBytes) {
                AX_LOG_TRACE(@"%s httpclient sent: %d / %d bytes", __PRETTY_FUNCTION__, sentBytes, totalBytes);
                NSArray *progress = [NSArray arrayWithObjects:[NSNumber numberWithInt:sentBytes], [NSNumber numberWithInt:totalBytes], nil];

                [closedContext sendWatchResult:[NSDictionary dictionaryWithObjectsAndKeys:
                                                SENT_CALLBACK, @"kind",
                                                progress, @"payload", nil]];
            };
        }

        if (received) {
            httpClient.onReceivedBlock = ^(HttpClient *_httpClient, int receivedBytes, int totalBytes) {
                AX_LOG_TRACE(@"%s httpclient received: %d / %d bytes", __PRETTY_FUNCTION__, receivedBytes, totalBytes);
                NSArray *progress = [NSArray arrayWithObjects:[NSNumber numberWithInt:receivedBytes], [NSNumber numberWithInt:totalBytes], nil];

                [closedContext sendWatchResult:[NSDictionary dictionaryWithObjectsAndKeys:
                                                RECEIVED_CALLBACK, @"kind",
                                                progress, @"payload", nil]];
            };
        }

        @try {
            [httpClient execute:request];
        } @catch(AxError *e) {
            AX_LOG_TRACE(@"%s httpclient error: code=%d, message=%@", __PRETTY_FUNCTION__, e.code, e.message);
            [closedContext sendWatchError:e.code message:e.message];
            [httpClient release];
        } @catch(NSException *e) {
            AX_LOG_TRACE(@"%s httpclient error: code=%d, message=%@", __PRETTY_FUNCTION__, AX_IO_ERR, e.description);
            [closedContext sendWatchError:AX_IO_ERR message:e.description];
            [httpClient release];
        } @finally {
            [request release];
        }
    });
}

- (void)__removeContext:(NSObject<AxPluginContext> *)context {
    int contextId = [[context getParamAsNumber:0] intValue];
    AX_LOG_TRACE(@"%s id=%d", __PRETTY_FUNCTION__, contextId);

    // curl 컨텍스트 객체들을 따로 맵에 넣어서 관리하지 않고 클로저로 그냥 처리하고 있다.
    // 그래서 스크립트로부터의 이 호출은 그냥 무시..
    [context sendResult];
}

- (void)sendMail:(NSObject<AxPluginContext>*)context {
	if(![MFMailComposeViewController canSendMail]) {
		[context sendError:AX_NOT_SUPPORTED_ERR message:@"cannot sent mail"];
		return;
	}

    if(_sendMailListener) {
		[context sendError:AX_INVALID_STATE_ERR message:@"duplicated call! this function is not re-enterant."];
		return;
    }

	dispatch_async(dispatch_get_main_queue(), ^{
		NSString *subject = [context getParamAsString:0 name:@"subject" defaultValue:@""];
		NSString *message = [context getParamAsString:0 name:@"message" defaultValue:@""];
		NSArray *to = [context getParamAsArray:0 name:@"to" defaultValue:[NSArray array]];
		NSArray *cc = [context getParamAsArray:0 name:@"cc" defaultValue:[NSArray array]];
		NSArray *bcc = [context getParamAsArray:0 name:@"bcc" defaultValue:[NSArray array]];
		NSArray *attachments = [context getParamAsArray:0 name:@"attachments" defaultValue:[NSArray array]];
		int listener = [context getParamAsInteger:0 name:@"listener" defaultValue:-1];
		//AX_LOG_TRACE(@"%s: subject=%@", __PRETTY_FUNCTION__, subject);

		MFMailComposeViewController* mfController = [[MFMailComposeViewController alloc] init];
		mfController.mailComposeDelegate = self;
		[mfController setSubject:subject];
		[mfController setMessageBody:message isHTML:YES];
		if(to && [to count] > 0) {
			[mfController setToRecipients:to];
		}
		if(cc && [cc count] > 0) {
			[mfController setCcRecipients:cc];
		}
		if(bcc && [bcc count] > 0) {
			[mfController setBccRecipients:bcc];
		}
		if(attachments) {
			for(NSString *attachment in attachments) {
				[mfController addAttachmentData:[NSData dataWithContentsOfFile:[[self.runtimeContext getFileSystemManager] toNativePath:attachment]]
									   mimeType:[MimeTypeUtils getMimeType:attachment]
									   fileName:[attachment lastPathComponent]];
			}
		}

        _sendMailListener = listener;

		[[self.runtimeContext getViewController] presentModalViewController:mfController animated:YES];
		[mfController release];
	});

	[context sendResult];
}

#pragma mark MFMailComposeViewControllerDelegate

- (void)mailComposeController:(MFMailComposeViewController*)controller didFinishWithResult:(MFMailComposeResult)result error:(NSError*)error {
	AX_LOG_TRACE(@"%s: result=%d", __PRETTY_FUNCTION__, result);
	[controller dismissModalViewControllerAnimated:YES];

    if(_sendMailListener != -1) {
        if(error) {
            [self.runtimeContext invokeWatchErrorListener:_sendMailListener
                                                     code:AX_IO_ERR
                                                  message:[error description]];
        } else {
            [self.runtimeContext invokeWatchSuccessListener:_sendMailListener
                                                     result:[NSNumber numberWithBool:(result == MFMailComposeResultSent)]];
        }
    }

    _sendMailListener = 0;
}

@end
