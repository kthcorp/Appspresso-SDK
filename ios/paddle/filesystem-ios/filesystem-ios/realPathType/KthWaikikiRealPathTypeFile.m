//
//  KthWaikikiRealPathTypeFile.m
//  filesystem-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import "KthWaikikiRealPathTypeFile.h"
#import "KthWaikikiFilesystem.h"

#import "AxLog.h"
#import "AxError.h"

@interface KthWaikikiRealPathTypeFile ()
@end


@implementation KthWaikikiRealPathTypeFile
- (BOOL)isExistPath:(NSString *)path isFullPath:(BOOL)isFullPath {
	NSString *subPath = isFullPath ? [KthWaikikiFile subPath:path of:_virtualPath] : path;
	if (nil == subPath) {
		return NO;
	}
	
	NSString *target = [_nativePath stringByAppendingPathComponent:subPath];
	NSFileManager *fm = [[NSFileManager alloc] init];
	BOOL exist = [fm fileExistsAtPath:target];
	[fm release];
	
	return exist;
}

- (void)setPropertiesWithRealPath:(NSString *)realPath {
	NSFileManager *fm = [[NSFileManager alloc] init];
	NSError *error;
	NSDictionary *dict = [fm attributesOfItemAtPath:realPath error:&error];
	
	_nativePath = [realPath retain];
	
	// TODO: Consider for symbolic link.
	_isDirectory = [[dict objectForKey:NSFileType] isEqualToString:NSFileTypeDirectory] ? YES : NO;
	_isFile = !_isDirectory;
	
	[self setCreated:[dict objectForKey:NSFileCreationDate]];
	[self setModified:[dict objectForKey:NSFileModificationDate]];
	
	if (_isDirectory) {
		_length = [[fm contentsOfDirectoryAtPath:_nativePath error:&error] count];
	}
	
	if (_isFile) {
		[self setFileSize:[dict objectForKey:NSFileSize]];
	}
	
	[fm release];	
}

- (id)initWithFullPath:(NSString *)fullPath mode:(NSString *)mode result:(DefaultPluginResult*)result {
	return [super initWithFullPath:fullPath mode:mode result:result];
}

- (void)validate {
	NSFileManager *fm = [[NSFileManager alloc] init];
	NSDictionary *dict = [fm attributesOfItemAtPath:_nativePath error:nil];
	[self setModified:[dict objectForKey:NSFileModificationDate]];	
	
	if (_isDirectory) {		
		_length = [[fm contentsOfDirectoryAtPath:_nativePath error:nil] count];
	} else {
		_fileSize = [dict objectForKey:NSFileSize];
	}
	[fm release];
}

- (KthWaikikiFile *)resolveWithFilePath:(NSString *)subPath result:(DefaultPluginResult *)result {
	(*result).succeeded = NO;
	if (_isFile) {
		(*result).code = AX_IO_ERR;
        (*result).msg = kErrMsgIoErrNotADirectory;
		return nil;
	}
	if (![KthWaikikiFile isValidPath:subPath of:_virtualPath isFullPath:NO] ) {
		AX_LOG_TRACE(@"invalid path for : %@", subPath);
        (*result).code = AX_INVALID_VALUES_ERR;
        (*result).msg = kErrMsgIoErrInvalidFilePath;
		return nil;
	}
    if (![self isExistPath:subPath isFullPath:NO]) {
        (*result).code = AX_NOT_FOUND_ERR;
        (*result).msg = AX_NOT_FOUND_ERR_MSG;
		return nil;
	}
	
	(*result).succeeded = YES;
	NSString *fullPath = [_virtualPath stringByAppendingPathComponent:subPath];
	
	return [[[[self class] alloc] initWithFullPath:fullPath mode:_rwMode result:result] autorelease];
}

- (NSArray *)listFilesWithFilter:(NSDictionary *)filter result:(DefaultPluginResult *)result {
	(*result).succeeded = YES;
	
	NSString *filterName = [filter objectForKey:@"name"];
	NSNumber *filterStartModified = [filter objectForKey:@"startModified"];
	NSNumber *filterEndModified = [filter objectForKey:@"endModified"];
	NSNumber *filterStartCrated = [filter objectForKey:@"startCreated"];
	NSNumber *filterEndCreated = [filter objectForKey:@"endCreated"];
	if (0 == [filterName length]) {
		filterName = nil;
	}

	if (_isFile) {
		(*result).succeeded = NO;
		(*result).code = AX_IO_ERR;
		(*result).msg = kErrMsgIoErrNotADirectory;
		return nil;
	}
	
	BOOL hasStartModified = (filterStartModified && (-1 != [filterStartModified intValue]));
	BOOL hasEndModified = (filterEndModified && (-1 != [filterEndModified intValue]));
	BOOL hasStartCreated = (filterStartCrated && (-1 != [filterStartCrated intValue]));
	BOOL hasEndCreated = (filterEndCreated && (-1 != [filterEndCreated intValue]));
	
	BOOL hasFilter = hasStartModified || hasEndModified || hasStartCreated || hasEndCreated || filterName;	
	
	NSMutableArray *resultTemp = [[NSMutableArray alloc] init];
	NSFileManager *fm = [[NSFileManager alloc] init];
	NSError *error;
	NSMutableArray *contents = [NSMutableArray arrayWithArray:[fm contentsOfDirectoryAtPath:_nativePath error:&error]];
	
	if (hasFilter) {
		// TODO: confirm
		NSNumber *c, *m;
		NSMutableArray *remove = [[NSMutableArray alloc] init];
		for (NSString *path in contents) {
			NSAutoreleasePool *pool = [[NSAutoreleasePool alloc] init];
			NSDictionary *dict = [fm attributesOfItemAtPath:[_nativePath stringByAppendingPathComponent:path] error:&error];
			if (filterName) {
				if ([filterName rangeOfString:@"%"].location == NSNotFound) {
					if (![filterName isEqualToString:path]) {
						[remove addObject:path];
						continue;
					}
				} else {
					NSString *exp = [filterName stringByReplacingOccurrencesOfString:@"%" withString:@".*"];
					NSError *error;
					NSRegularExpression *regex = [NSRegularExpression regularExpressionWithPattern:exp
																						   options:0
																							 error:&error];
					if ([regex numberOfMatchesInString:path options:0 range:NSMakeRange(0, [path length])] == 0) {
						[remove addObject:path];
					}
					continue;
				}
			}
			
			c = [NSNumber numberWithDouble:[[dict objectForKey:NSFileCreationDate] timeIntervalSince1970] / 1000];
			m = [NSNumber numberWithDouble:[[dict objectForKey:NSFileModificationDate] timeIntervalSince1970] / 1000];
			
			if (hasStartModified && ([filterStartModified compare:m] == NSOrderedDescending)) {
				[remove addObject:path];
				continue;
			}
			if (hasEndModified && ([filterEndModified compare:m] == NSOrderedAscending)) {
				[remove addObject:path];
				continue;
			}
			if (hasStartCreated && ([filterStartCrated compare:c] == NSOrderedDescending)) {
				[remove addObject:path];
				continue;
			}
			if (hasEndCreated && ([filterEndCreated compare:c] == NSOrderedAscending)) {
				[remove addObject:path];
				continue;
			}
			[pool release];
		}
		[contents removeObjectsInArray:remove];
		[remove release];
	}
	[fm release];
	
	for (NSString *path in contents) {
		// Ignore nonvalid files
		DefaultPluginResult tr;
		KthWaikikiRealPathTypeFile *file = [[[self class] alloc] initWithFullPath:[_virtualPath stringByAppendingPathComponent:path] mode:_rwMode result:&tr];
		if (nil == file) { continue; }
		
		[resultTemp addObject:[file returnData]];
		[file release];
	}
	
	return [[[NSArray alloc] initWithArray:[resultTemp autorelease]] autorelease];
}

- (NSString *)readAsText:(NSString *)encoding result:(DefaultPluginResult *)result {
	(*result).succeeded = NO;
	(*result).code = AX_IO_ERR;

	if (_isDirectory) {
		(*result).msg = kErrMsgIoErrNotAFile;
		return nil;
	}
	
	NSError *error;
	NSStringEncoding enc = [KthWaikikiFile encodingFromString:encoding];
	if (!enc) {
		(*result).msg = kErrMsgIoErrUnknownEncoding;
		return nil;
	}
	
	NSString *contents = [NSString stringWithContentsOfFile:_nativePath encoding:enc error:&error];
	if (nil == contents) {
		(*result).code = AX_UNKNOWN_ERR;
        (*result).msg = AX_UNKNOWN_ERR_MSG;
		return nil;
	}
	
	return contents;
}

- (KthWaikikiFile *)createDirectory:(NSString *)subPath result:(DefaultPluginResult *)result {
	(*result).succeeded = NO;
	(*result).code = AX_IO_ERR;
	
	if (_isFile) {
		(*result).msg = kErrMsgIoErrInvokedCreateInFile;
		return nil;
	}
	
	if (![KthWaikikiFile isValidPath:subPath of:_virtualPath isFullPath:NO]) {
		(*result).code = AX_NOT_FOUND_ERR;
        (*result).msg = kErrMsgIoErrInvalidFilePath;
		return nil;
	}

	NSFileManager *fm = [[[NSFileManager alloc] init] autorelease];
	NSString *fullPath = [_nativePath stringByAppendingPathComponent:subPath];
	KthWaikikiFile *file;
    BOOL isDir;
	if ([fm fileExistsAtPath:fullPath isDirectory:&isDir] && isDir){
        (*result).msg = kErrMsgIoErrAlreadyExist;
        return nil;
    }
    else if ((_fileMode==KthWaikikiFileModeReadOnly) || ![fm createDirectoryAtPath:fullPath withIntermediateDirectories:YES attributes:nil error:nil]) {
        (*result).msg = kErrMsgIoErrPermissionDenied;
        return nil;
    }
    else {
		file = [self resolveWithFilePath:subPath result:result];
		if (nil == file) {
			(*result).code = AX_UNKNOWN_ERR;
            (*result).msg = AX_UNKNOWN_ERR_MSG;
            return nil;
		}
	}
	
	(*result).succeeded = YES;
	return file;
}

- (KthWaikikiFile *)createFile:(NSString *)subPath result:(DefaultPluginResult *)result {
	(*result).succeeded = NO;
	(*result).code = AX_IO_ERR;
		
	if (_isFile) {
		(*result).msg = kErrMsgIoErrInvokedCreateInFile;
		return nil;
	}

    if (![KthWaikikiFile isValidPath:subPath of:_virtualPath isFullPath:NO]) {
        (*result).code = AX_NOT_FOUND_ERR;
        (*result).msg = kErrMsgIoErrInvalidFilePath;
		return nil;
	}
	
	NSFileManager *fm = [[[NSFileManager alloc] init] autorelease];
	NSString *fullPath = [_nativePath stringByAppendingPathComponent:subPath];
	KthWaikikiFile *file;
    BOOL isDir;
	if ([fm fileExistsAtPath:fullPath isDirectory:&isDir] && !isDir){
        (*result).msg = kErrMsgIoErrAlreadyExist;
        return nil;
    }
    else if ((_fileMode==KthWaikikiFileModeReadOnly) || ![fm createFileAtPath:fullPath contents:nil attributes:nil]) {
		(*result).msg = kErrMsgIoErrPermissionDenied;
        return nil;
	} else {
		file = [self resolveWithFilePath:subPath result:result];
		if (nil == file) {
			(*result).code = AX_UNKNOWN_ERR;
            (*result).msg = AX_UNKNOWN_ERR_MSG;
            return nil;
		}
	}
	
	(*result).succeeded = YES;
	return file;
}

- (BOOL)deleteDirectory:(NSString *)fullPath recursive:(BOOL)recursive result:(DefaultPluginResult *)result {
	(*result).succeeded = NO;
	(*result).code = AX_IO_ERR;
	
	if (_fileMode==KthWaikikiFileModeReadOnly) {
        (*result).msg = kErrMsgIoErrPermissionDenied;
		return NO;
	}
	
	if (_isFile) {
		(*result).msg = kErrMsgIoErrInvokedDeletedInFile;
		return NO;
	}
	
	NSString *subPath = [KthWaikikiFile subPath:fullPath of:_virtualPath];
	if (![KthWaikikiFile isValidPath:subPath of:_virtualPath isFullPath:NO] || ![self isExistPath:subPath isFullPath:NO]) {
        (*result).code = AX_NOT_FOUND_ERR;
        (*result).msg = kErrMsgIoErrInvalidFilePath;
		return NO;
	}
	
	NSError *error = nil;
	NSFileManager *fm = [[[NSFileManager alloc] init] autorelease];
	NSString *realPath = [_nativePath stringByAppendingPathComponent:subPath];
	if (0 != [[fm contentsOfDirectoryAtPath:realPath error:&error] count] && !recursive) {
		(*result).msg = kErrMsgIoErrDirectoryNotEmpty;
		return NO;
	} 
	
	NSDictionary *dict = [fm attributesOfItemAtPath:realPath error:&error];
	if (![[dict objectForKey:NSFileType] isEqualToString:NSFileTypeDirectory]) {
        (*result).msg = kErrMsgIoErrNotADirectory;
		return NO;
	}	
	
	if (![fm removeItemAtPath:realPath error:&error]) {
		(*result).msg = kErrMsgIoErrFailedToDeleteDirectory;
		return NO;
	}
	
	(*result).succeeded = YES;
	return YES;
}

- (BOOL)deleteFile:(NSString *)fullPath result:(DefaultPluginResult *)result {
	(*result).succeeded = NO;
	(*result).code = AX_IO_ERR;
	
	if (_fileMode==KthWaikikiFileModeReadOnly) {
        (*result).msg = kErrMsgIoErrPermissionDenied;
		return NO;
	}	
	
	if (_isFile) {
		(*result).msg = kErrMsgIoErrInvokedDeletedInFile;
		return NO;
	}
	
	NSString *subPath = [KthWaikikiFile subPath:fullPath of:_virtualPath];
	if (![KthWaikikiFile isValidPath:subPath of:_virtualPath isFullPath:NO] || ![self isExistPath:subPath isFullPath:NO]) {
        (*result).code = AX_NOT_FOUND_ERR;
        (*result).msg = kErrMsgIoErrInvalidFilePath;
		return NO;
	}	
	
	// TODO: specification says the file must be under the current directory.
	// If "under" means depth 0, must check filePath is under depth 0.
	NSError *error = nil;
	NSFileManager *fm = [[[NSFileManager alloc] init] autorelease];
	NSString *realPath = [_nativePath stringByAppendingPathComponent:subPath];
	NSDictionary *dict = [fm attributesOfItemAtPath:realPath error:&error];
	if ([[dict objectForKey:NSFileType] isEqualToString:NSFileTypeDirectory]) {
        (*result).msg = kErrMsgIoErrNotAFile;
		return NO;
	}
	
	if (![fm removeItemAtPath:realPath error:&error]) {
		(*result).msg = kErrMsgIoErrFailedToDeleteDirectory;
		return NO;
	}
	
	(*result).succeeded = YES;
	return YES;
}

// Static analyzer address this method return not owning object. It is just becuase this method name start with copy-.
- (BOOL)copyToWithSrc:(NSString *)srcFullPath des:(NSString *)desFullPath overwrite:(BOOL)overwrite result:(DefaultPluginResult *)result {
	(*result).succeeded = NO;
    (*result).code = AX_NOT_FOUND_ERR;
	
	if (![KthWaikikiFilesystem validateDestFullPath:desFullPath] || ![KthWaikikiFile isValidPath:desFullPath of:desFullPath isFullPath:YES]) {
        (*result).code = AX_NOT_FOUND_ERR;
        (*result).msg = [NSString stringWithFormat:kErrMsgIoErrDesPathInvalid,desFullPath];
        return NO;
    }
    
	NSString *srcSubPath = [KthWaikikiFile subPath:srcFullPath of:_virtualPath];
	if (![KthWaikikiFile isValidPath:srcSubPath of:_virtualPath isFullPath:NO] || ![self isExistPath:srcSubPath isFullPath:NO]) {
        (*result).msg = kErrMsgIoErrInvalidFilePath;
		return NO;
	}
    
    NSFileManager *fm = [[[NSFileManager alloc] init] autorelease];
    NSError *error;
	BOOL srcIsDir, desIsDir;
	NSString *srcRealPath = [KthWaikikiFilesystem realPathFromFullPath:srcFullPath];
    [fm fileExistsAtPath:srcRealPath isDirectory:&srcIsDir];
    if ([KthWaikikiFilesystem _isDstPath:desFullPath ContainsSrcPath:srcFullPath]) {
        (*result).code = AX_IO_ERR;
        (*result).msg = kErrMsgIoErrDesPathHasSrcPath;
        return !srcIsDir;
    }
    
    NSString *desRealPath = [KthWaikikiFilesystem realPathFromFullPath:desFullPath];
	if ([fm fileExistsAtPath:desRealPath isDirectory:&desIsDir]) {
        (*result).code = AX_IO_ERR;
        if (!overwrite) {
			(*result).msg = kErrMsgIoErrCanNotOverwrite;
			return NO;
		}
        if (srcIsDir == YES || desIsDir == YES) {
            (*result).msg = kErrMsgIoErrOccureed;
            return NO;
        }
		if (![fm isDeletableFileAtPath:desRealPath]) {
			(*result).msg = kErrMsgIoErrInvokedDeletedInFile;
			return NO;
		}
		if (![fm removeItemAtPath:desRealPath error:&error]) {
			(*result).msg = kErrMsgIoErrFailedToDeleteDirectory;
			return NO;
		}
	}
	
    [fm copyItemAtPath:srcRealPath toPath:desRealPath error:&error];
    if (![fm fileExistsAtPath:desRealPath]) {
        (*result).code = AX_UNKNOWN_ERR;
        (*result).msg = AX_UNKNOWN_ERR_MSG;
        return NO;
    }
    (*result).succeeded = YES;
    return YES;
}

- (BOOL)moveToWithSrc:(NSString *)srcFullPath des:(NSString *)desFullPath overwrite:(BOOL)overwrite result:(DefaultPluginResult *)result {
	(*result).succeeded = NO;
	(*result).code = AX_NOT_FOUND_ERR;
	
	if (_fileMode==KthWaikikiFileModeReadOnly) {
        (*result).code = AX_IO_ERR;
        (*result).msg = kErrMsgIoErrPermissionDenied;
		return NO;
	}
    if (![KthWaikikiFilesystem validateDestFullPath:desFullPath] || ![KthWaikikiFile isValidPath:desFullPath of:desFullPath isFullPath:YES]) {
        (*result).code = AX_NOT_FOUND_ERR;
        (*result).msg = [NSString stringWithFormat:kErrMsgIoErrDesPathInvalid,desFullPath];
        return NO;
    }

	NSString *srcSubPath = [KthWaikikiFile subPath:srcFullPath of:_virtualPath];
	if (![KthWaikikiFile isValidPath:srcSubPath of:_virtualPath isFullPath:NO] || ![self isExistPath:srcSubPath isFullPath:NO]) {
        (*result).msg = kErrMsgIoErrInvalidFilePath;
		return NO;
	}
	NSFileManager *fm = [[[NSFileManager alloc] init] autorelease];
	NSError *error;
    BOOL srcIsDir, desIsDir;
	NSString *srcRealPath = [KthWaikikiFilesystem realPathFromFullPath:srcFullPath];
	[fm fileExistsAtPath:srcRealPath isDirectory:&srcIsDir];
    if ([KthWaikikiFilesystem _isDstPath:desFullPath ContainsSrcPath:srcFullPath]) {
        (*result).code = AX_IO_ERR;
        (*result).msg = kErrMsgIoErrDesPathHasSrcPath;
        return !srcIsDir;
    }
    
	NSString *desRealPath = [KthWaikikiFilesystem realPathFromFullPath:desFullPath];
	if ([fm fileExistsAtPath:desRealPath isDirectory:&desIsDir]) {
        (*result).code = AX_IO_ERR;
        if (!overwrite) {
			(*result).msg = kErrMsgIoErrCanNotOverwrite;
			return NO;
		}
        if (srcIsDir == YES || desIsDir == YES) {
            (*result).msg = kErrMsgIoErrOccureed;
            return NO;
        }
		if (![fm isDeletableFileAtPath:desRealPath]) {
			(*result).msg = kErrMsgIoErrInvokedDeletedInFile;
			return NO;
		}
		if (![fm removeItemAtPath:desRealPath error:&error]) {
			(*result).msg = kErrMsgIoErrFailedToDeleteDirectory;
			return NO;
		}
	}
	
    [fm moveItemAtPath:srcRealPath toPath:desRealPath error:&error];
    if (![fm fileExistsAtPath:desRealPath]){
        (*result).code = AX_UNKNOWN_ERR;
        (*result).msg = AX_UNKNOWN_ERR_MSG;
        return NO;
    }
    (*result).succeeded = YES;
	
	return YES;
}

- (NSString *)toURI:(DefaultPluginResult *)result {
	(*result).succeeded = NO;
	(*result).code = AX_UNKNOWN_ERR;
	(*result).msg = kErrMsgIoErrNonValidLocation;
	return nil;
}

- (NSString *)realpathFromFullPath:(NSString *)fullPath {
	return nil;
}

- (NSString *)realpathFromURI:(NSString *)uri {	
	return nil;
}

+ (NSString *)realpathFromFullPath:(NSString *)fullPath ofClass:(Class)cls {
	id file = [[[cls alloc] init] autorelease];
	return [file realpathFromFullPath:fullPath];
}

+ (NSString *)realpathFromURI:(NSString *)uri ofClass:(Class)cls {
	id file = [[[cls alloc] init] autorelease];
	return [file realpathFromURI:uri];
}
@end