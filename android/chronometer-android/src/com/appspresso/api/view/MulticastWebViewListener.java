/*
 * Appspresso
 *
 * Copyright (c) 2011 KT Hitel Corp.
 *
 * Appspresso SDK may be freely distributed under the MIT license.
 */
package com.appspresso.api.view;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.view.KeyEvent;
import android.view.MotionEvent;
import android.webkit.WebView;

/**
 * 모든 이벤트 처리를 여러 개의 구현체에게 위임하는 {@link WebViewListener} 구현체.
 * 
 * @version 1.0
 * @see http://en.wikipedia.org/wiki/Composite_pattern
 */
public class MulticastWebViewListener implements WebViewListener, Iterable<WebViewListener> {

    private final List<WebViewListener> listeners = new LinkedList<WebViewListener>();

    public MulticastWebViewListener() {}

    /**
     * 리스너 추가.
     * 
     * @param l 리스너
     */
    public void addListener(WebViewListener l) {
        this.listeners.add(l);
    }

    /**
     * 리스너 삭제.
     * 
     * @param l 리스너
     */
    public void removeListener(WebViewListener l) {
        this.listeners.remove(l);
    }

    //
    // implements Iterable<WebViewClientListener>
    //

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<WebViewListener> iterator() {
        return listeners.iterator();
    }

    //
    // implements WebViewListener
    //

    /**
     * {@inheritDoc}
     * <p>
     * 최초로 true를 리턴하는 리스너에서 이벤트 처리가 중지됨.
     */
    @Override
    public boolean onKeyDown(WebView webView, int keyCode, KeyEvent event) {
        for (WebViewListener listener : listeners) {
            if (listener.onKeyDown(webView, keyCode, event)) { return true; }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * <p>
     * 최초로 true를 리턴하는 리스너에서 이벤트 처리가 중지됨.
     */
    @Override
    public boolean onKeyUp(WebView webView, int keyCode, KeyEvent event) {
        for (WebViewListener listener : listeners) {
            if (listener.onKeyUp(webView, keyCode, event)) { return true; }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onScrollChanged(WebView webView, int l, int t, int oldl, int oldt) {
        for (WebViewListener listener : listeners) {
            listener.onScrollChanged(webView, l, t, oldl, oldt);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * 최초로 true를 리턴하는 리스너에서 이벤트 처리가 중지됨.
     */
    @Override
    public boolean onTouchEvent(WebView webView, MotionEvent ev) {
        for (WebViewListener listener : listeners) {
            if (listener.onTouchEvent(webView, ev)) { return true; }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * <p>
     * 최초로 true를 리턴하는 리스너에서 이벤트 처리가 중지됨.
     */
    @Override
    public boolean onTrackballEvent(WebView webView, MotionEvent ev) {
        for (WebViewListener listener : listeners) {
            if (listener.onTrackballEvent(webView, ev)) { return true; }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * <p>
     * 최초로 true를 리턴하는 리스너에서 이벤트 처리가 중지됨.
     */
    @Override
    public void invalidate(WebView webView) {
        for (WebViewListener listener : listeners) {
            listener.invalidate(webView);
        }
    }

}
