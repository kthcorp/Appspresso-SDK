//
//  DefaultFileSystemManager.h
//  sail-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import <Foundation/Foundation.h>
#import "AxFileSystemManager.h"

@interface DefaultFileSystemManager : NSObject <AxFileSystemManager> {
@private
    NSMutableDictionary* _fileSystemTable;    
}

@end
