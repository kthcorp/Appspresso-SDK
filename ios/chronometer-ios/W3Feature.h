/*
 * Appspresso
 *
 * Copyright (c) 2011 KT Hitel Corp.
 *
 * Appspresso SDK may be freely distributed under the MIT license.
 */


#import <Foundation/Foundation.h>

/*!
 * W3C의 Widget API를 정의한 인터페이스. <br>
 * 위젯 설정(config.xml 파일)에서 지정한 feature 들을 접근.
 * @deprecated 대체 스펙없이 W3C Widget API에서 제외됨.
 * @version 1.0
 * @see //w3Feature http://www.w3.org/TR/widgets-apis/
 */
@protocol W3Feature

/*!
 * 피쳐의 고유한 이름을 얻음.
 * 
 * @deprecated 대체 스펙없이 W3C Widget API에서 제외됨.
 * @return 이름 문자열
 * @since 1.0
 */
-(NSString*)getName;
/*!
 * 필수 피쳐인지 여부 확인(feature태그의 required속성)
 * 
 * @deprecated 대체 스펙없이 W3C Widget API에서 제외됨.
 * @return 필수이면 YES, 그렇지 않으면 NO
 * @since 1.0
 */
-(BOOL)isRequired;
/*!
 * 피쳐 추가 파라메터들((feature태그 아래의 param태그들)을 얻음.
 * 
 * @deprecated 대체 스펙없이 W3C Widget API에서 제외됨.
 * @return 피쳐의 추가 파라메터들의 NSDictionary
 * @since 1.0
 */
-(NSDictionary*)getParams;

@end
