package com.appspresso.screw.ui;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebStorage.QuotaUpdater;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.appspresso.api.AxError;
import com.appspresso.api.AxLog;
import com.appspresso.api.AxRuntimeContext;

/**
 * Singletone Class 로 구현
 * 
 * 
 */
public class WebViewManager {
    static private final Log L = AxLog.getLog(WebViewManager.class);

    static private WebViewManager instance;
    static private final String JS_CALLBACK_WEBVIEW_ONSTART = "ax.ext.ui.onStart";
    static private final String JS_CALLBACK_WEBVIEW_ONFINISH = "ax.ext.ui.onFinish";
    static private final String JS_CALLBACK_WEBVIEW_ONERROR = "ax.ext.ui.onError";
    static private final String JS_CALLBACK_WEBVIEW_ONLOAD = "ax.ext.ui.onLoad";

    // TODO: althjs. 여러 웹뷰가 추가될것을 대비해 handle을 추가. 당장은 static으로 처리. 추후 생성자에 동적으로
    // handle 추가가 필요하며 그러려면 Singleton 구현자체를 바꿔야함.
    static private final String JS_CALLBACK_PARAM_HANDLE = "_webview";

    private WebView parent;
    private Map<String, WebView> map = new HashMap<String, WebView>();

    AxRuntimeContext context = null;

    // XXX Mungyu. Singleton? 앱이 재시작(Back button -> 앱 실행)을 할 때 static 변수는 살아 있을
    // 수 있다.
    // 하지만, Activity, WebView의 인스턴스는 달라지기 때문에 WebView를 캐시하는 것은 충분히 문제가 될 수 있다.
    static private String childId = "childWebView";
    static {
        instance = new WebViewManager();
    }

    private WebViewManager() {
        parent = null;
    }

    public static WebViewManager getInstance() {
        return instance;
    }

    public String getHandle() {
        return JS_CALLBACK_PARAM_HANDLE;
    }

    public void addWebView(final AxRuntimeContext context, final String url, final int top,
            final int left, final int width, final int height, final int start, final int finish,
            final int error, final int load, final String zoom) {

        // @@Mungyu. When application restart, webview instance change.
        if (parent == null || parent != context.getWebView()) {
            parent = context.getWebView();
        }

        if (map.containsValue(childId)) { throw new AxError(AxError.INVALID_ACCESS_ERR,
                "No more Web View"); }

        this.context = context;
        this.context.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                try {
                    final float scale = context.getWebView().getScale();
                    final int t = (int) Math.round(scale * top);
                    final int l = (int) Math.round(scale * left);
                    final int w = (int) Math.round(scale * width);
                    final int h = (int) Math.round(scale * height);

                    WebView childWebView = new WebView(context.getActivity());
                    childWebView.setWebChromeClient(new UiWebChromeClient());
                    childWebView.setWebViewClient(new UiWebViewClient(context.getActivity(),
                            parent, start, finish, error, load));

                    childWebView.setScrollBarStyle(android.view.View.SCROLLBARS_INSIDE_OVERLAY);
                    // parent webview 와 같은 Scale 적용
                    childWebView.setInitialScale((int) (scale * 100.0f));
                    childWebView.getSettings().setJavaScriptEnabled(true);
                    childWebView.getSettings().setSavePassword(false);

                    // WebView에서 ZoomDensity기능을 설정 가능하도록 옵션 추가
                    ZoomDensity zoomDensity = null;
                    try {
                        zoomDensity = ZoomDensity.valueOf(zoom);
                    }
                    catch (Exception e) {
                        zoomDensity = context.getWebView().getSettings().getDefaultZoom();
                    }
                    childWebView.getSettings().setDefaultZoom(zoomDensity);

                    RelativeLayout cView = new RelativeLayout(context.getActivity());
                    RelativeLayout.LayoutParams lParam = new RelativeLayout.LayoutParams(w, h);
                    lParam.topMargin = t;
                    lParam.leftMargin = l;

                    cView.addView(childWebView, lParam);
                    FrameLayout.LayoutParams params =
                            new FrameLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                                    ViewGroup.LayoutParams.FILL_PARENT);

                    ((FrameLayout) context.getWebView().getParent()).addView(cView, params);
                    childWebView.requestFocus(android.view.View.FOCUS_DOWN);
                    childWebView.loadUrl(url);
                    map.put(childId, childWebView);
                }
                catch (Exception e) {
                    if (L.isWarnEnabled()) {
                        L.warn("Exception in addWebView ==> " + e);
                    }
                }
            }
        });
    }

    public void removeWebView(final AxRuntimeContext context) {
        this.context.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                try {
                    WebView childWebView = (WebView) map.get(childId);
                    ViewGroup r = (ViewGroup) childWebView.getParent();
                    r.removeView(childWebView);
                    ((ViewGroup) context.getWebView().getParent()).removeView(r);

                    map.remove(childId);
                }
                catch (Exception e) {
                    if (L.isWarnEnabled()) {
                        L.warn(e);
                    }
                }
            }
        });
        this.context = null;
    }

    public class UiWebViewClient extends WebViewClient {
        Activity act;
        WebView parent;
        int start;
        int finish;
        int error;
        int load;

        public UiWebViewClient(Activity act, WebView parent, int start, int finish, int error,
                int load) {
            this.act = act;
            this.parent = parent;
            this.start = start;
            this.finish = finish;
            this.error = error;
            this.load = load;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if (start != -1) {
                if (WebViewManager.this.context == null) {
                    if (L.isWarnEnabled()) {
                        L.warn("onPageStarted ==> context is null");
                    }
                    super.onPageStarted(view, url, favicon);
                    return;
                }

                WebViewManager.this.context.invokeJavaScriptFunction(
                        WebViewManager.JS_CALLBACK_WEBVIEW_ONSTART, JS_CALLBACK_PARAM_HANDLE, url,
                        start);
            }
            else {
                super.onPageStarted(view, url, favicon);
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (finish != -1) {
                if (WebViewManager.this.context == null) {
                    if (L.isWarnEnabled()) {
                        L.warn("onPageFinished ==> context is null");
                    }
                    super.onPageFinished(view, url);
                    return;
                }

                WebViewManager.this.context.invokeJavaScriptFunction(
                        WebViewManager.JS_CALLBACK_WEBVIEW_ONFINISH, JS_CALLBACK_PARAM_HANDLE, url,
                        finish);
            }
            else {
                super.onPageFinished(view, url);
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith("tel:")) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                act.startActivity(intent);
                return true;
            }
            else if (url.startsWith("market:")) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                act.startActivity(intent);
                return true;
            }

            if (WebViewManager.this.context == null) {
                if (L.isWarnEnabled()) {
                    L.warn("shouldOverrideUrlLoading ==> context is null");
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
            else if (load != -1) {
                WebViewManager.this.context.invokeJavaScriptFunction(
                        WebViewManager.JS_CALLBACK_WEBVIEW_ONLOAD, JS_CALLBACK_PARAM_HANDLE, url,
                        load);
                if (url.indexOf(".") == -1) { return false; }
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description,
                String failingUrl) {
            if (error != -1) {
                if (WebViewManager.this.context == null) {
                    if (L.isWarnEnabled()) {
                        L.warn("onReceivedError ==> context is null");
                    }
                    super.onReceivedError(view, errorCode, description, failingUrl);
                    return;
                }
                WebViewManager.this.context.invokeJavaScriptFunction(
                        WebViewManager.JS_CALLBACK_WEBVIEW_ONERROR, JS_CALLBACK_PARAM_HANDLE,
                        failingUrl, description, errorCode);
            }
            else {
                super.onReceivedError(view, errorCode, description, failingUrl);
            }
        }
    }

    public static class UiWebChromeClient extends WebChromeClient {
        @Override
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setIcon(android.R.drawable.ic_dialog_info);
            builder.setCancelable(false);
            builder.setMessage(message);
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    result.confirm();
                }
            });
            builder.create().show();
            return true;
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setIcon(android.R.drawable.ic_dialog_info);
            builder.setCancelable(false);
            builder.setMessage(message);
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    result.confirm();
                }
            });
            builder.setNegativeButton(android.R.string.cancel,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            result.cancel();
                        }
                    });
            builder.create().show();
            return true;
        }

        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue,
                final JsPromptResult result) {

            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setIcon(android.R.drawable.ic_dialog_info);
            builder.setCancelable(false);
            builder.setMessage(message);

            final EditText editText = new EditText(view.getContext());

            if (!TextUtils.isEmpty(defaultValue)) {
                editText.setText(defaultValue);
            }

            builder.setView(editText);
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    result.confirm(editText.getText().toString());
                }
            });

            builder.setNegativeButton(android.R.string.cancel,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            result.cancel();
                        }
                    });
            return true;
        }

        @Override
        public void onExceededDatabaseQuota(String url, String databaseIdentifier,
                long currentQuota, long estimatedSize, long totalUsedQuota,
                QuotaUpdater quotaUpdater) {
            super.onExceededDatabaseQuota(url, databaseIdentifier, currentQuota, estimatedSize,
                    totalUsedQuota, quotaUpdater);
            quotaUpdater.updateQuota(estimatedSize * 2);
        }
    }
}
