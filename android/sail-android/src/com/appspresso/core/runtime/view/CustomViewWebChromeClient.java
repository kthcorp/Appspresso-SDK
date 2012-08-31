package com.appspresso.core.runtime.view;

import android.R;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.webkit.WebView;

import com.appspresso.api.view.WebChromeClientAdapter;

/**
 */
public class CustomViewWebChromeClient extends WebChromeClientAdapter {
    private Bitmap poster = null;

    final private WebView webView;
    private View customView;

    private CustomViewCallback customViewCallback;

    public CustomViewWebChromeClient(WebView webView) {
        super();

        this.webView = webView;
    }

    @Override
    public Bitmap getDefaultVideoPoster() {
        if (poster == null) {
            poster = BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.ic_media_play);
        }
        return poster;
    }

    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {
        // if a view already exists then immediately terminate the new one
        if (customView != null) {
            callback.onCustomViewHidden();
            return;
        }

        ViewParent parent = this.webView.getParent();
        if (parent instanceof ViewGroup) {
            customView = view;
            customViewCallback = callback;

            customView.setBackgroundColor(Color.BLACK);
            ((ViewGroup) parent).addView(customView, new LayoutParams(LayoutParams.FILL_PARENT,
                    LayoutParams.FILL_PARENT));
        }
    }

    @Override
    public void onHideCustomView() {
        hideCustomView();
    }

    // ///////////////////////////////////////////////////////////////////////////
    // ActivityListener
    public boolean onBackPressed(Activity activity) {
        if (customView == null && customViewCallback == null) { return false; }

        hideCustomView();

        return true;
    }

    private void hideCustomView() {
        if (customViewCallback != null) customViewCallback.onCustomViewHidden();

        ViewParent parent = this.webView.getParent();
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeView(customView);
            customView = null;
            customViewCallback = null;
        }
    }

}
