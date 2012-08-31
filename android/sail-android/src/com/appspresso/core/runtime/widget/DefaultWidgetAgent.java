package com.appspresso.core.runtime.widget;

import java.io.IOException;
import java.util.List;
import org.apache.commons.logging.Log;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.CookieManager;

import com.appspresso.api.AxLog;
import com.appspresso.api.AxRuntimeContext;
import com.appspresso.api.activity.MulticastActivityListener;
import com.appspresso.core.runtime.filesystem.FileSystemManager;
import com.appspresso.core.runtime.filesystem.WaikikiFileSystems;
import com.appspresso.core.runtime.plugin.PluginManager;
import com.appspresso.core.runtime.plugin.PluginManagerFactory;
import com.appspresso.core.runtime.server.IWebServer;
import com.appspresso.core.runtime.server.WebServerFactory;
import com.appspresso.core.runtime.util.AxSessionKeyHolder;
import com.appspresso.core.runtime.view.WidgetView;
import com.appspresso.internal.AxConfig;
import com.appspresso.w3.widget.DefaultWidgetConfig;
import com.appspresso.w3.widget.WidgetConfigParser;

/**
 * This class implements {@link WidgetAgent} using the built-in {@link IWebServer}.
 * 
 */
public class DefaultWidgetAgent extends MulticastActivityListener implements WidgetAgent {
    private static final Log L = AxLog.getLog(DefaultWidgetAgent.class);

    private final String BASE_DIR = "ax_www";

    private AndroidAxContext axContext;

    private final WidgetView widgetView;
    private DefaultWidgetConfig widgetConfig;
    private PluginManager pluginManager;
    private IWebServer server;
    private FileSystemManager fsManager;

    public DefaultWidgetAgent(WidgetView widgetView) {
        this.widgetView = widgetView;
    }

    @Override
    public PluginManager getPluginManager() {
        return pluginManager;
    }

    @Override
    public WidgetView getWidgetView() {
        return widgetView;
    }

    @Override
    public DefaultWidgetConfig getWidget() {
        return widgetConfig;
    }

    @Override
    public AxRuntimeContext getAxRuntimeContext() {
        return axContext;
    }

    @Override
    public String getBaseDir() {
        return BASE_DIR;
    }

    //
    //
    //

    protected void loadWidgetConfig(Activity activity) {
        try {
            if (L.isTraceEnabled()) {
                L.trace("parse widget config...");
            }

            widgetConfig =
                    WidgetConfigParser.newInstance().newWidgetConfig(activity,
                            BASE_DIR + "/config.xml");
        }
        catch (IOException e) {
            // XXX: error handling!
            throw new RuntimeException("Fatal Error: failed to parse widget config!", e);
        }
    }

    protected void loadWidgetContent(Activity activity) {
        try {
            String contentUrl = widgetConfig.getContentSrc();
            // XXX: make absolute url only if content src is relative url
            // to load content from asset: <content
            // src="file:///android_asset/www/index.html" />
            // to load content from local web server: <content src="index.html"
            // />
            if (!contentUrl.contains("://")) {
                contentUrl =
                        "http://" + server.getHost() + ":" + server.getPort() + "/" + contentUrl;
            }
            if (widgetView.getWebView().getUrl() != null) {
                if (L.isTraceEnabled()) {
                    L.trace("@@@@@@@@@@@@ widget content is already loaded: "
                            + widgetView.getWebView().getUrl());
                }
                return;
            }

            if (L.isTraceEnabled()) {
                L.trace("load widget content: url=" + contentUrl);
            }

            widgetView.getWebView().loadUrl(contentUrl);
        }
        catch (Exception e) {
            // XXX: error handling!
            throw new RuntimeException("Fatal Error: failed to load widget content!", e);
        }
    }

    //
    //
    //

    @Override
    public void onActivityCreate(final Activity activity, Bundle savedInstanceState) {
        loadWidgetConfig(activity);

        fsManager = new FileSystemManager();
        new WaikikiFileSystems(fsManager, activity).mountFileSystems();

        axContext = new AndroidAxContext(activity, this, widgetView, widgetConfig, fsManager);

        server = WebServerFactory.newWebServer(this);

        widgetView.onActivityCreate(activity, savedInstanceState);

        pluginManager = PluginManagerFactory.newPluginManager(axContext);
        pluginManager.onActivityCreate(activity, savedInstanceState);

        server.onActivityCreate(activity, savedInstanceState);

        mountPluginFileSystem(activity, fsManager, pluginManager.getPluginLoadedOrder());

        plantAuthenticateCookie();

        super.onActivityCreate(activity, savedInstanceState);
    }

    private void plantAuthenticateCookie() {
        if (AxConfig.getAttributeAsBoolean("app.devel", false)) { return; }

        // release 모드일 때 웹뷰에 쿠키를 하나 심고, 이걸 가지고 AxRequestHandler 에서 외부 접속인지 내부
        // 접속인지를 판별한다.
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setCookie(String.format("http://%s:%d/", server.getHost(), server.getPort()),
                "AXSESSIONID=" + AxSessionKeyHolder.instance().generate() + "; path=/");
    }

    public void mountPluginFileSystem(final Activity activity, final FileSystemManager fsManager,
            final List<String> pluginLoadedOrder) {
        if (pluginLoadedOrder == null) return;

        AssetManager assetManager = activity.getAssets();
        for (String pluginId : pluginLoadedOrder) {
            fsManager.mountPlugin(assetManager, pluginId);
        }
    }

    @Override
    public void onActivityRestart(final Activity activity) {
        widgetView.onActivityRestart(activity);

        if (pluginManager != null) {
            pluginManager.onActivityRestart(activity);
        }

        if (server != null) {
            server.onActivityRestart(activity);
        }

        super.onActivityRestart(activity);
    }

    @Override
    public void onActivityStart(final Activity activity) {
        widgetView.onActivityStart(activity);

        if (pluginManager != null) {
            pluginManager.onActivityStart(activity);
        }

        if (server != null) {
            server.onActivityStart(activity);
        }

        loadWidgetContent(activity);

        super.onActivityStart(activity);
    }

    @Override
    public void onActivityResume(final Activity activity) {
        widgetView.onActivityResume(activity);

        if (pluginManager != null) {
            pluginManager.onActivityResume(activity);
        }

        if (server != null) {
            server.onActivityResume(activity);
        }

        super.onActivityResume(activity);
    }

    @Override
    public void onActivityPause(final Activity activity) {
        if (server != null) {
            server.onActivityPause(activity);
        }

        if (pluginManager != null) {
            pluginManager.onActivityPause(activity);
        }

        widgetView.onActivityPause(activity);

        super.onActivityPause(activity);
    }

    @Override
    public void onActivityStop(final Activity activity) {
        if (server != null) {
            server.onActivityStop(activity);
        }

        if (pluginManager != null) {
            pluginManager.onActivityStop(activity);
        }

        widgetView.onActivityStop(activity);

        super.onActivityStop(activity);
    }

    @Override
    public void onActivityDestroy(final Activity activity) {
        super.onActivityDestroy(activity);

        if (server != null) {
            server.onActivityDestroy(activity);
            server = null;
        }

        if (pluginManager != null) {
            pluginManager.onActivityDestroy(activity);
            pluginManager = null;
        }

        widgetView.onActivityDestroy(activity);

        fsManager = new FileSystemManager();
        new WaikikiFileSystems(fsManager, activity).unmountFileSystems();
    }

    @Override
    public void onRestoreInstanceState(final Activity activity, Bundle savedInstanceState) {
        widgetView.onRestoreInstanceState(activity, savedInstanceState);

        if (pluginManager != null) {
            pluginManager.onRestoreInstanceState(activity, savedInstanceState);
        }

        if (server != null) {
            server.onRestoreInstanceState(activity, savedInstanceState);
        }

        super.onRestoreInstanceState(activity, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(final Activity activity, Bundle outState) {
        if (server != null) {
            server.onSaveInstanceState(activity, outState);
        }

        if (pluginManager != null) {
            pluginManager.onSaveInstanceState(activity, outState);
        }

        widgetView.onRestoreInstanceState(activity, outState);

        super.onSaveInstanceState(activity, outState);
    }

    //
    //
    //

    @Override
    public boolean onActivityResult(final Activity activity, int requestCode, int resultCode,
            Intent imageReturnedIntent) {
        if (pluginManager != null
                && pluginManager.onActivityResult(activity, requestCode, resultCode,
                        imageReturnedIntent)) { return true; }
        return super.onActivityResult(activity, requestCode, resultCode, imageReturnedIntent);
    }

    @Override
    public boolean onBackPressed(final Activity activity) {
        if (pluginManager != null && pluginManager.onBackPressed(activity)) { return true; }
        if (super.onBackPressed(activity)) { return true; }
        return widgetView.onBackPressed(activity);
    }

    @Override
    public boolean onCreateOptionsMenu(final Activity activity, Menu menu) {
        if (pluginManager != null && pluginManager.onCreateOptionsMenu(activity, menu)) { return true; }

        return super.onCreateOptionsMenu(activity, menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(final Activity activity, Menu menu) {
        if (pluginManager != null && pluginManager.onPrepareOptionsMenu(activity, menu)) { return true; }

        return super.onPrepareOptionsMenu(activity, menu);
    }

    @Override
    public boolean onOptionsItemSelected(final Activity activity, MenuItem item) {
        if (pluginManager != null && pluginManager.onOptionsItemSelected(activity, item)) { return true; }

        return super.onOptionsItemSelected(activity, item);
    }

    @Override
    public void onNewIntent(Activity activity, Intent intent) {
        if (pluginManager != null) {
            pluginManager.onNewIntent(activity, intent);
        }

        super.onNewIntent(activity, intent);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (pluginManager != null) {
            pluginManager.onWindowFocusChanged(hasFocus);
        }

        super.onWindowFocusChanged(hasFocus);
    }

}
