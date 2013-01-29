/*
 * Appspresso
 *
 * Copyright (c) 2011 KT Hitel Corp.
 *
 * Appspresso SDK may be freely distributed under the MIT license.
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
 * {@link andorid.webkit.WebViewClient}의 기본 동작을 오버라이드하기 위한 리스너.
 * <p>
 * 이 인터페이스의 일부 메소드만 필요하다면, 이 인터페이스 전체를 구현하는 대신 {@link WebViewClientAdapter} 클래스를 상속하고 필요한 메소드만
 * 오버라이드.
 * 
 * @version 1.0
 * @see http://developer.android.com/reference/android/webkit/WebViewClient.html
 */
public interface WebViewClientListener {

    /**
     * Give the host application a chance to take over the control when a new url is about to be
     * loaded in the current WebView. If WebViewClient is not provided, by default WebView will ask
     * Activity Manager to choose the proper handler for the url. If WebViewClient is provided,
     * return true means the host application handles the url, while return false means the current
     * WebView handles the url.
     * 
     * @param view 웹뷰
     * @param url The url to be loaded.
     * @return
     * @since 1.0
     */
    boolean shouldOverrideUrlLoading(WebView view, String url);

    /**
     * Notify the host application that a page has started loading. This method is called once for
     * each main frame load so a page with iframes or framesets will call onPageStarted one time for
     * the main frame. This also means that onPageStarted will not be called when the contents of an
     * embedded frame changes, i.e. clicking a link whose target is an iframe.
     * 
     * @param view 웹뷰
     * @param url The url to be loaded.
     * @param favicon The favicon for this page if it already exists in the database.
     * @since 1.0
     */
    void onPageStarted(WebView view, String url, Bitmap favicon);

    /**
     * Notify the host application that a page has finished loading. This method is called only for
     * main frame. When onPageFinished() is called, the rendering picture may not be updated yet. To
     * get the notification for the new Picture, use onNewPicture(WebView, Picture).
     * 
     * @param view 웹뷰
     * @param url The url of the page.
     * @since 1.0
     */
    void onPageFinished(WebView view, String url);

    /**
     * Notify the host application that the WebView will load the resource specified by the given
     * url.
     * 
     * @param view 웹뷰
     * @param url The url of the resource the WebView will load.
     * @since 1.0
     */
    void onLoadResource(WebView view, String url);

    /**
     * 
     * @param view 웹뷰
     * @param errorCode
     * @param description
     * @param failingUrl
     * @since 1.0
     */
    void onReceivedError(WebView view, int errorCode, String description, String failingUrl);

    /**
     * As the host application if the browser should resend data as the requested page was a result
     * of a POST. The default is to not resend the data.
     * 
     * @param view 웹뷰
     * @param dontResend The message to send if the browser should not resend
     * @param resend The message to send if the browser should resend data
     * @since 1.0
     */
    void onFormResubmission(WebView view, Message dontResend, Message resend);

    /**
     * Notify the host application to update its visited links database.
     * 
     * @param view 웹뷰
     * @param url The url being visited.
     * @param isReload True if this url is being reloaded.
     * @since 1.0
     */
    void doUpdateVisitedHistory(WebView view, String url, boolean isReload);

    /**
     * Notify the host application that an SSL error occurred while loading a resource. The host
     * application must call either handler.cancel() or handler.proceed(). Note that the decision
     * may be retained for use in response to future SSL errors. The default behavior is to cancel
     * the load.
     * 
     * @param view 웹뷰
     * @param handler An SslErrorHandler object that will handle the user's response.
     * @param error The SSL error object.
     * @since 1.0
     * @since 안드로이드 2.2(api 8)
     */
    void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error);

    /**
     * Notify the host application to handle an authentication request. The default behavior is to
     * cancel the request.
     * 
     * @param view 웹뷰
     * @param handler The HttpAuthHandler that will handle the user's response.
     * @param host The host requiring authentication.
     * @param realm A description to help store user credentials for future visits.
     * @since 1.0
     */
    void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm);

    /**
     * Give the host application a chance to handle the key event synchronously. e.g. menu shortcut
     * key events need to be filtered this way. If return true, WebView will not handle the key
     * event. If return false, WebView will always handle the key event, so none of the super in the
     * view chain will see the key event. The default behavior returns false.
     * 
     * @param view 웹뷰
     * @param event The key event.
     * @return True if the host application wants to handle the key event itself, otherwise return
     *         false
     * @since 1.0
     */
    boolean shouldOverrideKeyEvent(WebView view, KeyEvent event);

    /**
     * Notify the host application that a key was not handled by the WebView. Except system keys,
     * WebView always consumes the keys in the normal flow or if shouldOverrideKeyEvent returns
     * true. This is called asynchronously from where the key is dispatched. It gives the host
     * application an chance to handle the unhandled key events.
     * 
     * @param view 웹뷰
     * @param event The key event.
     * @since 1.0
     */
    void onUnhandledKeyEvent(WebView view, KeyEvent event);

    /**
     * Notify the host application that the scale applied to the WebView has changed.
     * 
     * @param view 웹뷰
     * @param oldScale The old scale factor
     * @param newScale The new scale factor
     * @since 1.0
     */
    void onScaleChanged(WebView view, float oldScale, float newScale);

}
