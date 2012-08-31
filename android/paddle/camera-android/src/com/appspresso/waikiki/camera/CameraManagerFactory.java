package com.appspresso.waikiki.camera;

import com.appspresso.api.AxRuntimeContext;

class CameraManagerFactory {

    public static ICameraManagerInternal newCameraManager(AxRuntimeContext runtimeContext) {
        // if (Build.VERSION.SDK_INT > 8) {
        // return new CameraManagerLevel9(axContext);
        // }
        // if (Build.VERSION.SDK_INT > 7) {
        // return new CameraManagerAPILevel8(axContext);
        // }
        return new CameraManagerAPILevel7(runtimeContext);
    }

}
