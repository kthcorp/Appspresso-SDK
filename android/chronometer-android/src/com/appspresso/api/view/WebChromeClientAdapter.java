/*
 * Appspresso
 *
 * Copyright (c) 2011 KT Hitel Corp.
 *
 * Appspresso SDK may be freely distributed under the MIT license.
 */
package com.appspresso.api.view;

import android.graphics.Bitmap;
import android.os.Message;
import android.view.View;
import android.webkit.*;

/**
 * {@link WebChromeClientListener}의 기본 구현체.
 * <p>
 * {@link WebChromeClientListener}의 일부 메소드만 필요할 때, 이 클래스를 상속하고 해당 메소드만 오버라이드.
 * 
 * @version 1.0
 */
public class WebChromeClientAdapter implements WebChromeClientListener {

    /**
     * {@inheritDoc}
     */
    @Override
    public void onProgressChanged(WebView view, int newProgress) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReceivedTitle(WebView view, String title) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReceivedIcon(WebView view, Bitmap icon) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReceivedTouchIconUrl(WebView view, String url, boolean precomposed) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void onHideCustomView() {}

    /**
     * {@inheritDoc}
     * 
     * @return false(이벤트를 처리하지 않음)
     */
    @Override
    public boolean onCreateWindow(WebView view, boolean dialog, boolean userGesture,
            Message resultMsg) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onRequestFocus(WebView view) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCloseWindow(WebView window) {}

    /**
     * {@inheritDoc}
     * 
     * @return false(이벤트를 처리하지 않음)
     */
    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @return false(이벤트를 처리하지 않음)
     */
    @Override
    public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @return false(이벤트를 처리하지 않음)
     */
    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue,
            JsPromptResult result) {
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @return false(이벤트를 처리하지 않음)
     */
    @Override
    public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onExceededDatabaseQuota(String url, String databaseIdentifier, long currentQuota,
            long estimatedSize, long totalUsedQuota, WebStorage.QuotaUpdater quotaUpdater) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReachedMaxAppCacheSize(long spaceNeeded, long totalUsedQuota,
            WebStorage.QuotaUpdater quotaUpdater) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void onGeolocationPermissionsShowPrompt(String origin,
            GeolocationPermissions.Callback callback) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void onGeolocationPermissionsHidePrompt() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onJsTimeout() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onConsoleMessage(String message, int lineNumber, String sourceID) {}

    /**
     * {@inheritDoc}
     * 
     * @return false(이벤트를 처리하지 않음)
     */
    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @return null(이벤트를 처리하지 않음)
     */
    @Override
    public Bitmap getDefaultVideoPoster() {
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @return null(이벤트를 처리하지 않음)
     */
    @Override
    public View getVideoLoadingProgressView() {
        return null;
    }

}
