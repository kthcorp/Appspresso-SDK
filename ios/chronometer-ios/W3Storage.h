/*
 * Appspresso
 *
 * Copyright (c) 2011 KT Hitel Corp.
 *
 * Appspresso SDK may be freely distributed under the MIT license.
 */

#import <Foundation/Foundation.h>

/*!
 * W3C의 WebStorage API를 정의한 인터페이스. 
 * 위젯 설정(config.xml 파일)에서 지정한 preference 들을 접근.
 * @version 1.0
 * @see //w3Webstorage http://www.w3.org/TR/webstorage/
 */
@protocol W3Storage

/*!
 * 저장된 항목 개수를 얻음.
 * 
 * @return 항목 개수
 * @since 1.0
 */
-(long)length;
/*!
 * 지정한 순번에 해당하는 항목의 키를 얻음.
 * 해당하는 항목이 존재 하지 않으면 nil을 반환.
 * 
 * @param index
 *           항목의 순번
 * @return 항목의 키 or nil
 * @since 1.0
 */
-(NSString *)key:(long)index;
/*!
 * 지정한 키에 해당하는 항목의 값을 얻음.
 * 해당하는 항목이 존재 하지 않으면 nil을 반환
 * 
 * @param key
 *           항목의 키
 * @return 항목의 값 or nil
 * @since 1.0
 */
-(NSString *)getItem:(NSString *)key;
/*!
 * 지정한 키에 해당하는 항목의 값을 변경.
 * 해당하는 항목이 존재하지 않으면 새로운 항목을 추가하고, 이미 존재하면 기존 항목을 덮어씀.
 * 
 * @param key
 *           항목의 키
 * @param value
 *           항목의 값
 * @since 1.0
 */
-(void)setItem:(NSString *)key :(NSString*)value;
/*!
 * 지정한 키에 해당하는 항목을 삭제.
 * 해당하는 항목이 존재하지 않으면 아무런 효과 없음(무시).
 * 
 * @param key
 *           항목의 키
 * @since 1.0
 */
-(void)removeItem:(NSString *)key;
/*!
 * 저장소의 모든 항목을 삭제.
 * 
 * @since 1.0
 */
-(void)clear;

@end