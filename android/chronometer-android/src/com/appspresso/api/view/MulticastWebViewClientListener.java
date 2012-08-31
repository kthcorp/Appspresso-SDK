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

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Message;
import android.view.KeyEvent;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * 모든 이벤트 처리를 여러 개의 구현체에게 위임하는 {@link WebViewClientListener} 구현체.
 * 
 * @version 1.0
 * @see http://en.wikipedia.org/wiki/Composite_pattern
 */
public class MulticastWebViewClientListener
        implements
            WebViewClientListener,
            Iterable<WebViewClientListener> {

    private final List<WebViewClientListener> listeners = new LinkedList<WebViewClientListener>();

    public MulticastWebViewClientListener() {}

    /**
     * 리스너 추가.
     * 
     * @param l 리스너
     */
    public void addListener(WebViewClientListener l) {
        this.listeners.add(l);
    }

    /**
     * 리스너 삭제.
     * 
     * @param l 리스너
     */
    public void removeListener(WebViewClientListener l) {
        this.listeners.remove(l);
    }

    //
    // implements Iterable<WebViewClientListener>
    //

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<WebViewClientListener> iterator() {
        return listeners.iterator();
    }

    //
    // implements WebViewClientListener
    //

    /**
     * {@inheritDoc}
     * <p>
     * 최초로 true를 리턴하는 리스너에서 이벤트 처리가 중지됨.
     */
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        for (WebViewClientListener l : listeners) {
            if (l.shouldOverrideUrlLoading(view, url)) { return true; }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        for (WebViewClientListener l : listeners) {
            l.onPageStarted(view, url, favicon);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPageFinished(WebView view, String url) {
        for (WebViewClientListener l : listeners) {
            l.onPageFinished(view, url);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onLoadResource(WebView view, String url) {
        for (WebViewClientListener l : listeners) {
            l.onLoadResource(view, url);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        for (WebViewClientListener l : listeners) {
            l.onReceivedError(view, errorCode, description, failingUrl);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onFormResubmission(WebView view, Message dontResend, Message resend) {
        for (WebViewClientListener l : listeners) {
            l.onFormResubmission(view, dontResend, resend);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
        for (WebViewClientListener l : listeners) {
            l.doUpdateVisitedHistory(view, url, isReload);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        for (WebViewClientListener l : listeners) {
            l.onReceivedSslError(view, handler, error);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host,
            String realm) {
        for (WebViewClientListener l : listeners) {
            l.onReceivedHttpAuthRequest(view, handler, host, realm);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * 최초로 true를 리턴하는 리스너에서 이벤트 처리가 중지됨.
     */
    @Override
    public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
        for (WebViewClientListener l : listeners) {
            if (l.shouldOverrideKeyEvent(view, event)) { return true; }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
        for (WebViewClientListener l : listeners) {
            l.onUnhandledKeyEvent(view, event);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onScaleChanged(WebView view, float oldScale, float newScale) {
        for (WebViewClientListener l : listeners) {
            l.onScaleChanged(view, oldScale, newScale);
        }
    }

}
