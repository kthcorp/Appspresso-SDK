/*
 * Appspresso
 *
 * Copyright (c) 2011 KT Hitel Corp.
 *
 * Appspresso SDK may be freely distributed under the MIT license.
 */


#import <Foundation/Foundation.h>
#import "AxFile.h"

@class DefaultFileSystem;

@interface DefaultFile : NSObject <AxFile>{
    DefaultFileSystem* _fileSystem;
    NSObject<AxFile> *_parent;
    NSString* _name;
    NSString* _nativePath;
    
    BOOL _exist;
    BOOL _isFile;
    BOOL _isDirectory;
    NSUInteger _length;
    NSDate* _created;
    NSDate* _modified;
    BOOL _canRead;
    BOOL _canWrite;

    NSFileHandle* _fileHandle;
}

-(id)initWithFilePath:(NSString*)path parent:(NSObject<AxFile>*)parent fileSystem:(DefaultFileSystem*)fileSystem;

+(id)fileWithFilePath:(NSString*)path parent:(NSObject<AxFile>*)parent fileSystem:(DefaultFileSystem*)fileSystem;

@end
