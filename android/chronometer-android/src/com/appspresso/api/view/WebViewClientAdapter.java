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

/**
 * {@link WebViewClientListener}의 기본 구현체.
 * <p>
 * {@link WebViewClientListener}의 일부 메소드만 필요할 때, 이 클래스를 상속하고 필요한 메소드만 오버라이드.
 * 
 * @version 1.0
 */
public class WebViewClientAdapter implements WebViewClientListener {

    /**
     * {@inheritDoc}
     * 
     * @return false(이벤트를 처리하지 않음)
     */
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPageFinished(WebView view, String url) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void onLoadResource(WebView view, String url) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void onFormResubmission(WebView view, Message dontResend, Message resend) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host,
            String realm) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onUnhandledKeyEvent(WebView view, KeyEvent event) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void onScaleChanged(WebView view, float oldScale, float newScale) {}

}
