/*
 * Appspresso
 *
 * Copyright (c) 2011 KT Hitel Corp.
 *
 * Appspresso SDK may be freely distributed under the MIT license.
 */
package com.appspresso.api.view;

import android.graphics.Bitmap;
import android.os.Message;
import android.view.View;
import android.webkit.*;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * 모든 이벤트 처리를 여러 개의 구현체에게 위임하는 {@link WebChromeClientListener} 구현체.
 * 
 * @version 1.0
 * @see http://en.wikipedia.org/wiki/Composite_pattern
 */
public class MulticastWebChromeClientListener
        implements
            WebChromeClientListener,
            Iterable<WebChromeClientListener> {

    private final List<WebChromeClientListener> listeners =
            new LinkedList<WebChromeClientListener>();

    public MulticastWebChromeClientListener() {}

    /**
     * 리스너 추가.
     * 
     * @param l 리스너
     */
    public void addListener(WebChromeClientListener l) {
        this.listeners.add(l);
    }

    /**
     * 리스너 삭제.
     * 
     * @param l 리스너
     */
    public void removeListener(WebChromeClientListener l) {
        this.listeners.remove(l);
    }

    //
    // implements Iterable<WebChromeClientListener>
    //

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<WebChromeClientListener> iterator() {
        return listeners.iterator();
    }

    //
    // implements WebChromeClientListener
    //

    /**
     * {@inheritDoc}
     */
    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        for (WebChromeClientListener l : listeners) {
            l.onProgressChanged(view, newProgress);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReceivedTitle(WebView view, String title) {
        for (WebChromeClientListener l : listeners) {
            l.onReceivedTitle(view, title);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReceivedIcon(WebView view, Bitmap icon) {
        for (WebChromeClientListener l : listeners) {
            l.onReceivedIcon(view, icon);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReceivedTouchIconUrl(WebView view, String url, boolean precomposed) {
        for (WebChromeClientListener l : listeners) {
            l.onReceivedTouchIconUrl(view, url, precomposed);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback) {
        for (WebChromeClientListener l : listeners) {
            l.onShowCustomView(view, callback);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onHideCustomView() {
        for (WebChromeClientListener l : listeners) {
            l.onHideCustomView();
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * 최초로 true를 리턴하는 리스너에서 이벤트 처리가 중지됨.
     */
    @Override
    public boolean onCreateWindow(WebView view, boolean dialog, boolean userGesture,
            Message resultMsg) {
        for (WebChromeClientListener l : listeners) {
            l.onHideCustomView();
            if (l.onCreateWindow(view, dialog, userGesture, resultMsg)) { return true; }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onRequestFocus(WebView view) {
        for (WebChromeClientListener l : listeners) {
            l.onRequestFocus(view);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCloseWindow(WebView window) {
        for (WebChromeClientListener l : listeners) {
            l.onCloseWindow(window);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * 최초로 true를 리턴하는 리스너에서 이벤트 처리가 중지됨.
     */
    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        for (WebChromeClientListener l : listeners) {
            if (l.onJsAlert(view, url, message, result)) { return true; }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * <p>
     * 최초로 true를 리턴하는 리스너에서 이벤트 처리가 중지됨.
     */
    @Override
    public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
        for (WebChromeClientListener l : listeners) {
            if (l.onJsConfirm(view, url, message, result)) { return true; }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * <p>
     * 최초로 true를 리턴하는 리스너에서 이벤트 처리가 중지됨.
     */
    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue,
            JsPromptResult result) {
        for (WebChromeClientListener l : listeners) {
            if (l.onJsPrompt(view, url, message, defaultValue, result)) { return true; }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * <p>
     * 최초로 true를 리턴하는 리스너에서 이벤트 처리가 중지됨.
     */
    @Override
    public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
        for (WebChromeClientListener l : listeners) {
            if (l.onJsBeforeUnload(view, url, message, result)) { return true; }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onExceededDatabaseQuota(String url, String databaseIdentifier, long currentQuota,
            long estimatedSize, long totalUsedQuota, WebStorage.QuotaUpdater quotaUpdater) {
        for (WebChromeClientListener l : listeners) {
            l.onExceededDatabaseQuota(url, databaseIdentifier, currentQuota, estimatedSize,
                    totalUsedQuota, quotaUpdater);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReachedMaxAppCacheSize(long spaceNeeded, long totalUsedQuota,
            WebStorage.QuotaUpdater quotaUpdater) {
        for (WebChromeClientListener l : listeners) {
            l.onReachedMaxAppCacheSize(spaceNeeded, totalUsedQuota, quotaUpdater);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onGeolocationPermissionsShowPrompt(String origin,
            GeolocationPermissions.Callback callback) {
        for (WebChromeClientListener l : listeners) {
            l.onGeolocationPermissionsShowPrompt(origin, callback);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onGeolocationPermissionsHidePrompt() {
        for (WebChromeClientListener l : listeners) {
            l.onGeolocationPermissionsHidePrompt();
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * 최초로 true를 리턴하는 리스너에서 이벤트 처리가 중지됨.
     */
    @Override
    public boolean onJsTimeout() {
        for (WebChromeClientListener l : listeners) {
            if (l.onJsTimeout()) { return true; }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onConsoleMessage(String message, int lineNumber, String sourceID) {
        for (WebChromeClientListener l : listeners) {
            l.onConsoleMessage(message, lineNumber, sourceID);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * 최초로 true를 리턴하는 리스너에서 이벤트 처리가 중지됨.
     */
    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        for (WebChromeClientListener l : listeners) {
            if (l.onConsoleMessage(consoleMessage)) { return true; }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * <p>
     * 최초로 null이 아닌 결과를 리턴하는 리스너에서 이벤트 처리가 중지됨.
     */
    @Override
    public Bitmap getDefaultVideoPoster() {
        Bitmap poster = null;
        for (WebChromeClientListener l : listeners) {
            if ((poster = l.getDefaultVideoPoster()) != null) {
                break;
            }
        }
        return poster;
    }

    /**
     * {@inheritDoc}
     * <p>
     * 최초로 null이 아닌 결과를 리턴하는 리스너에서 이벤트 처리가 중지됨.
     */
    @Override
    public View getVideoLoadingProgressView() {
        View progress = null;
        for (WebChromeClientListener l : listeners) {
            if ((progress = l.getVideoLoadingProgressView()) != null) {
                break;
            }
        }
        return progress;
    }
}
