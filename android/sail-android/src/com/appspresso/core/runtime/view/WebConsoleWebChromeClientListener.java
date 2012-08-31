package com.appspresso.core.runtime.view;

import com.appspresso.api.view.WebChromeClientAdapter;

import android.util.Log;

public class WebConsoleWebChromeClientListener extends WebChromeClientAdapter {

    private static final String TAG = "console.log";

    @Override
    public void onConsoleMessage(String message, int lineNumber, String sourceID) {
        Log.d(TAG,
                new StringBuilder(200).append(message).append(" at ").append(sourceID).append(':')
                        .append(lineNumber).toString());
    }

}
