//
//  KthWaikikiWgtPackageFile.h
//  filesystem-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import "KthWaikikiWgtPackageFile.h"

#import "AxError.h"

@interface KthWaikikiWgtPackageFile ()
@end


@implementation KthWaikikiWgtPackageFile
-(NSMutableArray*)pathComponentsRemovingPrefixAndSuffixFromString:(NSString*)str {
	NSMutableArray *components = [NSMutableArray arrayWithArray:[str pathComponents]];
	if ([str hasSuffix:@"/"]) { [components removeLastObject]; }
	if ([str hasPrefix:@"/"]) {[components removeObjectAtIndex:0];	}
	
	return components;
}

- (id)initWithFullPath:(NSString *)fullPath mode:(NSString *)mode result:(DefaultPluginResult*)result {
	(*result).succeeded = NO;
	if (![mode isEqualToString:kFileModeReadOnly]) { 
		// Read only location.
		(*result).code = AX_INVALID_VALUES_ERR;
		(*result).msg = kErrMsgIoErrPermissionDenied;
		return nil; 
	}
	
	if ((self = [super initWithFullPath:fullPath mode:mode result:result])) {		
		NSString *wwwPath = [[NSBundle mainBundle] pathForResource:kWWWRootPath ofType:nil];
		NSMutableArray *components = [self pathComponentsRemovingPrefixAndSuffixFromString:fullPath];
		
		if (0 == [components count]) {
			(*result).code = AX_NOT_FOUND_ERR;
			(*result).msg = kErrMsgIoErrInvalidFilePath;
			[self release];
			return nil;
		}		
		
		[components removeObjectAtIndex:0];
		NSString *realPath = [wwwPath stringByAppendingPathComponent:[NSString pathWithComponents:components]];
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
	NSRange range = [_virtualPath rangeOfString:kLocationWgtPackage];
	// TODO: vailidation
	(*result).succeeded = YES;
	return [_virtualPath stringByReplacingCharactersInRange:range withString:kURIWgtPackage];
}

- (NSString *)realpathFromFullPath:(NSString *)fullPath {
	NSString *wwwPath = [[NSBundle mainBundle] pathForResource:kWWWRootPath ofType:nil];
	NSMutableArray *components = [NSMutableArray arrayWithArray:[fullPath pathComponents]];
	[components removeObjectAtIndex:0];
	return [wwwPath stringByAppendingPathComponent:[NSString pathWithComponents:components]];
}

- (NSString *)realpathFromURI:(NSString *)uri {	
	NSRange range = [uri rangeOfString:kURIWgtPackage];	
	NSString *wwwPath = [[NSBundle mainBundle] pathForResource:kWWWRootPath ofType:nil];
	return [wwwPath stringByAppendingPathComponent:[uri substringFromIndex:range.length]];
}

- (BOOL)writable {
	return NO;
}
@end