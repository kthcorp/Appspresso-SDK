package com.appspresso.waikiki.camera;

import org.apache.commons.logging.Log;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Camera.Size;
import android.os.Handler;
import android.os.Looper;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AbsoluteLayout;

import com.appspresso.api.AxLog;
import com.appspresso.api.view.WebViewAdapter;
import com.appspresso.api.view.WebViewListener;

@SuppressWarnings("deprecation")
public class PreviewLayer extends AbsoluteLayout {
    private Log Log = AxLog.getLog(PreviewLayer.class.getSimpleName());

    private PreviewArea pvArea;
    private Handler scrollHandler;

    // preview position
    private int left;
    private int top;

    private Size previewSize;

    public PreviewLayer(Context context, SurfaceHolder.Callback callback, Size previewSize) {
        super(context);

        this.setBackgroundColor(Color.TRANSPARENT);
        this.scrollHandler = new Handler(Looper.getMainLooper());
        this.previewSize = previewSize;
        this.pvArea = new PreviewArea(context, callback);
        addView(this.pvArea);
    }

    public static class PreviewArea extends SurfaceView {

        public PreviewArea(Context context, SurfaceHolder.Callback callback) {
            super(context);

            SurfaceHolder holder = getHolder();
            holder.addCallback(callback);
            holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
    }

    private void optimize(int w, int h, int x, int y, Size cameraPreviewSize,
            AbsoluteLayout.LayoutParams out) {
        double wRate = (double) cameraPreviewSize.width / w;
        double hRate = (double) cameraPreviewSize.height / h;

        if (wRate - 1 < hRate - 1) {
            out.height = h;
            out.y = y;
            out.width = (int) (cameraPreviewSize.width / hRate);
            out.x = (int) (x + (w / 2.0) - (out.width / 2.0));
        }
        else {
            out.width = w;
            out.x = x;
            out.height = (int) (cameraPreviewSize.height / wRate);
            out.y = (int) (y + (h / 2.0) - (out.height / 2.0));
        }
    }

    public void setPreviewLayout(int w, int h, int x, int y) {
        ViewGroup.LayoutParams p = pvArea.getLayoutParams();
        if (p == null || !(p instanceof AbsoluteLayout.LayoutParams)) {
            p = new AbsoluteLayout.LayoutParams(0, 0, 0, 0);
        }
        final AbsoluteLayout.LayoutParams params = (AbsoluteLayout.LayoutParams) p;
        optimize(w, h, x, y, previewSize, params);

        if (Log.isTraceEnabled()) {
            Log.trace("----------------------------------------------------------");
            Log.trace("    DOM : " + w + " " + h + " " + x + " " + y);
            Log.trace("Preview : " + previewSize.width + " " + previewSize.height);
            Log.trace(" Params : " + params.width + " " + params.height + " " + params.x + " "
                    + params.y);
        }

        left = params.x;
        top = params.y;

        scrollHandler.postAtFrontOfQueue(new Runnable() {
            @Override
            public void run() {
                if (Log.isTraceEnabled()) {
                    Log.trace(" post : " + params.width + " " + params.height + " " + params.x
                            + " " + params.y);
                }
                pvArea.setLayoutParams(params);
                invalidate();
            }
        });
    }

    WebViewListener scrollhandler = new PreviewScrollHandler();

    class PreviewScrollHandler extends WebViewAdapter {

        @Override
        public void invalidate(WebView webView) {
            final AbsoluteLayout.LayoutParams p =
                    (AbsoluteLayout.LayoutParams) pvArea.getLayoutParams();

            int x = left - webView.getScrollX();
            int y = top - webView.getScrollY();
            if (x != p.x || y != p.y) {
                p.x = left - webView.getScrollX();
                p.y = top - webView.getScrollY();
                if (Log.isTraceEnabled()) {
                    Log.trace("Invalidate : " + p.width + " " + p.height + " " + p.x + " " + p.y);
                }

                pvArea.setLayoutParams(p);
            }
        }
    }

    WebViewListener getScrollHandler() {
        return scrollhandler;
    }
}
