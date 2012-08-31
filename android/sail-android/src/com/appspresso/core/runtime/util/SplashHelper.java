package com.appspresso.core.runtime.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.appspresso.api.view.WebViewClientAdapter;
import com.appspresso.api.view.WebViewClientListener;
import com.appspresso.core.runtime.view.WidgetView;
import com.appspresso.internal.AxConfig;

/**
 * This class provides facility to support "splash screen" without creating a separated activity.
 */
public class SplashHelper {

    private static final String DEF_SPLASH_RES_NAME = "ax_splash";
    private static final int DEF_SPLASH_BACKGROUND_COLOR = Color.WHITE;
    // 스플래시의 최소 지속 시간(page load후에도 이 시간 동안 무조건 스플래시를 표시)
    private static final int DEF_SPLASH_DURATION_MIN = 0;
    // 스플래시의 최대 지속 시간(이 시간이 넘어가면 page load 여부에 관계없이 무조건 스플래시를 숨김)
    private static final int DEF_SPLASH_DURATION_MAX = 0;

    private static final int DEF_SPLASH_DELAY = 500;

    // NOTE: undocumented 스플래시 지속시간 설정 appspresso-config 키 이름
    private static final String CONFIG_SPLASH_DURATION_MIN = "splash.duration.min";
    private static final String CONFIG_SPLASH_DURATION_MAX = "splash.duration.max";
    private static final String CONFIG_SPLASH_DELAY = "splash.delay";

    private static final String CONFIG_SPLASH_ENABLE = "splash.enable";
    private static final String CONFIG_SPLASH_ORIENTATION = "splash.orientation";

    private final static int ORIENTATION_DEFAULT = 0;
    private final static int ORIENTATION_PORTRAIT = 1;
    private final static int ORIENTATION_LANDSCAPE = 2;
    private final static int ORIENTATION_REVERSE_PORTRAIT = 3;
    private final static int ORIENTATION_REVERSE_LANDSCAPE = 4;

    final static int WHAT_MIN_TIME = 0;
    final static int WHAT_MAX_TIME = 1;
    final static int WHAT_FINISH_TIME = 2;

    /**
     * 스플래시를 지원하기 위해 위젯 뷰를 적절함 감싸는 컨텐츠 뷰를 생성.
     * 
     * @param context 컨텍스트(아마도 액티비티)
     * @param widgetView 감쌀 위젯 뷰
     * @return 감싼 컨텐츠 뷰 또는 {@literal null}
     */
    public static View createContentViewWithSplash(final Context context,
            final WidgetView widgetView) {
        boolean splashEnable = AxConfig.getAttributeAsBoolean(CONFIG_SPLASH_ENABLE, true);
        if (!splashEnable) { return null; }

        final int splashImageResource =
                context.getResources().getIdentifier(DEF_SPLASH_RES_NAME, "drawable",
                        context.getPackageName());
        if (splashImageResource == 0) {
            // 스플래시 없음!
            return null;
        }

        final int activityOrientation = ((Activity) context).getRequestedOrientation();
        final int configSplashOrientation =
                AxConfig.getAttributeAsInteger(CONFIG_SPLASH_ORIENTATION, ORIENTATION_DEFAULT);
        if (configSplashOrientation != ORIENTATION_DEFAULT) {
            int platformSplashOrientation = getPlatformOrientation(configSplashOrientation);

            if (platformSplashOrientation != activityOrientation) {
                ((Activity) context).setRequestedOrientation(platformSplashOrientation);
            }
        }

        final int splashDurationMin =
                AxConfig.getAttributeAsInteger(CONFIG_SPLASH_DURATION_MIN, DEF_SPLASH_DURATION_MIN);
        final int splashDurationMax =
                AxConfig.getAttributeAsInteger(CONFIG_SPLASH_DURATION_MAX, DEF_SPLASH_DURATION_MAX);
        final int splashDelay =
                AxConfig.getAttributeAsInteger(CONFIG_SPLASH_DELAY, DEF_SPLASH_DELAY);

        FrameLayout.LayoutParams fillParentLayoutParams =
                new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT,
                        FrameLayout.LayoutParams.FILL_PARENT);

        widgetView.getWebView().setLayoutParams(fillParentLayoutParams);

        final FrameLayout splashBackgroundView = new FrameLayout(context);
        splashBackgroundView.setLayoutParams(fillParentLayoutParams);
        splashBackgroundView.setBackgroundColor(DEF_SPLASH_BACKGROUND_COLOR);

        final ImageView splashView = new ImageView(context);
        splashView.setScaleType(ImageView.ScaleType.FIT_XY); // FIT_CENTER or
                                                             // CENTER_INSIDE
        splashView.setImageResource(splashImageResource);
        splashView.setLayoutParams(fillParentLayoutParams);

        final FrameLayout contentView = new FrameLayout(context);
        contentView.setLayoutParams(fillParentLayoutParams);

        // stack a splashView over the original contentView
        // at this time, only the splashView is visible
        // we'll remove the splashView after the original contentView is ready
        // after that time, the original contentView is visible
        splashBackgroundView.addView(splashView);
        contentView.addView(widgetView.getWebView());
        contentView.addView(splashBackgroundView);

        final Handler handler = new Handler() {
            private boolean isPageFinished = false;

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case WHAT_MIN_TIME:
                        if (!isPageFinished) { return; }
                        break;
                    case WHAT_MAX_TIME:
                        if (isPageFinished) { return; }
                        break;
                    case WHAT_FINISH_TIME:
                        isPageFinished = true;
                        removeMessages(WHAT_MAX_TIME);
                        if (hasMessages(WHAT_MIN_TIME)) { return; }
                        break;
                }

                removeSplashImage();
            }

            private void removeSplashImage() {
                contentView.removeView(splashBackgroundView);
                ((Activity) context).setRequestedOrientation(activityOrientation);
                widgetView
                        .getWebView()
                        .loadUrl(
                                "javascript:(function(){if(ax.event.onHideSplash){ax.event.onHideSplash();}})();");
            }
        };

        // final Runnable removeSplashViewTask = new Runnable() {
        // @Override
        // public void run() {
        // if (!splashBackgroundView.isShown()) {
        // return;
        // }
        //
        // // restore titlebar/statusbar visibilities
        // //
        // activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //
        // // remove the splashView
        // // now, the original contentView below the splashView is visible
        // //splashBackgroundView.setVisibility(View.INVISIBLE);
        // contentView.removeView(splashBackgroundView);
        // // splashBackgroundView = null;
        // // splashView = null;
        //
        // // restore orientation
        // ((Activity) context).setRequestedOrientation(activityOrientation);
        //
        // widgetView.getWebView().loadUrl(
        // "javascript:(function(){if(ax.event.onHideSplash){ax.event.onHideSplash();}})();");
        // }
        // };

        // if (splashDurationMax > 0) {
        // new Handler().postDelayed(removeSplashViewTask, splashDurationMax);
        // }
        //
        // final WebViewClientListener webViewClientListener = new
        // WebViewClientAdapter() {
        // @Override
        // public void onPageFinished(WebView view, String url) {
        // if (!splashBackgroundView.isShown()) {
        // return;
        // }
        // new Handler().postDelayed(removeSplashViewTask,
        // Math.max(splashDurationMin, 0));
        // // ConcurrentModificationException???
        // // widgetView.removeWebViewClientListener(this);
        // }
        // };

        if (splashDurationMin > 0) {
            handler.sendMessageDelayed(Message.obtain(handler, WHAT_MIN_TIME), splashDurationMin);
        }

        if (splashDurationMax > 0) {
            handler.sendMessageDelayed(Message.obtain(handler, WHAT_MAX_TIME), splashDurationMax);
        }

        final WebViewClientListener webViewClientListener = new WebViewClientAdapter() {
            @Override
            public void onPageFinished(WebView view, String url) {
                handler.sendMessageDelayed(Message.obtain(handler, WHAT_FINISH_TIME),
                        splashDelay > 0 ? splashDelay : 0);
            }
        };

        // 스플래시 표시 중이면... 시작 페이지가 로드 완료되는 시점에 스플래시를 숨김
        widgetView.addWebViewClientListener(webViewClientListener);

        // save titlebar/statusbar visibilities and hide them
        // activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // in AndroidManifest.xml: <activity ...
        // android:theme="@android:style/Theme.NoTitleBar.Fullscreen">

        return contentView;
    }

    private static int getPlatformOrientation(int o) {
        int platformOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;

        if (o == ORIENTATION_REVERSE_LANDSCAPE && Build.VERSION.SDK_INT >= 9) {
            platformOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
        }
        else if (o == ORIENTATION_REVERSE_PORTRAIT && Build.VERSION.SDK_INT >= 9) {
            platformOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
        }
        else if (o == ORIENTATION_LANDSCAPE || o == ORIENTATION_REVERSE_LANDSCAPE) {
            platformOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        }
        else if (o == ORIENTATION_PORTRAIT || o == ORIENTATION_REVERSE_PORTRAIT) {
            platformOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        }

        return platformOrientation;
    }
}
