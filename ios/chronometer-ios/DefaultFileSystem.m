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


#import "DefaultFileSystem.h"
#import "DefaultFile.h"
#import "AxRuntimeContext.h"

@implementation DefaultFileSystem

#pragma mark -

-(id)initWithBaseDirectory:(NSString*)path canRead:(BOOL)r canWrite:(BOOL)w {
    if ((self = [super init])) {
        _baseDir = [path retain];
        _isReadable = r;
        _isWritable = w;

        _root = [[DefaultFile alloc] initWithFilePath:_baseDir parent:nil fileSystem:self];
        
        // 쓰기가능한 파일시스템이라면... 베이스디렉토리가 없으면 지금 만들어준다.???
        if(w) {
            NSFileManager *fm = [[NSFileManager alloc]init];
            if (![fm fileExistsAtPath:path]) {
                [fm createDirectoryAtPath:path withIntermediateDirectories:NO attributes:nil error:nil];
            }
            [fm release];
        }
    }
    return self;
}

-(void)dealloc {
    [_root release];
    [_baseDir release];
    [super dealloc];
}

#pragma mark AxFileSystem

-(BOOL)onMount:(NSString*)prefix option:(NSDictionary*)option{
    return TRUE;
}

-(void)onUnmount{
}

-(NSObject<AxFile>*)getRoot{ 
    if (!_isReadable) {
        return nil;
    }
    return [_root retain];
}

-(NSObject<AxFile>*)getFile:(NSString*)path{
    if (!_isReadable || path == nil) {
        return nil;
    }
    if([path length] == 0 || [path isEqualToString:@"/"]) {
        return [_root retain];
    }
    // NOTE: 재귀 호출!!
    NSObject<AxFile>* parent = [self getFile:[path stringByDeletingLastPathComponent]]; 
    return [DefaultFile fileWithFilePath:[_baseDir stringByAppendingPathComponent:path] parent:parent fileSystem:self];
}

-(BOOL)canRead{
    return _isReadable;
}

-(BOOL)canWrite{
    return _isWritable;
}

-(BOOL)isThisValidPathComponent:(NSString*)component {
    if (component == @"." || component == @"..") {
        return NO;
    }
    return YES;
}

-(NSString*)validateVirtualPath:(NSString*)virtualPath {
    // TODO: 앞뒤에 / 등을 포함한 좀 더 정교한 에러 검사
    if ([virtualPath isEqualToString:@"/"]) {
        return nil;
    }
    NSMutableArray *pathComponet = [NSMutableArray arrayWithArray:[virtualPath pathComponents]];
    for (NSString* component in pathComponet) {
        if (![self isThisValidPathComponent:component]){
            return nil;
        }
    }
    if ([virtualPath hasSuffix:@"/"]){
        [pathComponet removeLastObject];
    }
    if ([virtualPath hasPrefix:@"/"]) {
        [pathComponet removeObjectAtIndex:0];
    }
    return [NSString pathWithComponents:pathComponet];
}

-(NSString*)toVirtualPath:(NSString *)nativePath {
    // 지정한 네이티브 경로가
    // 이 파일시스템의 베이스 디렉토리에 아래를 가리키는 경로일때만 변환 가능
    if([nativePath hasPrefix:_baseDir]) {
        return [self validateVirtualPath:[nativePath substringFromIndex:[_baseDir length]+1]];
    }
    return nil;
}

-(NSString*)toNativePath:(NSString *)virtualPath {
    return [_baseDir stringByAppendingPathComponent:[self validateVirtualPath:virtualPath]];
}


#pragma mark -

+(id)fileSystemWithBaseDirectory:(NSString*)path canRead:(BOOL)r canWrite:(BOOL)w {
    return [[[DefaultFileSystem alloc] initWithBaseDirectory:path canRead:r canWrite:w] autorelease];
}

@end
