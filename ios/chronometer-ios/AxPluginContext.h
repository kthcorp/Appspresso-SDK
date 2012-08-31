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

@protocol AxRuntimeContext;

/*!
 * 앱스프레소 플러그인의 메소드를 실행하는 동안 필요한 정보를 보유하는 인터페이스.
 * <p>
 * @link //apple_ref/occ/intf/AxRuntimeContext @/link, @link //apple_ref/occ/intf/AxPlugin @/link 과 더불어
 * 앱스프레소 플러그인 개발에서 가장 핵심적인 인터페이스.
 * <p>
 * <ul>
 * <li>메소드의 ID, 이름, 파라메터를 확인.</li>
 * <li>메소드의 실행 결과 또는 에러를 자바스크립트에 전달.</li>
 * <li>메소드를 실행하는 동안 유효한 범용 속성(attribute) 집합.</li>
 * </ul>
 * <p>
 * 파라메터 확인 예:
 * 
 * <pre>
 * @textblock
 * // 자바스크립트에서 이렇게 파라메터를 전달했다면...
 * foo(true, 123, "abc", { "first": true, "second": 123, "thrid": "abc" }, [ true, 123, "abc" ]);
 * @/textblock
 * </pre>
 * 
 * <pre>
 * @textblock
 * // Objective-C에서는 이렇게 파라메터을 얻는다...
 * [context getParamAsBoolean:0]; // true
 * [context getParamAsBoolean:1[; // error!
 * [context getParamAsBoolean:1 defaultValue:false]; // false
 * [context getParamAsNumber:1]; // 123
 * [context getParamAsNumber:2]; // error!
 * [context getParamAsNumber:2 defaultValue:456]; // 456
 * [context getParamAsString:2]; // "abc"
 * [context getParamAsString:3]; // error!
 * [context getParamAsString:3 defaultValue:"xyz"]; // "xyz"
 * [context getNamedParamAsBoolean:3 name:"first"]; // true
 * [context getNamedParamAsBoolean:3 name:"missing"]; // error!
 * [context getNamedParamAsBoolean:3 name:"missing" defaultValue:false]; // false
 * [context getNamedParamAsNumber:3 name:"second"]; // 123
 * [context getNamedParamAsNumber:3 name:"missing"]; // error!
 * [context getNamedParamAsNumber:3 name:"missing" defaultValue:456]; // 456
 * [context getNamedParamAsNumber:3 name:"third"]; // "abc"
 * [context getNamedParamAsNumber:3 name:"missing"]; // error!
 * [context getNamedParamAsNumber:3 name:"missing" defaultValue:"xyz"]; // "xyz"
 * ...
 * @/textblock
 * </pre>
 * 
 * @version 1.0
 * @see //apple_ref/occ/intf/AxRuntimeContext AxRuntimeContext protocol
 * @see //apple_ref/occ/intf/AxPlugin AxPlugin protocol
 */
@protocol AxPluginContext <NSObject>

/*!
 * 메소드 호출의 고유 ID를 얻음.
 * <p>
 * 고유 ID는 앱이 실행하는 동안 유일한 정수. 같은 메소드라도 호출할 때 마다 ID가 다름.
 * <p>
 * 긴 시간이 필요한 비동기 호출에서 각 메소드 호출을 식별하기 위해서 사용.
 * 
 * @return 메소드 ID
 * @since 1.0
 */
-(NSNumber*)getId;

/*!
 * 메소드 이름을 얻음.
 * 
 * @return 메소드 이름.
 * @since 1.0
 */
-(NSString*)getMethod;

/*!
 * 메소드 접두어(네임스페이스)를 얻음.
 * <p>
 * 자바스크립트에서 <code>ax.plugin(prefix, pluginObject, namespace)</code> 함수로 플러그인
 * 자바스크립트-네이티브 브릿지를 등록할 때 지정한 값.
 * 
 * @return 메소드 접두어.
 * @since 1.0
 */
-(NSString*)getPrefix;

/*!
 * 자바스크립트로 부터 전달받은 파라메터 목록을 "객체 배열"로 얻음.
 * <p>
 * 결과 배열의 각 객체는 NSString, NSNumber, NSArray, NSDictionary 또는 nil.
 * 
 * @return 파라메터 목록.
 * @since 1.0
 */
-(NSArray*)getParams;

/*!
 * 지정한 위치의 파라메터 값을 "객체"로 얻음.
 * <p>
 * 결과는 NSString, NSNumber, NSArray, NSDictionary 또는 nil.
 * <p>
 * <code>getParams()[index]</code>와 동일.
 * 
 * @param index
 *            파라메터 위치
 * @return 파라메터 값
 * @throws AxError
 *             지정한 파라메터 없거나 형식이 맞지 않을 경우
 * @since 1.0
 */
-(id)getParam:(int)index;

/*!
 * 지정한 위치의 파라메터 값을 "객체"로 얻음.
 * <p>
 * 지정한 파라메터가 없거나 형식이 맞지 않을 경우 기본 값을 반환.
 * <p>
 * 결과는 NSString, NSNumber, NSArray, NSDictionary 또는 nil.
 * 
 * @param index
 *            파라메터 위치
 * @param defaultValue
 *            기본 값
 * @return 파라메터 값.
 * @since 1.0
 */
-(id)getParam:(int)index defaultValue:(id)defaultValue;

/*!
 * 지정한 위치의 "JSON 객체" 파라메터에서 지정한 이름을 가진 속성을 "객체"로 얻음.
 * <p>
 * 결과는 NSString, NSNumber, NSArray, NSDictionary 또는 nil.
 * <p>
 * <code>[context getParamAsMap:index] objectForKey:name]</code>와 동일.
 * 
 * @param index
 *            JSON 객체 파라메터의 위치
 * @param name
 *            JSON 객체 파라메터의 속성 이름
 * @return 파라메터 값
 * @throws AxError
 *             지정한 파라메터 없거나 형식이 맞지 않을 경우
 * @since 1.0
 */
-(id)getParam:(int)index name:(NSString*)name;

/*!
 * 지정한 위치의 "JSON 객체" 파라메터에서 지정한 이름을 가진 속성을 "객체"로 얻음.
 * <p>
 * 지정한 파라메터가 없거나 형식이 맞지 않을 경우 기본 값을 반환.
 * <p>
 * 결과는 NSString, NSNumber, NSArray, NSDictionary 또는 nil.
 * <p>
 * <code>[context getParamAsMap:index] objectForKey:name]</code>와 동일.
 * 
 * @param index
 *            JSON 객체 파라메터의 위치
 * @param name
 *            JSON 객체 파라메터의 속성 이름
 * @param defaultValue
 *            기본 값
 * @return 파라메터 값
 * @throws AxError
 *             지정한 파라메터 없거나 형식이 맞지 않을 경우
 * @since 1.0
 */
-(id)getParam:(int)index name:(NSString*)name defaultValue:(id)defaultValue;

/*!
 * 지정한 위치의 파라메터 값을 "NSString" 객체로 얻음.
 * 
 * @param index
 *            파라메터 위치
 * @return 파라메터 값
 * @throws AxError
 *             지정한 파라메터 없거나 형식이 맞지 않을 경우
 * @since 1.0
 */
-(NSString*)getParamAsString:(int)index;

/*!
 * 지정한 위치의 파라메터 값을 "NSString" 객체로 얻음.
 * <p>
 * 지정한 파라메터가 없거나 형식이 맞지 않을 경우 기본 값을 반환.
 * 
 * @param index
 *            파라메터 위치
 * @param defaultValue
 *            기본 값
 * @return 파라메터 값.
 * @since 1.0
 */
-(NSString*)getParamAsString:(int)index defaultValue:(NSString*)defaultValue;

/*!
 * 지정한 위치의 "JSON 객체" 파라메터에서 지정한 이름을 가진 속성을 "NSString" 객체로 얻음.
 * 
 * @param index
 *            JSON 객체 파라메터의 위치
 * @param name
 *            JSON 객체 파라메터의 속성 이름
 * @return 파라메터 값
 * @throws AxError
 *             지정한 파라메터 없거나 형식이 맞지 않을 경우
 * @since 1.0
 */
-(NSString*)getParamAsString:(int)index name:(NSString*)name;

/*!
 * 지정한 위치의 "JSON 객체" 파라메터에서 지정한 이름을 가진 속성을 "NSString" 객체로 얻음.
 * <p>
 * 지정한 파라메터가 없거나 형식이 맞지 않을 경우 기본 값을 반환.
 * 
 * @param index
 *            JSON 객체 파라메터의 위치
 * @param name
 *            JSON 객체 파라메터의 속성 이름
 * @param defaultValue
 *            기본 값
 * @return 파라메터 값
 * @since 1.0
 */
-(NSString*)getParamAsString:(int)index name:(NSString*)name defaultValue:(NSString*)defaultValue;

/*!
 * 지정한 위치의 파라메터 값을 "NSNumber" 객체로 얻음.
 * <p>
 * 결과 NSNumber 객체의 intValue, longValue, floatValue, boolValue 등의 메소드를 통해
 * int, long, float, double, bool 등의 프리미티브 타입으로 변환할 수 있음.
 * 
 * @param index
 *            파라메터 위치
 * @return 파라메터 값
 * @throws AxError
 *             지정한 파라메터 없거나 형식이 맞지 않을 경우
 * @since 1.0
 */
-(NSNumber*)getParamAsNumber:(int)index;

/*!
 * 지정한 위치의 파라메터 값을 "NSNumber" 객체로 얻음.
 * <p>
 * 지정한 파라메터가 없거나 형식이 맞지 않을 경우 기본 값을 반환.
 * 
 * @param index
 *            파라메터 위치
 * @param defaultValue
 *            기본 값
 * @return 파라메터 값.
 * @since 1.0
 */
-(NSNumber*)getParamAsNumber:(int)index defaultValue:(NSNumber*)defaultValue;

/*!
 * 지정한 위치의 "JSON 객체" 파라메터에서 지정한 이름을 가진 속성을 "NSNumber" 객체로 얻음.
 * 
 * @param index
 *            JSON 객체 파라메터의 위치
 * @param name
 *            JSON 객체 파라메터의 속성 이름
 * @return 파라메터 값
 * @throws AxError
 *             지정한 파라메터 없거나 형식이 맞지 않을 경우
 * @since 1.0
 */
-(NSNumber*)getParamAsNumber:(int)index name:(NSString*)name;

/*!
 * 지정한 위치의 "JSON 객체" 파라메터에서 지정한 이름을 가진 속성을 "NSNumber" 객체로 얻음.
 * <p>
 * 지정한 파라메터가 없거나 형식이 맞지 않을 경우 기본 값을 반환.
 * 
 * @param index
 *            JSON 객체 파라메터의 위치
 * @param name
 *            JSON 객체 파라메터의 속성 이름
 * @param defaultValue
 *            기본 값
 * @return 파라메터 값
 * @since 1.0
 */
-(NSNumber*)getParamAsNumber:(int)index name:(NSString*)name defaultValue:(NSNumber*)defaultValue;

/*!
 * 지정한 위치의 파라메터 값을 "BOOL" 값으로 얻음.
 *
 * @param index
 *            파라메터 위치
 * @return 파라메터 값
 * @throws AxError
 *             지정한 파라메터 없거나 형식이 맞지 않을 경우
 * @since 1.0
 */
-(BOOL)getParamAsBoolean:(int)index;

/*!
 * 지정한 위치의 파라메터 값을 "BOOL" 값으로 얻음.
 * <p>
 * 지정한 파라메터가 없거나 형식이 맞지 않을 경우 기본 값을 반환.
 * 
 * @param index
 *            파라메터 위치
 * @param defaultValue
 *            기본 값
 * @return 파라메터 값.
 * @since 1.0
 */
-(BOOL)getParamAsBoolean:(int)index defaultValue:(BOOL)defaultValue;

/*!
 * 지정한 위치의 "JSON 객체" 파라메터에서 지정한 이름을 가진 속성을 "BOOL" 값으로 얻음.
 * 
 * @param index
 *            JSON 객체 파라메터의 위치
 * @param name
 *            JSON 객체 파라메터의 속성 이름
 * @return 파라메터 값
 * @throws AxError
 *             지정한 파라메터 없거나 형식이 맞지 않을 경우
 * @since 1.0
 */
-(BOOL)getParamAsBoolean:(int)index name:(NSString*)name;

/*!
 * 지정한 위치의 "JSON 객체" 파라메터에서 지정한 이름을 가진 속성을 "BOOL" 값으로 얻음.
 * <p>
 * 지정한 파라메터가 없거나 형식이 맞지 않을 경우 기본 값을 반환.
 * 
 * @param index
 *            JSON 객체 파라메터의 위치
 * @param name
 *            JSON 객체 파라메터의 속성 이름
 * @param defaultValue
 *            기본 값
 * @return 파라메터 값
 * @since 1.0
 */
-(BOOL)getParamAsBoolean:(int)index name:(NSString*)name defaultValue:(BOOL)defaultValue;

/*!
 * 지정한 위치의 파라메터 값을 "NSInteger" 값으로 얻음.
 *
 * @param index
 *            파라메터 위치
 * @return 파라메터 값
 * @throws AxError
 *             지정한 파라메터 없거나 형식이 맞지 않을 경우
 * @since 1.0
 */
-(NSInteger)getParamAsInteger:(int)index;

/*!
 * 지정한 위치의 파라메터 값을 "NSInteger" 값으로 얻음.
 * <p>
 * 지정한 파라메터가 없거나 형식이 맞지 않을 경우 기본 값을 반환.
 * 
 * @param index
 *            파라메터 위치
 * @param defaultValue
 *            기본 값
 * @return 파라메터 값.
 * @since 1.0
 */
-(NSInteger)getParamAsInteger:(int)index defaultValue:(NSInteger)defaultValue;

/*!
 * 지정한 위치의 "JSON 객체" 파라메터에서 지정한 이름을 가진 속성을 "NSInteger" 값으로 얻음.
 * 
 * @param index
 *            JSON 객체 파라메터의 위치
 * @param name
 *            JSON 객체 파라메터의 속성 이름
 * @return 파라메터 값
 * @throws AxError
 *             지정한 파라메터 없거나 형식이 맞지 않을 경우
 * @since 1.0
 */
-(NSInteger)getParamAsInteger:(int)index name:(NSString*)name;

/*!
 * 지정한 위치의 "JSON 객체" 파라메터에서 지정한 이름을 가진 속성을 "NSInteger" 값으로 얻음.
 * <p>
 * 지정한 파라메터가 없거나 형식이 맞지 않을 경우 기본 값을 반환.
 * 
 * @param index
 *            JSON 객체 파라메터의 위치
 * @param name
 *            JSON 객체 파라메터의 속성 이름
 * @param defaultValue
 *            기본 값
 * @return 파라메터 값
 * @since 1.0
 */
-(NSInteger)getParamAsInteger:(int)index name:(NSString*)name defaultValue:(NSInteger)defaultValue;

/*!
 * 지정한 위치의 파라메터 값을 "NSArray"로 얻음.
 * <p>
 * 결과 배열의 각 객체는 NSString, NSNumber, NSArray, NSDictionary 또는 nil.
 * 
 * @param index
 *            파라메터 위치
 * @return 파라메터 값
 * @throws AxError
 *             지정한 파라메터 없거나 형식이 맞지 않을 경우
 * @since 1.0
 */
-(NSArray*)getParamAsArray:(int)index;

/*!
 * 지정한 위치의 파라메터 값을 "NSArray"로 얻음.
 * <p>
 * 결과 배열의 각 요소는 NSString, NSNumber, NSArray, NSDictionary 또는 nil.
 * <p>
 * 지정한 파라메터가 없거나 형식이 맞지 않을 경우 기본 값을 반환.
 * 
 * @param index
 *            파라메터 위치
 * @param defaultValue
 *            기본 값
 * @return 파라메터 값.
 * @since 1.0
 */
-(NSArray*)getParamAsArray:(int)index defaultValue:(NSArray*)defaultValue;

/*!
 * 지정한 위치의 "JSON 배열" 파라메터에서 지정한 이름을 가진 속성을 "NSArray"로 얻음.
 * <p>
 * 결과 배열의 각 요소는 NSString, NSNumber, NSArray, NSDictionary 또는 nil.
 * 
 * @param index
 *            JSON 배열 파라메터의 위치
 * @param name
 *            JSON 배열 파라메터의 속성 이름
 * @return 파라메터 값
 * @throws AxError
 *             지정한 파라메터 없거나 형식이 맞지 않을 경우
 * @since 1.0
 */
-(NSArray*)getParamAsArray:(int)index name:(NSString*)name;

/*!
 * 지정한 위치의 "JSON 배열" 파라메터에서 지정한 이름을 가진 속성을 "NSArray"로 얻음.
 * <p>
 * 결과 배열의 각 요소는 NSString, NSNumber, NSArray, NSDictionary 또는 nil.
 * <p>
 * 지정한 파라메터가 없거나 형식이 맞지 않을 경우 기본 값을 반환.
 * 
 * @param index
 *            JSON 배열 파라메터의 위치
 * @param name
 *            JSON 배열 파라메터의 속성 이름
 * @param defaultValue
 *            기본 값
 * @return 파라메터 값
 * @since 1.0
 */
-(NSArray*)getParamAsArray:(int)index name:(NSString*)name defaultValue:(NSArray*)defaultValue;

/*!
 * 지정한 위치의 파라메터 값을 "NSDictionary"로 얻음.
 * <p>
 * 결과 사전의 키는 NSString, 값은 NSString, NSNumber, NSArray, NSDictionary 또는 nil.
 * 
 * @param index
 *            파라메터 위치
 * @return 파라메터 값
 * @throws AxError
 *             지정한 파라메터 없거나 형식이 맞지 않을 경우
 * @since 1.0
 */
-(NSDictionary*)getParamAsDictionary:(int)index;

/*!
 * 지정한 위치의 파라메터 값을 "NSDictionary"로 얻음.
 * <p>
 * 결과 사전의 키는 NSString, 값은 NSString, NSNumber, NSArray, NSDictionary 또는 nil.
 * <p>
 * 지정한 파라메터가 없거나 형식이 맞지 않을 경우 기본 값을 반환.
 * 
 * @param index
 *            파라메터 위치
 * @param defaultValue
 *            기본 값
 * @return 파라메터 값.
 * @since 1.0
 */
-(NSDictionary*)getParamAsDictionary:(int)index defaultValue:(NSDictionary*)defaultValue;

/*!
 * 지정한 위치의 "JSON 배열" 파라메터에서 지정한 이름을 가진 속성을 "NSDictionary"로 얻음.
 * <p>
 * 결과 사전의 키는 NSString, 값은 NSString, NSNumber, NSArray, NSDictionary 또는 nil.
 * 
 * @param index
 *            JSON 배열 파라메터의 위치
 * @param name
 *            JSON 배열 파라메터의 속성 이름
 * @return 파라메터 값
 * @throws AxError
 *             지정한 파라메터 없거나 형식이 맞지 않을 경우
 * @since 1.0
 */
-(NSDictionary*)getParamAsDictionary:(int)index name:(NSString*)name;

/*!
 * 지정한 위치의 "JSON 배열" 파라메터에서 지정한 이름을 가진 속성을 "NSDictionary"로 얻음.
 * <p>
 * 결과 사전의 요소는 NSString, NSNumber, NSArray, NSDictionary 또는 nil.
 * <p>
 * 지정한 파라메터가 없거나 형식이 맞지 않을 경우 기본 값을 반환.
 * 
 * @param index
 *            JSON 배열 파라메터의 위치
 * @param name
 *            JSON 배열 파라메터의 속성 이름
 * @param defaultValue
 *            기본 값
 * @return 파라메터 값
 * @since 1.0
 */
-(NSDictionary*)getParamAsDictionary:(int)index name:(NSString*)name defaultValue:(NSDictionary*)defaultValue;

/*!
 * 메소드 실행 성공(반환할 결과 없음).
 * 
 * 전달할 결과가 없더라도 성공 또는 에러 여부는 전달해야 함.
 * 
 * @since 1.0
 */
-(void)sendResult;

/*!
 * 메소드 실행 성공.
 * 
 * @param result
 *            반환할 결과.
 * @since 1.0
 */
-(void)sendResult:(id)result;

/*!
 * 메소드 호출 실패.
 * 
 * @param code
 *            에러 코드
 * @since 1.0
 */
-(void)sendError:(NSInteger)code;

/*!
 * 메소드 호출 실패.
 * 
 * @param code
 *            에러 코드
 * @param message
 *            에러 메시지
 * @since 1.0
 */
-(void)sendError:(NSInteger)code message:(NSString *)message;

//-(void)sendError:(AxError error);

/*!
 * response watch result
 *
 * @param object
 *            반환할 결과.
 * @since 1.2
 */
-(void)sendWatchResult:(id)result;

/*!
 * response watch error
 *
 * @param code
 *            에러 코드
 * @param message
 *            에러 메시지
 * @since 1.2
 */
-(void)sendWatchError:(NSInteger)code message:(NSString *)message;

/*!
 * 속성 설정.
 * <p>
 * 같은 이름을 가진 속성이 이미 있을 경우에는 기존 속성 값을 변경하고, 그렇지 않을 경우에는 새 속성이 추가됨.
 * <p>
 * 이 속성은 메소드가 실행되는 동안 유효하므로, 긴 메소드 처리동안 상태 유지 등의 다양한 용도로 활용할 수 있음.
 * 
 * @param key
 *            속성 이름
 * @param value
 *            속성 값
 * @since 1.0
 */
-(void)setAttribute:(NSString*)key value:(id)value;

/*!
 * 속성 값을 얻음.
 * <p>
 * 지정한 이름을 가진 속성이 없을 경우 nil을 반환.
 * 
 * @param key
 *            속성 이름
 * @return 속성 값
 * @since 1.0
 */
-(id)getAttribute:(NSString*)key;

/*!
 * 속성 삭제.
 * <p>
 * 지정한 이름을 가진 속성이 없을 경우 아무런 효과 없음(무시).
 * 
 * @param key
 *            속성 이름
 * @since 1.0
 */
-(void)removeAttribute:(NSString*)key;

@end
