package com.appspresso.waikiki.messaging;

import android.webkit.WebView;

public class MessagePackageFactory {
    public static MessagePackage createMessageFactory(WebView webView, long handle,
            boolean isIndividually, Message[] messages) {
        MessagePackage messagePackage = null;
        if (isIndividually) {
            messagePackage = new IndividualMessagePackage(webView, handle);
        }
        else {
            messagePackage = new EntireMessagePackage(webView, handle);
        }

        int length = messages.length;
        for (int i = 0; i < length; i++) {
            messagePackage.addMessage(messages[i]);
        }

        return messagePackage;
    }
}
