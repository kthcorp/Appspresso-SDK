/*
 * Appspresso
 * 
 * Copyright (c) 2011 KT Hitel Corp.
 * 
 * This source is subject to Appspresso license terms. Please see http://appspresso.com/ for more
 * information.
 * 
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
 * FOR A PARTICULAR PURPOSE.
 */
package com.appspresso.api;

import java.util.Map;

/**
 * 앱스프레소 플러그인의 메소드를 실행하는 동안 필요한 정보를 보유하는 인터페이스.
 * <p>
 * {@link AxRuntimeContext}, {@link AxPlugin}과 더불어 앱스프레소 플러그인 개발에서 가장 핵심적인 인터페이스.
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
 * // 자바스크립트에서 이렇게 파라메터를 전달했다면...
 * foo(true, 123, "abc", { "first": true, "second": 123, "thrid": "abc" }, [ true, 123, "abc" ]);
 * </pre>
 * 
 * <pre>
 * // 자바에서는 이렇게 파라메터을 얻는다...
 * getParamAsBoolean(0); // true
 * getParamAsBoolean(1); // error!
 * getParamAsBoolean(1, false); // false
 * getParamAsNumber(1); // 123
 * getParamAsNumber(2); // error!
 * getParamAsNumber(2, 456); // 456
 * getParamAsString(2); // "abc"
 * getParamAsString(3); // error!
 * getParamAsString(3, "xyz"); // "xyz"
 * getNamedParamAsBoolean(3, "first"); // true
 * getNamedParamAsBoolean(3, "missing"); // error!
 * getNamedParamAsBoolean(3, "missing", false); // false
 * getNamedParamAsNumber(3, "second"); // 123
 * getNamedParamAsNumber(3, "missing"); // error!
 * getNamedParamAsNumber(3, "missing", 456); // 456
 * getNamedParamAsNumber(3, "third"); // "abc"
 * getNamedParamAsNumber(3, "missing"); // error!
 * getNamedParamAsNumber(3, "missing", "xyz"); // "xyz"
 * ...
 * </pre>
 * 
 * @version 1.0
 */
public interface AxPluginContext {

    /**
     * 메소드 호출의 고유 ID를 얻음.
     * <p>
     * 고유 ID는 앱이 실행하는 동안 유일한 정수. 같은 메소드라도 호출할 때 마다 ID가 다름.
     * <p>
     * 긴 시간이 필요한 비동기 호출에서 각 메소드 호출을 식별하기 위해서 사용.
     * 
     * @return 메소드 ID
     * @since 1.0
     */
    int getId();

    /**
     * 메소드 이름을 얻음.
     * 
     * @return 메소드 이름.
     * @since 1.0
     */
    String getMethod();

    /**
     * 메소드 접두어(네임스페이스)를 얻음.
     * <p>
     * 자바스크립트에서 <code>ax.plugin(prefix, pluginObject, namespace)</code> 함수로 플러그인 자바스크립트-네이티브 브릿지를
     * 등록할 때 지정한 값.
     * 
     * @return 메소드 접두어.
     * @since 1.0
     */
    String getPrefix();

    /**
     * 자바스크립트로 부터 전달받은 파라메터 목록을 "객체 배열"로 얻음.
     * <p>
     * 결과 배열의 각 객체는 String, Number, Boolean, Object[], Map<String, Object> 또는 {@literal null}.
     * 
     * @return 파라메터 목록.
     * @since 1.0
     */
    Object[] getParams();

    /**
     * 지정한 위치의 파라메터 값을 "객체"로 얻음.
     * <p>
     * 결과는 String, Number, Boolean, Object[], Map<String, Object> 또는 {@literal null}.
     * <p>
     * <code>getParams()[index]</code>와 동일.
     * 
     * @param index 파라메터 위치
     * @return 파라메터 값
     * @throws AxError 지정한 파라메터 없거나 형식이 맞지 않을 경우
     * @since 1.0
     */
    Object getParam(int index);

    /**
     * 지정한 위치의 파라메터 값을 "객체"로 얻음.
     * <p>
     * 지정한 파라메터가 없거나 형식이 맞지 않을 경우 기본 값을 반환.
     * <p>
     * 결과는 String, Number, Boolean, Object[], Map<String, Object> 또는 {@literal null}.
     * 
     * @param index 파라메터 위치
     * @param defaultValue 기본 값
     * @return 파라메터 값.
     * @since 1.0
     */
    Object getParam(int index, Object defaultValue);

    /**
     * 지정한 위치의 "JSON 객체" 파라메터에서 지정한 이름을 가진 속성을 "객체"로 얻음.
     * <p>
     * 결과는 String, Number, Boolean, Object[], Map<String, Object> 또는 {@literal null}.
     * <p>
     * <code>getParamAsMap(index).get(name)</code>와 동일.
     * 
     * @param index JSON 객체 파라메터의 위치
     * @param name JSON 객체 파라메터의 속성 이름
     * @return 파라메터 값
     * @throws AxError 지정한 파라메터 없거나 형식이 맞지 않을 경우
     * @since 1.0
     */
    Object getNamedParam(int index, String name);

    /**
     * 지정한 위치의 "JSON 객체" 파라메터에서 지정한 이름을 가진 속성을 "객체"로 얻음.
     * <p>
     * 지정한 파라메터가 없거나 형식이 맞지 않을 경우 기본 값을 반환.
     * <p>
     * 결과는 String, Number, Boolean, Object[], Map<String, Object> 또는 {@literal null}.
     * 
     * @param index JSON 객체 파라메터의 위치
     * @param name JSON 객체 파라메터의 속성 이름
     * @param defaultValue 기본 값
     * @return 파라메터 값
     * @since 1.0
     */
    Object getNamedParam(int index, String name, Object defaultValue);

    /**
     * 지정한 위치의 파라메터 값을 "String" 객체로 얻음.
     * 
     * @param index 파라메터 위치
     * @return 파라메터 값
     * @throws AxError 지정한 파라메터 없거나 형식이 맞지 않을 경우
     * @since 1.0
     */
    String getParamAsString(int index);

    /**
     * 지정한 위치의 파라메터 값을 "String" 객체로 얻음.
     * <p>
     * 지정한 파라메터가 없거나 형식이 맞지 않을 경우 기본 값을 반환.
     * 
     * @param index 파라메터 위치
     * @param defaultValue 기본 값
     * @return 파라메터 값.
     * @since 1.0
     */
    String getParamAsString(int index, String defaultValue);

    /**
     * 지정한 위치의 "JSON 객체" 파라메터에서 지정한 이름을 가진 속성을 "String" 객체로 얻음.
     * 
     * @param index JSON 객체 파라메터의 위치
     * @param name JSON 객체 파라메터의 속성 이름
     * @return 파라메터 값
     * @throws AxError 지정한 파라메터 없거나 형식이 맞지 않을 경우
     * @since 1.0
     */
    String getNamedParamAsString(int index, String name);

    /**
     * 지정한 위치의 "JSON 객체" 파라메터에서 지정한 이름을 가진 속성을 "String" 객체로 얻음.
     * <p>
     * 지정한 파라메터가 없거나 형식이 맞지 않을 경우 기본 값을 반환.
     * 
     * @param index JSON 객체 파라메터의 위치
     * @param name JSON 객체 파라메터의 속성 이름
     * @param defaultValue 기본 값
     * @return 파라메터 값
     * @since 1.0
     */
    String getNamedParamAsString(int index, String name, String defaultValue);

    /**
     * 지정한 위치의 파라메터 값을 "Number" 객체로 얻음.
     * <p>
     * 결과 Number 객체의 {@link Number#intValue()}, {@link Number#longValue()},
     * {@link Number#floatValue()}, {@link Number#doubleValue()} 등의 메소드를 통해 int, long, float, double
     * 등의 프리미티브 타입으로 변환할 수 있음.
     * 
     * @param index 파라메터 위치
     * @return 파라메터 값
     * @throws AxError 지정한 파라메터 없거나 형식이 맞지 않을 경우
     * @since 1.0
     */
    Number getParamAsNumber(int index);

    /**
     * 지정한 위치의 파라메터 값을 "Number" 객체로 얻음.
     * <p>
     * 지정한 파라메터가 없거나 형식이 맞지 않을 경우 기본 값을 반환.
     * 
     * @param index 파라메터 위치
     * @param defaultValue 기본 값
     * @return 파라메터 값.
     * @since 1.0
     */
    Number getParamAsNumber(int index, Number defaultValue);

    /**
     * 지정한 위치의 "JSON 객체" 파라메터에서 지정한 이름을 가진 속성을 "Number" 객체로 얻음.
     * 
     * @param index JSON 객체 파라메터의 위치
     * @param name JSON 객체 파라메터의 속성 이름
     * @return 파라메터 값
     * @throws AxError 지정한 파라메터 없거나 형식이 맞지 않을 경우
     * @since 1.0
     */
    Number getNamedParamAsNumber(int index, String name);

    /**
     * 지정한 위치의 "JSON 객체" 파라메터에서 지정한 이름을 가진 속성을 "Number" 객체로 얻음.
     * <p>
     * 지정한 파라메터가 없거나 형식이 맞지 않을 경우 기본 값을 반환.
     * 
     * @param index JSON 객체 파라메터의 위치
     * @param name JSON 객체 파라메터의 속성 이름
     * @param defaultValue 기본 값
     * @return 파라메터 값
     * @since 1.0
     */
    Number getNamedParamAsNumber(int index, String name, Number defaultValue);

    /**
     * 지정한 위치의 파라메터 값을 "Boolean" 객체로 얻음.
     * 
     * @param index 파라메터 위치
     * @return 파라메터 값
     * @throws AxError 지정한 파라메터 없거나 형식이 맞지 않을 경우
     * @since 1.0
     */
    Boolean getParamAsBoolean(int index);

    /**
     * 지정한 위치의 파라메터 값을 "Boolean" 객체로 얻음.
     * <p>
     * 지정한 파라메터가 없거나 형식이 맞지 않을 경우 기본 값을 반환.
     * 
     * @param index 파라메터 위치
     * @param defaultValue 기본 값
     * @return 파라메터 값.
     * @since 1.0
     */
    Boolean getParamAsBoolean(int index, Boolean defaultValue);

    /**
     * 지정한 위치의 "JSON 객체" 파라메터에서 지정한 이름을 가진 속성을 "Boolean" 객체로 얻음.
     * 
     * @param index JSON 객체 파라메터의 위치
     * @param name JSON 객체 파라메터의 속성 이름
     * @return 파라메터 값
     * @throws AxError 지정한 파라메터 없거나 형식이 맞지 않을 경우
     * @since 1.0
     */
    Boolean getNamedParamAsBoolean(int index, String name);

    /**
     * 지정한 위치의 "JSON 객체" 파라메터에서 지정한 이름을 가진 속성을 "Boolean" 객체로 얻음.
     * <p>
     * 지정한 파라메터가 없거나 형식이 맞지 않을 경우 기본 값을 반환.
     * 
     * @param index JSON 객체 파라메터의 위치
     * @param name JSON 객체 파라메터의 속성 이름
     * @param defaultValue 기본 값
     * @return 파라메터 값
     * @since 1.0
     */
    Boolean getNamedParamAsBoolean(int index, String name, Boolean defaultValue);

    /**
     * 지정한 위치의 파라메터 값을 "String 배열"로 얻음.
     * 
     * @param index 파라메터 위치
     * @return 파라메터 값
     * @throws AxError 지정한 파라메터 없거나 형식이 맞지 않을 경우
     * @since 1.0
     */
    String[] getParamAsStringArray(int index);

    /**
     * 지정한 위치의 파라메터 값을 "String 배열"로 얻음.
     * <p>
     * 지정한 파라메터가 없거나 형식이 맞지 않을 경우 기본 값을 반환.
     * 
     * @param index 파라메터 위치
     * @param defaultValue 기본 값
     * @return 파라메터 값.
     * @since 1.0
     */
    String[] getParamAsStringArray(int index, String[] defaultValue);

    /**
     * 지정한 위치의 "JSON 배열" 파라메터에서 지정한 이름을 가진 속성을 "String 배열"로 얻음.
     * 
     * @param index JSON 배열 파라메터의 위치
     * @param name JSON 배열 파라메터의 속성 이름
     * @return 파라메터 값
     * @throws AxError 지정한 파라메터 없거나 형식이 맞지 않을 경우
     * @since 1.0
     */
    String[] getNamedParamAsStringArray(int index, String name);

    /**
     * 지정한 위치의 "JSON 배열" 파라메터에서 지정한 이름을 가진 속성을 "String 배열"로 얻음.
     * <p>
     * 지정한 파라메터가 없거나 형식이 맞지 않을 경우 기본 값을 반환.
     * 
     * @param index JSON 배열 파라메터의 위치
     * @param name JSON 배열 파라메터의 속성 이름
     * @param defaultValue 기본 값
     * @return 파라메터 값
     * @since 1.0
     */
    String[] getNamedParamAsStringArray(int index, String name, String[] defaultValue);

    /**
     * 지정한 위치의 파라메터 값을 "Number 배열"로 얻음.
     * 
     * @param index 파라메터 위치
     * @return 파라메터 값
     * @throws AxError 지정한 파라메터 없거나 형식이 맞지 않을 경우
     * @since 1.0
     */
    Number[] getParamAsNumberArray(int index);

    /**
     * 지정한 위치의 파라메터 값을 "Number 배열"로 얻음.
     * <p>
     * 지정한 파라메터가 없거나 형식이 맞지 않을 경우 기본 값을 반환.
     * 
     * @param index 파라메터 위치
     * @param defaultValue 기본 값
     * @return 파라메터 값.
     * @since 1.0
     */
    Number[] getParamAsNumberArray(int index, Number[] defaultValue);

    /**
     * 지정한 위치의 "JSON 배열" 파라메터에서 지정한 이름을 가진 속성을 "Number 배열"로 얻음.
     * 
     * @param index JSON 배열 파라메터의 위치
     * @param name JSON 배열 파라메터의 속성 이름
     * @return 파라메터 값
     * @throws AxError 지정한 파라메터 없거나 형식이 맞지 않을 경우
     * @since 1.0
     */
    Number[] getNamedParamAsNumberArray(int index, String name);

    /**
     * 지정한 위치의 "JSON 배열" 파라메터에서 지정한 이름을 가진 속성을 "Number 배열"로 얻음.
     * <p>
     * 지정한 파라메터가 없거나 형식이 맞지 않을 경우 기본 값을 반환.
     * 
     * @param index JSON 배열 파라메터의 위치
     * @param name JSON 배열 파라메터의 속성 이름
     * @param defaultValue 기본 값
     * @return 파라메터 값
     * @since 1.0
     */
    Number[] getNamedParamAsNumberArray(int index, String name, Number[] defaultValue);

    /**
     * 지정한 위치의 파라메터 값을 "Boolean 배열"로 얻음.
     * 
     * @param index 파라메터 위치
     * @return 파라메터 값
     * @throws AxError 지정한 파라메터 없거나 형식이 맞지 않을 경우
     * @since 1.0
     */
    Boolean[] getParamAsBooleanArray(int index);

    /**
     * 지정한 위치의 파라메터 값을 "Boolean 배열"로 얻음.
     * <p>
     * 지정한 파라메터가 없거나 형식이 맞지 않을 경우 기본 값을 반환.
     * 
     * @param index 파라메터 위치
     * @param defaultValue 기본 값
     * @return 파라메터 값.
     * @since 1.0
     */
    Boolean[] getParamAsBooleanArray(int index, Boolean[] defaultValue);

    /**
     * 지정한 위치의 "JSON 배열" 파라메터에서 지정한 이름을 가진 속성을 "Boolean 배열"로 얻음.
     * 
     * @param index JSON 배열 파라메터의 위치
     * @param name JSON 배열 파라메터의 속성 이름
     * @return 파라메터 값
     * @throws AxError 지정한 파라메터 없거나 형식이 맞지 않을 경우
     * @since 1.0
     */
    Boolean[] getNamedParamAsBooleanArray(int index, String name);

    /**
     * 지정한 위치의 "JSON 배열" 파라메터에서 지정한 이름을 가진 속성을 "Boolean 배열"로 얻음.
     * <p>
     * 지정한 파라메터가 없거나 형식이 맞지 않을 경우 기본 값을 반환.
     * 
     * @param index JSON 배열 파라메터의 위치
     * @param name JSON 배열 파라메터의 속성 이름
     * @param defaultValue 기본 값
     * @return 파라메터 값
     * @since 1.0
     */
    Boolean[] getNamedParamAsBooleanArray(int index, String name, Boolean[] defaultValue);

    /**
     * 지정한 위치의 "JSON 객체" 파라메터를 "Map" 객체로 얻음.
     * 
     * @param index JSON 객체 파라메터의 위치
     * @return 파라메터 값
     * @throws AxError 지정한 파라메터 없거나 형식이 맞지 않을 경우
     * @since 1.0
     */
    <K, V> Map<K, V> getParamAsMap(int index);

    /**
     * 메소드 실행 성공(반환할 결과 없음).
     * <p>
     * 전달할 결과가 없더라도 성공 또는 에러 여부는 전달해야 함.
     * 
     * @since 1.0
     */
    void sendResult();

    /**
     * 메소드 실행 성공.
     * 
     * @param object 반환할 결과.
     * @since 1.0
     */
    void sendResult(Object object);

    /**
     * 메소드 호출 실패.
     * 
     * @param code 에러 코드
     * @since 1.0
     */
    void sendError(int code);

    /**
     * 메소드 호출 실패.
     * 
     * @param code 에러 코드
     * @param message 에러 메시지
     * @since 1.0
     */
    void sendError(int code, String message);

    /**
     * 메소드 호출 실패.
     * 
     * @param error 에러 코드/메시지 정보를 가진 AxError 인스턴스
     * @since 1.0
     */
    void sendError(AxError error);

    /**
     * response watch result
     * 
     * @param object 반환할 결과.
     * @since 1.2
     */
    void sendWatchResult(Object object);

    /**
     * response watch error
     * 
     * @param code 에러 코드
     * @param message 에러 메시지
     * @since 1.2
     */
    void sendWatchError(int code, String message);

    /**
     * 속성 설정.
     * <p>
     * 같은 이름을 가진 속성이 이미 있을 경우에는 기존 속성 값을 변경하고, 그렇지 않을 경우에는 새 속성이 추가됨.
     * <p>
     * 이 속성은 메소드가 실행되는 동안 유효하므로, 긴 메소드 처리동안 상태 유지 등의 다양한 용도로 활용할 수 있음.
     * 
     * @param key 속성 이름
     * @param value 속성 값
     * @since 1.0
     */
    void setAttribute(String key, Object value);

    /**
     * 속성 값을 얻음.
     * <p>
     * 지정한 이름을 가진 속성이 없을 경우 {@literal null}을 반환.
     * 
     * @param key 속성 이름
     * @return 속성 값
     * @since 1.0
     */
    Object getAttribute(String key);

    /**
     * 속성 삭제.
     * <p>
     * 지정한 이름을 가진 속성이 없을 경우 아무런 효과 없음(무시).
     * 
     * @param key 속성 이름
     * @since 1.0
     */
    void removeAttribute(String key);

}
