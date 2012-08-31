//
//  KthWaikikiDocumentsFile.m
//  filesystem-ios
//
//  Copyright (c) 2012 KTH Corp.
//


#import "KthWaikikiDocumentsFile.h"

#import "AxError.h"

@interface KthWaikikiDocumentsFile ()
@end


@implementation KthWaikikiDocumentsFile
- (id)initWithFullPath:(NSString *)fullPath mode:(NSString *)mode result:(DefaultPluginResult*)result {
	(*result).succeeded = NO;
	if ((self = [super initWithFullPath:fullPath mode:mode result:result])) {		
		NSString *document = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) objectAtIndex:0];
		NSString *realPath = [document stringByAppendingPathComponent:fullPath];
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
	(*result).succeeded = YES;
	NSArray *components = [_virtualPath pathComponents];
	NSRange range;
	
	if ([kLocationDocuments isEqualToString:[components objectAtIndex:0]]) {
		range = [_virtualPath rangeOfString:kLocationDocuments];	
		return [_virtualPath stringByReplacingCharactersInRange:range withString:kURIDocuments];
	} else if  ([kLocationImages isEqualToString:[components objectAtIndex:0]]) {
		range = [_virtualPath rangeOfString:kLocationImages];	
		return [_virtualPath stringByReplacingCharactersInRange:range withString:kURIImages];
	} else if  ([kLocationVideos isEqualToString:[components objectAtIndex:0]]) {
		range = [_virtualPath rangeOfString:kLocationVideos];	
		return [_virtualPath stringByReplacingCharactersInRange:range withString:kURIVideos];
	} else if  ([kLocationMusic isEqualToString:[components objectAtIndex:0]]) {
		range = [_virtualPath rangeOfString:kLocationMusic];	
		return [_virtualPath stringByReplacingCharactersInRange:range withString:kURIMusic];
	} else if  ([kLocationDownloads isEqualToString:[components objectAtIndex:0]]) {
		range = [_virtualPath rangeOfString:kLocationDownloads];	
		return [_virtualPath stringByReplacingCharactersInRange:range withString:kURIDownloads];
	} else if  ([kLocationWgtPrivate isEqualToString:[components objectAtIndex:0]]) {
		range = [_virtualPath rangeOfString:kLocationWgtPrivate];	
		return [_virtualPath stringByReplacingCharactersInRange:range withString:kURIWgtPrivate];
	} else {
		(*result).succeeded = NO;
		(*result).code = AX_UNKNOWN_ERR;
		(*result).msg = kErrMsgIoErrUnknownURI;
		return nil;
	}
}

- (NSString *)realpathFromFullPath:(NSString *)fullPath {
	NSString *document = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) objectAtIndex:0];
	return [document stringByAppendingPathComponent:fullPath];
}

- (NSString *)realpathFromURI:(NSString *)uri {	
	NSString *r;
	
	if ([[uri substringToIndex:[kURIDocuments length]] isEqualToString:kURIDocuments]) {
		NSRange range = [uri rangeOfString:kURIDocuments];	
		r = [uri stringByReplacingCharactersInRange:range withString:kLocationDocuments];
	} else if ([[uri substringToIndex:[kURIImages length]] isEqualToString:kURIImages]) {
		NSRange range = [uri rangeOfString:kURIImages];	
		r = [uri stringByReplacingCharactersInRange:range withString:kLocationImages];	
	} else if ([[uri substringToIndex:[kURIVideos length]] isEqualToString:kURIVideos]) {
		NSRange range = [uri rangeOfString:kURIVideos];	
		r = [uri stringByReplacingCharactersInRange:range withString:kLocationVideos];	
	} else if ([[uri substringToIndex:[kURIMusic length]] isEqualToString:kURIMusic]) {
		NSRange range = [uri rangeOfString:kURIMusic];	
		r = [uri stringByReplacingCharactersInRange:range withString:kLocationMusic];		
	} else if ([[uri substringToIndex:[kURIDownloads length]] isEqualToString:kURIDownloads]) {
		NSRange range = [uri rangeOfString:kURIDownloads];	
		r = [uri stringByReplacingCharactersInRange:range withString:kLocationDownloads];		
	} else if ([[uri substringToIndex:[kURIWgtPrivate length]] isEqualToString:kURIWgtPrivate]) {
		NSRange range = [uri rangeOfString:kURIWgtPrivate];	
		r = [uri stringByReplacingCharactersInRange:range withString:kLocationWgtPrivate];		
	} else {
		return nil;
	}
	
	NSString *base = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) objectAtIndex:0];
	return [base stringByAppendingPathComponent:r];
}
@end