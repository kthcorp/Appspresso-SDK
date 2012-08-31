package com.appspresso.waikiki.messaging.utils;

import android.app.Activity;
import android.webkit.WebView;

public class JavaScriptCaller {
    private Activity activity;
    private WebView webView;

    private static JavaScriptCaller instance;

    private JavaScriptCaller() {}

    public void init(Activity activity, WebView webView) {
        this.activity = activity;
        this.webView = webView;
    }

    public static JavaScriptCaller getInstance() {
        if (instance == null) instance = new JavaScriptCaller();
        return instance;
    }

    public void callSentSuccess(long handle, boolean completed, String recipient) {
        String url =
                "javascript:deviceapis.messaging.callSuccessCallback(" + handle + ", " + completed
                        + ", [" + recipient + "])";
        callJavaScript(url);
    }

    public void callSentFail(long handle, boolean completed, String recipient) {
        String url =
                "javascript:deviceapis.messaging.callErrorCallback(" + handle + ", " + completed
                        + ", [" + recipient + "])";
        callJavaScript(url);
    }

    public void callSentSuccess(long handle) {
        String url = "javascript:deviceapis.messaging.callSuccessCallback(" + handle + ", true)";
        callJavaScript(url);
    }

    public void callSentFail(long handle) {
        String url = "javascript:deviceapis.messaging.callErrorCallback(" + handle + ", true)";
        callJavaScript(url);
    }

    private boolean isCurrentMainThread() {
        return Thread.currentThread().getName().equals("main");
    }

    private void callJavaScript(final String url) {
        if (isCurrentMainThread()) {
            webView.loadUrl(url);
        }
        else {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    webView.loadUrl(url);
                }
            });
        }
    }
}
