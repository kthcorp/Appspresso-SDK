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

import android.app.Activity;
import android.webkit.WebView;

import com.appspresso.api.activity.ActivityListener;
import com.appspresso.api.fs.AxFileSystemManager;
import com.appspresso.api.view.WebChromeClientListener;
import com.appspresso.api.view.WebViewClientListener;
import com.appspresso.api.view.WebViewListener;
import com.appspresso.w3.Feature;
import com.appspresso.w3.Widget;

/**
 * 앱스프레소 앱을 실행하는 동안 필요한 정보를 보유하는 인터페이스.
 * <p>
 * {@link AxPluginContext}, {@link AxPlugin}과 더불어 앱스프레소 플러그인 개발에서 가장 핵심적인 인터페이스.
 * <p>
 * 앱스프레소 앱은 오직 하나의 AxRuntimeContext 인스턴스를 갖고 있으며, AxPlugin의 activate / deactivate 메소드를 통해 개별 플러그인에
 * 주입(inject)된다.
 * <p>
 * <ul>
 * <li>주요 네이티브 객체(WebView, Activity 등) 접근.</li>
 * <li>웹뷰와 액티비티의 기본 동작을 가로채서 미세 조정할 수 있는 리스너(WebViewListener, WebChromeClientListener,
 * WebViewClientListener, ActivityListener 등).</li>
 * <li>웹뷰와 통신(자바스크립트 호출 등).</li>
 * <li>앱을 실행하는 동안 유효한 범용 속성(attribute) 집합.</li>
 * <li>플러그인 의존성 해결.</li>
 * </ul>
 * <p>
 * 
 * @see AxPlugin#activate(AxRuntimeContext)
 * @see AxPlugin#deactivate(AxRuntimeContext)
 * @version 1.0
 */
public interface AxRuntimeContext {

    /**
     * 웹앱을 실행하고 있는 웹뷰({@link android.webkit.WebView}) 인스턴스를 얻음.
     * 
     * @return WebView 인스턴스
     * @since 1.0
     */
    WebView getWebView();

    /**
     * 웹앱을 감싸하고 있는 액티비티({@link android.app.Activity}) 인스턴스를 얻음.
     * 
     * @return Activity 인스턴스
     * @since 1.0
     */
    Activity getActivity();

    /**
     * W3C 위젯 인스턴스를 얻음.
     * 
     * @return Widget 인스턴스
     * @since 1.0
     */
    Widget getWidget();

    /**
     * 파일 시스템 관리자 인스턴스를 얻음.
     * <p>
     * <code>deviceapis.filesystem</code>의 가상 루트를 제공하기 위해서 사용.
     * 
     * @return AxFileSystemManager 인스턴스
     * @since 1.0
     */
    AxFileSystemManager getFileSystemManager();

    /**
     * 지정한 이름(일반적으로 URI 형식)을 가진 피쳐의 활성 상태 확인.
     * 
     * @param feature 피쳐 이름
     * @return 활성 상태면 true, 아니면 false
     */
    boolean isActivatedFeature(String feature);

    /**
     * 활성화된 피쳐의 목록을 얻음.
     * 
     * @return 피쳐 목록
     * @since 1.0
     */
    Feature[] getActivatedFeatures();

    /**
     * 지정한 ID(일반적으로, reverse domain 형식)를 가진 플러그인을 적재.
     * <p>
     * 지정한 ID를 가진 플러그인을 적재하지 못했으면 {@literal null}을 반환.
     * 
     * @param pluginId 플러그인 ID
     * @return AxPlugin 인스턴스 또는 {@literal null}.
     * @since 1.0
     */
    AxPlugin requirePlugin(String pluginId);

    /**
     * 지정한 이름의 피쳐(일반적으로, URL 형식)를 가진 플러그인을 적재.
     * <p>
     * 지정한 이름의 피쳐를 가진 플러그인을 적재하지 못했으면 {@literal null}을 반환.
     * 
     * @param featureUri 피쳐 이름
     * @return AxPlugin 인스턴스 또는 {@literal null}.
     * @since 1.0
     */
    AxPlugin requirePluginWithFeature(String featureUri);

    /**
     * 자바스크립트 코드를 실행.
     * <p>
     * 에러 검사 없음. 리턴값 받을 수 없음.
     * 
     * @param script 자바스크립트 문장
     * @since 1.0
     */
    void executeJavaScript(String script);

    /**
     * 자바스크립트 함수를 호출.
     * <p>
     * String, Boolean, Number와 Number의 서브클래스, List, Map 객체 타입과 이 타입의 배열을 제외한 인자는 지원하지 않음.
     * 
     * @param functionName 자바스크립트 함수 이름
     * @param args 인자들
     * @since 1.0
     */
    void invokeJavaScriptFunction(String functionName, Object... args);

    /**
     * 자바스크립트 코드를 실행.
     * <p>
     * 에러 발생시 onError 호출. 리턴값 받을 수 없음.
     * 
     * @param onError error callback. 메인스레드에서 자바스크립트 실행 도중 에러 발생시 호출.
     * @param script 자바스크립트 문장
     * @since 1.2
     */
    void executeJavaScript(AxErrorHandler onError, String script);

    /**
     * 자바스크립트 함수를 호출.
     * <p>
     * String, Boolean, Number와 Number의 서브클래스, List, Map 객체 타입과 이 타입의 배열을 제외한 인자는 지원하지 않음.
     * 
     * @param onError error callback. 메인스레드에서 자바스크립트 실행 도중 에러 발생시 호출.
     * @param functionName 자바스크립트 함수 이름
     * @param args 인자들
     * @since 1.2
     */
    void invokeJavaScriptFunction(AxErrorHandler onError, String functionName, Object... args);

    /**
     * 자바스크립트 주기적인 성공 콜백을 호출.
     * <p>
     * 자바스크립트에서 <code>ax.Plugin#watch(method,callback,errback,params)</code> 메소드를 호출했을 때, 자바스크립트로
     * "결과"를 전달하기 위해서 사용.
     * 
     * @param id 자바스크립트에서 부여한 watch 식별자
     * @param result 자바스크립트로 전달할 결과
     * @since 1.0
     * @deprecated since 1.2. You should now use {@link AxPluginContext#sendWatchResult(Object)}
     */
    void invokeWatchSuccessListener(long id, Object result);

    /**
     * 자바스크립트 주기적인 에러 콜백을 호출.
     * <p>
     * 자바스크립트에서 <code>ax.Plugin#watch(method,callback,errback,params)</code> 메소드를 호출했을 때, 자바스크립트로
     * "에러"를 전달하기 위해서 사용.
     * 
     * @param id watch ID.
     * @param code 자바스크립트로 전달할 에러 코드
     * @param message 자바스크립트로 전달할 에러 메시지
     * @since 1.0
     * @deprecated since 1.2. You should now use {@link AxPluginContext#sendWatchError(int, String)}
     */
    void invokeWatchErrorListener(long id, int code, String message);

    /**
     * 속성 설정.
     * <p>
     * 같은 이름을 가진 속성이 이미 있을 경우에는 기존 속성 값을 변경하고, 그렇지 않을 경우에는 새 속성이 추가됨.
     * <p>
     * 이 속성은 앱이 실행되는 동안 유효하므로, 플러그인 간의 통신, 상태 유지 등의 다양한 용도로 활용할 수 있음.
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

    /**
     * WebViewListener 추가.
     * <p>
     * 네이티브 안드로이드 앱 개발에서, {@link android.webkit.WebView}를 상속해서 일부 메소드를 오버라이드하는 를 사용하는 것과 유사하지만, 여러
     * 개의 리스너를 가질 수 있음.
     * 
     * @param l WebViewListener 인스턴스
     * @see android.webkit.WebView
     * @since 1.0
     */
    void addWebViewListener(WebViewListener l);

    /**
     * WebViewListener 제거.
     * 
     * @param l WebViewListener 인스턴스
     * @since 1.0
     */
    void removeWebViewListener(WebViewListener l);

    /**
     * WebChromeClientListener 추가.
     * <p>
     * 네이티브 안드로이드 앱 개발에서,
     * {@link android.webkit.WebView#setWebChromeClient(android.webkit.WebChromeClient)} 를 사용하는 것과
     * 유사하지만, 여러 개의 리스너를 가질 수 있음.
     * 
     * @param l WebChromeClientListener 인스턴스
     * @see android.webkit.WebView#setWebChromeClient(android.webkit.WebChromeClient)
     * @see android.webkit.WebChromeClient
     * @since 1.0
     */
    void addWebChromeClientListener(WebChromeClientListener l);

    /**
     * WebChromeClientListener 제거.
     * 
     * @param l WebChromeClientListener 인스턴스
     * @since 1.0
     */
    void removeWebChromeClientListener(WebChromeClientListener l);

    /**
     * WebViewClientListener 추가.
     * <p>
     * 네이티브 안드로이드 앱 개발에서,
     * {@link android.webkit.WebView#setWebViewClient(android.webkit.WebViewClient)} 를 사용하는 것과
     * 유사하지만, 여러 개의 리스너를 가질 수 있음.
     * 
     * @param l WebViewClientListener 인스턴스
     * @see android.webkit.WebView#setWebViewClient(android.webkit.WebViewClient)
     * @see android.webkit.WebViewClient
     * @since 1.0
     */
    void addWebViewClientListener(WebViewClientListener l);

    /**
     * WebViewClientListener 제거.
     * 
     * @param l WebViewClientListener 인스턴스
     * @since 1.0
     */
    void removeWebViewClientListener(WebViewClientListener l);

    /**
     * ActivityListener 추가.
     * <p>
     * 네이티브 안드로이드 앱 개발에서, {@link android.app.Activity}를 상속하는 것과 유사하지만, 여러 개의 리스너를 가질 수 있음.
     * 
     * @param l ActivityListener 인스턴스
     * @see android.app.Activity
     * @since 1.0
     */
    void addActivityListener(ActivityListener l);

    /**
     * ActivityListener 제거.
     * 
     * @param l ActivityListener 인스턴스
     * @since 1.0
     */
    void removeActivityListener(ActivityListener l);

}
