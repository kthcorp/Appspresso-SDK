//
//  FileSystemContentHandler.m
//  sail-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import "FileSystemContentHandler.h"
#import "DefaultWidgetAgent.h"
#import "DefaultFileSystemManager.h"
#import "DefaultFileSystem.h"
#import "DefaultFile.h"
#import "MimeTypeUtils.h"

@implementation FileSystemContentHandler

- (void)specificHandlerForApi:(NSString *)name value:(NSString *)value {
    NSString *uri = [name stringByAppendingPathComponent:[value stringByReplacingPercentEscapesUsingEncoding:NSUTF8StringEncoding]];
    NSString *path = [[self.widgetAgent getFileSystemManager] fromUri:uri];
    
    NSObject<AxFile> *axfile = [[self.widgetAgent getFileSystemManager] getFile:path];
    if(axfile == nil || ![axfile exists]) {
        // 404 not found
        self.status = 404;
        [self setContentType:MIME_TYPE_TEXT];
        [self replyResponse:[@"404 NOT FOUND" dataUsingEncoding:NSUTF8StringEncoding]];
        return;
    }
    if(![axfile isFile] || ![axfile canRead]) {
        // 400 bad request
        self.status = 400;
        [self setContentType:MIME_TYPE_TEXT];
        [self replyResponse:[@"400 BAD REQUEST" dataUsingEncoding:NSUTF8StringEncoding]];
        return;
    }

    [self setContentType:[MimeTypeUtils getMimeType:path]];
    [self replyResponse:[axfile getContentsAsData]];
}

@end