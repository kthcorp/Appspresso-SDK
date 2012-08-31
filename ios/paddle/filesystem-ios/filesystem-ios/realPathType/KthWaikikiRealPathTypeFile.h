//
//  KthWaikikiRealPathTypeFile.h
//  filesystem-ios
//
//  Copyright (c) 2012 KTH Corp.
//


#import <Foundation/Foundation.h>
#import "KthWaikikiFile.h"

#define URI_PREFIX @"/appspresso/file/"

@interface KthWaikikiRealPathTypeFile : KthWaikikiFile
{
}
- (KthWaikikiFile *)resolveWithFilePath:(NSString *)subPath result:(DefaultPluginResult *)result;
- (NSArray *)listFilesWithFilter:(NSDictionary *)filter result:(DefaultPluginResult *)result;
- (NSString *)readAsText:(NSString *)encoding result:(DefaultPluginResult *)result;
- (KthWaikikiFile *)createDirectory:(NSString *)subPath result:(DefaultPluginResult *)result;
- (KthWaikikiFile *)createFile:(NSString *)subPath result:(DefaultPluginResult *)result;
- (BOOL)deleteDirectory:(NSString *)fullPath recursive:(BOOL)recursive result:(DefaultPluginResult *)result;
- (BOOL)deleteFile:(NSString *)fullPath result:(DefaultPluginResult *)result;
- (BOOL)copyToWithSrc:(NSString *)srcFullPath des:(NSString *)desFullPath overwrite:(BOOL)overwrite result:(DefaultPluginResult *)result;
- (BOOL)moveToWithSrc:(NSString *)srcFullPath des:(NSString *)desFullPath overwrite:(BOOL)overwrite result:(DefaultPluginResult *)result;

- (BOOL)isExistPath:(NSString *)path isFullPath:(BOOL)isFullPath;
- (void)setPropertiesWithRealPath:(NSString *)realPath;

// Override
- (NSString *)toURI:(DefaultPluginResult *)result;
- (void)validate;

- (id)initWithFullPath:(NSString *)fullPath mode:(NSString *)mode result:(DefaultPluginResult*)result;
- (NSString *)realpathFromFullPath:(NSString *)fullPath;
- (NSString *)realpathFromURI:(NSString *)uri;

+ (NSString *)realpathFromFullPath:(NSString *)fullPath ofClass:(Class)cls;
+ (NSString *)realpathFromURI:(NSString *)uri ofClass:(Class)cls;
@end
