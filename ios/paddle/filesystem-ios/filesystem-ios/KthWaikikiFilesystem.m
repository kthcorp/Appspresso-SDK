//
//  KthWaikikiFilesystem.m
//  filesystem-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import "KthWaikikiFileSystem.h"

// file
#import "KthWaikikiFile.h"
#import "KthWaikikiDocumentsFile.h"
#import "KthWaikikiWgtPackageFile.h"
#import "KthWaikikiWgtPrivateTmpFile.h"

// streams
#import "KthWaikikiFileStream.h"
#import "KthWaikikiDocumentsFileStream.h"
#import "KthWaikikiWgtPackageFileStream.h"
#import "KthWaikikiWgtPrivateTmpFileStream.h"

#import "AxRuntimeContext.h"
#import "AxPluginContext.h"
#import "AxLog.h"
#import "AxError.h"

// Max file chache size
#define kMaxFileCacheSize 64

// Custom key
//#define kCustomParamKeyHandle @"handle"
#define kCustomParamKeyFullPath @"fullPath"
#define kCustomParamKeyMode @"mode"
//#define kCustomParamKeyReadOnly @"readOnly"

#define WAC_FILESYSTEM @"http://wacapps.net/api/filesystem"
#define WAC_FILESYSTEM_READ @"http://waclapps.net/api/filesystem.read"
#define WAC_FILESYSTEM_WRITE @"http://wacapps.net/api/filesystem.write"

// 캐시...
NSMutableDictionary *_streams = nil;

@implementation KthWaikikiFilesystem

- (BOOL)_isActivatedFeatureFileSystem {
    return [[self runtimeContext]isActivatedFeature:WAC_FILESYSTEM];
}

- (BOOL)_isActivatedFeatureFileSystemRead {
    return [[self runtimeContext]isActivatedFeature:WAC_FILESYSTEM_READ];
}

- (BOOL)_isActivatedFeatureFileSystemWrite {
    return [[self runtimeContext]isActivatedFeature:WAC_FILESYSTEM_WRITE];
}

- (void)activate:(id<AxRuntimeContext>)runtimeContext
{
    [super activate:runtimeContext];
    [runtimeContext requirePlugin:@"deviceapis"];

	_streams = [[NSMutableDictionary alloc] init];
	
	// Make directories (TODO: set to documents root)
	NSString *document = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) objectAtIndex:0];
	
	// location specifiers (TODO: set to instance)
	NSString *documents = [document stringByAppendingPathComponent:kLocationDocuments];
	NSString *images = [document stringByAppendingPathComponent:kLocationImages];
	NSString *videos = [document stringByAppendingPathComponent:kLocationVideos];
	NSString *music = [document stringByAppendingPathComponent:kLocationMusic];
	NSString *downloads = [document stringByAppendingPathComponent:kLocationDownloads];
	NSString *private = [document stringByAppendingPathComponent:kLocationWgtPrivate];
	
	// check directories.
	NSFileManager *fm = [[NSFileManager alloc] init];
	if (![fm fileExistsAtPath:documents])	{ [fm createDirectoryAtPath:documents withIntermediateDirectories:NO attributes:nil error:nil]; }
	if (![fm fileExistsAtPath:images])		{ [fm createDirectoryAtPath:images withIntermediateDirectories:NO attributes:nil error:nil]; }
	if (![fm fileExistsAtPath:videos])		{ [fm createDirectoryAtPath:videos withIntermediateDirectories:NO attributes:nil error:nil]; }
	if (![fm fileExistsAtPath:music])		{ [fm createDirectoryAtPath:music withIntermediateDirectories:NO attributes:nil error:nil]; }
	if (![fm fileExistsAtPath:downloads])	{ [fm createDirectoryAtPath:downloads withIntermediateDirectories:NO attributes:nil error:nil]; }
	if (![fm fileExistsAtPath:private])		{ [fm createDirectoryAtPath:private withIntermediateDirectories:NO attributes:nil error:nil]; }
	
	// Remove tmp directory contents
	NSString *tmp = NSTemporaryDirectory();
	NSArray *tmpTargets = [fm contentsOfDirectoryAtPath:tmp error:NULL];
	for (NSString *t in tmpTargets) {
		[fm removeItemAtPath:[tmp stringByAppendingPathComponent:t] error:NULL];
	}
	
	[fm release];
}

+ (KthWaikikiFile *)getFileWithCustomInfo:(NSDictionary *)info result:(DefaultPluginResult *)result
{
	NSString *fullPath = [info objectForKey:kCustomParamKeyFullPath];
	NSString *mode = [info objectForKey:kCustomParamKeyMode];
    //BOOL readOnly = [[info objectForKey:kCustomParamKeyReadOnly] boolValue];
	//KthWaikikiFileMode mode = readOnly ? KthWaikikiFileModeReadOnly : KthWaikikiFileModeReadWrite;
	
    KthWaikikiFile *file = [KthWaikikiFilesystem fileWithFullPath:fullPath mode:mode result:result];
    
    if (file == nil) { //eg.removable
        return nil;
    }
    
    if (file.rwMode != mode) {
		if ([mode isEqualToString:kFileModeReadWrite] && ![file writable]) {
			(*result).msg = kErrMsgIoErrPermissionDenied;
			(*result).code = AX_IO_ERR;
			(*result).succeeded = NO;
			return nil;
		} else {
			file.rwMode = mode;
		}
	}
	
	return file;
}


#pragma mark FileSystemManager implementation

/****************************************************************************************************************************************/
/* PendingOperation resolve(FileSystemSuccessCallback successCallback, ErrorCallback errorCallback, DOMString location, DOMString mode) */
/****************************************************************************************************************************************/

- (void)getMaxPathLength:(id<AxPluginContext>)context
{
	[context sendResult:[NSNumber numberWithInt:PATH_MAX]];
}

- (void)resolve:(id<AxPluginContext>)context
{
    NSString *location = [KthWaikikiFilesystem _removeLastPathDelimiter:[context getParamAsString:0]];
	NSString *mode = [context getParamAsString:1];
	
	DefaultPluginResult result;
	NSDictionary *custom = [NSDictionary dictionaryWithObjectsAndKeys:
							//location, kCustomParamKeyHandle,
							location, kCustomParamKeyFullPath,
                            mode, kCustomParamKeyMode,
							//[NSNumber numberWithBool:[KthWaikikiFile fileModeFromString:mode]==KthWaikikiFileModeReadOnly], kCustomParamKeyReadOnly, 
							nil];
	KthWaikikiFile *file = [KthWaikikiFilesystem getFileWithCustomInfo:custom result:&result];
	if (nil == file) {
		[context sendError:result.code message:result.msg];
	} else {
		[context sendResult:[file returnData]];
	}
}

- (void)getParent:(id<AxPluginContext>)context {
	NSString *fullPath = [context getParamAsString:0 name:kCustomParamKeyFullPath];
    NSString *mode = [context getParamAsString:0 name:kCustomParamKeyMode];
	NSMutableArray *components = [KthWaikikiFilesystem pathComponentsRemovingPrefixAndSuffixFromString:fullPath];
    
	NSInteger count = [components count];
	if (2 > count) {
		[context sendResult:[NSNull null]];
		return;
	}
	[components removeLastObject];
	
	NSString *parentFullPath = [NSString pathWithComponents:components];
	[components objectAtIndex:count-2];
	
    DefaultPluginResult result;
    KthWaikikiFile *file =[KthWaikikiFilesystem fileWithFullPath:parentFullPath mode:mode result:&result];
    if (!file) {
        [context sendError:result.code message:result.msg];
        return;
    }
	[context sendResult:[file returnData]];
}

- (void)getReadOnly:(id<AxPluginContext>)context {
	NSString *fullPath = [context getParamAsString:0 name:kCustomParamKeyFullPath];
    NSString *mode = [context getParamAsString:0 name:kCustomParamKeyMode];
	
    DefaultPluginResult result;
    KthWaikikiFile *file =[KthWaikikiFilesystem fileWithFullPath:fullPath mode:mode result:&result];
    if (!file) {
        [context sendError:result.code message:result.msg];
        return;
    }
	[context sendResult: [NSNumber numberWithBool:(file.fileMode == KthWaikikiFileModeReadOnly) ? YES : NO]];
}

- (void)getCreated:(id<AxPluginContext>)context {
	NSString *fullPath = [context getParamAsString:0 name:kCustomParamKeyFullPath];
    NSString *mode = [context getParamAsString:0 name:kCustomParamKeyMode];
	
    DefaultPluginResult result;
    KthWaikikiFile *file =[KthWaikikiFilesystem fileWithFullPath:fullPath mode:mode result:&result];
    if (!file) {
        [context sendError:result.code message:result.msg];
        return;
    }
    if (file.created) {
        [context sendResult:[NSNumber numberWithLongLong:[file.created timeIntervalSince1970]*1000]];
        return;
	}
    [context sendResult];
	
	
}

- (void)getModified:(id<AxPluginContext>)context {
	NSString *fullPath = [context getParamAsString:0 name:kCustomParamKeyFullPath];
    NSString *mode = [context getParamAsString:0 name:kCustomParamKeyMode];
	
    DefaultPluginResult result;
    KthWaikikiFile *file =[KthWaikikiFilesystem fileWithFullPath:fullPath mode:mode result:&result];
    if (!file) {
        [context sendError:result.code message:result.msg];
        return;
    }
	
    if (file.modified) {
        [context sendResult:[NSNumber numberWithLongLong:[file.modified timeIntervalSince1970]*1000]];
        return;
	} 
    [context sendResult];
}

- (void)getFileSize:(id<AxPluginContext>)context {
	NSString *fullPath = [context getParamAsString:0 name:kCustomParamKeyFullPath];
    NSString *mode = [context getParamAsString:0 name:kCustomParamKeyMode];
	
    DefaultPluginResult result;
    KthWaikikiFile *file =[KthWaikikiFilesystem fileWithFullPath:fullPath mode:mode result:&result];
    if (!file) {
        [context sendError:result.code message:result.msg];
        return;
    }
    
    if (file.isFile) {
        [context sendResult:file.fileSize];
        return;
	}
	[context sendResult];
}

- (void)getLength:(id<AxPluginContext>)context {
	NSString *fullPath = [context getParamAsString:0 name:kCustomParamKeyFullPath];
    NSString *mode = [context getParamAsString:0 name:kCustomParamKeyMode];
	
    DefaultPluginResult result;
    KthWaikikiFile *file =[KthWaikikiFilesystem fileWithFullPath:fullPath mode:mode result:&result];
    if (!file) {
        [context sendError:result.code message:result.msg];
        return;
    }
	[context sendResult:[file returnData]];
    if (file.isDirectory) {
		[context sendResult:[NSNumber numberWithUnsignedInt:file.length]];
        return;
	}
    [context sendResult];
}


#pragma mark File implementation

/*********************************************************************************************************************************************************************/
/* DOMString toURI()                                                                                                                                                 */
/* PendingOperation listFiles(FileSystemListSuccessCallback successCallback, ErrorCallback errorCallback, FileFilter filter)                                         */
/* PendingOperation openStream(FileOpenSuccessCallback successCallback, ErrorCallback errorCallback, DOMString mode, DOMString encoding)                             */
/* PendingOperation readAsText(ReadFileAsStringSuccessCallback successCallback, ErrorCallback errorCallback, DOMString encoding)                                     */
/* PendingOperation copyTo(SuccessCallback successCallback, ErrorCallback errorCallback, DOMString originFilePath, DOMString destinationFilePath, boolean overwrite) */
/* PendingOperation moveTo(SuccessCallback successCallback, ErrorCallback errorCallback, DOMString originFilePath, DOMString destinationFilePath, boolean overwrite) */
/* File createDirectory(DOMString dirPath)                                                                                                                           */
/* File createFile(DOMString filePath)                                                                                                                               */
/* File resolve(DOMString filePath)                                                                                                                                  */
/* PendingOperation deleteDirectory(SuccessCallback successCallback, ErrorCallback errorCallback, DOMString directory, boolean recursive)                            */
/* PendingOperation deleteFile(SuccessCallback successCallback, ErrorCallback errorCallback, DOMString file)                                                         */
/*********************************************************************************************************************************************************************/
- (void)toURI:(id<AxPluginContext>)context {
	NSDictionary *custom = [context getParamAsDictionary:0];

	DefaultPluginResult result;
	KthWaikikiFile *file = [[KthWaikikiFilesystem getFileWithCustomInfo:custom result:&result] retain];
	if (nil == file) {
		[context sendError:result.code message:result.msg];
		return;
	}

	NSString *uri = [file toURI:&result];
	[file release];
	
	if (nil == uri) {
		[context sendError:result.code message:result.msg];
	} else {
		[context sendResult:uri];
	}
}

- (void)listFiles:(id<AxPluginContext>)context {
    NSDictionary *custom = [context getParamAsDictionary:0];
    
    NSString *filterName = [context getParamAsString:1 name:@"name"];//[filter objectForKey:@"name"];
	NSNumber *filterStartModified = [context getParamAsNumber:1 name:@"startModified" defaultValue:[NSNumber numberWithInt:-1]];
	NSNumber *filterEndModified = [context getParamAsNumber:1 name:@"endModified" defaultValue:[NSNumber numberWithInt:-1]];
	NSNumber *filterStartCrated = [context getParamAsNumber:1 name:@"startCreated" defaultValue:[NSNumber numberWithInt:-1]];
	NSNumber *filterEndCreated = [context getParamAsNumber:1 name:@"endCreated" defaultValue:[NSNumber numberWithInt:-1]];
		
	DefaultPluginResult result;
	KthWaikikiFile *file = [[KthWaikikiFilesystem getFileWithCustomInfo:custom result:&result] retain];
	if (nil == file) {
		[context sendError:result.code message:result.msg];
        return;
	}	
    
    NSDictionary *filter = [[NSDictionary alloc]initWithObjectsAndKeys:
                            filterName, @"name", filterStartModified, @"startModified", filterEndModified, @"endModified", filterStartCrated, @"startCreated", filterEndCreated, @"endCreated", nil];
    AX_LOG_TRACE(@"filter = %@", [filter description]);
    
	NSArray *files = [file listFilesWithFilter:filter result:&result];
	[file release];
	[filter release];
	if (nil == files) {
		[context sendError:result.code message:result.msg];
	} else {
		[context sendResult:files];
	}
}

- (void)openStream:(id<AxPluginContext>)context {
    NSDictionary *custom = [context getParamAsDictionary:0];
	NSString *mode = [context getParamAsString:1];
	NSString *encoding = [context getParamAsString:2];
	
    if ([self _isActivatedFeatureFileSystemWrite] && ![self _isActivatedFeatureFileSystem] && ![self _isActivatedFeatureFileSystemRead]) {
        if ([mode isEqualToString:kFileStreamModeRead]) {
            [context sendError:AX_SECURITY_ERR message:AX_SECURITY_ERR_MSG];
            return;
        }
    }
    
    if ([self _isActivatedFeatureFileSystemRead] && ![self _isActivatedFeatureFileSystem] && ![self _isActivatedFeatureFileSystemWrite]) {
        if ([mode isEqualToString:kFileStreamModeWrite] || [mode isEqualToString:kFileStreamModeAppend]) {
            [context sendError:AX_SECURITY_ERR message:AX_SECURITY_ERR_MSG];
            return;
        }
    }
    
	DefaultPluginResult result;
	KthWaikikiFile *file = [KthWaikikiFilesystem getFileWithCustomInfo:custom result:&result];
	if (nil == file) {
		[context sendError:result.code message:result.msg];
		return;
	}	

	if (file.isDirectory) {
		[context sendError:AX_IO_ERR message:kErrMsgIoErrNotAFile];
		return;
	}
	
	if (([KthWaikikiFileStream fileStreamModeFromString:mode] != KthWaikikiFileStreamModeRead) && file.fileMode == KthWaikikiFileModeReadOnly) {
		[context sendError:AX_IO_ERR message:kErrMsgIoErrPermissionDenied];
		return;
	}
	
	[file retain];
	KthWaikikiFileStream * stream = [KthWaikikiFilesystem fileStreamWithFile:file];

	if (!stream) {
		[context sendError:AX_UNKNOWN_ERR message:kErrMsgIoErrFSNotOpend];
        [file release];
        return;
	} 
    result = [stream open:file.fullPath mode:mode encoding:encoding];
	[file release];
	
	if (result.succeeded) {
		[_streams setObject:stream forKey:[NSString stringWithFormat:@"%d",[stream.fileHandle hash]]];
		[context sendResult:[stream returnData]];
	} else {
		[context sendError:result.code message:result.msg];
	}
}

-(void)readAsText:(id<AxPluginContext>)context {
    if ([self _isActivatedFeatureFileSystemWrite] && ![self _isActivatedFeatureFileSystem] && ![self _isActivatedFeatureFileSystemRead]) {
        [context sendError:AX_SECURITY_ERR message:AX_SECURITY_ERR_MSG];
        return;
    }

    NSDictionary *custom = [context getParamAsDictionary:0];
	NSString *encoding = [context getParamAsString:1];
	
	DefaultPluginResult result;
	KthWaikikiFile *file = [[KthWaikikiFilesystem getFileWithCustomInfo:custom result:&result] retain];
	if (nil == file) {
		[context sendError:result.code message:result.msg];
		return;
	}
	
	NSString *text = [file readAsText:encoding result:&result];
	[file release];	
	
	if (nil == text) {
		[context sendError:result.code message:result.msg];		
	} else {
		[context sendResult:text];
	}
}

-(void)copyTo:(id<AxPluginContext>)context {
	if ([self _isActivatedFeatureFileSystemRead] && ![self _isActivatedFeatureFileSystem] && ![self _isActivatedFeatureFileSystemWrite]) {
        [context sendError:AX_SECURITY_ERR message:AX_SECURITY_ERR_MSG];
        return;
    }
    NSDictionary *custom = [context getParamAsDictionary:0];
	NSString *src = [context getParamAsString:1];
	NSString *des = [context getParamAsString:2];
	BOOL overwrite = [context getParamAsBoolean:3];
	
	DefaultPluginResult result;
	KthWaikikiFile *file = [[KthWaikikiFilesystem getFileWithCustomInfo:custom result:&result] retain];
	if (nil == file) {
		[context sendError:result.code message:result.msg];
		return;
	}
    
    //des 가 wgt_package 이면 불가!
    NSArray* components = [NSArray arrayWithArray:[des pathComponents]];
    if ([[components objectAtIndex:0]isEqualToString:kLocationWgtPackage]) {
        [context sendError:AX_IO_ERR message:kErrMsgIoErrPermissionDenied];
        return; 
    }
    
	BOOL succeeded = [file copyToWithSrc:src des:des overwrite:overwrite result:&result];
	[file release];
	
	if (succeeded == NO) {
		[context sendError:result.code message:result.msg];
	} else {
		//[KthWaikikiFilesystem addFile:copiedFile];
		[context sendResult];		
	}
}

-(void)moveTo:(id<AxPluginContext>)context {
	if ([self _isActivatedFeatureFileSystemRead] && ![self _isActivatedFeatureFileSystem] && ![self _isActivatedFeatureFileSystemWrite]) {
        [context sendError:AX_SECURITY_ERR message:AX_SECURITY_ERR_MSG];
        return;
    }
    NSDictionary *custom = [context getParamAsDictionary:0];
	NSString *src = [context getParamAsString:1];
	NSString *des = [context getParamAsString:2];
	BOOL overwrite = [context getParamAsBoolean:3];
    
    //rootPath 는 불가!
    NSArray* components = [NSArray arrayWithArray:[src pathComponents]];
    if ([components count] <= 1) {
        [context sendError:AX_NOT_FOUND_ERR message:kErrMsgIoErrInvalidFilePath];
        return; 
    }
    
	DefaultPluginResult result;
	KthWaikikiFile *file = [[KthWaikikiFilesystem getFileWithCustomInfo:custom result:&result] retain];
	if (nil == file) {
		[context sendError:result.code message:result.msg];
		return;
	}
    
    //des 가 wgt_package 이면 불가!
    components = [NSArray arrayWithArray:[des pathComponents]];
    if ([[components objectAtIndex:0]isEqualToString:kLocationWgtPackage]) {
        [context sendError:AX_IO_ERR message:kErrMsgIoErrPermissionDenied];
        return; 
    }
    
	BOOL succeeded = [file moveToWithSrc:src des:des overwrite:overwrite result:&result];
	[file release];
	
	if (succeeded == NO) {
		[context sendError:result.code message:result.msg];
	} else {
		[file setDirty:YES];
		[context sendResult];		
	}
}

-(void)createDirectory:(id<AxPluginContext>)context {
	if ([self _isActivatedFeatureFileSystemRead] && ![self _isActivatedFeatureFileSystem] && ![self _isActivatedFeatureFileSystemWrite]) {
        [context sendError:AX_SECURITY_ERR message:AX_SECURITY_ERR_MSG];
        return;
    }
    NSDictionary *custom = [context getParamAsDictionary:0];
	NSString *dirPath = [context getParamAsString:1];
	
	DefaultPluginResult result;
	KthWaikikiFile *file = [[KthWaikikiFilesystem getFileWithCustomInfo:custom result:&result] retain];
	if (nil == file) {
		[context sendError:result.code message:result.msg];
		return;
	}
	
	KthWaikikiFile *created = [file createDirectory:dirPath result:&result];
	[file release];
	
	if (nil == created) {
		[context sendError:result.code message:result.msg];
	} else {
		if ([created isKindOfClass:[NSNull class]]) {
			[context sendError:AX_UNKNOWN_ERR message:kErrMsgIoErrNotADirectory];
            return;
		}
		[context sendResult:[created returnData]];		
	}
}

-(void)createFile:(id<AxPluginContext>)context {
	if ([self _isActivatedFeatureFileSystemRead] && ![self _isActivatedFeatureFileSystem] && ![self _isActivatedFeatureFileSystemWrite]) {
        [context sendError:AX_SECURITY_ERR message:AX_SECURITY_ERR_MSG];
        return;
    }
    NSDictionary *custom = [context getParamAsDictionary:0];
	NSString *filePath = [context getParamAsString:1];
	
	DefaultPluginResult result;
	KthWaikikiFile *file = [[KthWaikikiFilesystem getFileWithCustomInfo:custom result:&result] retain];
	if (nil == file) {
		[context sendError:result.code message:result.msg];
		return;
	}
	
	KthWaikikiFile *created = [file createFile:filePath result:&result];
	[file release];
	
	if (nil == created) {
		[context sendError:result.code message:result.msg];
	} else {
		if ([created isKindOfClass:[NSNull class]]) {
            [context sendError:AX_UNKNOWN_ERR message:kErrMsgIoErrNotAFile];
            return;
		}
		[context sendResult:[created returnData]];
	}
}

- (void)resolveFilePath:(id<AxPluginContext>)context {
    NSDictionary *custom = [context getParamAsDictionary:0];
	NSString *filePath = [KthWaikikiFilesystem _removeLastPathDelimiter:[context getParamAsString:1]];
	
	DefaultPluginResult result;
	KthWaikikiFile *file = [[KthWaikikiFilesystem getFileWithCustomInfo:custom result:&result] retain];
	if (nil == file) {
		[context sendError:result.code message:result.msg];
		return;
	}
	
	KthWaikikiFile *target = [file resolveWithFilePath:filePath result:&result];
	[file release];
	
	if (nil == target) {
		[context sendError:result.code message:result.msg];
	} else {
		[context sendResult:[target returnData]];
	}
}

- (void)deleteDirectory:(id<AxPluginContext>)context {
	if ([self _isActivatedFeatureFileSystemRead] && ![self _isActivatedFeatureFileSystem] && ![self _isActivatedFeatureFileSystemWrite]) {
        [context sendError:AX_SECURITY_ERR message:AX_SECURITY_ERR_MSG];
        return;
    }
    NSDictionary *custom = [context getParamAsDictionary:0];
	NSString *directoryPath = [context getParamAsString:1];
	BOOL recursive = [context getParamAsBoolean:2];
	
    //file.fullPath
    NSArray* components = [NSArray arrayWithArray:[directoryPath pathComponents]];
    if ([components count] <= 1) {
        [context sendError:AX_NOT_FOUND_ERR message:kErrMsgIoErrInvalidFilePath];
        return; 
    }
    
	DefaultPluginResult result;
	KthWaikikiFile *file = [[KthWaikikiFilesystem getFileWithCustomInfo:custom result:&result] retain];
	if (nil == file) {
		[context sendError:result.code message:result.msg];
		return;
	}
	
	BOOL succeeded = [file deleteDirectory:directoryPath recursive:recursive result:&result];
	[file release];
	
	if (NO == succeeded) {
		[context sendError:result.code message:result.msg];		
	} else {
		[context sendResult];
	}
}

- (void)deleteFile:(id<AxPluginContext>)context {
	if ([self _isActivatedFeatureFileSystemRead] && ![self _isActivatedFeatureFileSystem] && ![self _isActivatedFeatureFileSystemWrite]) {
        [context sendError:AX_SECURITY_ERR message:AX_SECURITY_ERR_MSG];
        return;
    }
    NSDictionary *custom = [context getParamAsDictionary:0];
	NSString *filePath = [context getParamAsString:1];
	
    //file.fullPath
    NSArray* components = [NSArray arrayWithArray:[filePath pathComponents]];
    if ([components count] <= 1) {
        [context sendError:AX_NOT_FOUND_ERR message:kErrMsgIoErrInvalidFilePath];
        return; 
    }
    
	DefaultPluginResult result;
	KthWaikikiFile *file = [[KthWaikikiFilesystem getFileWithCustomInfo:custom result:&result] retain];
	if (nil == file) {
		[context sendError:result.code message:result.msg];
		return;
	}
	
	BOOL succeeded = [file deleteFile:filePath result:&result];
	[file release];
	
	if (NO == succeeded) {
		[context sendError:result.code message:result.msg];
	} else {
		[context sendResult];
	}
}


#pragma mark FileStream implementation

/*************************************************/
/* void close()                                  */
/* DOMString read(unsigned long charCount)       */
/* ByteArray readBytes(unsigned long byteCount)  */
/* DOMString readBase64(unsigned long byteCount) */
/* void write(DOMString stringData)              */
/* void writeBytes(ByteArray byteData)           */
/* void writeBase64(DOMString base64Data)        */
/*************************************************/


- (void)isEOF:(id<AxPluginContext>)context {
	NSString *handle = [context getParamAsString:0];
	KthWaikikiFileStream *stream = [_streams objectForKey:handle];
	if (nil == stream) {
		[context sendError:AX_UNKNOWN_ERR message:kErrMsgIoErrFSNotOpend];
		return;
	}
	
	[context sendResult:[NSNumber numberWithBool:[stream isEOF]]];
}

- (void)getPosition:(id<AxPluginContext>)context {
	NSString *handle = [context getParamAsString:0];
	KthWaikikiFileStream *stream = [_streams objectForKey:handle];
	if (nil == stream) {
		[context sendError:AX_UNKNOWN_ERR message:kErrMsgIoErrFSNotOpend];
		return;
	}
	
	[context sendResult:[NSNumber numberWithUnsignedLong:[stream getPosition]]];
}

- (void)setPosition:(id<AxPluginContext>)context {
	NSString *handle = [context getParamAsString:0];
	unsigned long position = [[context getParamAsNumber:1]unsignedLongValue];
	
	KthWaikikiFileStream *stream = [_streams objectForKey:handle];
	if (nil == stream) {
		[context sendError:AX_UNKNOWN_ERR message:kErrMsgIoErrFSNotOpend];
		return;
	}
	
	DefaultPluginResult result = [stream setPosition:position];
	if (result.succeeded) {
		[context sendResult];
	} else {
		[context sendError:result.code message:result.msg];
	}
}

- (void)getBytesAvailable:(id<AxPluginContext>)context {
	NSString *handle = [context getParamAsString:0];
	KthWaikikiFileStream *stream = [_streams objectForKey:handle];
	if (nil == stream) {
		[context sendError:AX_UNKNOWN_ERR message:kErrMsgIoErrFSNotOpend];
		return;
	}
	
	[context sendResult:[NSNumber numberWithLong:[stream getBytesAvailable]]];	
}

-(void)close:(id<AxPluginContext>)context {
    NSString *handle = [context getParamAsString:0];
	
	KthWaikikiFileStream *stream = [_streams objectForKey:handle];
	if (nil == stream) {
		[context sendError:AX_UNKNOWN_ERR message:kErrMsgIoErrFSNotOpend];
		return;
	} 
	
	[stream close];	
	[_streams removeObjectForKey:handle];
	[context sendResult];
}

-(void)read:(id<AxPluginContext>)context {
    NSString *handle = [context getParamAsString:0];
	NSUInteger count = [context getParamAsInteger:1];
	
	KthWaikikiFileStream *stream = [_streams objectForKey:handle];
	if (!stream) {
		[context sendError:AX_UNKNOWN_ERR message:kErrMsgIoErrFSNotOpend];
		return;
	}
	
	NSString *res;
	DefaultPluginResult result = [stream read:count result:&res];
	if (result.succeeded) {
		[context sendResult:res];
	} else {
		[context sendError:result.code message:result.msg];
	}
}

-(void)readBytes:(id<AxPluginContext>)context {
    NSString *handle = [context getParamAsString:0];
	NSUInteger count = [context getParamAsInteger:1];
	
	KthWaikikiFileStream *stream = [_streams objectForKey:handle];
	if (!stream) {
		[context sendError:AX_UNKNOWN_ERR message:kErrMsgIoErrFSNotOpend];
		return;
	}
	
	NSData *res;
	DefaultPluginResult result = [stream readBytes:count result:&res];
	if (result.succeeded) {
		NSUInteger len = [res length];
		NSMutableArray *ar = [[NSMutableArray alloc] initWithCapacity:len];
		uint8_t *bytes = (uint8_t *)[res bytes]; 
		for (NSInteger i=0; i < len; ++i) {
			[ar addObject:[NSNumber numberWithUnsignedInt:bytes[i]]];
		}
		[context sendResult:[ar autorelease]];
	} else {
		[context sendError:result.code message:result.msg];
	}
}

-(void)readBase64:(id<AxPluginContext>)context {
    NSString *handle = [context getParamAsString:0];
	NSUInteger count = [context getParamAsInteger:1];
	
	KthWaikikiFileStream *stream = [_streams objectForKey:handle];
	if (!stream) {
		[context sendError:AX_UNKNOWN_ERR message:kErrMsgIoErrFSNotOpend];
		return;
	}
	
	NSString *res;
	DefaultPluginResult result = [stream readBase64:count result:&res];
	if (result.succeeded) {
		[context sendResult:res];
	} else {
		[context sendError:result.code message:result.msg];
	}
}

-(void)write:(id<AxPluginContext>)context {
    NSString *handle = [context getParamAsString:0];
	NSString *string = [context getParamAsString:1];
	
	KthWaikikiFileStream *stream = [_streams objectForKey:handle];
	if (!stream) {
		[context sendError:AX_UNKNOWN_ERR message:kErrMsgIoErrFSNotOpend];
		return;
	}
	
	DefaultPluginResult result = [stream write:string];
	if (result.succeeded) {
		[context sendResult];
	} else {
		[context sendError:result.code message:result.msg];
	}
}

-(void)writeBytes:(id<AxPluginContext>)context {
    NSString *handle = [context getParamAsString:0];
	NSArray *byteArr = [context getParamAsArray:1];
	
	KthWaikikiFileStream *stream = [_streams objectForKey:handle];
	if (!stream) {
		[context sendError:AX_UNKNOWN_ERR message:kErrMsgIoErrFSNotOpend];
		return;
	}
	
	NSUInteger count = [byteArr count];
	if (0 == count) {
		[context sendResult];
		return;
	}
	
	uint8_t *bytes = (uint8_t*)malloc(sizeof(uint8_t)*count+1);
	for (NSUInteger i=0; i < count; ++i) {
		// TODO: check the type. (Can not catch with @try-@catch...)
		bytes[i] = [[byteArr objectAtIndex:i] unsignedIntValue];
	}		
	
	bytes[count] = 0;
	NSData *data = [NSData dataWithBytes:bytes length:count];
	
	DefaultPluginResult result = [stream writeBytes:data];
	free(bytes);
	
	if (result.succeeded) {
		[context sendResult];
	} else {
		[context sendError:result.code message:result.msg];
	}
}

-(void)writeBase64:(id<AxPluginContext>)context {
    NSString *handle = [context getParamAsString:0];
	NSString *base64String = [context getParamAsString:1];
	
	KthWaikikiFileStream *stream = [_streams objectForKey:handle];
	if (!stream) {
		[context sendError:AX_UNKNOWN_ERR message:kErrMsgIoErrFSNotOpend];
		return;
	}
	
	DefaultPluginResult result = [stream writeBase64:base64String];
	if (result.succeeded) {
		[context sendResult];
	} else {
		[context sendError:result.code message:result.msg];
	}
}

#pragma mark -
#pragma mark Static methods
+ (KthWaikikiFile *)fileWithFullPath:(NSString *)fullPath mode:(NSString *)mode result:(DefaultPluginResult *)result 
{
	KthWaikikiFile *file = nil;
	NSString *location = [[NSMutableArray arrayWithArray:[fullPath pathComponents]] objectAtIndex:0];
	if ([KthWaikikiFilesystem _isDocumentRootLocationWithFullPath:location]) {
		file = [[KthWaikikiDocumentsFile alloc] initWithFullPath:fullPath mode:mode result:result];
	} else if ([kLocationWgtPackage isEqualToString:location]) {
		file = [[KthWaikikiWgtPackageFile alloc] initWithFullPath:fullPath mode:mode result:result];
	} else if ([kLocationWgtPrivateTmp isEqualToString:location]) {
		file = [[KthWaikikiWgtPrivateTmpFile alloc] initWithFullPath:fullPath mode:mode result:result];
	} else {
		(*result).code=AX_NOT_FOUND_ERR;
		(*result).succeeded=NO;
		(*result).msg=kErrMsgIoErrNonValidLocation;
	}
	return [file autorelease];
}
+ (KthWaikikiFileStream *)fileStreamWithFile:(KthWaikikiFile *)file
{
	KthWaikikiFileStream *result;
	NSString *location = [[NSMutableArray arrayWithArray:[file.fullPath pathComponents]] objectAtIndex:0];
	if ([KthWaikikiFilesystem _isDocumentRootLocationWithFullPath:location]) {
		result = [[KthWaikikiDocumentsFileStream alloc] init];
	} else if ([kLocationWgtPackage isEqualToString:location]) {
		result = [[KthWaikikiWgtPackageFileStream alloc] init];
	} else if ([kLocationWgtPrivateTmp isEqualToString:location]) {
		result = [[KthWaikikiWgtPrivateTmpFileStream alloc] init];
	} else {
		return nil;
	}
	
	return [result autorelease];
}

+ (BOOL)_isDocumentRootLocationWithFullPath:(NSString *)fullPath {
	NSMutableArray *components = [NSMutableArray arrayWithArray:[fullPath pathComponents]];
	NSString *rootLocation = ([components count] == 0) ? nil : [components objectAtIndex:0];
	if (nil == rootLocation) { return NO; }
	
	if ([rootLocation isEqualToString:kLocationDocuments] ||
		[rootLocation isEqualToString:kLocationImages] ||
		[rootLocation isEqualToString:kLocationVideos] ||
		[rootLocation isEqualToString:kLocationMusic] ||
		[rootLocation isEqualToString:kLocationDownloads] ||
		[rootLocation isEqualToString:kLocationWgtPrivate]) {
		return YES;
	} else {
		return NO;
	}
}

+ (BOOL)_isDocumentRootLocationWithURI:(NSString *)uri {
	NSMutableArray *components = [KthWaikikiFilesystem pathComponentsRemovingPrefixAndSuffixFromString:uri];//[self pathComponentsRemovingPrefixAndSuffixFromString:uri]; 
	NSString *rootLocation = ([components count] == 0) ? nil : [@"/" stringByAppendingPathComponent:[components objectAtIndex:0]];
	if (nil == rootLocation) { return NO; }
	
	if ([rootLocation isEqualToString:kURIDocuments] ||
		[rootLocation isEqualToString:kURIImages] ||
		[rootLocation isEqualToString:kURIVideos] ||
		[rootLocation isEqualToString:kURIMusic] ||
		[rootLocation isEqualToString:kURIDownloads] ||
		[rootLocation isEqualToString:kURIWgtPrivate]) {
		return YES;
	} else {
		return NO;
	}	
}

+ (NSString *)_removeLastPathDelimiter:(NSString *)path {
	if (![path hasSuffix:@"/"]) {
		return path;
	}
	
	NSMutableString *p = [NSMutableString stringWithString:path];
	while ((0 != [p length]) && [p hasSuffix:@"/"]) {
		[p deleteCharactersInRange:NSMakeRange([p length]-1, 1)];
	}
	return [NSString stringWithString:p];
}

#pragma mark -
#pragma mark Public APIs
// This Apis used other plugin modules.
+ (NSString *)realPathFromFullPath:(NSString *)fullPath
{
	NSMutableArray *components = [NSMutableArray arrayWithArray:[fullPath pathComponents]];
	NSString *rootLocation = ([components count] == 0) ? nil : [components objectAtIndex:0];

	if ([KthWaikikiFilesystem _isDocumentRootLocationWithFullPath:fullPath]) {
		// Documents type (documents, images, videos, music, downloads) 
		return [KthWaikikiRealPathTypeFile realpathFromFullPath:fullPath ofClass:[KthWaikikiDocumentsFile class]];
	} else if ([kLocationWgtPackage isEqualToString:rootLocation]) {
		// wgt-package
		return [KthWaikikiRealPathTypeFile realpathFromFullPath:fullPath ofClass:[KthWaikikiWgtPackageFile class]];
	} else if ([kLocationWgtPrivateTmp isEqualToString:rootLocation]) {
		// wgt-private-tmp
		return [KthWaikikiRealPathTypeFile realpathFromFullPath:fullPath ofClass:[KthWaikikiWgtPrivateTmpFile class]];
	} else {
		return nil;
	}
}

+ (BOOL)validateDestFullPath:(NSString *)desFullPath
{
	NSMutableArray *components = [NSMutableArray arrayWithArray:[desFullPath pathComponents]];
	NSString *rootLocation = ([components count] == 0) ? nil : [components objectAtIndex:0];
    
	if ([KthWaikikiFilesystem _isDocumentRootLocationWithFullPath:desFullPath]) {
		// Documents type (documents, images, videos, music, downloads) 
		return YES;
	} else if ([kLocationWgtPrivateTmp isEqualToString:rootLocation]) {
		// wgt-private-tmp
		return YES;
	} else if ([kLocationWgtPackage isEqualToString:rootLocation]) {
		// wgt-package
		return YES;
	}  
    else {
		return NO;
	}
}


+ (NSString *)fullPathFromRealPath:(NSString *)realPath
{	
	// 1. Under documnets
	NSString *documentRoot = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) objectAtIndex:0];
	NSRange documentRange = [realPath rangeOfString:documentRoot];
	if (documentRange.location != NSNotFound && documentRange.location == 0) {
		NSString *subPath = [realPath substringFromIndex:documentRange.length];
        if (0 != [subPath length] && [subPath hasPrefix:@"/"]) {
            return [subPath substringFromIndex:1];
        } else {
            return subPath;
        }
	}
	
	// 2. wgt-package
	NSString *wwwRoot = [[NSBundle mainBundle] pathForResource:@"assets/ax_www" ofType:nil];
	NSRange wwwRange = [realPath rangeOfString:wwwRoot];
	if (wwwRange.location != NSNotFound && wwwRange.location == 0) {
		NSString *subPath = [realPath substringFromIndex:wwwRange.length];
		return [kLocationWgtPackage stringByAppendingPathComponent:subPath];
	}
	
	// 3. wgt-private-tmp
	NSString *tmpRoot =  NSTemporaryDirectory();
	NSRange tmpRange = [realPath rangeOfString:tmpRoot];
	if (tmpRange.location != NSNotFound && tmpRange.location == 0) {
		NSString *subPath = [realPath substringFromIndex:tmpRange.length];
		return [kLocationWgtPrivateTmp stringByAppendingPathComponent:subPath];
	}
	
	return nil;
}

+ (NSString *)realPathFromURI:(NSString *)uri
{
	NSArray *components = [KthWaikikiFilesystem pathComponentsRemovingPrefixAndSuffixFromString:uri];
	NSString *rootLocation = ([components count] == 0) ? nil : [components objectAtIndex:0];
	NSString *desURI = [@"/" stringByAppendingPathComponent:rootLocation];
	
	if ([KthWaikikiFilesystem _isDocumentRootLocationWithURI:uri]) {
		// Documents type (documents, images, videos, music, downloads) 
		return [KthWaikikiRealPathTypeFile realpathFromURI:uri ofClass:[KthWaikikiDocumentsFile class]];
	} else if ([kURIWgtPackage isEqualToString:desURI]) {
		// wgt-package
		return [KthWaikikiWgtPackageFile realpathFromURI:uri ofClass:[KthWaikikiWgtPackageFile class]];
	} else if ([kURIWgtPrivateTmp isEqualToString:desURI]) {
		// wgt-package
		return [KthWaikikiWgtPrivateTmpFile realpathFromURI:uri ofClass:[KthWaikikiWgtPrivateTmpFile class]];
	} else {
		return nil;
	}
}

+ (NSMutableArray*)pathComponentsRemovingPrefixAndSuffixFromString:(NSString*)str {
	NSMutableArray *components = [NSMutableArray arrayWithArray:[str pathComponents]];
	if ([str hasSuffix:@"/"]) { [components removeLastObject]; }
	if ([str hasPrefix:@"/"]) {[components removeObjectAtIndex:0];	}
	
	return components;
}
+ (BOOL)_isDstPath:(NSString*) dstPath ContainsSrcPath:(NSString*) srcPath {
    NSArray *dstComponent = [dstPath pathComponents];
    NSArray *srcComponent = [srcPath pathComponents];
    if (srcComponent.count > dstComponent.count) {
        return NO;
    }
    int limit = srcComponent.count;
    BOOL isContain = YES;
    for (int i=0; i<limit; i++) {
        if (![[srcComponent objectAtIndex:i]isEqualToString:[dstComponent objectAtIndex:i]]) {
            isContain = NO;
            break;
        }
    }
    return isContain;
}
@end
