package com.appspresso.waikiki.camera;

import android.app.Activity;
import com.appspresso.api.AxRuntimeContext;
import com.appspresso.api.AxError;
import com.appspresso.api.AxPluginContext;
import com.appspresso.api.DefaultAxPlugin;
import com.appspresso.api.activity.ActivityAdapter;

public class CameraManager extends DefaultAxPlugin implements ICameraManager, ICamera {
    public static final String FEATURE_DEFAULT = "http://wacapps.net/api/camera";
    public static final String FEATURE_SHOW = "http://wacapps.net/api/camera.show";
    public static final String FEATURE_CAPTURE = "http://wacapps.net/api/camera.capture";

    private ICameraManagerInternal cameraManager;
    private boolean canShow;
    private boolean canCapture;

    @Override
    public void activate(AxRuntimeContext runtimeContext) {
        super.activate(runtimeContext);

        runtimeContext.requirePlugin("deviceapis");

        canShow =
                runtimeContext.isActivatedFeature(FEATURE_DEFAULT)
                        || runtimeContext.isActivatedFeature(FEATURE_SHOW);
        canCapture =
                runtimeContext.isActivatedFeature(FEATURE_DEFAULT)
                        || runtimeContext.isActivatedFeature(FEATURE_CAPTURE);
        if (!canShow && !canCapture) { throw new AxError(AxError.SECURITY_ERR, ""); // Error message
        }

        this.cameraManager = CameraManagerFactory.newCameraManager(runtimeContext);
        runtimeContext.addActivityListener(new CamearaActivityListener());
    }

    @Override
    public void getCameras(AxPluginContext context) {
        this.cameraManager.getCameras(context);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.kthcorp.wp.paddle.model.camera.ICamera#createPreviewNode(com.kthcorp
     * .wp.chronometer.IPluginContext)
     */
    @Override
    public void createPreviewNode(AxPluginContext context) {
        if (!canShow) {
            context.sendError(AxError.SECURITY_ERR, ""); // TODO Error message
            return;
        }

        ICameraInternal camera = this.cameraManager.getCamera(context);
        camera.createPreviewNode(context);
    }

    public void startPreview(AxPluginContext context) {
        ICameraInternal camera = this.cameraManager.getCamera(context);
        camera.startPreview(context);
    }

    public void stopPreview(AxPluginContext context) {
        ICameraInternal camera = this.cameraManager.getCamera(context);
        camera.stopPreview(context);
    }

    public void setPreviewLayout(AxPluginContext context) {
        ICameraInternal camera = this.cameraManager.getCamera(context);
        camera.setPreviewLayout(context);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.kthcorp.wp.paddle.model.camera.ICamera#captureImage(com.kthcorp.wp
     * .chronometer.IPluginContext)
     */
    @Override
    public void captureImage(AxPluginContext context) {
        if (!canCapture) {
            context.sendError(AxError.SECURITY_ERR, ""); // TODO Error message
            return;
        }

        ICameraInternal camera = this.cameraManager.getCamera(context);
        camera.captureImage(context);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.kthcorp.wp.paddle.model.camera.ICamera#startVideoCapture(com.kthcorp
     * .wp.chronometer.IPluginContext)
     */
    @Override
    public void startVideoCapture(AxPluginContext context) {
        if (!canCapture) {
            context.sendError(AxError.SECURITY_ERR, ""); // TODO Error message
            return;
        }

        ICameraInternal camera = this.cameraManager.getCamera(context);
        camera.startVideoCapture(context);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.kthcorp.wp.paddle.model.camera.ICamera#stopVedioCapture(com.kthcorp
     * .wp.chronometer.IPluginContext)
     */
    @Override
    public void stopVideoCapture(AxPluginContext context) {
        ICameraInternal camera = this.cameraManager.getCamera(context);
        camera.stopVideoCapture(context);
    }

    class CamearaActivityListener extends ActivityAdapter {
        @Override
        public void onActivityPause(Activity activity) {
            cameraManager.onActivityPause(activity);
        }

        @Override
        public void onActivityResume(Activity activity) {
            cameraManager.onActivityResume(activity);
        }
    }
}
