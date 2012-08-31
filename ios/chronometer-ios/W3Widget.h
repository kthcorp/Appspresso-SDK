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

@protocol W3Storage;

/*!
 * W3C의 Widget API를 정의한 인터페이스. <br>
 * 위젯 설정(config.xml 파일)의 내용을 런타임/플러그인에서 접근. <br>
 * TODO: 다국어 설정 지원.
 * @version 1.0
 * @see //W3Widget http://www.w3.org/TR/widgets-apis
 */
@protocol W3Widget

/*!
 * 위젯 설정에서 지정한 식별자(widget태그의 id속성)를 얻음.
 * 
 * @return 위젯 식별자 문자열
 * @since 1.0
 */
-(NSString *)getId;
/*!
 * 위젯 설정에서 지정한 설명(description태그)을 얻음. 지정한 설명이 없으면 빈문자열("")을 반환.
 * @return 위젯 설명 문자열
 * @since 1.0
 */
-(NSString *)getDescription;
/*!
 * 위젯 설정에서 지정한 이름(name태그)을 얻음. 지정한 이름이 없으면 빈문자열("")을 반환.
 * @return 위젯 이름 문자열
 * @since 1.0
 */
-(NSString *)getName;
/*!
 * 위젯 설정에서 지정한 짧은 이름(name태그)을 얻음. 지정한 이름이 없으면 빈문자열("")을 반환.
 * @return 위젯 짧은 이름 문자열
 * @since 1.0
 */
-(NSString *)getShortName;
/*!
 * 위젯 설정에서 지정한 버전(widget태그의 version속성)를 얻음.
 * @return 위젯 짧은 이름 문자열
 * @since 1.0
 */
-(NSString *)getVersion;

/*!
 * 위젯 설정에서 지정한 제작자(@{literal author}태그)를 얻음. 지정한 제작자가 없으면 빈문자열("")을 반환.
 * @return 위젯 제작자 문자열
 * @since 1.0
 */
-(NSString *)getAuthor;
/*!
 * 위젯 설정에서 지정한 제작자 이메일(author태그의 email속성)을 얻음. 지정한 제작자 이메일이 없으면 빈문자열("")을 반환.
 * @return 위젯 제작자 이메일 문자열
 * @since 1.0
 */
-(NSString *)getAuthorEmail;
/*!
 * 위젯 설정에서 지정한 제작자 홈페이지(author태그의 href속성)을 얻음. 지정한 제작자 홈페이지가 없으면 빈문자열("")을 반환.
 * @return 위젯 제작자 홈페이지 문자열
 * @since 1.0
 */
-(NSString *)getAuthorHref;

/*!
 * 위젯 설정에서 지정한 preference들을 W3C WebStorage API의 Storage 인스턴스로 얻음.
 *
 * @return 위젯 preference들을 가진 Storage 인스턴스
 * @since 1.0
 */
-(id<W3Storage>)getPreferences;

/*!
 * 위젯 설정에서 지정한 너비(widget태그의 width속성)를 얻음.
 *
 * @return 위젯 너비 정수
 * @since 1.0
 */
-(long)getWidth;
/*!
 * 위젯 설정에서 지정한 높이(widget태그의 height속성)를 얻음.
 *
 * @return 위젯 높이 정수
 * @since 1.0
 */
-(long)getHeight;

@end