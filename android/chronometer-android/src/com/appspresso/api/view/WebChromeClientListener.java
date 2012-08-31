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
import android.os.Message;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebStorage;
import android.webkit.WebView;

/**
 * {@link andorid.webkit.WebChromeClient}의 기본 동작을 오버라이드하기 위한 리스너.
 * <p>
 * 이 인터페이스의 일부 메소드만 필요하다면, 이 인터페이스 전체를 구현하는 대신 {@link WebChromeClientAdapter} 클래스를 상속하고 필요한 메소드만
 * 오버라이드.
 * 
 * @version 1.0
 * @see http ://developer.android.com/reference/android/webkit/WebChromeClient.html
 */
public interface WebChromeClientListener {

    /**
     * Tell the host application the current progress of loading a page.
     * 
     * @param view The WebView that initiated the callback.
     * @param newProgress Current page loading progress, represented by an integer between 0 and
     *        100.
     */
    void onProgressChanged(WebView view, int newProgress);

    /**
     * Notify the host application of a change in the document title.
     * 
     * @param view The WebView that initiated the callback.
     * @param title A String containing the new title of the document.
     */
    void onReceivedTitle(WebView view, String title);

    /**
     * Notify the host application of a new favicon for the current page.
     * 
     * @param view The WebView that initiated the callback.
     * @param icon A Bitmap containing the favicon for the current page.
     */
    void onReceivedIcon(WebView view, Bitmap icon);

    /**
     * Notify the host application of the url for an apple-touch-icon.
     * 
     * @param view The WebView that initiated the callback.
     * @param url The icon url.
     * @param precomposed True if the url is for a precomposed touch icon.
     */
    void onReceivedTouchIconUrl(WebView view, String url, boolean precomposed);

    /**
     * Notify the host application that the current page would like to show a custom View in a
     * particular orientation.
     * 
     * @param view is the View object to be shown.
     * @param callback is the callback to be invoked if and when the view is dismissed.
     */
    void onShowCustomView(android.view.View view, WebChromeClient.CustomViewCallback callback);

    /**
     * Notify the host application that the current page would like to hide its custom view.
     */
    void onHideCustomView();

    /**
     * Request the host application to create a new Webview. The host application should handle
     * placement of the new WebView in the view system. The default behavior returns null.
     * 
     * @param view The WebView that initiated the callback.
     * @param dialog True if the new window is meant to be a small dialog window.
     * @param userGesture True if the request was initiated by a user gesture such as clicking a
     *        link.
     * @param resultMsg The message to send when done creating a new WebView. Set the new WebView
     *        through resultMsg.obj which is WebView.WebViewTransport() and then call
     *        resultMsg.sendToTarget();
     * @return Similar to javscript dialogs, this method should return true if the client is going
     *         to handle creating a new WebView. Note that the WebView will halt processing if this
     *         method returns true so make sure to call resultMsg.sendToTarget(). It is undefined
     *         behavior to call resultMsg.sendToTarget() after returning false from this method.
     */
    boolean onCreateWindow(WebView view, boolean dialog, boolean userGesture, Message resultMsg);

    /**
     * Request display and focus for this WebView. This may happen due to another WebView opening a
     * link in this WebView and requesting that this WebView be displayed.
     * 
     * @param view The WebView that needs to be focused.
     */
    void onRequestFocus(WebView view);

    /**
     * Notify the host application to close the given WebView and remove it from the view system if
     * necessary. At this point, WebCore has stopped any loading in this window and has removed any
     * cross-scripting ability in javascript.
     * 
     * @param window The WebView that needs to be closed.
     */
    void onCloseWindow(WebView window);

    /**
     * Tell the client to display a javascript alert dialog. If the client returns true, WebView
     * will assume that the client will handle the dialog. If the client returns false, it will
     * continue execution.
     * 
     * @param view The WebView that initiated the callback.
     * @param url The url of the page requesting the dialog.
     * @param message Message to be displayed in the window.
     * @param result A JsResult to confirm that the user hit enter.
     * @return boolean Whether the client will handle the alert dialog.
     */
    boolean onJsAlert(WebView view, String url, String message, JsResult result);

    /**
     * Tell the client to display a dialog to confirm navigation away from the current page. This is
     * the result of the onbeforeunload javascript event. If the client returns true, WebView will
     * assume that the client will handle the confirm dialog and call the appropriate JsResult
     * method. If the client returns false, a default value of true will be returned to javascript
     * to accept navigation away from the current page. The default behavior is to return false.
     * Setting the JsResult to true will navigate away from the current page, false will cancel the
     * navigation.
     * 
     * @param view The WebView that initiated the callback.
     * @param url The url of the page requesting the dialog.
     * @param message Message to be displayed in the window.
     * @param result A JsResult used to send the user's response to javascript.
     * @return boolean Whether the client will handle the confirm dialog.
     */
    boolean onJsConfirm(WebView view, String url, String message, JsResult result);

    /**
     * Tell the client to display a prompt dialog to the user. If the client returns true, WebView
     * will assume that the client will handle the prompt dialog and call the appropriate
     * JsPromptResult method. If the client returns false, a default value of false will be returned
     * to to javascript. The default behavior is to return false.
     * 
     * @param view The WebView that initiated the callback.
     * @param url The url of the page requesting the dialog.
     * @param message Message to be displayed in the window.
     * @param defaultValue The default value displayed in the prompt dialog.
     * @param result A JsPromptResult used to send the user's reponse to javascript.
     * @return boolean Whether the client will handle the prompt dialog.
     */
    boolean onJsPrompt(WebView view, String url, String message, String defaultValue,
            JsPromptResult result);

    /**
     * Tell the client to display a dialog to confirm navigation away from the current page. This is
     * the result of the onbeforeunload javascript event. If the client returns true, WebView will
     * assume that the client will handle the confirm dialog and call the appropriate JsResult
     * method. If the client returns false, a default value of true will be returned to javascript
     * to accept navigation away from the current page. The default behavior is to return false.
     * Setting the JsResult to true will navigate away from the current page, false will cancel the
     * navigation.
     * 
     * @param view The WebView that initiated the callback.
     * @param url The url of the page requesting the dialog.
     * @param message Message to be displayed in the window.
     * @param result A JsResult used to send the user's response to javascript.
     * @return boolean Whether the client will handle the confirm dialog.
     */
    boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result);

    /**
     * Tell the client that the database quota for the origin has been exceeded.
     * 
     * @param url The URL that triggered the notification
     * @param databaseIdentifier The identifier of the database that caused the quota overflow.
     * @param currentQuota The current quota for the origin.
     * @param estimatedSize The estimated size of the database.
     * @param totalUsedQuota is the sum of all origins' quota.
     * @param quotaUpdater A callback to inform the WebCore thread that a new quota is available.
     *        This callback must always be executed at some point to ensure that the sleeping
     *        WebCore thread is woken up.
     */
    void onExceededDatabaseQuota(String url, String databaseIdentifier, long currentQuota,
            long estimatedSize, long totalUsedQuota, WebStorage.QuotaUpdater quotaUpdater);

    /**
     * Tell the client that the Application Cache has exceeded its max size.
     * 
     * @param spaceNeeded is the amount of disk space that would be needed in order for the last
     *        appcache operation to succeed.
     * @param totalUsedQuota is the sum of all origins' quota.
     * @param quotaUpdater A callback to inform the WebCore thread that a new app cache size is
     *        available. This callback must always be executed at some point to ensure that the
     *        sleeping WebCore thread is woken up.
     */
    void onReachedMaxAppCacheSize(long spaceNeeded, long totalUsedQuota,
            WebStorage.QuotaUpdater quotaUpdater);

    /**
     * Instructs the client to show a prompt to ask the user to set the Geolocation permission state
     * for the specified origin.
     * 
     * @param origin
     * @param callback
     */
    void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback);

    /**
     * Instructs the client to hide the Geolocation permissions prompt.
     */
    void onGeolocationPermissionsHidePrompt();

    /**
     * Tell the client that a JavaScript execution timeout has occured. And the client may decide
     * whether or not to interrupt the execution. If the client returns true, the JavaScript will be
     * interrupted. If the client returns false, the execution will continue. Note that in the case
     * of continuing execution, the timeout counter will be reset, and the callback will continue to
     * occur if the script does not finish at the next check point.
     * 
     * @return boolean Whether the JavaScript execution should be interrupted.
     */
    boolean onJsTimeout();

    /**
     * Report a JavaScript error message to the host application. The ChromeClient should override
     * this to process the log message as they see fit.
     * <p>
     * NOTE: deprecated since api 8
     * 
     * @param message The error message to report.
     * @param lineNumber The line number of the error.
     * @param sourceID The name of the source file that caused the error.
     */
    void onConsoleMessage(String message, int lineNumber, String sourceID);

    /**
     * Report a JavaScript console message to the host application. The ChromeClient should override
     * this to process the log message as they see fit.
     * 
     * @param consoleMessage Object containing details of the console message.
     * @return true if the message is handled by the client.
     * @since android 2.2(api 8)+
     */
    boolean onConsoleMessage(ConsoleMessage consoleMessage);

    /**
     * When not playing, video elements are represented by a 'poster' image. The image to use can be
     * specified by the poster attribute of the video tag in HTML. If the attribute is absent, then
     * a default poster will be used. This method allows the ChromeClient to provide that default
     * image.
     * 
     * @return Bitmap The image to use as a default poster, or null if no such image is available.
     */
    Bitmap getDefaultVideoPoster();

    /**
     * When the user starts to playback a video element, it may take time for enough data to be
     * buffered before the first frames can be rendered. While this buffering is taking place, the
     * ChromeClient can use this function to provide a View to be displayed. For example, the
     * ChromeClient could show a spinner animation.
     * 
     * @return View The View to be displayed whilst the video is loading.
     */
    View getVideoLoadingProgressView();
}
