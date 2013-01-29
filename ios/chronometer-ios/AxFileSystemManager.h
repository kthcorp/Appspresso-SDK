/*
 * Appspresso
 *
 * Copyright (c) 2011 KT Hitel Corp.
 *
 * Appspresso SDK may be freely distributed under the MIT license.
 */


#import <Foundation/Foundation.h>

@protocol AxFileSystem, AxFile;

/*!
 * 앱스프레소 런타임의 가상 파일시스템 관리자.
 * <p>
 * 커스텀 파일시스템을 제공하려면:
 * <ul>
 * <li>AxFile의 구현체(DefaultFile 또는 커스텀 구현체)</li>
 * <li>AxFileSystem의 구현체(DefaultFileSystem 또는 커스텀 구현체)</li>
 * <li>플러그인이 활성화될 때 mount: fileSystem: option 메소드를 이용해서 마운트</li>
 * <li>플러그인이 비활성화될 때 unmount: 메소드를 이용해서 마운트</li>
 * </ul>
 * 위의 절차를 거치면 deviceapis.filesystem을 통해 접근할 수 있는 가상 루트(virtual root)가 추가됨.
 * 
 * @version 1.0
 * @see //apple_ref/occ/intf/AxFile AxFile
 * @see //apple_ref/occ/intf/AxFileSystem AxFileSystem
 */

@protocol AxFileSystemManager <NSObject>

/*!
 * 지정한 접두어에 해당하는 가상 파일시스템을 마운트. <br>
 * 해당하는 가상 파일시스템이 이미 마운트되어 있을 경우 아무 효과 없음(무시).
 * @param prefix
 *          파일시스템 접두어(deviceapis.filesystem을 통해 접근할 수 있는 가상 루트(virtual root) 이름).
 * @param fileSystem
 *          앱스프레소 가상 파일시스템 구현체의 인스턴스
 * @param option
 *          추가 마운트 옵션 or nil
 * @return 마운트에 성공하면 YES, 실패시 NO
 */
-(BOOL)mount:(NSString*)prefix fileSystem:(NSObject<AxFileSystem>*)fileSystem option:(NSDictionary*)option;
/*!
 * 지정한 접두어에 해당 가상 파일시스템의 마운트 해제. <br>
 * 해당하는 가상 파일시스템이 없을 경우 아무 효과 없음(무시).
 * @param prefix
 *          파일시스템 접두어(deviceapis.filesystem을 통해 접근할 수 있는 가상 루트(virtual root) 이름).
 */
-(void)unmount:(NSString*)prefix;
/*!
 * 지정한 접두어로 마운트된 가상 파일시스템 인스턴스를 얻음.
 * @param prefix
 *          파일시스템 접두어(deviceapis.filesystem을 통해 접근할 수 있는 가상 루트(virtual root) 이름).
 * @return 가상 파일시스템 인스턴스 or nil
 */
-(NSObject<AxFileSystem>*)getFileSystem:(NSString*)prefix;
/*!
 * "파일시스템 접두어를 포함한 절대 & 가상 경로"에 해당하는 앱스프레소 가상 파일을 얻음.
 * @param path
 *          파일 경로
 * @return 가상 파일 인스턴스 or nil
 */
-(NSObject<AxFile>*)getFile:(NSString*)path;
/*!
 * 실제 파일의 절대 경로를 가상 경로로 변환. <br>
 * NOTE: 지정한 실제 경로에 대응하는 가상 파일시스템이 마운트되어 있지 않으면 nil을 변환.
 * @param nativePath
 *          실제 경로
 * @return 가상 경로 or nil
 */
-(NSString*)toVirtualPath:(NSString*)nativePath; //return virtaul path
/*!
 * 가상 경로를 실제 파일의 절대 경로로 변환. <br>
 * NOTE: 지정한 가상 경로에 대응하는 가상 파일 시스템이 마운트되어 있지 않으면 nil을 반환.
 * @param virtualPath
 *          가상 경로
 * @return 실제 경로 or nil
 */
-(NSString*)toNativePath:(NSString*)virtualPath; //return native path
/*!
 * URI를 "파일시스템 접두어를 포함한 절대 & 가상 경로"로 변환한다. <br>
 * NOTE: 파일에 대응하는 URI는 중복없이 고유하므로, 파일을 식별하기 위한 용도로 사용될 수 있지만, 문자열의 내용 자체는 무의미.
 * @param uri
 *          변환할 URI
 * @return 변환된 파일 경로
 */
-(NSString*)fromUri:(NSString*)uri; //return virtual path
/*!
 * "파일 시스템 접두어를 포함한 절대 & 가상 경로"를 URI로 변환한다. <br>
 * NOTE: 파일에 대응하는 URI는 중복없이 고유하므로, 파일을 식별하기 위한 용도로 사용될 수 있지만, 문자열의 내용 자체는 무의미.
 * @param virtualPath
 *          변환할 파일 경로
 * @return 변환된 URI
 */
-(NSString*)toUri:(NSString*)virtualPath; //return URI

@end
