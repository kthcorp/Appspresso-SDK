/*
 * Appspresso
 *
 * Copyright (c) 2011 KT Hitel Corp.
 *
 * This source is subject to Appspresso license terms.
 * Please see http://appspresso.com/ for more information.
 *
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY
 * KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A
 * PARTICULAR PURPOSE.
 */


#import "DefaultFile.h"
#import "DefaultFileSystem.h"
#import "AxFileFilter.h"

@implementation DefaultFile

-(void)update {
    NSFileManager *fm = [[NSFileManager alloc] init];
    NSError *error;
    
    if([fm fileExistsAtPath:_nativePath]) {
        _exist = YES;            
        NSDictionary *dict = [fm attributesOfItemAtPath:_nativePath error:&error];            
        _isDirectory = [[dict objectForKey:NSFileType] isEqualToString:NSFileTypeDirectory];
        _isFile = [[dict objectForKey:NSFileType] isEqualToString:NSFileTypeRegular];
        
        _created = [dict objectForKey:NSFileCreationDate];
        _modified = [dict objectForKey:NSFileModificationDate];           
        if (_isDirectory) {
            _length = [[fm contentsOfDirectoryAtPath:_nativePath error:&error] count];
        }
        if (_isFile) {
            _length = [[dict objectForKey:NSFileSize] unsignedIntValue]; 
        }
        _canRead = [_fileSystem canRead];
        _canWrite = [_fileSystem canWrite];
    } else {
        // TODO: 존재하지 않는 파일을 resolve하면 어떻게 되더라?
        // [fm createFileAtPath:_nativePath contents:nil attributes:nil];
        _exist = NO;
        _isDirectory = NO;            
        _isFile = NO;
        _created = nil;
        _modified = nil;
        _length = 0;
        _canRead = NO;
        _canWrite = [_fileSystem canWrite];
    }
    
    [fm release];
}

#pragma mark -

-(id)initWithFilePath:(NSString*)path parent:(NSObject<AxFile>*)parent fileSystem:(DefaultFileSystem*)fileSystem {
    if ((self=[super init])) {
        _fileSystem = [fileSystem retain];
        _parent = [parent retain];
        _nativePath = [path retain];
        _name = [_nativePath lastPathComponent];
        _fileHandle = nil;
        [self update];
    }
    return self;
}

-(void)dealloc {
    [self close];
    //[_fileHandle release];
    //[_created release];
    //[_modified release];
    //[_name release];
    [_nativePath release];
    [_parent release];
    [_fileSystem release];
    [super dealloc];
}

#pragma mark AxFile

-(NSString*)getName{
    return _name;
}

-(NSString*)getPath{
    return _nativePath;
}

-(NSObject<AxFile>*)getParent{
    return _parent;
}


-(BOOL)isFile{
    return _isFile;
}

-(BOOL)isDirectory{
    return _isDirectory;
}

-(NSUInteger)getLength{
    return _length;
}

-(NSDate*)getCreated{
    return _created;
}

-(NSDate*)getModified{
    return _modified;
}

-(BOOL)exists{
    return _exist;
}

-(BOOL)canRead{
    return _canRead;
}

-(BOOL)canWrite{
    return _canWrite;
}

-(NSData*)getContentsAsData {
    return [NSData dataWithContentsOfFile:_nativePath];
}

-(void)setContentsWithData:(NSData*)data {
    [data writeToFile:_nativePath atomically:NO];
}

-(NSString*)getContentsAsString {
    return [NSString stringWithContentsOfFile:_nativePath encoding:NSUTF8StringEncoding error:nil];
}

-(void)setContentsWithString:(NSString*)str {
    [str writeToFile:str atomically:NO encoding:NSUTF8StringEncoding error:nil];
}

-(NSString*)getContentsAsString:(NSString*)encoding {
    NSStringEncoding nsencoding = CFStringConvertEncodingToNSStringEncoding(CFStringConvertIANACharSetNameToEncoding((CFStringRef)encoding));
    return [NSString stringWithContentsOfFile:_nativePath encoding:nsencoding error:nil];
}

-(void)setContentsWithString:(NSString*)str encoding:(NSString*)encoding {
    NSStringEncoding nsencoding = CFStringConvertEncodingToNSStringEncoding(CFStringConvertIANACharSetNameToEncoding((CFStringRef)encoding));
    [str writeToFile:str atomically:NO encoding:nsencoding error:nil];
}

-(NSArray*)listFiles : (NSObject<AxFileFilter>*) filter{
	NSFileManager *fm = [[NSFileManager alloc] init];
	NSError *error;
	NSArray *contents = [fm contentsOfDirectoryAtPath:_nativePath error:&error];
    NSMutableArray *result = [NSMutableArray arrayWithCapacity:[contents count]];
    for (NSString *path in contents) {
        NSObject<AxFile>* file = [_fileSystem getFile:path];
        if ([filter isEqual:nil] || [filter acceptFile:file]) {
            [result addObject:file];
        }
    }
    [fm release];
    return result;
}

-(id)getPeer{
    return _nativePath;
}

-(BOOL)open:(int)mode {
    // TODO: 에러 처리?
    switch (mode) {
        case AxFileOpenForReadOnly:
            if(!_exist || !_canRead) {
                return NO;
            }
            _fileHandle = [NSFileHandle fileHandleForReadingAtPath:_nativePath];
            break;
        case AxFileOpenForWriteOnly:
            if(!_canWrite) {
                return NO;
            }
            if(!_exist) {
                [[NSFileManager defaultManager] createFileAtPath:_nativePath contents:nil attributes:nil];
                [self update];
            }
            _fileHandle = [NSFileHandle fileHandleForWritingAtPath:_nativePath];
            break;
        case AxFileOpenForReadWrite:
            if(!_canWrite) {
                return NO;
            }
            if(!_exist) {
                [[NSFileManager defaultManager] createFileAtPath:_nativePath contents:nil attributes:nil];
                [self update];
            }
            _fileHandle = [NSFileHandle fileHandleForUpdatingAtPath:_nativePath];
            break;
        default:
            return NO;
    }
    return (_fileHandle != nil);
}

-(void)close{
    // TODO: 에러 처리?
    if(_fileHandle == nil) {
        // not yet open!
        return;
    }
    [_fileHandle closeFile];
    [_fileHandle release];
    _fileHandle = nil;
}

-(BOOL)isEof{
    // TODO: 에러 처리?
    if(_fileHandle == nil) {
        // not yet open!
        return YES;
    }
    return ([_fileHandle offsetInFile] >= _length);
}

-(void)seek:(NSUInteger)position{
    // TODO: 에러 처리?
    if(_fileHandle == nil) {
        // not yet open!
        return;
    }
    [_fileHandle seekToFileOffset:position];
}

-(NSUInteger)getPosition{
    // TODO: 에러 처리?
    if(_fileHandle == nil) {
        // not yet open!
        return 0;
    }
    return [_fileHandle offsetInFile];
}

-(NSData*)read:(NSUInteger)size{
    // TODO: 에러 처리?
    if(_fileHandle == nil) {
        // not yet open!
        return nil;
    }
    return [_fileHandle readDataOfLength:size];
}

-(void)write:(NSData*)data{
    // TODO: 에러 처리?
    if(_fileHandle == nil) {
        // not yet open!
        return;
    }
    [_fileHandle writeData:data];
}

#pragma mark -

+(id)fileWithFilePath:(NSString*)path parent:(NSObject<AxFile>*)parent fileSystem:(DefaultFileSystem*)fileSystem {
    return [[[DefaultFile alloc] initWithFilePath:path parent:parent fileSystem:fileSystem] autorelease];
}

@end
