package com.appspresso.core.runtime.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.appspresso.core.runtime.util.SplashHelper;
import com.appspresso.core.runtime.view.AlertDialogWebChromeClientListener;
import com.appspresso.core.runtime.view.CustomSchemeWebViewClientListener;
import com.appspresso.core.runtime.view.CustomViewWebChromeClient;
import com.appspresso.core.runtime.view.GalaxyWorkaroundWebViewClientListener;
import com.appspresso.core.runtime.view.RpcPollStarterWebViewClientListener;
import com.appspresso.core.runtime.view.WebSQLDatabaseWebChromeClientListener;
import com.appspresso.core.runtime.view.WidgetLogForSDKWebChromeClient;
import com.appspresso.core.runtime.view.WidgetView;
import com.appspresso.core.runtime.view.WidgetViewFactory;
import com.appspresso.core.runtime.widget.WidgetAgent;
import com.appspresso.core.runtime.widget.WidgetAgentFactory;
import com.appspresso.internal.AxConfig;

/**
 * This class extends {@link Activity} and delegate all activity events to the containing
 * {@link WidgetAgent}.
 * 
 */
public abstract class WidgetActivity extends Activity {

    private WidgetAgent widgetAgent;

    private CustomViewWebChromeClient customViewWebChromeClient;

    public WidgetActivity() {}

    public WidgetAgent getWidgetAgent() {
        return widgetAgent;
    }

    //
    //
    //

    private final boolean NEEDS_RESUME_TIMERS = ("SHW-M110S".equals(Build.MODEL)
            || "SHW-M180S".equals(Build.MODEL) || "SHW-M130K".equals(Build.MODEL));

    // XXX: optimus-X needs web console workaround
    // private final boolean NEEDS_WEB_CONSOLE = "LU2300".equals(Build.MODEL);

    /**
     * 위젯 뷰를 포함하는 컨텐츠 뷰 생성.
     * 
     * @param widgetView
     * @return
     */
    protected View createContentView(WidgetView widgetView) {
        // 스플래시가 있으면... 스플래시를 포함한 컨텐츠 뷰를 준비~
        View contentView = SplashHelper.createContentViewWithSplash(this, widgetView);
        if (contentView == null) {
            // 스플래시가 없으면... WebView로 화면 전체를 가득 채우자~
            contentView = new FrameLayout(this);
            ((FrameLayout) contentView).addView(widgetView.getWebView());
        }
        // contentView.setBackgroundColor(Color.WHITE);
        return contentView;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT > 9) {
            honeycombDeviceExceptionWorkaround();
        }

        WidgetView widgetView = WidgetViewFactory.newInstance().newWidgetView(this);
        if (isClearCache()) {
            widgetView.getWebView().clearCache(true);
        }

        // XXX: device specific workarounds!
        if (NEEDS_RESUME_TIMERS) {
            widgetView.addWebViewClientListener(new GalaxyWorkaroundWebViewClientListener());
        }
        // if (NEEDS_WEB_CONSOLE) {
        // widgetView.addWebChromeClientListener(new
        // WebConsoleWebChromeClientListener());
        // }
        widgetView.addWebChromeClientListener(new AlertDialogWebChromeClientListener());
        widgetView.addWebChromeClientListener(new WebSQLDatabaseWebChromeClientListener());
        widgetView.addWebViewClientListener(new CustomSchemeWebViewClientListener(this));

        setContentView(createContentView(widgetView));

        widgetAgent = WidgetAgentFactory.newInstance().newWidgetAgent(widgetView);

        widgetAgent.onActivityCreate(this, savedInstanceState);

        // XXX after parse config.xml(IWidgetAgent.onActivityCreate)
        if (AxConfig.getAttribute("nessie.project") == null) {
            String widgetId = widgetAgent.getWidget().getId();
            widgetView.addWebChromeClientListener(new WidgetLogForSDKWebChromeClient(widgetId));
        }

        customViewWebChromeClient = new CustomViewWebChromeClient(widgetView.getWebView());
        widgetView.addWebChromeClientListener(customViewWebChromeClient);
        widgetView.addWebViewClientListener(new RpcPollStarterWebViewClientListener(widgetAgent
                .getAxRuntimeContext()));
    }

    private boolean isClearCache() {
        boolean cacheEnable = AxConfig.getAttributeAsBoolean("webview.cache.enable", true);
        boolean isDevel = AxConfig.getAttributeAsBoolean("app.devel", false);

        if (isDevel || !cacheEnable) { return true; }

        // export
        try {
            SharedPreferences preference = getPreferences(Activity.MODE_PRIVATE);
            int oldVersionCode = preference.getInt("ax.app.versionCode", -1);

            PackageInfo pkgInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            int versionCode = pkgInfo.versionCode;

            if (oldVersionCode < versionCode) {
                Editor editor = preference.edit();
                editor.putInt("ax.app.versionCode", versionCode);
                editor.commit();
                return true;
            }
        }
        catch (NameNotFoundException ignored) {}

        return false;
    }

    private void honeycombDeviceExceptionWorkaround() {
        // XXX Mungyu. workaround... android.os.NetworkOnMainThreadException at
        // android.os.StrictMode$AndroidBlockGuardPolicy.onNetwork
        // see also...
        // http://stackoverflow.com/questions/4821845/honycomb-and-defaulthttpclient
        // http://stackoverflow.com/questions/6350614/getting-the-html-source-code-with-a-url-in-an-android-application
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
        }
    }

    @Override
    public void onRestart() {
        // Assert.assertNotNull(widgetAgent);

        super.onRestart();
        widgetAgent.onActivityRestart(this);
    }

    @Override
    public void onStart() {
        // Assert.assertNotNull(widgetAgent);

        super.onStart();
        widgetAgent.onActivityStart(this);
    }

    @Override
    public void onResume() {
        // Assert.assertNotNull(widgetAgent);

        super.onResume();
        widgetAgent.onActivityResume(this);
    }

    @Override
    public void onPause() {
        // Assert.assertNotNull(widgetAgent);

        super.onPause();
        widgetAgent.onActivityPause(this);
    }

    @Override
    public void onStop() {
        // Assert.assertNotNull(widgetAgent);

        widgetAgent.onActivityStop(this);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        // Assert.assertNotNull(widgetAgent);

        widgetAgent.onActivityDestroy(this);
        super.onDestroy();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // Assert.assertNotNull(widgetAgent);

        // XXX: by default, try to restore content view state. but it's the our
        // webview!
        // super.onRestoreInstanceState(savedInstanceState);
        widgetAgent.onRestoreInstanceState(this, savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Assert.assertNotNull(widgetAgent);

        widgetAgent.onSaveInstanceState(this, outState);
        // XXX: by default, try to restore content view state. but it's the our
        // webview!
        // super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        // Assert.assertNotNull(widgetAgent);

        if (widgetAgent.onActivityResult(this, requestCode, resultCode, imageReturnedIntent)) { return; }
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
    }

    @Override
    public void onBackPressed() {
        // Assert.assertNotNull(widgetAgent);

        // XXX Mungyu do WidgetAgent.addActivityListener(customView~~)
        // but consider what to broadcast order (plugin? widgetview?)
        // see also, DefaultWidgetAgent.onBackPressed()
        if (customViewWebChromeClient.onBackPressed(this)) { return; }

        if (widgetAgent.onBackPressed(this)) { return; }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Assert.assertNotNull(widgetAgent);

        if (widgetAgent.onCreateOptionsMenu(this, menu)) { return true; }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Assert.assertNotNull(widgetAgent);

        if (widgetAgent.onPrepareOptionsMenu(this, menu)) { return true; }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Assert.assertNotNull(widgetAgent);

        if (widgetAgent.onOptionsItemSelected(this, item)) { return true; }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // Assert.assertNotNull(widgetAgent);

        widgetAgent.onNewIntent(this, intent);

        super.onNewIntent(intent);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        widgetAgent.onWindowFocusChanged(hasFocus);

        super.onWindowFocusChanged(hasFocus);
    }

}
