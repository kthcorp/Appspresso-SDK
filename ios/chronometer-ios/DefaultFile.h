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
