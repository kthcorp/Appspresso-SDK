//
//  KthWaikikiFile.m
//  filesystem-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import "KthWaikikiFile.h"

#import "AxError.h"

@implementation KthWaikikiFile
@synthesize rwMode = _rwMode;
@synthesize handle = _handle;
@synthesize realpath = _nativePath;
@synthesize fileMode = _fileMode;
@synthesize created = _created;
@synthesize modified = _modified;
@synthesize path = _path;
@synthesize name = _name;
@synthesize fullPath = _virtualPath;
@synthesize dirty = _dirty;
@synthesize fileSize = _fileSize;
@synthesize length = _length;
@synthesize isFile = _isFile;
@synthesize isDirectory = _isDirectory;

-(NSMutableArray*)pathComponentsRemovingPrefixAndSuffixFromString:(NSString*)str {
	NSMutableArray *components = [NSMutableArray arrayWithArray:[str pathComponents]];
	if ([str hasSuffix:@"/"]) { [components removeLastObject]; }
	if ([str hasPrefix:@"/"]) {[components removeObjectAtIndex:0];	}
	
	return components;
}

- (id)init {
	if ((self = [super init])) {
		_dirty = NO;
		_nativePath = nil;
		_created = nil;
		_modified = nil;
	}
	return self;
}

- (id)initWithFullPath:(NSString *)fullPath mode:(NSString *)mode result:(DefaultPluginResult*)result {
	if (![mode isEqualToString:kFileModeReadOnly] && ![mode isEqualToString:kFileModeReadWrite]) {
		(*result).succeeded = NO;
		(*result).code = AX_UNKNOWN_ERR;
		(*result).msg = kErrMsgIoErrUnknownMode;
		return nil;
	}
	
	if ((self = [self init])) {
		NSMutableArray *components = [self pathComponentsRemovingPrefixAndSuffixFromString:fullPath];
		_virtualPath = [fullPath retain];
		if (1 == [components count]) {
			// fullpath is Root location
			_name = @"";
			_path = [fullPath copy];
		} else {
			_name = [[fullPath lastPathComponent] retain];
			_path = [[fullPath substringToIndex:[fullPath rangeOfString:_name options:NSBackwardsSearch].location] retain];
		}
		
		_handle = [fullPath copy];
		_rwMode = mode;
        NSFileManager *fm = [[NSFileManager alloc]init];
        if ([fm isReadableFileAtPath:fullPath]) {
            _fileMode = KthWaikikiFileModeReadOnly;
            if ([fm isWritableFileAtPath:fullPath]) {
                _fileMode = KthWaikikiFileModeReadWrite;
            }
        }
        else {
            _fileMode = [KthWaikikiFile fileModeFromString:mode];
        }
        [fm release];
	} else {
        (*result).succeeded = NO;
        (*result).code = AX_UNKNOWN_ERR;
        (*result).msg = AX_UNKNOWN_ERR_MSG;
    }
    
	return self;
}

- (void)dealloc {
	[self setHandle:nil];
	[self setRealpath:nil];
	[self setCreated:nil];
	[self setModified:nil];
	[self setPath:nil];
	[self setName:nil];
	[self setFullPath:nil];
	[super dealloc];
}

- (NSDictionary*)returnData {
	NSMutableDictionary *res = [NSMutableDictionary dictionaryWithObjectsAndKeys:
								_handle, @"_handle",
								[NSNumber numberWithBool:(_fileMode == KthWaikikiFileModeReadOnly) ? YES : NO], @"readOnly",
								[NSNumber numberWithBool:_isFile], @"isFile",
								[NSNumber numberWithBool:_isDirectory], @"isDirectory",
								_path, @"path",
								_name, @"name",
								_virtualPath, @"fullPath",
                                _rwMode, @"mode",
								nil];
	if (_isFile) {
		[res setObject:_fileSize forKey:@"fileSize"];
	}
	
	if (_isDirectory) {
		[res setObject:[NSNumber numberWithUnsignedInt:_length] forKey:@"length"];
	}
	
	if (_created) {
		[res setObject:[NSNumber numberWithLongLong:[_created timeIntervalSince1970]*1000] forKey:@"created"];
	} else {
		[res setObject:[NSNull null] forKey:@"created"];
	}
	
	if (_modified) {
		[res setObject:[NSNumber numberWithLongLong:[_modified timeIntervalSince1970]*1000] forKey:@"modified"];
	} else {
		[res setObject:[NSNull null] forKey:@"modified"];
	}
	
	return res;
}

/*- (NSDictionary*)returnData {
	NSMutableDictionary *res = [NSMutableDictionary dictionaryWithObjectsAndKeys:
								[NSNumber numberWithBool:_isFile], @"isFile",
								[NSNumber numberWithBool:_isDirectory], @"isDirectory",
								_path, @"path",
								_name, @"name",
								_virtualPath, @"fullPath",
                                _rwMode, @"mode",
								nil];
	return res;
}*/

- (NSDictionary*)returnDataForFileInfo {
    NSMutableDictionary *res = [NSMutableDictionary dictionaryWithObjectsAndKeys:
                                _virtualPath, @"fullPath",
								nil];
    if (_isFile) {
		[res setObject:_fileSize forKey:@"fileSize"];
	}
	
	if (_isDirectory) {
		[res setObject:[NSNumber numberWithUnsignedInt:_length] forKey:@"length"];
	}
	
	if (_created) {
		[res setObject:[NSNumber numberWithLongLong:[_created timeIntervalSince1970]*1000] forKey:@"created"];
	} else {
		[res setObject:[NSNull null] forKey:@"created"];
	}
	
	if (_modified) {
		[res setObject:[NSNumber numberWithLongLong:[_modified timeIntervalSince1970]*1000] forKey:@"modified"];
	} else {
		[res setObject:[NSNull null] forKey:@"modified"];
	}
    return res;
}

- (NSString*)description {
	return [[self returnData] description];
}

#pragma mark -
#pragma mark Basemethods

- (KthWaikikiFile *)resolveWithFilePath:(NSString *)subPath result:(DefaultPluginResult *)result {
	(*result).succeeded = NO;
	(*result).code = AX_UNKNOWN_ERR;
	(*result).msg = kErrMsgIoErrNonValidLocation;
	return nil;
}

- (NSArray *)listFilesWithFilter:(NSDictionary *)filter result:(DefaultPluginResult *)result {
	(*result).succeeded = NO;
	(*result).code = AX_UNKNOWN_ERR;
	(*result).msg = kErrMsgIoErrNonValidLocation;
	return nil;
}

- (NSString *)readAsText:(NSString *)encoding result:(DefaultPluginResult *)result {
	(*result).succeeded = NO;
	(*result).code = AX_UNKNOWN_ERR;
	(*result).msg = kErrMsgIoErrNonValidLocation;
	return nil;
}

- (KthWaikikiFile *)createDirectory:(NSString *)subPath result:(DefaultPluginResult *)result {
	(*result).succeeded = NO;
	(*result).code = AX_UNKNOWN_ERR;
	(*result).msg = kErrMsgIoErrNonValidLocation;
	return nil;
}

- (KthWaikikiFile *)createFile:(NSString *)subPath result:(DefaultPluginResult *)result {
	(*result).succeeded = NO;
	(*result).code = AX_UNKNOWN_ERR;
	(*result).msg = kErrMsgIoErrNonValidLocation;
	return nil;
}

- (BOOL)deleteDirectory:(NSString *)fullPath recursive:(BOOL)recursive result:(DefaultPluginResult *)result {
	(*result).succeeded = NO;
	(*result).code = AX_UNKNOWN_ERR;
	(*result).msg = kErrMsgIoErrNonValidLocation;
	return NO;
}

- (BOOL)deleteFile:(NSString *)fullPath result:(DefaultPluginResult *)result {
	(*result).succeeded = NO;
	(*result).code = AX_UNKNOWN_ERR;
	(*result).msg = kErrMsgIoErrNonValidLocation;
	return NO;
}

- (BOOL)copyToWithSrc:(NSString *)srcFullPath des:(NSString *)desFullPath overwrite:(BOOL)overwrite result:(DefaultPluginResult *)result {
	(*result).succeeded = NO;
	(*result).code = AX_UNKNOWN_ERR;
	(*result).msg = kErrMsgIoErrNonValidLocation;
	return NO;
}

- (BOOL)moveToWithSrc:(NSString *)srcFullPath des:(NSString *)desFullPath overwrite:(BOOL)overwrite result:(DefaultPluginResult *)result {
	(*result).succeeded = NO;
	(*result).code = AX_UNKNOWN_ERR;
	(*result).msg = kErrMsgIoErrNonValidLocation;
	return NO;
}

- (NSString *)toURI:(DefaultPluginResult *)result {
	(*result).succeeded = NO;
	(*result).code = AX_UNKNOWN_ERR;
	(*result).msg = kErrMsgIoErrNonValidLocation;
	return nil;
}

- (void)validate {
}

- (BOOL)writable {
	return YES;
}

#pragma mark -
#pragma mark Static methods
+ (NSStringEncoding)encodingFromString:(NSString *)enc {
	if (NSOrderedSame == [enc compare:kEncoding_UTF8 options:NSCaseInsensitiveSearch]) {
		return NSUTF8StringEncoding;
	} else if (NSOrderedSame == [enc compare:kEncoding_ISO8859_1 options:NSCaseInsensitiveSearch]) {
		return NSISOLatin1StringEncoding;
	} else if (NSOrderedSame == [enc compare:kEncoding_EUC_KR options:NSCaseInsensitiveSearch]) {
		return NSEUCKRStringEncoding;
	}
	
	return 0;
}

+ (KthWaikikiFileMode)fileModeFromString:(NSString *)mode {
	if ([mode isEqualToString:kFileModeReadOnly]) {
		return KthWaikikiFileModeReadOnly;
	} else if ([mode isEqualToString:kFileModeReadWrite]) {
		return KthWaikikiFileModeReadWrite;
	}
	
	return KthWaikikiFileModeUnknown;
}

+ (NSString *)subPath:(NSString *)childFullPath of:(NSString *)parentFullPath {
	NSRange range = [childFullPath rangeOfString:parentFullPath];
	if (0 != range.location) {
		return nil;
	}
	return ([childFullPath length] == range.length) ? @"" : [childFullPath substringFromIndex:range.length+1];	
}

+ (BOOL)isValidPath:(NSString *)path of:(NSString *)fullPath isFullPath:(BOOL)isFullPath {
	NSString *subPath = isFullPath ? [KthWaikikiFile subPath:path of:fullPath] : path;
	if (!subPath) {
		return NO;
	}
	
	NSMutableArray *components = [NSMutableArray arrayWithArray:[subPath pathComponents]];	
	for (NSString *comp in components) {
		// ".", ".." not allowed.
		if ([comp isEqualToString:@"."] || [comp isEqualToString:@".."]) {
			return NO;
		}
	}
	return YES;	
}
@end