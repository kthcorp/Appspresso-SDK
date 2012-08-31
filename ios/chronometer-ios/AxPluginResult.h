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
