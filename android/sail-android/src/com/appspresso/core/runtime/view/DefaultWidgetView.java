package com.appspresso.core.runtime.view;

import java.io.File;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.appspresso.api.activity.ActivityAdapter;
import com.appspresso.api.view.MulticastWebChromeClientListener;
import com.appspresso.api.view.MulticastWebViewClientListener;
import com.appspresso.api.view.MulticastWebViewListener;
import com.appspresso.api.view.WebChromeClientListener;
import com.appspresso.api.view.WebViewClientListener;
import com.appspresso.api.view.WebViewListener;
import com.appspresso.internal.AxConfig;

/**
 *
 */
class DefaultWidgetView extends ActivityAdapter implements WidgetView {
    protected final WebView webView;

    private final MulticastWebViewListener multicastWebViewListener =
            new MulticastWebViewListener();

    private final MulticastWebViewClientListener multicastWebViewClientListener =
            new MulticastWebViewClientListener();

    private final MulticastWebChromeClientListener multicastWebChromeClientListener =
            new MulticastWebChromeClientListener();

    public DefaultWidgetView(Context context) {
        this.webView = createWebView(context);
        this.webView.setWebViewClient(createWebViewClient(context));
        this.webView.setWebChromeClient(createWebChromeClient(context));
        configureView(this.webView);
        configureSettings(this.webView.getSettings());
    }

    //
    //
    //

    @Override
    public WebView getWebView() {
        return webView;
    }

    @Override
    public void addWebViewListener(WebViewListener l) {
        multicastWebViewListener.addListener(l);
    }

    @Override
    public void removeWebViewListener(WebViewListener l) {
        multicastWebViewListener.removeListener(l);
    }

    @Override
    public void addWebViewClientListener(WebViewClientListener l) {
        multicastWebViewClientListener.addListener(l);
    }

    @Override
    public void removeWebViewClientListener(WebViewClientListener l) {
        multicastWebViewClientListener.removeListener(l);
    }

    @Override
    public void addWebChromeClientListener(WebChromeClientListener l) {
        multicastWebChromeClientListener.addListener(l);
    }

    @Override
    public void removeWebChromeClientListener(WebChromeClientListener l) {
        multicastWebChromeClientListener.addListener(l);
    }

    //
    //
    //

    /**
     * NOTE: might be overridden to use alternative {@link WebView}.
     * 
     * @param context the containing android context(might be {@link Activity})
     * @return instance of WebChromeClient or subclass
     */
    protected WebView createWebView(final Context context) {
        return new DelegatingWebView(context, multicastWebViewListener);
    }

    /**
     * WebViewClient 구현체를 바꾸려면 이 메소드를 오버라이드. 기본 값은 {@link DelegatingWebViewClient}
     * 
     * @param context
     * @return WebChromeClient(또는 서브클래스) 인스턴스
     */
    protected WebViewClient createWebViewClient(final Context context) {
        return new DelegatingWebViewClient(multicastWebViewClientListener);
    }

    /**
     * WebChromeClient 구현체를 바꾸려면 이 메소드를 오버라이드. 기본 값은 {@link DelegatingWebChromeClient}
     * 
     * @param context
     * @return WebChromeClient(또는 서브클래스) 인스턴스
     */
    protected WebChromeClient createWebChromeClient(final Context context) {
        return new DelegatingWebChromeClient(multicastWebChromeClientListener);
    }

    /**
     * NOTE: might be overridden to setup properties on {@link WebView}.
     * 
     * @param view
     */
    protected void configureView(final WebView view) {
        // remove extra space on the right for vertical scrollbar
        view.setScrollBarStyle(android.view.View.SCROLLBARS_INSIDE_OVERLAY);
        // view.setHorizontalScrollbarOverlay(true);
        // view.setVerticalScrollbarOverlay(true);

        // set pixel-perfect view
        // view.setInitialScale(100);
    }

    /**
     * NOTE: might be overridden to setup properties on {@link WebSettings}.
     * 
     * @param settings
     */
    protected void configureSettings(final WebSettings settings) {
        settings.setJavaScriptEnabled(true);

        // Browser supports multiple windows
        // XXX false - window.open() 지원
        settings.setSupportMultipleWindows(false);

        // HTML5 API flags
        settings.setAppCacheEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setGeolocationEnabled(true);

        // HTML5 configuration parameters.
        // settings.setAppCacheMaxSize(b.appCacheMaxSize);
        // settings.setAppCachePath(b.appCachePath);
        File databases = webView.getContext().getDatabasePath(".");
        settings.setDatabasePath(databases.getAbsolutePath());
        // settings.setGeolocationDatabasePath(b.geolocationDatabasePath);

        // cache
        boolean useCache = AxConfig.getAttributeAsBoolean("webview.cache.enable", true);
        settings.setCacheMode(useCache ? WebSettings.LOAD_DEFAULT : WebSettings.LOAD_NO_CACHE);

        // zoom configuration
        settings.setBuiltInZoomControls(AxConfig.getAttributeAsBoolean("webview.zoom.control",
                false));
        settings.setSupportZoom(AxConfig.getAttributeAsBoolean("webview.zoom.support", false));

        String zoomValue = AxConfig.getAttribute("webview.zoom.default", "MEDIUM");
        WebSettings.ZoomDensity zoom = WebSettings.ZoomDensity.valueOf(zoomValue);
        settings.setDefaultZoom(zoom);

        // settings.setRenderPriority(WebSettings.RenderPriority.HIGH);

        settings.setAllowFileAccess(true);

        // custom user agent string
        settings.setUserAgentString(settings.getUserAgentString() + " " + "Appspresso/1.1.2");
    }

    //
    //
    //

    @Override
    public void onActivityCreate(final Activity activity, Bundle savedInstanceState) {
        CookieSyncManager.createInstance(activity);

        final String KEY_LANGUAGE = "language";
        final String KEY_COUNTRY = "country";

        // support contents localization.
        // Locale 정보가 달라진 경우에 최종 실행 당시의 캐시 파일을 사용하는 것을 막기 위함.
        // XXX .....in AssetsHandler? in DefaultWidgetView?
        if (AxConfig.getAttributeAsBoolean("webview.cache.enable", true)) {
            // get current locale
            Locale locale = Locale.getDefault();
            String language = locale.getLanguage().toLowerCase();
            String country = locale.getCountry().toLowerCase();

            // last locale
            SharedPreferences pref =
                    activity.getSharedPreferences("ax.runtime.locale", Context.MODE_WORLD_WRITEABLE);
            String lastLanguage = pref.getString(KEY_LANGUAGE, "").toLowerCase();
            String lastCountry = pref.getString(KEY_COUNTRY, "").toLowerCase();

            if (!language.equals(lastLanguage) || !country.equals(lastCountry)) {
                webView.clearCache(true);

                Editor editor = pref.edit();
                editor.putString(KEY_LANGUAGE, language);
                editor.putString(KEY_COUNTRY, country);

                editor.commit();
            }
        }
    }

    @Override
    public void onActivityRestart(final Activity activity) {
        CookieSyncManager.createInstance(activity);
    }

    @Override
    public void onActivityStart(final Activity activity) {
        CookieSyncManager.getInstance().startSync();
    }

    @Override
    public void onActivityResume(final Activity activity) {
        webView.loadUrl("javascript:(function(){if(ax.event.onRestoreState){ax.event.onRestoreState();}})();");
        webView.resumeTimers();
    }

    @Override
    public void onActivityPause(final Activity activity) {
        webView.loadUrl("javascript:(function(){if(ax.event.onSaveState){ax.event.onSaveState();}})();");
        webView.pauseTimers();
    }

    @Override
    public void onActivityStop(final Activity activity) {
        webView.stopLoading();
        CookieSyncManager.getInstance().stopSync();
    }

    @Override
    public void onActivityDestroy(final Activity activity) {
        boolean b = AxConfig.getAttributeAsBoolean("webview.cache.clearonfinish", false);
        webView.clearCache(b);

        webView.freeMemory();
        webView.destroy();
    }

    @Override
    public void onRestoreInstanceState(final Activity activity, Bundle savedInstanceState) {
        webView.restoreState(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(final Activity activity, Bundle outState) {
        webView.saveState(outState);
    }

    @Override
    public boolean onBackPressed(final Activity activity) {
        // on back key: browser history.back()
        if (webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return false;
    }

}
