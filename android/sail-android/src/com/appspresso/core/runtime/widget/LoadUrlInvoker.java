package com.appspresso.core.runtime.widget;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.logging.Log;

import android.webkit.WebView;

import com.appspresso.api.AxError;
import com.appspresso.api.AxLog;

@SuppressWarnings({"rawtypes", "unchecked"})
class LoadUrlInvoker {
    @SuppressWarnings("unused")
    private final static Log L = AxLog.getLog("LoadUrlInvoker");

    private static Field coreField;
    private static Constructor urlDataConstructor;
    private static Field urlDataUrlField;
    private static Method coreSendMessage;
    private static int LOAD_URL_ID;
    private static boolean initialized = false;
    private static boolean support = false;
    private static Object mutex = new Object();
    private static boolean froyo;
    private static boolean jellyBean;
    private static Field providerField;

    private static void prepare(WebView webView) throws SecurityException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException, NoSuchMethodException {
        initialized = true;

        extractLoadUrlMessageId();
        prepareUrlDataClass();

        Class webViewClass = getWebViewClassConsideringJellyBean(webView);

        coreField = webViewClass.getDeclaredField("mWebViewCore");
        coreField.setAccessible(true);

        Class coreClass = getWebViewCoreInstance(webView).getClass();
        coreSendMessage = coreClass.getDeclaredMethod("sendMessage", Integer.TYPE, Object.class);
        coreSendMessage.setAccessible(true);

        support = true;
    }

    private static Class getWebViewClassConsideringJellyBean(WebView webView)
            throws IllegalArgumentException, IllegalAccessException {
        Class webViewClass = WebView.class;
        try {
            providerField = webViewClass.getDeclaredField("mProvider");
            providerField.setAccessible(true);
            Object classicWebView = providerField.get(webView);
            webViewClass = classicWebView.getClass();
            jellyBean = true;
        }
        catch (NoSuchFieldException ignore) {
            // not jelly bean ;)
        }
        return webViewClass;
    }

    private static void prepareUrlDataClass() throws NoSuchMethodException, NoSuchFieldException {
        try {
            Class urlDataClass = Class.forName("android.webkit.WebViewCore$GetUrlData");
            urlDataConstructor = urlDataClass.getDeclaredConstructor();
            urlDataConstructor.setAccessible(true);

            urlDataUrlField = urlDataClass.getDeclaredField("mUrl");
            urlDataUrlField.setAccessible(true);
        }
        catch (ClassNotFoundException e) {
            froyo = true;
        }
    }

    private static void extractLoadUrlMessageId() throws NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException {
        try {
            Class eventHubClass = Class.forName("android.webkit.WebViewCore$EventHub");
            Field f = eventHubClass.getDeclaredField("LOAD_URL");
            f.setAccessible(true);
            LOAD_URL_ID = f.getInt(null);
        }
        catch (ClassNotFoundException e) {
            throw new AxError(AxError.NOT_SUPPORTED_ERR,
                    "Cannot find 'EventHub' class declaration from WebViewCore");
        }
    }

    private static Object getWebViewCoreInstance(WebView webView) throws IllegalArgumentException,
            IllegalAccessException {
        Object wv = webView;
        if (jellyBean) {
            wv = providerField.get(webView);
        }

        return coreField.get(wv);
    }

    public static void invoke(WebView webView, String location) throws IllegalArgumentException,
            InstantiationException, IllegalAccessException, InvocationTargetException,
            SecurityException, NoSuchFieldException, NoSuchMethodException, ClassNotFoundException {

        synchronized (mutex) {
            if (!initialized) prepare(webView);
        }

        if (!support)
            throw new AxError(AxError.NOT_SUPPORTED_ERR, "WebViewCore private API was not usable");

        if (froyo) {
            coreSendMessage.invoke(getWebViewCoreInstance(webView), LOAD_URL_ID, location);
            return;
        }

        Object urlData = urlDataConstructor.newInstance();
        urlDataUrlField.set(urlData, location);
        coreSendMessage.invoke(getWebViewCoreInstance(webView), LOAD_URL_ID, urlData);
    }
}
