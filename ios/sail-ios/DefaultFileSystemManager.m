//
//  DefaultFileSystemManager.m
//  sail-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import "DefaultFileSystemManager.h"
#import "DefaultFileSystem.h"
#import "AxFile.h"

static const NSString *FILESYSTEM_URI_PREFIX = @"/appspresso/file/";

@implementation DefaultFileSystemManager

#pragma mark -

-(id)init{
    if ((self = [super init])) {
        _fileSystemTable = [[NSMutableDictionary alloc]init];
    }
    return self;
}

-(void)dealloc{
    // unmount all!
    for(NSString *prefix in [_fileSystemTable allKeys]) {
        [self unmount:prefix];
    }
    [_fileSystemTable release];
    [super dealloc];
}

-(BOOL)mount:(NSString*)prefix fileSystem:(NSObject<AxFileSystem>*)fileSystem option:(NSDictionary*)option {
    if([_fileSystemTable objectForKey:prefix] != nil) {
        // already mounted!
        return NO;
    }
    if ([fileSystem onMount:prefix option:option]) {
        [_fileSystemTable setObject:fileSystem forKey:prefix];
        return TRUE;
    }
    return FALSE;
}

-(void)unmount:(NSString*)prefix {
    NSObject<AxFileSystem>* fileSystem = [_fileSystemTable objectForKey:prefix];
    if(fileSystem == nil) {
        // not yet mounted!
        return;
    }
    [fileSystem onUnmount];
    [_fileSystemTable removeObjectForKey:prefix];
}

-(NSObject<AxFileSystem>*)getFileSystem:(NSString*)prefix{
    return [_fileSystemTable objectForKey:prefix];
}

-(NSObject<AxFile>*)getFile:(NSString*)path{
    // TODO: 경로 형식 정규화... 예: "/"로 시작하거나 끝나는 경로... "//"가 포함된 경로...
    // TODO: 상대경로 지원 & 인젝션 방지... 예: 경로에 ".."이나 "."이 포함된 경로...
    NSRange range = [path rangeOfString:@"/"];
    if(range.location == NSNotFound) {
        id<AxFileSystem> fileSystem = [_fileSystemTable objectForKey:path];
        return (fileSystem == nil) ? nil : [fileSystem getRoot];
    }
    NSString *prefix = [path substringToIndex:range.location];
    id<AxFileSystem> fileSystem = [_fileSystemTable objectForKey:prefix];
    return (fileSystem == nil) ? nil : [fileSystem getFile:[path substringFromIndex:range.location+1]];
}

-(NSString*)fromUri:(NSString*)uri{
    // TODO: 일단은... URI == prefix+논리경로... 인데... 좀 더 깔끔하게 고쳐보자
    NSString* strUri = [[NSURL URLWithString:uri]path];
    return [strUri substringFromIndex:[FILESYSTEM_URI_PREFIX length]];
}

-(NSString*)toUri:(NSString*)virtualPath{
    // TODO: 일단은... URI == prefix+논리경로... 인데... 좀 더 깔끔하게 고쳐보자
    return [FILESYSTEM_URI_PREFIX stringByAppendingPathComponent:virtualPath];
}

-(NSString*)toVirtualPath:(NSString*)nativePath {
    for(NSString *prefix in [_fileSystemTable allKeys]) {
        NSObject<AxFileSystem>* fileSystem = [_fileSystemTable objectForKey:prefix];
        // toVirtualPath는 선택적 구현
        if([fileSystem respondsToSelector:@selector(toVirtualPath:)]) {
            NSString *ret = [fileSystem toVirtualPath:nativePath];
            if(ret != nil) {
                return [prefix stringByAppendingPathComponent:ret];
            }
        }
    }
    return nil;
}

-(NSString*)toNativePath:(NSString*)virtualPath {
    if (virtualPath == nil) {
        return nil;
    }
    NSArray *virtualPathComponent = [virtualPath pathComponents];
    NSString *prefix = [virtualPathComponent objectAtIndex:0];//[virtualPathComponent indexOfObject:0];
    NSObject<AxFileSystem> *fileSystem = [_fileSystemTable objectForKey:prefix];
    NSRange range = [virtualPath rangeOfString:prefix];//NSRangeFromString(prefix);//[virtualPath rangeOfString:@"/"];
    if(fileSystem != nil && [fileSystem respondsToSelector:@selector(toNativePath:)]) {
        return [fileSystem toNativePath:[virtualPath substringFromIndex:range.length]];//[virtualPath substringFromIndex:range.location+1]];
    }
    return nil;
}


@end
