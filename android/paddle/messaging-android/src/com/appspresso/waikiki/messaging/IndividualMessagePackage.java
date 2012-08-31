package com.appspresso.waikiki.messaging;

import java.util.Observer;

import com.appspresso.waikiki.messaging.utils.JavaScriptCaller;

import android.webkit.WebView;

public class IndividualMessagePackage extends MessagePackageImpl implements Observer {
    public IndividualMessagePackage(WebView webView, long handle) {
        super(webView, handle);
    }

    @Override
    protected void setMessageisSucceed(Message message) {
        JavaScriptCaller.getInstance().callSentSuccess(handle, isAllMessageSent(), message.getTo());
    }

    @Override
    protected void setMessageFailed(Message message) {
        JavaScriptCaller.getInstance().callSentFail(handle, isAllMessageSent(), message.getTo());
    }
}
