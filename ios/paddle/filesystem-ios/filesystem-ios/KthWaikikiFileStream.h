//
//  KthWaikikiFileStream.h
//  filesystem-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import <Foundation/Foundation.h>
#import "KthWaikikiFile.h"

// filestream mode
#define kFileStreamModeRead @"r"
#define kFileStreamModeWrite @"w"
#define kFileStreamModeAppend @"a"

typedef enum {
	KthWaikikiFileStreamModeUnknown,
	KthWaikikiFileStreamModeRead,
	KthWaikikiFileStreamModeWrite,
	KthWaikikiFileStreamModeAppend,
} KthWaikikiFileStreamMode;


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
@interface KthWaikikiFileStream : NSObject {
@protected
	NSString *_handle;
	unsigned long long _lastOffset;
	
@protected
	KthWaikikiFileStreamMode _mode;
	NSStringEncoding _encoding;
	NSFileHandle *_fileHandle;
}
@property (nonatomic, retain, readwrite) NSString *handle;
@property (nonatomic, retain, readwrite) NSFileHandle *fileHandle;
+ (KthWaikikiFileStreamMode)fileStreamModeFromString:(NSString *)mode;
- (NSDictionary*)returnData;
// Override depending on root location
- (BOOL)isEOF;
- (unsigned long)getPosition;
- (DefaultPluginResult)setPosition:(unsigned long)position;
- (long)getBytesAvailable;
- (DefaultPluginResult)open:(NSString*)fullPath mode:(NSString *)mode encoding:(NSString*)encoding;
- (void)close;
- (DefaultPluginResult)read:(NSUInteger)count result:(NSString **)res;
- (DefaultPluginResult)readBytes:(NSUInteger)count result:(NSData **)res;
- (DefaultPluginResult)readBase64:(NSUInteger)count result:(NSString **)res;
- (DefaultPluginResult)write:(NSString *)string;
- (DefaultPluginResult)writeBytes:(NSData *)data;
- (DefaultPluginResult)writeBase64:(NSString *)base64String;
@end
