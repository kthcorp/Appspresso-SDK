//
//  KthWaikikiFileStream.h
//  filesystem-ios
//
//  Copyright (c) 2012 KTH Corp.
//


#import "KthWaikikiFileStream.h"
#import "Base64Codec.h"

#import "AxError.h"

@implementation KthWaikikiFileStream
@synthesize handle = _handle;
@synthesize fileHandle = _fileHandle;

- (void)dealloc {
	[self close];
	
	[self setFileHandle:nil];
	[self setHandle:nil];
	[super dealloc];
}

- (DefaultPluginResult)open:(NSString*)fullPath mode:(NSString *)mode encoding:(NSString*)encoding {
	// Can not reach here
	DefaultPluginResult result = {.succeeded=NO, .code=AX_UNKNOWN_ERR, .msg=kErrMsgIoErrNonValidLocation};
	return result;
}

- (void)close {
	// Can not reach here
}

- (BOOL)isEOF {
	// Can not reach here
	return YES;
}

- (unsigned long)getPosition {
	// Can not reach here
	return 0;
}

- (DefaultPluginResult)setPosition:(unsigned long)position {
	// Can not reach here
	DefaultPluginResult result = {.succeeded=NO, .code=AX_UNKNOWN_ERR, .msg=kErrMsgIoErrNonValidLocation};
	return result;
}

- (long)getBytesAvailable {
	// Can not reach here
	return 0;
}

- (DefaultPluginResult)read:(NSUInteger)count result:(NSString **)res {
	// Can not reach here
	DefaultPluginResult result = {.succeeded=NO, .code=AX_UNKNOWN_ERR, .msg=kErrMsgIoErrNonValidLocation};
	*res = nil;
	return result;
}

- (DefaultPluginResult)readBytes:(NSUInteger)count result:(NSData **)res {
	// Can not reach here
	DefaultPluginResult result = {.succeeded=NO, .code=AX_UNKNOWN_ERR, .msg=kErrMsgIoErrNonValidLocation};
	*res = nil;
	return result;
}

- (DefaultPluginResult)readBase64:(NSUInteger)count result:(NSString **)res {
	// Can not reach here
	DefaultPluginResult result = {.succeeded=NO, .code=AX_UNKNOWN_ERR, .msg=kErrMsgIoErrNonValidLocation};
	*res = nil;
	return result;
}

- (DefaultPluginResult)write:(NSString *)string {
	// Can not reach here
	DefaultPluginResult result = {.succeeded=NO, .code=AX_UNKNOWN_ERR, .msg=kErrMsgIoErrNonValidLocation};
	return result;
}

- (DefaultPluginResult)writeBytes:(NSData *)data {
	// Can not reach here
	DefaultPluginResult result = {.succeeded=NO, .code=AX_UNKNOWN_ERR, .msg=kErrMsgIoErrNonValidLocation};
	return result;
}

- (DefaultPluginResult)writeBase64:(NSString *)base64String {
	// Can not reach here
	DefaultPluginResult result = {.succeeded=NO, .code=AX_UNKNOWN_ERR, .msg=kErrMsgIoErrNonValidLocation};
	return result;
}
- (NSDictionary*)returnData {
    return [NSDictionary dictionaryWithObjectsAndKeys:
            [NSString stringWithFormat:@"%d",[_fileHandle hash]] , @"_handle",
			nil];
}

+ (KthWaikikiFileStreamMode)fileStreamModeFromString:(NSString *)mode {
	if ([mode isEqualToString:kFileStreamModeRead]) {
		return KthWaikikiFileStreamModeRead;
	} else if ([mode isEqualToString:kFileStreamModeWrite]) {
		return KthWaikikiFileStreamModeWrite;
	} else if ([mode isEqualToString:kFileStreamModeAppend]) {
		return KthWaikikiFileStreamModeAppend;
	} else {
		return KthWaikikiFileStreamModeUnknown;
	}
}
@end
