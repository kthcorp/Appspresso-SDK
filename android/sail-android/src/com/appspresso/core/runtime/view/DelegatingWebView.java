package com.appspresso.core.runtime.view;

import android.content.Context;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.webkit.WebView;

import com.appspresso.api.view.WebViewListener;

/**
 * 주요 이벤트를 별도의 {@link WebViewListener} 인스턴스({@link #delegate})에게 위임하는 WebView 서브클래스.
 * <p>
 * 이 리스너로 {@link com.appspresso.api.view.MulticastWebViewListener}를 사용하면 여러 개의 리스너에게 위임하는 WebView가
 * 됨.
 * <p>
 * TODO: 이벤트(오버라이드해서 위임할 메소드) 추가/제거... 리뷰가 필요함.
 * 
 */
public class DelegatingWebView extends WebView {

    private final WebViewListener delegate;

    public DelegatingWebView(Context context, WebViewListener delegate) {
        super(context);
        if (delegate == null) { throw new IllegalArgumentException("delegate"); }
        this.delegate = delegate;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (delegate.onKeyDown(this, keyCode, event)) { return true; }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (delegate.onKeyUp(this, keyCode, event)) { return true; }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        delegate.onScrollChanged(this, l, t, oldl, oldt);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (delegate.onTouchEvent(this, ev)) { return true; }
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean onTrackballEvent(MotionEvent ev) {
        if (delegate.onTrackballEvent(this, ev)) { return true; }
        return super.onTrackballEvent(ev);
    }

    @Override
    public void invalidate() {
        super.invalidate();

        if (delegate != null) {
            delegate.invalidate(this);
        }
    }
}
