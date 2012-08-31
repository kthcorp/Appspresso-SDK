package com.appspresso.waikiki.camera;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import org.apache.commons.logging.Log;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.ViewGroup;
import android.webkit.WebView;
import com.appspresso.api.AxRuntimeContext;
import com.appspresso.api.AxError;
import com.appspresso.api.AxLog;
import com.appspresso.api.AxPluginContext;
import com.appspresso.api.AxPluginResult;
import com.appspresso.api.fs.AxFile;

class CameraAPILevel7 implements ICameraInternal, SurfaceHolder.Callback, AxPluginResult {
    private final static Log Log = AxLog.getLog(CameraAPILevel7.class);

    private final String CameraID = "Camera";

    protected Semaphore lock = new Semaphore(0);

    final protected String id;
    final protected AxRuntimeContext runtimeContext;
    protected Camera camera;
    protected MediaRecorder recorder;

    // XXX
    protected boolean isPreviewing;
    protected SurfaceHolder holder;
    protected AxFile recodingOutputFile;
    protected PreviewLayer previewLayer;

    // for preview
    private int orientation;

    protected DeviceOrientationListener deviceOrientationListener;

    public CameraAPILevel7(AxRuntimeContext runtimeContext) {
        this.id = CameraID;
        this.runtimeContext = runtimeContext;
    }

    @Override
    public void captureImage(AxPluginContext context) {
        String path = null;

        boolean highRes = false;
        Object[] params = context.getParams();

        if (2 == params.length && params[1] != null) {
            @SuppressWarnings("unchecked")
            CameraOptions cameraOptions = new CameraOptions((Map<String, Object>) params[1]);
            path = cameraOptions.getDestinationFileName();
            highRes = cameraOptions.isHighRes();
        }

        AxFile axFile = getAxFile(path, CameraConstants.EXTENSION_PICTURE);
        Camera camera = null;
        try {
            camera = getNativeCamera();
            setCaptureParameter(camera, highRes);

            int deviceOrientation = deviceOrientationListener.getCurrentDeviceOrientation();
            JpegImageCallback callback = new JpegImageCallback(lock);

            // allsiwhite 2011-11-11 현재 raw data를 받아오는 방법을 모르겠다. 버퍼를 어떻게 늘려주지?
            // 그래서 일단 무시하기로 한다.
            camera.takePicture(null, null, callback);

            lock.acquire();

            byte[] data = callback.getData();
            if (data == null) {
                context.sendError(AxError.UNKNOWN_ERR, "Failed to capture image.");
            }
            else if (!ImageConvertor
                    .convertToFile(data, (File) axFile.getPeer(), deviceOrientation)) {
                context.sendError(AxError.UNKNOWN_ERR, "Failed to capture image.");
            }
            else {
                context.sendResult(axFile.getPath());
            }
        }
        catch (Exception e) {
            context.sendError(AxError.UNKNOWN_ERR, "Failed to capture image.");
        }
    }

    protected void setCaptureParameter(Camera camera, boolean highRes) {
        // setCaptureRotation(camera);
        // TODO highRes
        // Parameters parameters = camera.getParameters();
        // if (highRes) {
        // parameters.set
        // }
    }

    @Override
    public void startVideoCapture(AxPluginContext context) {
        @SuppressWarnings("unchecked")
        CameraOptions cameraOptions =
                new CameraOptions((Map<String, Object>) context.getParams()[1]);
        String path = cameraOptions.getDestinationFileName();

        if (recodingOutputFile != null) {
            context.sendError(AxError.UNKNOWN_ERR, "Recording is already working.");
            return;
        }
        recodingOutputFile = getAxFile(path, CameraConstants.EXTENSION_VIDEO);
        File outputFile = (File) recodingOutputFile.getPeer();

        // Initialized
        try {
            recorder = new MediaRecorder();

            releaseNativeCamera();

            recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
            recorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);

            if (Build.VERSION.SDK_INT < 8) {
                recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                recorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
                recorder.setVideoSize(CameraConstants.VIDEO_SIZE_WIDTH,
                        CameraConstants.VIDEO_SIZE_HEIGHT);
                recorder.setVideoFrameRate(CameraConstants.VIDEO_FRAME_RATE);
            }
            else {
                recorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
            }

            recorder.setOutputFile(outputFile.getAbsolutePath());
            recorder.setPreviewDisplay(this.holder.getSurface());

            recorder.prepare();
            recorder.start();
        }
        catch (Exception e) {
            try {
                outputFile.delete();
                outputFile = null;

                stopMediaRecorder();
                recodingOutputFile = null;
                setHolderAndStartPreview(this.holder);
            }
            catch (Exception e1) {}

            throw new AxError(AxError.UNKNOWN_ERR, e.getMessage());
        }

        context.sendResult(recodingOutputFile.getPath());
    }

    protected void stopMediaRecorder() {
        if (this.recorder != null) {
            try {
                this.recorder.release();
            }
            catch (Exception e) {}
            finally {
                this.recorder = null;
            }
        }
    }

    @Override
    public void stopVideoCapture(AxPluginContext context) {
        stopMediaRecorder();
        setHolderAndStartPreview(this.holder);

        if (recodingOutputFile == null) {
            context.sendError(AxError.UNKNOWN_ERR, "Recording is not working.");
        }
        else {
            recodingOutputFile = null;
            context.sendResult();
        }
    }

    protected void setHolderAndStartPreview(SurfaceHolder holder) {
        try {
            if (this.recorder == null) {
                Camera camera = getNativeCamera();
                camera.setPreviewDisplay(holder);
                camera.startPreview();
            }
        }
        catch (IOException e) {}
    }

    protected void insertImage(String title, String path) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, title);
        values.put(MediaStore.Images.Media.DATA, path);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        insertMediaStore(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
    }

    protected void insertVideo(String title, String path) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Video.Media.TITLE, title);
        values.put(MediaStore.Video.Media.DATA, path);
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/3gpp");

        insertMediaStore(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
    }

    protected void insertMediaStore(Uri uri, ContentValues values) {
        try {
            ContentResolver cr = runtimeContext.getActivity().getContentResolver();
            cr.insert(uri, values);
        }
        catch (Exception ignored) {
            // path가 internal storage인 경우 MediaContentProvider에서 접근 권한이 없으므로
            // exception 발생
        }
    }

    @Override
    public void createPreviewNode(AxPluginContext context) {
        // TODO Auto-generated method stub
    }

    @Override
    public void startPreview(final AxPluginContext context) {
        if (this.deviceOrientationListener == null) {
            this.deviceOrientationListener = new DeviceOrientationListener(runtimeContext);
        }
        this.deviceOrientationListener.enable();

        this.isPreviewing = true;
        final int x, y, w, h;

        try {
            float scale = runtimeContext.getWebView().getScale();

            x = (int) (context.getParamAsNumber(1).intValue() * scale);
            y = (int) (context.getParamAsNumber(2).intValue() * scale);
            w = (int) (context.getParamAsNumber(3).intValue() * scale);
            h = (int) (context.getParamAsNumber(4).intValue() * scale);
        }
        catch (Exception e) {
            context.sendError(AxError.INVALID_VALUES_ERR, "preview parameter error");
            return;
        }

        final Semaphore lock = new Semaphore(0);
        runtimeContext.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (CameraAPILevel7.this.previewLayer == null) {
                        attachPreviewLayer(w, h, x, y);
                        runtimeContext.addWebViewListener(CameraAPILevel7.this.previewLayer
                                .getScrollHandler());
                    }
                    context.sendResult();
                }
                catch (Exception e) {
                    context.sendError(AxError.UNKNOWN_ERR, "");
                }
                finally {
                    lock.release();
                }
            }
        });
        try {
            lock.acquire();
        }
        catch (InterruptedException e) {}
    }

    protected void attachPreviewLayer(int w, int h, int x, int y) {
        orientation = runtimeContext.getActivity().getRequestedOrientation();
        // XXX webView.getBackgroundColor?
        // axContext.getWebView().setBackgroundColor(Color.TRANSPARENT);
        runtimeContext.getActivity().setRequestedOrientation(
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        Size previewSize = getNativeCamera().getParameters().getPreviewSize();
        this.previewLayer = new PreviewLayer(runtimeContext.getActivity(), this, previewSize);
        this.previewLayer.setPreviewLayout(w, h, x, y);

        WebView webView = runtimeContext.getWebView();
        // XXX Mungyu PreviewLayer를 최상위로 올림. WebView보다 위에 위치함
        ((ViewGroup) webView.getParent()).addView(previewLayer, -1);
    }

    protected void detachPreviewLayer() {
        // XXX webView.setBackgroundColor(backupColor);
        // axContext.getWebView().setBackgroundColor(Color.WHITE);
        runtimeContext.getActivity().setRequestedOrientation(orientation);

        WebView webView = runtimeContext.getWebView();
        ((ViewGroup) webView.getParent()).removeView(this.previewLayer);
        this.previewLayer = null;
    }

    @Override
    public void stopPreview(final AxPluginContext context) {
        // / XXX Mungyu 2011-03-09 DOM Document와 LifeCycle을 동일하게 가져가야 할 것도 같다는
        // 생각도 들지만.
        // 이대로도 상관없지 않을까?? unload 에 stopPreview가 호출되기도 하고.
        if (this.deviceOrientationListener != null) {
            this.deviceOrientationListener.disable();
        }

        this.isPreviewing = false;

        if (recodingOutputFile != null) {
            stopMediaRecorder();
        }
        else if (null != camera) {
            try {
                camera.stopPreview();
            }
            catch (Exception e) {}

            releaseNativeCamera();
        }

        final Semaphore lock = new Semaphore(0);
        runtimeContext.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (CameraAPILevel7.this.previewLayer != null) {
                        runtimeContext.removeWebViewListener(CameraAPILevel7.this.previewLayer
                                .getScrollHandler());
                        detachPreviewLayer();
                    }
                    context.sendResult(null);
                }
                catch (Exception e) {
                    context.sendError(AxError.UNKNOWN_ERR, "");
                }
                finally {
                    lock.release();
                }
            }
        });
        try {
            lock.acquire();
        }
        catch (InterruptedException e) {}
    }

    @Override
    public void setPreviewLayout(AxPluginContext context) {
        int x, y, w, h;
        try {
            float scale = runtimeContext.getWebView().getScale();

            x = (int) (context.getParamAsNumber(1).intValue() * scale);
            y = (int) (context.getParamAsNumber(2).intValue() * scale);
            w = (int) (context.getParamAsNumber(3).intValue() * scale);
            h = (int) (context.getParamAsNumber(4).intValue() * scale);
        }
        catch (Exception e) {
            throw new AxError(AxError.INVALID_VALUES_ERR, "preview parameter error");
        }
        previewLayer.setPreviewLayout(w, h, x, y);

        context.sendResult();
    }

    @Override
    public Object getPluginResult() {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("id", id);
        return map;
    }

    protected android.hardware.Camera getNativeCamera() {
        if (this.camera == null) {
            synchronized (this) {
                if (this.camera == null) {
                    this.camera = Camera.open();
                }
            }
        }
        return this.camera;
    }

    protected void releaseNativeCamera() {
        if (this.camera != null) {
            synchronized (this) {
                if (this.camera != null) {
                    Camera temp = this.camera;
                    this.camera = null;
                    temp.release();
                }
            }
        }
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {}

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (isPreviewing) {
            this.holder = holder;

            setHolderAndStartPreview(this.holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        releaseNativeCamera();
    }

    private int getDegree() {
        // 허니컴에서 기본으로 하는 각도가 달라서 후에 사진을 돌렸을 때 엉뚱하게 돌아가는 일이 생겨서
        // 꼭 수정할 것
        if (Build.VERSION.SDK_INT >= 11 && Build.VERSION.SDK_INT <= 13) {
            return 270;
        }
        else {
            return 0;
        }
    }

    protected int roundOrientation(int orientation) {
        return ((orientation + 45 + getDegree()) / 90 * 90) % 360;
    }

    protected class DeviceOrientationListener extends OrientationEventListener {
        protected int deviceOrientation = ORIENTATION_UNKNOWN;

        DeviceOrientationListener(AxRuntimeContext runtimeContext) {
            super(runtimeContext.getActivity());
        }

        @Override
        public void onOrientationChanged(int orientation) {
            if (orientation == ORIENTATION_UNKNOWN) return;
            this.deviceOrientation = (roundOrientation(orientation) + 90) % 360;
            if (Log.isTraceEnabled()) {
                Log.trace("DeviceOrientation.onChanged(" + this.deviceOrientation + ")");
            }
        }

        int getCurrentDeviceOrientation() {
            int res = this.deviceOrientation;
            return res;
        }
    }

    @Override
    public void onActivityResume(Activity activity) {
        // previewLayer LifeCycle과의 일관성을 위해
        if (previewLayer != null) {
            // getNativeCamera().startPreview();
            deviceOrientationListener.enable();
        }

        // 이미 preview 중인걸로 되어있다면 Preview 를 살린다.
        if (isPreviewing) setHolderAndStartPreview(holder);
    }

    @Override
    public void onActivityPause(Activity activity) {
        if (previewLayer != null) {
            // getNativeCamera().stopPreview();
            deviceOrientationListener.disable();
        }
        releaseNativeCamera();
        stopMediaRecorder();

        recodingOutputFile = null;
        callJSStopVideoCapture();
    }

    private void callJSStopVideoCapture() {
        String url = "javascript:deviceapis.camera.stopVideoCaptureOf('" + id + "');";
        runtimeContext.getWebView().loadUrl(url);
    }

    /**
     * 주어진 가상 경로에 해당되는 AxFile을 반환하거나, 이 경로가 유효하지 않을 경우에는 새 AxFile을 생성한다. 이 AxFile의 peer인
     * java.io.file이 참조하는 파일이 이미 존재하는 파일일 경우엔 AxError을 발생시킨다. 파일이 존재하지 않을 시엔 새 파일을 생성하여 AxFile을
     * 반환한다.
     * 
     * @param path AxFile의 가상 경로
     * @param extension 해당 경로의 AxFile이 유효하지 않을 경우 새로 생성될 파일의 확장자
     * @return AxFile
     */
    private AxFile getAxFile(String path, String extension) {
        AxFile axFile = null;

        if ((path == null)
                || ((axFile = runtimeContext.getFileSystemManager().getFile(path)) == null)
                || (!(axFile.getPeer() instanceof File))) {
            axFile = createNewFile(extension);
        }

        File file = (File) axFile.getPeer();
        if (file.exists())
            throw new AxError(AxError.INVALID_VALUES_ERR, "the file already exists");

        File parent = file.getParentFile();
        if (!parent.exists() && !parent.mkdirs()) { throw new AxError(AxError.UNKNOWN_ERR,
                "the parent directory of the file cannot be created."); }

        try {
            if (!file.createNewFile() || !file.exists())
                throw new AxError(AxError.UNKNOWN_ERR, "failed to creating the file");
        }
        catch (IOException e) {
            // Nothing to do.
        }

        if (Log.isTraceEnabled()) {
            Log.trace("\"" + file.getAbsolutePath() + "\" is created.");
        }

        return axFile;
    }

    /**
     * 외부 메모리의 DCIM/Camera 디렉토리에 포함된 새 AxFile을 반환한다. 실제 유효하지 않는 AxFile을 반환할 수도 있다.
     * 
     * @param extension 생성할 파일의 확장자
     * @return AxFile
     */
    private AxFile createNewFile(String extension) {
        String newFilePath =
                "removable/DCIM/Camera/ax_" + System.currentTimeMillis() + "." + extension;
        return runtimeContext.getFileSystemManager().getFile(newFilePath);
    }
}
