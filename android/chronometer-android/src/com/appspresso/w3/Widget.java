/*
 * Appspresso
 *
 * Copyright (c) 2011 KT Hitel Corp.
 *
 * Appspresso SDK may be freely distributed under the MIT license.
 */
package com.appspresso.w3;

/**
 * W3C의 Widget API를 정의한 인터페이스.
 * <p>
 * 위젯 설정(config.xml 파일)의 내용을 런타임/플러그인에서 접근.
 * <p>
 * TODO: 다국어 설정 지원.
 * 
 * @version 1.0
 * @see http://www.w3.org/TR/widgets-apis/
 */
public interface Widget {

    /**
     * 위젯 설정에서 지정한 식별자({@literal widget}태그의 {@literal id}속성)를 얻음.
     * 
     * @return 위젯 식별자 문자열
     * @since 1.0
     */
    String getId();

    /**
     * 위젯 설정에서 지정한 설명({@literal description}태그)을 얻음.
     * <p>
     * 지정한 설명이 없으면 빈문자열("")을 반환.
     * 
     * @return 위젯 설명 문자열
     * @since 1.0
     */
    String getDescription();

    /**
     * 위젯 설정에서 지정한 이름({@literal name}태그)을 얻음.
     * <p>
     * 지정한 이름이 없으면 빈문자열("")을 반환.
     * 
     * @return 위젯 이름 문자열
     * @since 1.0
     */
    String getName();

    /**
     * 위젯 설정에서 지정한 짧은 이름({@literal name}태그의 {@literal short}속성)을 얻음
     * <p>
     * 지정한 짧은 이름이 없으면 빈문자열("")을 반환.
     * 
     * @return 위젯 짧은 이름 문자열
     * @since 1.0
     */
    String getShortName();

    /**
     * 위젯 설정에서 지정한 버전({@literal widget}태그의 {@literal version}속성)를 얻음.
     * 
     * @return 위젯 버전 문자열
     * @since 1.0
     */
    String getVersion();

    /**
     * 위젯 설정에서 지정한 제작자(@{literal author}태그)를 얻음.
     * <p>
     * 지정한 제작자가 없으면 빈문자열("")을 반환.
     * 
     * @return 위젯 제작자 문자열
     * @since 1.0
     */
    String getAuthor();

    /**
     * 위젯 설정에서 지정한 제작자 이메일({@literal author}태그의 {@literal email}속성)을 얻음.
     * <p>
     * 지정한 제작자 이메일이 없으면 빈문자열("")을 반환.
     * 
     * @return 위젯 제작자 이메일 문자열
     * @since 1.0
     */
    String getAuthorEmail();

    /**
     * 위젯 설정에서 지정한 제작자 홈페이지({@literal author}태그의 {@literal href}속성)을 얻음.
     * <p>
     * 지정한 제작자 홈페이지가 없으면 빈문자열("")을 반환.
     * 
     * @return 위젯 제작자 홈페이지 문자열
     */
    String getAuthorHref();

    /**
     * 위젯 설정에서 지정한 {@literal preference}들을 <a href="http://www.w3.org/TR/webstorage/">W3C WebStorage
     * API의 Storage</a> 인스턴스로 얻음.
     * 
     * @return 위젯 {@literal preference}들을 가진 Storage 인스턴스
     */
    Storage getPreferences();

    /**
     * 위젯 설정에서 지정한 너비({@literal widget}태그의 {@literal width}속성)를 얻음.
     * 
     * @return 위젯 너비 정수
     * @since 1.0
     */
    long getWidth();

    /**
     * 위젯 설정에서 지정한 높이({@literal widget}태그의 {@literal height}속성)를 얻음.
     * 
     * @return 위젯 높이 정수
     * @since 1.0
     */
    long getHeight();

}
