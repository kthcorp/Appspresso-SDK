/*
 * Appspresso
 *
 * Copyright (c) 2011 KT Hitel Corp.
 *
 * Appspresso SDK may be freely distributed under the MIT license.
 */

#import <Foundation/Foundation.h>

/*!
 * JSON 직렬화 지원. <br>
 * BOOL, NSArray, NSDictionary등의 JSON으로 직렬화할 수 있는 객체를 제외한 값을 자바스크립트와 주고 받으려면 이 인터페이스를 구현해야 함.
 * @version 1.0
 */
@protocol AxPluginResult <NSObject>

/*!
 * 객체를 JSON으로 직렬화할 수 있는 객체로 변환.
 * @return JSON으로 직렬화 할 수 있는 객체.
 */
-(id)getPluginResult;

@end
