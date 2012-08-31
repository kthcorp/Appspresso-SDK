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
package com.appspresso.api.view;

import android.view.KeyEvent;
import android.view.MotionEvent;
import android.webkit.WebView;

/**
 * 앱스프레소 웹앱을 실행하는 웹뷰({@link android.webkit.WebView})의 기본 동작을 오버라이드하기 위한 리스너.
 * <p>
 * 이 인터페이스의 일부 메소드만 필요하다면, 이 인터페이스 전체를 구현하는 대신 {@link WebViewAdapter} 클래스를 상속하고 필요한 메소드만 오버라이드.
 * 
 * @version 1.0
 * @see WebViewAdapter
 * @see com.appspresso.api.AxRuntimeContext#addWebViewListener(WebViewListener)
 * @see com.appspresso.api.AxRuntimeContext#removeWebViewListener(WebViewListener)
 * @see android.webkit.WebView
 * @see http://developer.android.com/reference/android/webkit/WebView.html
 */
public interface WebViewListener {

    /**
     * Default implementation of KeyEvent.Callback.onKeyDown(): perform press of the view when
     * KEYCODE_DPAD_CENTER or KEYCODE_ENTER is released, if the view is enabled and clickable.
     * 
     * @param webView 웹뷰
     * @param keyCode A key code that represents the button pressed, from KeyEvent.
     * @param event The KeyEvent object that defines the button action.
     * @return If you handled the event, return true. If you want to allow the event to be handled
     *         by the next receiver, return false.
     * @since 1.0
     */
    boolean onKeyDown(WebView webView, int keyCode, KeyEvent event);

    /**
     * Default implementation of KeyEvent.Callback.onKeyUp(): perform clicking of the view when
     * KEYCODE_DPAD_CENTER or KEYCODE_ENTER is released.
     * 
     * @param webView 웹뷰
     * @param keyCode A key code that represents the button pressed, from KeyEvent.
     * @param event The KeyEvent object that defines the button action.
     * @return 이벤트를 처리했으면 true, 아니면 false
     * @since 1.0
     */
    boolean onKeyUp(WebView webView, int keyCode, KeyEvent event);

    /**
     * This is called in response to an internal scroll in this view (i.e., the view scrolled its
     * own contents). This is typically as a result of scrollBy(int, int) or scrollTo(int, int)
     * having been called.
     * 
     * @param webView 웹뷰
     * @param l Current horizontal scroll origin.
     * @param t Current vertical scroll origin.
     * @param oldl Previous horizontal scroll origin.
     * @param oldt Previous vertical scroll origin.
     * @see android.webkit.WebView#onScrollChanged(int,int,int,int)
     * @since 1.0
     */
    void onScrollChanged(WebView webView, int l, int t, int oldl, int oldt);

    /**
     * Implement this method to handle touch screen motion events.
     * 
     * @param webView 웹뷰
     * @param ev The motion event.
     * @return True if the event was handled, false otherwise.
     * @see android.webkit.WebView#onTouchEvent(MotionEvent)
     * @since 1.0
     */
    boolean onTouchEvent(WebView webView, MotionEvent ev);

    /**
     * Implement this method to handle trackball motion events. The relative movement of the
     * trackball since the last event can be retrieve with MotionEvent.getX() and
     * MotionEvent.getY(). These are normalized so that a movement of 1 corresponds to the user
     * pressing one DPAD key (so they will often be fractional values, representing the more
     * fine-grained movement information available from a trackball).
     * 
     * @param webView 웹뷰
     * @param ev The motion event.
     * @return True if the event was handled, false otherwise.
     * @see android.webkit.WebView#onTrackballEvent(MotionEvent)
     * @since 1.0
     */
    boolean onTrackballEvent(WebView webView, MotionEvent ev);

    /**
     * Invalidate the whole view. If the view is visible, onDraw(android.graphics.Canvas) will be
     * called at some point in the future. This must be called from a UI thread. To call from a
     * non-UI thread, call postInvalidate().
     * 
     * @param webView 웹뷰
     * @see android.view.View#invalidate()
     * @since 1.0
     */
    void invalidate(WebView webView);

}
