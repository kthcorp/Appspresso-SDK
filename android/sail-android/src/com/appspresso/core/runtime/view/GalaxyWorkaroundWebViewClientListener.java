package com.appspresso.core.runtime.view;

import com.appspresso.api.view.WebViewClientAdapter;

import android.graphics.Bitmap;
import android.webkit.WebView;

public class GalaxyWorkaroundWebViewClientListener extends WebViewClientAdapter {

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        view.resumeTimers();
        // undocumented but daum app use this hack!
        try {
            android.webkit.WebView.class.getMethod("onResume").invoke(view);
        }
        catch (Exception e) {
            // e.printStackTrace();
        }
    }

}
