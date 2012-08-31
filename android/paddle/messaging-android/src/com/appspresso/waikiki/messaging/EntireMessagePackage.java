package com.appspresso.waikiki.messaging;

import com.appspresso.waikiki.messaging.utils.JavaScriptCaller;

import android.webkit.WebView;

public class EntireMessagePackage extends MessagePackageImpl {
    public EntireMessagePackage(WebView webView, long handle) {
        super(webView, handle);
    }

    @Override
    protected void setMessageisSucceed(Message message) {
        if (isAllMessageSent()) {
            JavaScriptCaller.getInstance().callSentSuccess(handle);
        }
    }

    @Override
    protected void setMessageFailed(Message message) {
        if (isAllMessageSent()) {
            JavaScriptCaller.getInstance().callSentFail(handle);
        }
    }
}
