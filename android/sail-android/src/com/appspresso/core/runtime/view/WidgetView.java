package com.appspresso.core.runtime.view;

import android.webkit.WebView;

import com.appspresso.api.activity.ActivityListener;
import com.appspresso.api.view.WebChromeClientListener;
import com.appspresso.api.view.WebViewClientListener;
import com.appspresso.api.view.WebViewListener;
import com.appspresso.core.runtime.widget.WidgetAgent;

/**
 * This interface provides a "view" for {@link WidgetAgent} using the containing {@link WebView}.
 * 
 * TODO: TBD...
 * 
 */
public interface WidgetView extends ActivityListener {

    WebView getWebView();

    void addWebViewListener(WebViewListener l);

    void removeWebViewListener(WebViewListener l);

    void addWebViewClientListener(WebViewClientListener l);

    void removeWebViewClientListener(WebViewClientListener l);

    void addWebChromeClientListener(WebChromeClientListener l);

    void removeWebChromeClientListener(WebChromeClientListener l);

}
