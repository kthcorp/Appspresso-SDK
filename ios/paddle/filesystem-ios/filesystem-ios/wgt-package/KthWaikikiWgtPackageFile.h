//
//  KthWaikikiWgtPackageFile.h
//  filesystem-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import <Foundation/Foundation.h>
#import "KthWaikikiRealPathTypeFile.h"

#define kURIWgtPackage URI_PREFIX@"wgt-package"
#define kWWWRootPath   @"assets/ax_www"

@interface KthWaikikiWgtPackageFile : KthWaikikiRealPathTypeFile {
}
- (NSString *)toURI:(DefaultPluginResult *)result;
- (id)initWithFullPath:(NSString *)fullPath mode:(NSString *)mode result:(DefaultPluginResult*)result;
- (NSString *)realpathFromFullPath:(NSString *)fullPath;
- (NSString *)realpathFromURI:(NSString *)uri;
- (BOOL)writable;
@end
