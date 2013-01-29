/*
 * Appspresso
 *
 * Copyright (c) 2011 KT Hitel Corp.
 *
 * Appspresso SDK may be freely distributed under the MIT license.
 */
package com.appspresso.w3;

/**
 * W3C의 WebStorage API를 정의한 인터페이스.
 * <p>
 * 위젯 설정(config.xml 파일)에서 지정한 preference 들을 접근.
 * 
 * @version 1.0
 * @see com.appspresso.w3.Widget#getPreferences()
 * @see http://www.w3.org/TR/webstorage/
 */
public interface Storage {

    /**
     * 저장된 항목 개수를 얻음.
     * 
     * @return 항목 개수
     * @since 1.0
     */
    long length();

    /**
     * 지정한 순번에 해당하는 항목의 키를 얻음.
     * <p>
     * 해당하는 항목이 존재 하지 않으면 {@literal null}을 반환.
     * 
     * @param index 항목의 순번
     * @return 항목의 키 or {@literal null}
     * @since 1.0
     */
    String key(long index);

    /**
     * 지정한 키에 해당하는 항목의 값을 얻음.
     * <p>
     * 해당하는 항목이 존재 하지 않으면 {@literal null}을 반환.
     * 
     * @param key 항목의 키
     * @return 항목의 값 or {@literal null}
     * @since 1.0
     */
    String getItem(String key);

    /**
     * 지정한 키에 해당하는 항목의 값을 변경.
     * <p>
     * 해당하는 항목이 존재하지 않으면 새로운 항목을 추가하고, 이미 존재하면 기존 항목을 덮어씀.
     * 
     * @param key 항목의 키
     * @param value 항목의 값
     * @since 1.0
     */
    void setItem(String key, String value);

    /**
     * 지정한 키에 해당하는 항목을 삭제.
     * <p>
     * 해당하는 항목이 존재하지 않으면 아무런 효과 없음(무시).
     * 
     * @param key 항목의 키
     * @since 1.0
     */
    void removeItem(String key);

    /**
     * 저장소의 모든 항목을 삭제.
     * 
     * @since 1.0
     */
    void clear();

}
