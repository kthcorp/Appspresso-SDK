//
//  KthWaikikiFile.h
//  filesystem-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import <Foundation/Foundation.h>

typedef struct {
	BOOL succeeded;
	NSInteger code;
	NSString *msg;
} DefaultPluginResult;


// Filesystem
#define kErrMsgIoErrFileHandleIsNotValid @"File handle is not valid"
#define kErrMsgIoErrNonValidLocation @"Invalid location"
#define kErrMsgIoErrNotADirectory @"Not a directory"
#define kErrMsgIoErrInvalidFilePath @"Invalid filePath"
#define kErrMsgIoErrDirectoryNotEmpty @"The Directory is not empty"
#define kErrMsgIoErrNotAFile @"Not a file"
#define kErrMsgIoErrAlreadyExist @"dirPath already exists."
#define kErrMsgIoErrCanNotOverwrite @"Can not overwite"
#define kErrMsgIoErrUnknownEncoding @"Unknown encoding"
#define kErrMsgIoErrUnknownMode @"Unknown mode"
#define kErrMsgIoErrCanNotOpenFile @"Can not open file"
#define kErrMsgIoErrPermissionDenied @"Permission denied"
#define kErrMsgIoErrUnknownURI @"Unknown URI"
#define kErrMsgIoErrInvokedCreateInFile @"The Directory or file to be created must be under the directory from which the method is invoked"
#define kErrMsgIoErrInvokedDeletedInFile @"The Directory or file to be deleted must be under the directory from which the method is invoked"
#define kErrMsgIoErrFailedToDeleteDirectory @"Failed to delete directory"
#define kErrMsgIoErrFSAlreadyOpend @"Filestream was already opened"
#define kErrMsgIoErrFSNotOpend @"Filestream is not opened"
#define kErrMsgIoErrFSOpFailed @"Filestream operation failed"
#define kErrMsgIoErrPositionOutOfRange @"Position was given that is out of the stream range"
#define kErrMsgIoErrOpenedForWriting @"Filestream was opend for writing"
#define kErrMsgIoErrOpenedForReading @"Filestream was opend for reading"
#define kErrMsgIoErrExceedsEof @"count exceeds eof"
#define kErrMsgIoErrOccureed @"IO Error occured"
#define kErrMsgIoErrDesPathHasSrcPath @"Destination path has source path"
#define kErrMsgIoErrDesPathInvalid @"Destination path invalid = %@"


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// file mode
#define kFileModeReadOnly @"r"
#define kFileModeReadWrite @"rw"

typedef enum {
	KthWaikikiFileModeReadWrite,
	KthWaikikiFileModeReadOnly,
	KthWaikikiFileModeUnknown,
} KthWaikikiFileMode;


// Encodings
#define NSEUCKRStringEncoding 0x80000003
#define kEncoding_UTF8 @"UTF-8"
#define kEncoding_ISO8859_1 @"ISO-8859-1"
#define kEncoding_EUC_KR @"EUC-KR"


// location specifiers
#define kLocationDocuments @"documents"
#define kLocationImages @"images"
#define kLocationVideos @"videos"
#define kLocationMusic @"music"
#define kLocationDownloads @"downloads"
#define kLocationWgtPrivate @"wgt-private"
#define kLocationWgtPrivateTmp @"wgt-private-tmp"
#define kLocationWgtPackage @"wgt-package"


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
@class KthWaikikiFileStream;
@interface KthWaikikiFile : NSObject {
@protected
	/** handle is a unique string represents a file object, is valid for both javascript and native side. */
	BOOL _dirty;
	NSString *_handle;
	NSString *_nativePath; //_realpath
	
@protected
	KthWaikikiFileMode _fileMode;
    NSString *_rwMode;
	BOOL _isFile;
	BOOL _isDirectory;
	NSDate *_created;
	NSDate *_modified;
	NSString *_path;
	NSString *_name;
	NSString *_virtualPath; //_fullPath (waikiki fullpath)
	NSUInteger _length;		// Undefined if it is file.
	NSNumber *_fileSize;	// Undefined if it is directory.
}
@property (nonatomic, assign, readwrite) KthWaikikiFileMode fileMode;
@property (nonatomic, assign, readwrite) NSString *rwMode;
@property (nonatomic, retain, readwrite) NSString *handle;
@property (nonatomic, retain, readwrite) NSString *realpath;
@property (nonatomic, retain, readwrite) NSDate *created;
@property (nonatomic, retain, readwrite) NSDate *modified;
@property (nonatomic, retain, readwrite) NSString *path;
@property (nonatomic, retain, readwrite) NSString *name;
@property (nonatomic, retain, readwrite) NSString *fullPath;
@property (nonatomic, retain, readwrite) NSNumber *fileSize;
@property (nonatomic, assign, readwrite) NSUInteger length;
@property (nonatomic, assign, readwrite, getter=isDirty) BOOL dirty;
@property (nonatomic, assign, readwrite) BOOL isFile;
@property (nonatomic, assign, readwrite) BOOL isDirectory;

- (NSMutableArray*)pathComponentsRemovingPrefixAndSuffixFromString:(NSString*)str;

//- (id)initWithFullPath:(NSString *)fullPath mode:(KthWaikikiFileMode)mode;
- (NSDictionary*)returnData;
//- (NSDictionary*)returnDataForPeer;
- (NSDictionary*)returnDataForFileInfo;

// Override depending on root location
- (id)initWithFullPath:(NSString *)fullPath mode:(NSString *)mode result:(DefaultPluginResult*)result;
- (KthWaikikiFile *)resolveWithFilePath:(NSString *)subPath result:(DefaultPluginResult *)result;
- (NSArray *)listFilesWithFilter:(NSDictionary *)filter result:(DefaultPluginResult *)result;
- (NSString *)readAsText:(NSString *)encoding result:(DefaultPluginResult *)result;
- (KthWaikikiFile *)createDirectory:(NSString *)subPath result:(DefaultPluginResult *)result;
- (KthWaikikiFile *)createFile:(NSString *)subPath result:(DefaultPluginResult *)result;
- (BOOL)deleteDirectory:(NSString *)fullPath recursive:(BOOL)recursive result:(DefaultPluginResult *)result;
- (BOOL)deleteFile:(NSString *)fullPath result:(DefaultPluginResult *)result;
- (BOOL)copyToWithSrc:(NSString *)srcFullPath des:(NSString *)desFullPath overwrite:(BOOL)overwrite result:(DefaultPluginResult *)result;
- (BOOL)moveToWithSrc:(NSString *)srcFullPath des:(NSString *)desFullPath overwrite:(BOOL)overwrite result:(DefaultPluginResult *)result;
- (NSString *)toURI:(DefaultPluginResult *)result;
- (void)validate;
- (BOOL)writable;


#pragma mark -
#pragma mark Static Methods
+ (NSStringEncoding)encodingFromString:(NSString *)enc;
+ (KthWaikikiFileMode)fileModeFromString:(NSString *)mode;
+ (NSString *)subPath:(NSString *)childFullPath of:(NSString *)parentFullPath;
+ (BOOL)isValidPath:(NSString *)path of:(NSString *)fullPath isFullPath:(BOOL)isFullPath;
@end
