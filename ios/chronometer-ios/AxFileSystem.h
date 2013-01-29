/*
 * Appspresso
 *
 * Copyright (c) 2011 KT Hitel Corp.
 *
 * Appspresso SDK may be freely distributed under the MIT license.
 */


#import <Foundation/Foundation.h>

@protocol AxFile;

/*!
 * 앱스프레소의 가상 파일시스템
 * @version 1.0
 */

@protocol AxFileSystem

/*!
 * AxFileSystem이 마운트 되었을 때 호출.
 * @param prefix
 *          prefix for mounted filesystem. similar to mount-point.
 * @param option
 *          AxFileSystem을 mount하는데 필요한 옵션
 * @return 마운트에 성공하면 YES, 실패시 NO
 */
-(BOOL)onMount:(NSString*)prefix option:(NSDictionary*)option;
/*!
 * AxFileSystem이 마운트 해제 되었을 때 호출.   
 */
-(void)onUnmount;
/*!
 * AxFileSystem의 root가 되는 AxFile을 반환.
 *
 * @return root AxFile
 */
-(NSObject<AxFile>*)getRoot;
/*!
 * 지정한 경로에 해당되는 AxFileSystem의 하위 AxFile을 반환.
 * @param path
 *          relative path to filesystem root(without filesystem prefix)
 * @return AxFile. 해당되는 AxFile이 존재하지 않을 시 nil.
 */
-(NSObject<AxFile>*)getFile:(NSString*)path;
/*!
 * AxFileSystem의 읽기 가능 여부를 반환.
 * @return 읽기 가능하면 YES, 아닐 경우 NO
 */
-(BOOL)canRead;
/*!
 * AxFileSystem의 쓰기 가능 여부를 반환.
 * @return 쓰기 가능하면 YES, 아닐 경우 NO
 */
-(BOOL)canWrite;

@optional

/*!
 * 가상 경로를 실제 파일의 절대 경로로 변환. 
 *
 * @param virtualPath
 *           "virtual" path (without the prefix)
 * @return "native" path (with the prefix)
 */
-(NSString*)toNativePath:(NSString*)virtualPath;

/*!
 * 실제 파일의 절대 경로를 가상 경로로 변환
 *
 * NOTE: 물리적인 파일 시스템 기반의 파일시스템만 구현.
 *
 * @param nativePath
 *           "native" path (with the prefix)
 * @return "virtual" path (with the prefix)
 */
-(NSString*)toVirtualPath:(NSString*)nativePath;

@end
