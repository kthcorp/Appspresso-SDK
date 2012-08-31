package com.appspresso.waikiki.camera;

import android.app.Activity;
import android.hardware.Camera;

import com.appspresso.api.AxRuntimeContext;
import com.appspresso.api.AxError;
import com.appspresso.api.AxException;
import com.appspresso.api.AxPluginContext;

public class CameraManagerAPILevel7 implements ICameraManagerInternal {
    protected AxRuntimeContext runtimeContext;
    protected ICameraInternal[] cameras = null;

    public CameraManagerAPILevel7(AxRuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext;
    }

    protected String getId(AxPluginContext context) {
        try {
            return context.getParamAsString(0);
        }
        catch (Exception e) {
            return null;
        }
    }

    @Override
    public ICameraInternal getCamera(AxPluginContext context) {
        ICameraInternal camera = null;
        String id = getId(context);

        // parameter에 handle이 지정되어 있는지 검사
        if (id == null) {
            throw new AxError(AxError.INVALID_VALUES_ERR, "null id");
        }
        else {
            try {
                // parameter로 지정된 handle이 맞는건지 확인
                // camera = cameras[handle];
                for (ICameraInternal c : cameras) {
                    if (c.getId().equals(id)) {
                        camera = c;
                        break;
                    }
                }
            }
            catch (Exception e) {
                throw new AxError(AxError.UNKNOWN_ERR, "invalid handle"); // TODO
                                                                          // 적당한
                                                                          // err
                                                                          // code,
                                                                          // err
                                                                          // message
            }
        }

        return camera;
    }

    /*
     * Waikiki err code - NOT_SUPPORTED_ERR: If this feature is not supported. - SECURITY_ERR: If
     * the operation is not allowed. - UNKNOWN_ERR: In any other error case.
     * 
     * @see com.kthcorp.wp.paddle.model.camera.ICameraManager#getCameras(com.kthcorp
     * .wp.chronometer.IPluginContext)
     */
    @Override
    public void getCameras(AxPluginContext context) {
        /*
         * camera 개수, default camera 찾기. Since Android API Level 9 // Find the total number of
         * cameras available int numberOfCameras = Camera.getNumberOfCameras(); CameraInfo
         * cameraInfo = new CameraInfo();
         * 
         * for (int i = 0; i < numberOfCameras; i++) { Camera.getCameraInfo(i, cameraInfo);
         * 
         * if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) defaultCameraId = i; }
         * 
         * // Camera.open() - Creates a new Camera object to access the first back-facing camera on
         * the device // Camera.open(int cameraId) - Creates a new Camera object to access a
         * particular hardware camera
         */

        /*
         * 지금은 카메라의 개수를 알 수 없으므로 카메라의 유무는 일단 open해 보고 판단한다. 그러나 향후 getNumberOfCameras를 사용할 것이므로
         * default camera는 index 번호 0으로 식별한다.
         */
        if (cameras == null) {
            try {
                Camera c = android.hardware.Camera.open();

                if (null != c) {
                    c.release();

                    // camera 개수만큼 array 생성. 현재는 항상 1
                    cameras = new ICameraInternal[1];
                    // result = new Map[1];

                    // XXX 1st param 0: array index, default camera
                    ICameraInternal camera = newCameraInstance(runtimeContext);
                    cameras[0] = camera;
                }
                else {
                    throw new AxError(AxError.NOT_SUPPORTED_ERR, "camera not found");
                }
            }
            catch (AxException e) {
                throw e;
            }
            catch (Exception e) {
                context.sendError(AxError.NOT_SUPPORTED_ERR, e.getMessage());
                return;
            }
        }

        context.sendResult(cameras);
    }

    @Override
    public ICameraInternal newCameraInstance(AxRuntimeContext runtimeContext) {
        return new CameraAPILevel7(runtimeContext);
    }

    @Override
    public void onActivityPause(Activity activity) {
        if (cameras != null) {
            for (ICameraInternal camera : cameras) {
                camera.onActivityPause(activity);
            }
        }
    }

    @Override
    public void onActivityResume(Activity activity) {
        if (cameras != null) {
            for (ICameraInternal camera : cameras) {
                camera.onActivityResume(activity);
            }
        }
    }
}
