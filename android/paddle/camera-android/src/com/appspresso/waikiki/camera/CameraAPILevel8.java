package com.appspresso.waikiki.camera;

import java.io.IOException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.view.SurfaceHolder;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.appspresso.api.AxRuntimeContext;

public class CameraAPILevel8 extends CameraAPILevel7 {

    public CameraAPILevel8(AxRuntimeContext runtimeContext) {
        super(runtimeContext);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (isPreviewing) {
            this.holder = holder;
            Camera camera = getNativeCamera();
            try {
                int rotation =
                        runtimeContext.getActivity().getWindowManager().getDefaultDisplay()
                                .getRotation();
                Parameters params = camera.getParameters();
                params.setRotation(rotation);

                // camera.setParameters(params);
                camera.setDisplayOrientation((360 - (rotation * 90) + 90) % 360);

                camera.setPreviewDisplay(holder);
                camera.startPreview();
                isPreviewing = true;
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void attachPreviewLayer(int w, int h, int x, int y) {
        Size previewSize = getNativeCamera().getParameters().getPreviewSize();
        this.previewLayer = new PreviewLayer(runtimeContext.getActivity(), this, previewSize);
        this.previewLayer.setPreviewLayout(w, h, x, y);
        WebView webView = runtimeContext.getWebView();
        ((ViewGroup) webView.getParent()).addView(previewLayer, 0);
    }

    @Override
    protected void detachPreviewLayer() {
        WebView webView = runtimeContext.getWebView();
        ((ViewGroup) webView.getParent()).removeView(this.previewLayer);
        this.previewLayer = null;
    }

    BroadcastReceiver rotationBroadcastReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_CONFIGURATION_CHANGED.equals(intent.getAction())) {
                int rotation =
                        runtimeContext.getActivity().getWindowManager().getDefaultDisplay()
                                .getRotation();

                // camera.stopPreview();
                // camera.setDisplayOrientation((360 - (rotation * 90) + 90) %
                // 360);
                // camera.startPreview();
            }
        }
    };

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        super.surfaceCreated(holder);
        runtimeContext.getActivity().registerReceiver(rotationBroadcastReciever,
                new IntentFilter(Intent.ACTION_CONFIGURATION_CHANGED));
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
        runtimeContext.getActivity().unregisterReceiver(rotationBroadcastReciever);
    }

}
