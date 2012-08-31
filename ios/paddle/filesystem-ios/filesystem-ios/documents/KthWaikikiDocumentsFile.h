//
//  KthWaikikiDocumentsFile.h
//  filesystem-ios
//
//  Copyright (c) 2012 KTH Corp.
//


#import <Foundation/Foundation.h>
#import "KthWaikikiRealPathTypeFile.h"

#define kURIDocuments  URI_PREFIX@"documents"
#define kURIImages     URI_PREFIX@"images"
#define kURIVideos     URI_PREFIX@"videos"
#define kURIMusic      URI_PREFIX@"music"
#define kURIWgtPrivate URI_PREFIX@"wgt-private"
#define kURIDownloads  URI_PREFIX@"downloads"


@interface KthWaikikiDocumentsFile : KthWaikikiRealPathTypeFile {
}
- (NSString *)toURI:(DefaultPluginResult *)result;
- (id)initWithFullPath:(NSString *)fullPath mode:(NSString *)mode result:(DefaultPluginResult*)result;
- (NSString *)realpathFromFullPath:(NSString *)fullPath;
- (NSString *)realpathFromURI:(NSString *)uri;
@end
