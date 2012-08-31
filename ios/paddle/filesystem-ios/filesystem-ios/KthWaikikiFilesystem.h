//
//  KthWaikikiFilesystem.h
//  filesystem-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import <Foundation/Foundation.h>
#import "DefaultAxPlugin.h"
#import "KthWaikikiFile.h"
#import "KthWaikikiFileStream.h"

/**
 * deviceapis.filesystem feature provider
 *
 * TODO: KthWaikikiFile/KthWaikikiFileStream 등을 AxFile 기반으로 다시 구현!!!
 */
@interface KthWaikikiFilesystem : DefaultAxPlugin {
}

#pragma mark FileSystemManager methods

/****************************************************************************************************************************************/
/* PendingOperation resolve(FileSystemSuccessCallback successCallback, ErrorCallback errorCallback, DOMString location, DOMString mode) */
/****************************************************************************************************************************************/

- (void)getMaxPathLength:(id<AxPluginContext>)context;
- (void)resolve:(id<AxPluginContext>)context;

#pragma mark File methods

/*********************************************************************************************************************************************************************/
/* DOMString toURI()                                                                                                                                                 */
/* PendingOperation listFiles(FileSystemListSuccessCallback successCallback, ErrorCallback errorCallback, FileFilter filter)                                         */
/* PendingOperation openStream(FileOpenSuccessCallback successCallback, ErrorCallback errorCallback, DOMString mode, DOMString encoding)                             */
/* PendingOperation readAsText(ReadFileAsStringSuccessCallback successCallback, ErrorCallback errorCallback, DOMString encoding)                                     */
/* PendingOperation copyTo(SuccessCallback successCallback, ErrorCallback errorCallback, DOMString originFilePath, DOMString destinationFilePath, boolean overwrite) */
/* PendingOperation moveTo(SuccessCallback successCallback, ErrorCallback errorCallback, DOMString originFilePath, DOMString destinationFilePath, boolean overwrite) */
/* File createDirectory(DOMString dirPath)                                                                                                                           */
/* File createFile(DOMString filePath)                                                                                                                               */
/* File resolve(DOMString filePath)                                                                                                                                  */
/* PendingOperation deleteDirectory(SuccessCallback successCallback, ErrorCallback errorCallback, DOMString directory, boolean recursive)                            */
/* PendingOperation deleteFile(SuccessCallback successCallback, ErrorCallback errorCallback, DOMString file)                                                         */
/*********************************************************************************************************************************************************************/

- (void)toURI:(id<AxPluginContext>)context;
- (void)listFiles:(id<AxPluginContext>)context;
- (void)openStream:(id<AxPluginContext>)context;
- (void)readAsText:(id<AxPluginContext>)context;
- (void)copyTo:(id<AxPluginContext>)context;
- (void)moveTo:(id<AxPluginContext>)context;
- (void)createDirectory:(id<AxPluginContext>)context;
- (void)createFile:(id<AxPluginContext>)context;
- (void)resolveFilePath:(id<AxPluginContext>)context;
- (void)deleteDirectory:(id<AxPluginContext>)context;
- (void)deleteFile:(id<AxPluginContext>)context;

#pragma mark FileStream methods

/*************************************************/
/* void close()                                  */
/* DOMString read(unsigned long charCount)       */
/* ByteArray readBytes(unsigned long byteCount)  */
/* DOMString readBase64(unsigned long byteCount) */
/* void write(DOMString stringData)              */
/* void writeBytes(ByteArray byteData)           */
/* void writeBase64(DOMString base64Data)        */
/*************************************************/

-(void)close:(id<AxPluginContext>)context;
-(void)read:(id<AxPluginContext>)context;
-(void)readBytes:(id<AxPluginContext>)context;
-(void)readBase64:(id<AxPluginContext>)context;
-(void)write:(id<AxPluginContext>)context;
-(void)writeBytes:(id<AxPluginContext>)context;
-(void)writeBase64:(id<AxPluginContext>)context;


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma public APIs
+ (KthWaikikiFileStream *)fileStreamWithFile:(KthWaikikiFile *)file;
+ (KthWaikikiFile *)fileWithFullPath:(NSString *)fullPath mode:(NSString *)mode result:(DefaultPluginResult *)result;
+ (NSString *)realPathFromFullPath:(NSString *)fullPath;
+ (NSString *)fullPathFromRealPath:(NSString *)realPath;
+ (NSString *)realPathFromURI:(NSString *)uri;
+ (BOOL)validateDestFullPath:(NSString *)desFullPath;
+ (NSMutableArray*)pathComponentsRemovingPrefixAndSuffixFromString:(NSString*)str;
//+ (KthWaikikiFile *)fileForHandle:(NSString *)handle;
//+ (void)addFile:(KthWaikikiFile *)file;
+ (KthWaikikiFile *)getFileWithCustomInfo:(NSDictionary *)info result:(DefaultPluginResult *)result;
+ (NSString *)_removeLastPathDelimiter:(NSString *)path;
+ (BOOL)_isDocumentRootLocationWithFullPath:(NSString *)fullPath;
+ (BOOL)_isDocumentRootLocationWithURI:(NSString *)uri;
+ (BOOL)_isDstPath:(NSString*) dstPath ContainsSrcPath:(NSString*) srcPath;
@end
