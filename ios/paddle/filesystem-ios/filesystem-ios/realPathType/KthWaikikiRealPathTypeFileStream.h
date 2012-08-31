//
//  KthWaikikiRealPathTypeFileStream.h
//  filesystem-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import <Foundation/Foundation.h>
#import "KthWaikikiFileStream.h"


/**
 * FileStream peer
 */
@interface KthWaikikiRealPathTypeFileStream : KthWaikikiFileStream {
}
- (void)setLastOffset;
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

- (Class)fileClass;
@end
