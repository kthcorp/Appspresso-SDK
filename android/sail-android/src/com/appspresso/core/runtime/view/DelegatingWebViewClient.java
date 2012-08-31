package com.appspresso.core.runtime.view;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Message;
import android.view.KeyEvent;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.appspresso.api.view.WebViewClientListener;

/**
 * 주요 이벤트를 별도의 {@link WebViewClient} 인스턴스({@link #delegate})에게 위임하는 WebViewClient 서브클래스.
 * <p>
 * 이 리스너로 {@link com.appspresso.api.view.MulticastWebViewClientListener}를 사용하면 여러개의 리스너에게 위임하는
 * WebViewClient가 됨.
 * <p>
 * TODO: 이벤트(오버라이드해서 위임할 메소드) 추가/제거... 리뷰가 필요함.
 * 
 */
public class DelegatingWebViewClient extends WebViewClient {

    private final WebViewClientListener delegate;

    public DelegatingWebViewClient(WebViewClientListener delegate) {
        if (delegate == null) { throw new IllegalArgumentException("delegate"); }
        this.delegate = delegate;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (delegate.shouldOverrideUrlLoading(view, url)) { return true; }
        return super.shouldOverrideUrlLoading(view, url);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        delegate.onPageStarted(view, url, favicon);
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        delegate.onPageFinished(view, url);
        super.onPageFinished(view, url);
    }

    @Override
    public void onLoadResource(WebView view, String url) {
        delegate.onLoadResource(view, url);
        super.onLoadResource(view, url);
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        delegate.onReceivedError(view, errorCode, description, failingUrl);
        super.onReceivedError(view, errorCode, description, failingUrl);
    }

    @Override
    public void onFormResubmission(WebView view, Message dontResend, Message resend) {
        delegate.onFormResubmission(view, dontResend, resend);
        super.onFormResubmission(view, dontResend, resend);
    }

    @Override
    public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
        delegate.doUpdateVisitedHistory(view, url, isReload);
        super.doUpdateVisitedHistory(view, url, isReload);
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        delegate.onReceivedSslError(view, handler, error);
        super.onReceivedSslError(view, handler, error);
    }

    @Override
    public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host,
            String realm) {
        delegate.onReceivedHttpAuthRequest(view, handler, host, realm);
        super.onReceivedHttpAuthRequest(view, handler, host, realm);
    }

    @Override
    public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
        if (delegate.shouldOverrideKeyEvent(view, event)) { return true; }
        return super.shouldOverrideKeyEvent(view, event);
    }

    @Override
    public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
        delegate.onUnhandledKeyEvent(view, event);
        super.onUnhandledKeyEvent(view, event);
    }

    @Override
    public void onScaleChanged(WebView view, float oldScale, float newScale) {
        delegate.onScaleChanged(view, oldScale, newScale);
        super.onScaleChanged(view, oldScale, newScale);
    }

}
