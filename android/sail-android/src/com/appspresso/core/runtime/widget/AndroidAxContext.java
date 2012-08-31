package com.appspresso.core.runtime.widget;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import android.app.Activity;
import android.webkit.WebView;

import com.appspresso.api.AxErrorHandler;
import com.appspresso.api.AxLog;
import com.appspresso.api.AxPlugin;
import com.appspresso.api.AxRuntimeContext;
import com.appspresso.api.activity.ActivityListener;
import com.appspresso.api.fs.AxFileSystemManager;
import com.appspresso.api.view.WebChromeClientListener;
import com.appspresso.api.view.WebViewClientListener;
import com.appspresso.api.view.WebViewListener;
import com.appspresso.core.runtime.filesystem.FileSystemManager;
import com.appspresso.core.runtime.util.JsonUtil;
import com.appspresso.core.runtime.view.WidgetView;
import com.appspresso.w3.Feature;
import com.appspresso.w3.Widget;
import com.appspresso.w3.widget.DefaultWidgetConfig;

class AndroidAxContext implements AxRuntimeContext {

    private final static Log L = AxLog.getLog("AxContext");

    private final Activity activity;
    private final DefaultWidgetAgent defaultWidgetAgent;
    private final DefaultWidgetConfig config;

    private final WidgetView widgetView;

    private final FileSystemManager fsManager;

    private final Feature[] features;

    Map<String, Object> attributes = new HashMap<String, Object>();

    AndroidAxContext(Activity activtiy, DefaultWidgetAgent defaultWidgetAgent,
            WidgetView widgetView, DefaultWidgetConfig config, FileSystemManager fsManager) {
        this.activity = activtiy;
        this.defaultWidgetAgent = defaultWidgetAgent;
        this.widgetView = widgetView;
        this.config = config;

        this.fsManager = fsManager;

        this.features = this.config.getFeatures().toArray(new Feature[0]);
    }

    public Feature[] getActivatedFeatures() {
        return this.features;
    }

    @Override
    public Activity getActivity() {
        return this.activity;
    }

    @Override
    public WebView getWebView() {
        return widgetView.getWebView();
    }

    @Override
    public Widget getWidget() {
        return this.config;
    }

    @Override
    public void executeJavaScript(final String script) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getWebView().loadUrl("javascript:" + script);
            }
        });
    }

    @Override
    public void invokeJavaScriptFunction(String functionName, Object... args) {
        executeJavaScript(makeInvokeFunctionString(functionName, args));
    }

    @Override
    public void executeJavaScript(final AxErrorHandler onError, final String script) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    LoadUrlInvoker.invoke(getWebView(), "javascript:" + script);
                }
                catch (Exception e) {
                    if (onError != null) {
                        onError.onError(e);
                    }
                }
            }
        });
    }

    @Override
    public void invokeJavaScriptFunction(AxErrorHandler onError, String functionName,
            Object... args) {
        executeJavaScript(onError, makeInvokeFunctionString(functionName, args));
    }

    private String makeInvokeFunctionString(String functionName, Object[] args) {
        StringBuilder script = new StringBuilder(200);
        script.append(functionName).append("(");

        String arguments = JsonUtil.toJson(args);
        script.append(arguments.substring(1, arguments.length() - 1));
        script.append(')');
        return script.toString();
    }

    final static String INVOKE_SUCCESS_METHOD_NAME = "ax.bridge.invokeWatchSuccessListener";
    final static String INVOKE_ERROR_METHOD_NAME = "ax.bridge.invokeWatchErrorListener";

    @Override
    public synchronized void invokeWatchSuccessListener(long id, Object value) {
        Map<String, Object> returnValue = new HashMap<String, Object>(1);
        returnValue.put("result", value);

        executeJavaScript(makeInvokeWatchString(INVOKE_SUCCESS_METHOD_NAME, id, returnValue));
    }

    @Override
    public synchronized void invokeWatchErrorListener(long id, int code, String message) {
        Map<String, Object> errorObject = new HashMap<String, Object>(2);
        errorObject.put("code", code);
        errorObject.put("message", message);

        Map<String, Object> returnValue = new HashMap<String, Object>(1);
        returnValue.put("error", errorObject);

        executeJavaScript(makeInvokeWatchString(INVOKE_ERROR_METHOD_NAME, id, returnValue));
    }

    private String makeInvokeWatchString(String method, long id, Object value) {
        StringBuilder script = new StringBuilder(method + "(");
        script.append(String.valueOf(id));

        script.append(",");
        script.append(JsonUtil.toJson(value));
        script.append(")");

        return script.toString();
    }

    @Override
    public void addWebViewListener(WebViewListener l) {
        widgetView.addWebViewListener(l);
    }

    @Override
    public void removeWebViewListener(WebViewListener l) {
        widgetView.removeWebViewListener(l);
    }

    @Override
    public void addWebChromeClientListener(WebChromeClientListener l) {
        widgetView.addWebChromeClientListener(l);
    }

    @Override
    public void removeWebChromeClientListener(WebChromeClientListener l) {
        widgetView.removeWebChromeClientListener(l);
    }

    @Override
    public void addWebViewClientListener(WebViewClientListener l) {
        widgetView.addWebViewClientListener(l);
    }

    @Override
    public void removeWebViewClientListener(WebViewClientListener l) {
        widgetView.removeWebViewClientListener(l);
    }

    @Override
    public void addActivityListener(ActivityListener l) {
        defaultWidgetAgent.addActivityListener(l);
    }

    @Override
    public void removeActivityListener(ActivityListener l) {
        defaultWidgetAgent.removeActivityListener(l);
    }

    @Override
    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    @Override
    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    @Override
    public void removeAttribute(String key) {
        attributes.remove(key);
    }

    @Override
    public AxPlugin requirePlugin(String pluginId) {
        return defaultWidgetAgent.getPluginManager().requirePlugin(pluginId);
    }

    @Override
    public AxPlugin requirePluginWithFeature(String featureUri) {
        return defaultWidgetAgent.getPluginManager().requirePluginWithFeature(featureUri);
    }

    @Override
    public boolean isActivatedFeature(String featureUri) {
        if (featureUri == null) { return false; }

        for (Feature feature : this.features) {
            if (featureUri.equals(feature.getUri())) { return true; }
        }

        return false;
    }

    @Override
    public AxFileSystemManager getFileSystemManager() {
        return this.fsManager;
    }

}
