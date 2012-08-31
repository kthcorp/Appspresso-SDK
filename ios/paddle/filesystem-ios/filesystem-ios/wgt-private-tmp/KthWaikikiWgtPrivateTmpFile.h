//
//  KthWaikikiWgtPrivateTmpFile.h
//  filesystem-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import <Foundation/Foundation.h>
#import "KthWaikikiRealPathTypeFile.h"

#define kURIWgtPrivateTmp URI_PREFIX@"wgt-private-tmp"

@interface KthWaikikiWgtPrivateTmpFile : KthWaikikiRealPathTypeFile {
}
- (NSString *)toURI:(DefaultPluginResult *)result;
- (id)initWithFullPath:(NSString *)fullPath mode:(NSString *)mode result:(DefaultPluginResult*)result;
- (NSString *)realpathFromFullPath:(NSString *)fullPath;
- (NSString *)realpathFromURI:(NSString *)uri;
@end
