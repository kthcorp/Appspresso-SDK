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
 * 앱스프레소 가상 파일시스템의 파일 목록을 필터링하는 인터페이스.
 * @version 1.0
 */
@protocol AxFileFilter

/*!
 * 주어진 파일이 필터링된 결과에 포함될지 여부.
 * @param file 
 *          앱스프레소 가상 파일
 * @return 결과에 포함된다면 YES, 아니면 NO
 */
-(BOOL)acceptFile:(NSObject<AxFile>*)file;

@end
