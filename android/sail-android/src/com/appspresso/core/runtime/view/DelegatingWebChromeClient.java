package com.appspresso.core.runtime.view;

import com.appspresso.api.view.WebChromeClientListener;

import android.graphics.Bitmap;
import android.os.Message;
import android.view.View;
import android.webkit.*;

/**
 * 주요 이벤트를 별도의 {@link WebChromeClientListener} 인스턴스({@link #delegate})에게 위임하는 WebChromeClient 서브클래스.
 * <p>
 * 이 리스너로 {@link com.appspresso.api.view.MulticastWebChromeClientListener}를 사용하면 여러개의 리스너에게 위임하는
 * WebChromeClient 됨.
 * <p>
 * TODO: 이벤트(오버라이드해서 위임할 메소드) 추가/제거... 리뷰가 필요함.
 * 
 */
public class DelegatingWebChromeClient extends WebChromeClient {

    private final WebChromeClientListener delegate;

    public DelegatingWebChromeClient(WebChromeClientListener delegate) {
        if (delegate == null) { throw new IllegalArgumentException("delegate"); }
        this.delegate = delegate;
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        delegate.onProgressChanged(view, newProgress);
        super.onProgressChanged(view, newProgress);
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        delegate.onReceivedTitle(view, title);
        super.onReceivedTitle(view, title);
    }

    @Override
    public void onReceivedIcon(WebView view, Bitmap icon) {
        delegate.onReceivedIcon(view, icon);
        super.onReceivedIcon(view, icon);
    }

    @Override
    public void onReceivedTouchIconUrl(WebView view, String url, boolean precomposed) {
        delegate.onReceivedTouchIconUrl(view, url, precomposed);
        super.onReceivedTouchIconUrl(view, url, precomposed);
    }

    @Override
    public void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback) {
        delegate.onShowCustomView(view, callback);
        super.onShowCustomView(view, callback);
    }

    @Override
    public void onHideCustomView() {
        delegate.onHideCustomView();
        super.onHideCustomView();
    }

    @Override
    public boolean onCreateWindow(WebView view, boolean dialog, boolean userGesture,
            Message resultMsg) {
        if (delegate.onCreateWindow(view, dialog, userGesture, resultMsg)) { return true; }
        return super.onCreateWindow(view, dialog, userGesture, resultMsg);
    }

    @Override
    public void onRequestFocus(WebView view) {
        delegate.onRequestFocus(view);
        super.onRequestFocus(view);
    }

    @Override
    public void onCloseWindow(WebView window) {
        delegate.onCloseWindow(window);
        super.onCloseWindow(window);
    }

    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        if (delegate.onJsAlert(view, url, message, result)) { return true; }
        return super.onJsAlert(view, url, message, result);
    }

    @Override
    public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
        if (delegate.onJsConfirm(view, url, message, result)) { return true; }
        return super.onJsConfirm(view, url, message, result);
    }

    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue,
            JsPromptResult result) {
        if (delegate.onJsPrompt(view, url, message, defaultValue, result)) { return true; }
        return super.onJsPrompt(view, url, message, defaultValue, result);
    }

    @Override
    public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
        if (delegate.onJsBeforeUnload(view, url, message, result)) { return true; }
        return super.onJsBeforeUnload(view, url, message, result);
    }

    @Override
    public void onExceededDatabaseQuota(String url, String databaseIdentifier, long currentQuota,
            long estimatedSize, long totalUsedQuota, WebStorage.QuotaUpdater quotaUpdater) {
        super.onExceededDatabaseQuota(url, databaseIdentifier, currentQuota, estimatedSize,
                totalUsedQuota, quotaUpdater);
        delegate.onExceededDatabaseQuota(url, databaseIdentifier, currentQuota, estimatedSize,
                totalUsedQuota, quotaUpdater);

    }

    @Override
    public void onReachedMaxAppCacheSize(long spaceNeeded, long totalUsedQuota,
            WebStorage.QuotaUpdater quotaUpdater) {
        super.onReachedMaxAppCacheSize(spaceNeeded, totalUsedQuota, quotaUpdater);
        delegate.onReachedMaxAppCacheSize(spaceNeeded, totalUsedQuota, quotaUpdater);
    }

    @Override
    public void onGeolocationPermissionsShowPrompt(String origin,
            GeolocationPermissions.Callback callback) {
        delegate.onGeolocationPermissionsShowPrompt(origin, callback);
        super.onGeolocationPermissionsShowPrompt(origin, callback);
    }

    @Override
    public void onGeolocationPermissionsHidePrompt() {
        delegate.onGeolocationPermissionsHidePrompt();
        super.onGeolocationPermissionsHidePrompt();
    }

    @Override
    public boolean onJsTimeout() {
        if (delegate.onJsTimeout()) { return true; }
        return super.onJsTimeout();
    }

    @Override
    public void onConsoleMessage(String message, int lineNumber, String sourceID) {
        delegate.onConsoleMessage(message, lineNumber, sourceID);
        super.onConsoleMessage(message, lineNumber, sourceID);
    }

    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        if (delegate.onConsoleMessage(consoleMessage)) { return true; }
        return super.onConsoleMessage(consoleMessage);
    }

    @Override
    public Bitmap getDefaultVideoPoster() {
        Bitmap poster = null;
        if ((poster = delegate.getDefaultVideoPoster()) != null) { return poster; }
        return super.getDefaultVideoPoster();
    }

    @Override
    public View getVideoLoadingProgressView() {
        View progress = null;
        if ((progress = delegate.getVideoLoadingProgressView()) != null) { return progress; }
        return super.getVideoLoadingProgressView();
    }

}
