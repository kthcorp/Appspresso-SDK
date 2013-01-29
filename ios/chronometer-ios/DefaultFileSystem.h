/*
 * Appspresso
 *
 * Copyright (c) 2011 KT Hitel Corp.
 *
 * Appspresso SDK may be freely distributed under the MIT license.
 */


#import <Foundation/Foundation.h>
#import "AxFileSystem.h"

@class DefaultFile;

@interface DefaultFileSystem : NSObject <AxFileSystem> {
@private
    NSString* _baseDir;
    BOOL _isReadable;
    BOOL _isWritable;
    DefaultFile *_root;
}

-(id)initWithBaseDirectory:(NSString*)path canRead:(BOOL)r canWrite:(BOOL)w;

+(id)fileSystemWithBaseDirectory:(NSString*)path canRead:(BOOL)r canWrite:(BOOL)w;

@end
