//
//  KthWaikikiWgtPrivateTmpFile.h
//  filesystem-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import "KthWaikikiWgtPrivateTmpFile.h"

#import "AxError.h"

@interface KthWaikikiWgtPrivateTmpFile ()
@end


@implementation KthWaikikiWgtPrivateTmpFile
- (NSMutableArray*)pathComponentsRemovingPrefixAndSuffixFromString:(NSString*)str {
	NSMutableArray *components = [NSMutableArray arrayWithArray:[str pathComponents]];
	if ([str hasSuffix:@"/"]) { [components removeLastObject]; }
	if ([str hasPrefix:@"/"]) {[components removeObjectAtIndex:0];	}
	
	return components;
}

- (id)initWithFullPath:(NSString *)fullPath mode:(NSString *)mode result:(DefaultPluginResult*)result {
	(*result).succeeded = NO;	
	if ((self = [super initWithFullPath:fullPath mode:mode result:result])) {		
		NSString *tmpPath = NSTemporaryDirectory();
		NSMutableArray *components = [self pathComponentsRemovingPrefixAndSuffixFromString:fullPath];
		
		if (0 == [components count]) {
			(*result).code = AX_NOT_FOUND_ERR;
			(*result).msg = kErrMsgIoErrInvalidFilePath;
			[self release];
			return nil;
		}		
		
		[components removeObjectAtIndex:0];
		NSString *realPath = [tmpPath stringByAppendingPathComponent:[NSString pathWithComponents:components]];
		// TODO: Detach allocation code.
		[self setPropertiesWithRealPath:realPath];
		
		if (![KthWaikikiFile isValidPath:fullPath of:fullPath isFullPath:YES] || ![self isExistPath:fullPath isFullPath:YES]) {
			(*result).code = AX_NOT_FOUND_ERR;
			(*result).msg = kErrMsgIoErrInvalidFilePath;
			[self release];
			return nil;
		}
	}
	(*result).succeeded = YES;
	return self;
}

- (NSString *)toURI:(DefaultPluginResult *)result {
	NSRange range = [_virtualPath rangeOfString:kLocationWgtPrivateTmp];
	// TODO: vailidation
	(*result).succeeded = YES;
	return [_virtualPath stringByReplacingCharactersInRange:range withString:kURIWgtPrivateTmp];
}

- (NSString *)realpathFromFullPath:(NSString *)fullPath {
	NSString *tmpPath = NSTemporaryDirectory();
	NSMutableArray *components = [NSMutableArray arrayWithArray:[fullPath pathComponents]];
	[components removeObjectAtIndex:0];
	return [tmpPath stringByAppendingPathComponent:[NSString pathWithComponents:components]];
}

- (NSString *)realpathFromURI:(NSString *)uri {	
	NSRange range = [uri rangeOfString:kURIWgtPrivateTmp];	
	NSString *tmpPath = NSTemporaryDirectory();
	return [tmpPath stringByAppendingPathComponent:[uri substringFromIndex:range.length]];
}
@end