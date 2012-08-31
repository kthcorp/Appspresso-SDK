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
 * {@link WebViewListener}의 기본 구현체.
 * <p>
 * {@link WebViewListener}의 일부 메소드만 필요할 때, 이 클래스를 상속하고 해당 메소드만 오버라이드.
 * 
 * @version 1.0
 */
public class WebViewAdapter implements WebViewListener {

    /**
     * {@inheritDoc}
     * 
     * @return false(이벤트를 처리하지 않음)
     */
    @Override
    public boolean onKeyDown(WebView webView, int keyCode, KeyEvent event) {
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @return false(이벤트를 처리하지 않음)
     */
    @Override
    public boolean onKeyUp(WebView webView, int keyCode, KeyEvent event) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onScrollChanged(WebView webView, int l, int t, int oldl, int oldt) {}

    /**
     * {@inheritDoc}
     * 
     * @return false(이벤트를 처리하지 않음)
     */
    @Override
    public boolean onTouchEvent(WebView webView, MotionEvent ev) {
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @return false(이벤트를 처리하지 않음)
     */
    @Override
    public boolean onTrackballEvent(WebView webView, MotionEvent ev) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void invalidate(WebView webView) {}

}
