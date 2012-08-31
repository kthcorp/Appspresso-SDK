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
package com.appspresso.w3;

import java.util.Map;

/**
 * W3C의 Widget API를 정의한 인터페이스.
 * <p>
 * 위젯 설정(config.xml 파일)에서 지정한 feature 들을 접근.
 * 
 * @version 1.0
 * @see http://www.w3.org/TR/widgets-apis/
 * @deprecated 대체 스펙없이 W3C Widget API에서 제외됨.
 */
public interface Feature {

    /**
     * 피쳐의 고유한 URI(식별자; {@literal feature}태그의 {@literal uri}속성)를 얻음.
     * 
     * @return URI 문자열
     * @since 1.0
     */
    String getUri();

    /**
     * 필수 피쳐인지 여부 확인({@literal feature}태그의 {@literal required}속성).
     * 
     * @return 필수이면 {@literal true}, 그렇지 않으면 {@literal false}
     * @since 1.0
     */
    boolean isRequired();

    /**
     * 피쳐 추가 파라메터들(({@literal feature}태그 아래의 {@literal param}태그들)을 얻음.
     * 
     * @return 피쳐의 추가 파라메터들의 맵
     * @since 1.0
     */
    Map<String, String> getParams();

}
