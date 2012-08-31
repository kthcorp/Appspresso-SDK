//
//  KthWaikikiRealPathTypeFileStream.h
//  filesystem-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import "KthWaikikiDocumentsFile.h"
#import "KthWaikikiFileStream.h"
#import "KthWaikikiRealPathTypeFileStream.h"
#import "Base64Codec.h"

#import "AxLog.h"
#import "AxError.h"

@interface KthWaikikiRealPathTypeFileStream ()
@end


@implementation KthWaikikiRealPathTypeFileStream

- (void)setLastOffset {
    int tempLastOffset;
    tempLastOffset = [_fileHandle offsetInFile];
    _lastOffset = [_fileHandle seekToEndOfFile];
	[_fileHandle seekToFileOffset:tempLastOffset];
}
- (Class)fileClass {
	return [KthWaikikiRealPathTypeFile class];
}

- (DefaultPluginResult)open:(NSString*)fullPath mode:(NSString *)mode encoding:(NSString*)encoding {
	DefaultPluginResult result = { .succeeded=NO, .code=AX_IO_ERR };
	
	_handle = [fullPath copy];
	_encoding = [KthWaikikiFile encodingFromString:encoding];
	_mode = [KthWaikikiFileStream fileStreamModeFromString:mode];
	
	if (0 == _encoding) {
        result.code = AX_INVALID_VALUES_ERR;
		result.msg = kErrMsgIoErrUnknownEncoding;
		return result;
	}
	
	if (KthWaikikiFileStreamModeUnknown == _mode) {
        result.code = AX_INVALID_VALUES_ERR;
		result.msg = kErrMsgIoErrUnknownMode;
		return result;
	}
	
	NSString *realPath = [KthWaikikiRealPathTypeFile realpathFromFullPath:fullPath ofClass:[self fileClass]];
	switch (_mode) {
		case KthWaikikiFileStreamModeRead: 
			_fileHandle = [NSFileHandle fileHandleForReadingAtPath:realPath];
			break;
		case KthWaikikiFileStreamModeWrite: {
            NSFileManager *fm = [[NSFileManager alloc]init];
            [fm removeItemAtPath:realPath error:nil];
			[fm createFileAtPath:realPath contents:nil attributes:nil];
            _fileHandle = [NSFileHandle fileHandleForWritingAtPath:realPath];
            [fm release];
			break;
        }
		case KthWaikikiFileStreamModeAppend: 
			_fileHandle = [NSFileHandle fileHandleForUpdatingAtPath:realPath];
			break;
		default:
			_fileHandle = nil;
			break;
	}
	
	if (nil == _fileHandle) {
		result.msg = kErrMsgIoErrCanNotOpenFile;
		return result;
	}
	
	[_fileHandle seekToEndOfFile];
	_lastOffset = [_fileHandle offsetInFile];
	if (_mode != KthWaikikiFileStreamModeAppend) {
		[_fileHandle seekToFileOffset:0];
	}
	
	[_fileHandle retain];
	result.succeeded = YES;
	return result;
}

- (void)dealloc {
	[self close];
	
	[self setFileHandle:nil];
	[self setHandle:nil];
	[super dealloc];
}

- (void)close {
	[_fileHandle closeFile];
	[self setFileHandle:nil];
}

- (BOOL)isEOF {
	return ([_fileHandle offsetInFile] >= _lastOffset); 
}

- (unsigned long)getPosition {
	return [_fileHandle offsetInFile];
}

- (DefaultPluginResult)setPosition:(unsigned long)position {
	DefaultPluginResult result = { .succeeded=NO, .code=AX_IO_ERR };
	if (_mode != KthWaikikiFileStreamModeAppend) {
        if (position > _lastOffset) {
            result.msg = kErrMsgIoErrPositionOutOfRange;
            return result;
        }
        [_fileHandle seekToFileOffset:position];
	}
	result.succeeded = YES;
	return result;
}

- (long)getBytesAvailable {
    //return (_mode != KthWaikikiFileStreamModeRead) ? -1 : MAX(0, _lastOffset - [_fileHandle offsetInFile]);
    return ([self isEOF]) ? -1 : MAX(0,_lastOffset - [_fileHandle offsetInFile]); 
}

- (DefaultPluginResult)canRead
{
	DefaultPluginResult result = { .succeeded = YES, .code = AX_IO_ERR } ;
	
	if (_mode == KthWaikikiFileStreamModeWrite) {
		result.succeeded = NO;
		result.msg = kErrMsgIoErrOpenedForWriting;
		return result;
	}
	
	if ([self getBytesAvailable] <= 0) {
		result.succeeded = NO;
		result.msg = kErrMsgIoErrExceedsEof;		
	}
	
	return result;
}

- (DefaultPluginResult)canWrite
{
	DefaultPluginResult result = { .succeeded = YES, .code = AX_IO_ERR } ;
	
	if (_mode == KthWaikikiFileStreamModeRead) {
		result.succeeded = NO;
		result.msg = kErrMsgIoErrOpenedForReading;
		return result;
	}
	
	return result;
}

- (NSUInteger)bytesWithCount:(NSUInteger)count {
	const int MAX_UTF8_CHAR_BYTE = 6;
	NSUInteger readBytes;
	
	if (_encoding == NSISOLatin1StringEncoding) {
		readBytes = count;
	} else if (_encoding == NSEUCKRStringEncoding) {
		unsigned long long posOrig = [_fileHandle offsetInFile];
		NSData *data = [_fileHandle readDataOfLength:count*2];
		NSUInteger len = [data length];
		uint8_t *q = (uint8_t *)[data bytes];
		uint8_t *p = q;
				
		while (p-q < len && !!count) {
			if (*p > 0x7F) { ++p; }
			++p; --count;
		}
		
		// Read Actual bytes
		[_fileHandle seekToFileOffset:posOrig];
		readBytes = p-q;
	} else { // TODO: FIX to "else if encoding is UTF8"
		// http://en.wikipedia.org/wiki/UTF-8#Modified_UTF-8
		unsigned long long posOrig = [_fileHandle offsetInFile];
		NSData *data = [_fileHandle readDataOfLength:count*MAX_UTF8_CHAR_BYTE];
		NSUInteger len = [data length];
		uint8_t *q = (uint8_t *)[data bytes];
		uint8_t *p = q;
		
		// Find first character (skip 10xxxxxx)
//		for (NSInteger i=0;; ++i, ++p) {
//			if (i >= MAX_UTF8_CHAR_BYTE || i >= len) { return MIN(len, count); } // NOT UTF-8 encoding
//			if (((*p^0x80)&0xC0) != 0) { break; }
//		}
		
		// Calc character length
		while (p-q < len && !!count) {
			if (!((*p >> 7) & 0x1))	{ p += 1; }
			else if (!((*p >> 5) & 0x1)) { p += 2; }
			else if (!((*p >> 4) & 0x1)) { p += 3; }
			else if (!((*p >> 3) & 0x1)) { p += 4; }
			else if (!((*p >> 2) & 0x1)) { p += 5; }
			else if (!((*p >> 1) & 0x1)) { p += 6; }
//			else { return MIN(len, count); } // NOT UTF-8 encoding
			else { p += 1; }
			
			--count;
		}
		
		// Read Actual bytes
		[_fileHandle seekToFileOffset:posOrig];
		readBytes = MIN(len, p-q);
	}
	
	return readBytes;
}

- (DefaultPluginResult)read:(NSUInteger)count result:(NSString **)res {
	DefaultPluginResult result = [self canRead];
	
	if (!result.succeeded) {
		return result;
	}
	
	@try {
		NSData *data = [_fileHandle readDataOfLength:[self bytesWithCount:count]];
		*res = [[[NSString alloc] initWithData:data encoding:_encoding] autorelease];
	}
	@catch (NSException * e) {
		AX_LOG_TRACE(@"Exception on filestream %@: %@", e.name, e.reason);
		result.succeeded = NO;
		result.code = AX_IO_ERR;
		result.msg = AX_UNKNOWN_ERR_MSG;
	}

	return result;
}

- (DefaultPluginResult)readBytes:(NSUInteger)count result:(NSData **)res
{
	DefaultPluginResult result = [self canRead];
	
	if (!result.succeeded) {
		return result;
	}
	
	@try {
		*res = [_fileHandle readDataOfLength:count];
	}
	@catch (NSException * e) {
		AX_LOG_TRACE(@"Exception on filestream %@: %@", e.name, e.reason);
		result.succeeded = NO;
		result.code = AX_IO_ERR;
		result.msg = AX_UNKNOWN_ERR_MSG;
	}
	
	return result;	
}

- (DefaultPluginResult)readBase64:(NSUInteger)count result:(NSString **)res
{
	DefaultPluginResult result = [self canRead];
	
	if (!result.succeeded) {
		return result;
	}
	
	@try {
		NSData *data = [_fileHandle readDataOfLength:count];
		*res = [Base64Codec encode:data];
	}
	@catch (NSException * e) {
		AX_LOG_TRACE(@"Exception on filestream %@: %@", e.name, e.reason);
		result.succeeded = NO;
		result.code = AX_IO_ERR;
		result.msg = AX_UNKNOWN_ERR_MSG;
	}
	
	return result;
}

- (DefaultPluginResult)write:(NSString *)string
{
	DefaultPluginResult result = [self canWrite];
	
	if (!result.succeeded) {
		return result;
	}
	
	@try {
		[_fileHandle writeData:[string dataUsingEncoding:_encoding]];
        [self setLastOffset];
	}
	@catch (NSException * e) {
		AX_LOG_TRACE(@"Exception on filestream %@: %@", e.name, e.reason);
		result.succeeded = NO;
		result.code = AX_IO_ERR;
		result.msg = AX_UNKNOWN_ERR_MSG;
	}
	
	return result;
}

- (DefaultPluginResult)writeBytes:(NSData *)data
{
	DefaultPluginResult result = [self canWrite];
	
	if (!result.succeeded) {
		return result;
	}
	
	@try {
		[_fileHandle writeData:data];
        [self setLastOffset];
	}
	@catch (NSException * e) {
		AX_LOG_TRACE(@"Exception on filestream %@: %@", e.name, e.reason);
		result.succeeded = NO;
		result.code = AX_IO_ERR;
		result.msg = AX_UNKNOWN_ERR_MSG;
	}
	
	return result;
}

- (DefaultPluginResult)writeBase64:(NSString *)base64String
{
	DefaultPluginResult result = [self canWrite];
	
	if (!result.succeeded) {
		return result;
	}

	@try {
		NSData *data = [Base64Codec decode:base64String];
		[_fileHandle writeData:data];
        [self setLastOffset];
	}
	@catch (NSException * e) {
		AX_LOG_TRACE(@"Exception on filestream %@: %@", e.name, e.reason);
		result.succeeded = NO;
		result.code = AX_IO_ERR;
		result.msg = AX_UNKNOWN_ERR_MSG;
	}
	
	return result;
}

- (NSDictionary*)returnData {
	return [NSDictionary dictionaryWithObjectsAndKeys:
			[NSString stringWithFormat:@"%d",[_fileHandle hash]], @"_handle",
			nil];
}
@end
