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
