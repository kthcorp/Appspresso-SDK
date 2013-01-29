/*
 * Appspresso
 *
 * Copyright (c) 2011 KT Hitel Corp.
 *
 * Appspresso SDK may be freely distributed under the MIT license.
 */
package com.appspresso.api;

import org.apache.commons.logging.Log;

import android.text.TextUtils;

import com.appspresso.internal.AxConfig;

/**
 * 앱스프레소 플러그인을 위한 로깅 지원 클래스.
 * <p>
 * 이 클래스는 안드로이드 표준 로깅({@link android.util.Log})을 이용하여 {@link org.apache.commons.logging.Log}를 구현하고
 * 있으며, {@link org.apache.commons.logging.LogFactory}를 대체하는 간단한 팩토리 메소드를 제공.
 * <p>
 * 앱스프레소 스튜디오를 통해서 소스 코드 수정없이 로그 레벨 설정 가능.
 * <p>
 * 사용 예:
 * 
 * <pre>
 * public class MyClass {
 * 		private static final AxLog LOG = AxLog.getLog(MyClass.class);
 * 
 * 		...
 * 
 * 		private void someMethod() {
 * 			...
 * 
 * 			if(LOG.isDebugEnabled()) { LOG.debug("blah blah blah..."); }
 * 
 * 			...
 * 		}
 * 
 * 		...
 * }
 * </pre>
 * 
 * @see org.apache.commons.logging.Log
 * @see android.util.Log
 * @version 1.0
 */
public class AxLog implements Log {

    private static final String DEF_TAG = "Appspresso";

    private static final int DEF_LEVEL = AxConfig.getAttributeAsBoolean("app.devel", true)
            ? android.util.Log.VERBOSE
            : android.util.Log.INFO;

    private final String tag;
    private final int level;

    /**
     * 지정한 태그와 레벨을 사용하는 로그 객체 생성.
     * 
     * @param tag 태그
     * @param level 레벨. {@link android.util.Log}에 지정된 상수
     */
    private AxLog(String tag, int level) {
        this.tag = tag;
        this.level = level;
    }

    /**
     * 상세 로그 허용 여부.
     * 
     * @return 허용하면 true, 그렇지 않으면 false.
     * @see android.util.Log#VERBOSE
     */
    @Override
    public boolean isTraceEnabled() {
        return level <= android.util.Log.VERBOSE;
    }

    /**
     * 디버그 로그 허용 여부.
     * 
     * @return 허용하면 true, 그렇지 않으면 false.
     * @see android.util.Log#DEBUG
     */
    @Override
    public boolean isDebugEnabled() {
        return level <= android.util.Log.DEBUG;
    }

    /**
     * 정보 로그 허용 여부.
     * 
     * @return 허용하면 true, 그렇지 않으면 false.
     * @see android.util.Log#INFO
     */
    @Override
    public boolean isInfoEnabled() {
        return level <= android.util.Log.INFO;
    }

    /**
     * 경고 로그 허용 여부.
     * 
     * @return 허용하면 true, 그렇지 않으면 false.
     * @see android.util.Log#WARN
     */
    @Override
    public boolean isWarnEnabled() {
        return level <= android.util.Log.WARN;
    }

    /**
     * 에러 로그 허용 여부.
     * 
     * @return 허용하면 true, 그렇지 않으면 false.
     * @see android.util.Log#ERROR
     */
    @Override
    public boolean isErrorEnabled() {
        return level <= android.util.Log.ERROR;
    }

    /**
     * 치명적인 에러 로그 허용 여부.
     * 
     * @return 허용하면 true, 그렇지 않으면 false.
     * @see android.util.Log#ASSERT
     */
    @Override
    public boolean isFatalEnabled() {
        return level <= android.util.Log.ASSERT;
    }

    /**
     * 상세 로그 출력.
     * 
     * @param o 메시지
     * @see android.util.Log#v(String, String)
     */
    @Override
    public void trace(Object o) {
        if (level <= android.util.Log.VERBOSE) {
            android.util.Log.v(tag, o.toString());
        }
    }

    /**
     * 상세 로그를 예외와 함께 출력.
     * 
     * @param o 메시지
     * @param t 참조 예외
     * @see android.util.Log#v(String, String, Throwable)
     */
    @Override
    public void trace(Object o, Throwable t) {
        if (level <= android.util.Log.VERBOSE) {
            android.util.Log.v(tag, o.toString(), t);
        }
    }

    /**
     * 디버그 로그 출력.
     * 
     * @param o 메시지
     * @see android.util.Log#d(String, String)
     */
    @Override
    public void debug(Object o) {
        if (level <= android.util.Log.DEBUG) {
            android.util.Log.d(tag, o.toString());
        }
    }

    /**
     * 디버그 로그를 예외와 함께 출력.
     * 
     * @param o 메시지
     * @param t 참조 예외
     * @see android.util.Log#d(String, String, Throwable)
     */
    @Override
    public void debug(Object o, Throwable t) {
        if (level <= android.util.Log.DEBUG) {
            android.util.Log.d(tag, o.toString(), t);
        }
    }

    /**
     * 정보 로그 출력.
     * 
     * @param o 메시지
     * @see android.util.Log#i(String, String)
     */
    @Override
    public void info(Object o) {
        if (level <= android.util.Log.INFO) {
            android.util.Log.i(tag, o.toString());
        }
    }

    /**
     * 정보 로그를 예외와 함께 출력.
     * 
     * @param o 메시지
     * @param t 참조 예외
     * @see android.util.Log#i(String, String, Throwable)
     */
    @Override
    public void info(Object o, Throwable t) {
        if (level <= android.util.Log.INFO) {
            android.util.Log.i(tag, o.toString(), t);
        }
    }

    /**
     * 경고 로그 출력.
     * 
     * @param o 메시지
     * @see android.util.Log#w(String, String)
     */
    @Override
    public void warn(Object o) {
        if (level <= android.util.Log.WARN) {
            android.util.Log.w(tag, o.toString());
        }
    }

    /**
     * 경고 로그를 예외와 함께 출력.
     * 
     * @param o 메시지
     * @param t 참조 예외
     * @see android.util.Log#w(String, String, Throwable)
     */
    @Override
    public void warn(Object o, Throwable t) {
        if (level <= android.util.Log.WARN) {
            android.util.Log.w(tag, o.toString(), t);
        }
    }

    /**
     * 에러(error) 로그 출력.
     * 
     * @param o 메시지
     * @see android.util.Log#e(String, String)
     */
    @Override
    public void error(Object o) {
        if (level <= android.util.Log.ERROR) {
            android.util.Log.e(tag, o.toString());
        }
    }

    /**
     * 에러 로그를 예외와 함께 출력.
     * 
     * @param o 메시지
     * @param t 참조 예외
     * @see android.util.Log#e(String, String, Throwable)
     */
    @Override
    public void error(Object o, Throwable t) {
        if (level <= android.util.Log.ERROR) {
            android.util.Log.e(tag, o.toString(), t);
        }
    }

    /**
     * 치명적인 에러 로그 출력.
     * 
     * @param o 메시지
     * @see android.util.Log#wtf(String, String)
     */
    @Override
    public void fatal(Object o) {
        if (level <= android.util.Log.ASSERT) {
            android.util.Log.println(android.util.Log.ASSERT, tag, o.toString());
        }
    }

    /**
     * 치명적인 에러 로그를 예외와 함께 출력.
     * 
     * @param o 메시지
     * @param t 참조 예외
     * @see android.util.Log#wtf(String, String, Throwable)
     */
    @Override
    public void fatal(Object o, Throwable t) {
        if (level <= android.util.Log.ASSERT) {
            // api8+: android.util.Log.wtf(tag, o.toString(), t);
            android.util.Log.println(android.util.Log.ASSERT, tag, o.toString() + '\n'
                    + android.util.Log.getStackTraceString(t));
        }
    }

    //
    // log factory methods
    //

    private static final int MAX_TAG_LEN = 20; // 23;

    private static String getTagName(Class<?> klass) {
        if (klass == null) { return DEF_TAG; }
        StringBuilder result = new StringBuilder(MAX_TAG_LEN);
        String className = klass.getSimpleName();
        result.append(className);
        String packageName = klass.getPackage().getName();
        for (String token : packageName.split(".")) {
            if (className.length() > MAX_TAG_LEN) {
                result.insert(0, "..");
                break;
            }
            result.insert(0, token.charAt(0)).insert(0, '.');
        }
        return result.toString();
    }

    // @Override
    /**
     * 클래스 이름(FQCN)을 태그로 사용하는 로그 객체 생성.
     * 
     * @return AxLog 인스턴스
     */
    public static Log getLog(Class<?> klass) {
        return new AxLog(getTagName(klass), DEF_LEVEL);
    }

    // @Override
    /**
     * 지정한 태그를 사용하는 로그 객체 생성.
     * 
     * @return AxLog 인스턴스
     */
    public static Log getLog(String name) {
        return new AxLog(TextUtils.isEmpty(name) ? DEF_TAG : name, DEF_LEVEL);
    }

}
